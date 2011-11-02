package org.junit.moire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

/**
 * Test utility designed to detect and isolate test that fail due to interference from other tests.
 * @author alexanderheinricher
 *
 */
public class Moire {
	//Name for temporary file
	public static final String FILENAME = "MoireTestDNU";
	/**
	 * Run a moire test on a test suite.  
	 * This checks for dependencies between tests within the suite
	 * by running the request in both forward and reverse order.
	 * If a test fails on both runs, it is assumed to fail normally.
	 * If it fails on only one run, the failure must be caused by interactions
	 * with a test somewhere ahead of it in the order.  
	 * The tester will run a pairwise check on all such tests to determine which one
	 * caused the error.
	 * @param request  The request describing the test suite to be run.
	 * @return An instance of MoireResult describing the results of the test, or null in case of a problem.
	 */
	public static MoireResult run(Request request)
	{
		//Retrieve the results of running the test suite in both directions.
		List<Description> descriptions = getDescriptions(request.getRunner());
		List<String> forwardFailures = runIsolatedTest(descriptions);
		Collections.reverse(descriptions);
		List<String> reverseFailures = runIsolatedTest(descriptions);
		if(forwardFailures == null || reverseFailures == null)
		{
			return null;
		}
		//If no errors, report that the test passed.
		if(forwardFailures.isEmpty() && reverseFailures.isEmpty())
		{
			return new Okay();
		}
		//If the above condition fails, there must be a failing or interfering test
		//Greedily, check the first test in the forward run.
		String s = null;
		int sIndex = 0;
		boolean forward = true;
		if(!forwardFailures.isEmpty())
		{
			s = forwardFailures.get(0);
			Collections.reverse(descriptions);
		}
		//If forward run is empty, a test in the reverse run must interfere.
		else
		{
			s = reverseFailures.get(0);
			forward = false;
		}
		//Find the index of the failing test in the list of tests in the suite.
		for(int i = 0; i < descriptions.size(); i++)
		{
			if(descriptions.get(i).toString().equals(s))
			{
				sIndex = i;
				break;
			}
		}
		//If a test appeared in both lists, check to see if it's failing in its own right.
		if(forwardFailures.contains(s) && reverseFailures.contains(s))
		{
			List<String> result = runIsolatedTest(Collections.singletonList(descriptions.get(sIndex)));
			if(result == null)
			{
				return null;
			}
			if(!result.isEmpty())
			{
				return new FailingTest(s);
			}
		}
		//For all tests before the failing test, run it and the failing test
		for(int i = 0; i <sIndex; i++)
		{
			List<Description> params = new ArrayList<Description>();
			params.add(descriptions.get(i));
			params.add(descriptions.get(sIndex));
			
			List<String> result = runIsolatedTest(params);

			if(result == null)
			{
				return null;
			}
			if(!result.isEmpty())
			{
				if(forward)
				{
					return new InterferingTest(descriptions.get(i).toString(), s);
				}
				else
				{
					return new RevInterferingTest(descriptions.get(i).toString(), s);
				}
			}
		}
		if(forward)
		{
			return new ComplexTest(s);
		}
		else
		{
			return new RevComplexTest(s);
		}
	}
	/**
	 * Given a runner, constructs an in-order list of its leaf descriptions.
	 * 
	 * @param runner  The runner to be described.
	 * @return  A list of descriptions detailing the tests runner runs in the order they will be run.
	 */
	public static List<Description> getDescriptions(Runner runner)
	{
		List<Description> out = new ArrayList<Description>();
		getDescriptionsRecursive(runner.getDescription(), out);
		return out;
	}
	private static void getDescriptionsRecursive(Description description, List<Description> out)
	{
		if(description.isEmpty())
		{
			return;
		}
		else if(description.isSuite())
		{
			for(Description child: description.getChildren())
			{
				getDescriptionsRecursive(child, out);
			}
		}
		else if(description.isTest())
		{
			out.add(description);
		}
		
	}
	/**
	 * Creates a comparator that, when run on runner will cause 
	 * runner.sortWith(reverseOrder(runner)) to return a reversed version
	 * of runner.
	 * 
	 * @param runner  The runner to be reversed
	 * @return A comparator that enforces a reverse ordering of runner.
	 */
	public static Comparator<Description> reverseOrder(Runner runner)
	{
		final HashMap<Description, Integer> map = new HashMap<Description, Integer>();
		Collection<Description> order = getDescriptions(runner);
		int index = 1;
		for(Description d : order)
		{
			map.put(d, order.size() - index);
			index ++;
		}
		return new Comparator<Description>(){
			public int compare(Description o1, Description o2)
			{
				return map.get(o1).compareTo(map.get(o2));
			}
		};
	}
	//A function for running a test suite in a separate process.
	//This allows for controlled checks on static state.
	private static List<String> runIsolatedTest(List<Description> params)
	{
		//Set up a new file
		File file = null;
		int index = 0;
		while(file == null)
		{
			index++;
			try {
				file = File.createTempFile(FILENAME, "_" + index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		file.deleteOnExit();
		//Construct the argument string for MoireStandAlone.
		List<String> args = new LinkedList<String>();
		args.add(params.toString());
		args.add(0, file.getAbsolutePath());
		args.add(0, "org.junit.moire.MoireStandAlone");
		args.add(0, System.getProperty("java.class.path"));
		args.add(0, "-classpath");
		args.add(0, "java");
		//Build and run the new process.
		ProcessBuilder builder = new ProcessBuilder(args);
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//Wait for the process.
		try {
			if(process.waitFor() != 0)
			{
				return null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		//Scan the results from the temporary file.
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		List<String> out = new ArrayList<String>();
		while(scan.hasNextLine())
		{
			out.add(scan.nextLine());
		}
		return out;
	}
}	
