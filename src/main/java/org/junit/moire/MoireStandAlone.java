package org.junit.moire;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
/**
 * A static class intended to allow Moire to run tests in a separate process.
 * @author alexanderheinricher
 *
 */
public class MoireStandAlone {
	/**
	 * Main method; Not intended for use outside of the Moire utility.
	 * @param args
	 */
	public static void main(String[] args)
	{
		JUnitCore core = new JUnitCore();
		try
		{	
			//set up the output file.
			File file = new File(args[0]);
			FileOutputStream os = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(os);
			//Retrieve the test data from the arguments
			String tests = args[1].substring(1, args[1].length() - 1);
			String[] testArray = tests.split(", ");
			String testClass = testArray[0].substring(testArray[0].indexOf("(")+1, testArray[0].indexOf(")"));
			//Construct a test request.
			Class<?> myClass = Class.forName(testClass);
			Request request = null;
			//Run all tests in the specified order, and record the results to the file.
			for(String s: testArray)
			{	
				//Construct a new request that will run the desired test.
				request = Request.aClass(myClass);
				Description des = null;
				for(Description d: Moire.getDescriptions(request.getRunner()))
				{
					if(d.getDisplayName().equals(s))
					{
						des = d;
					}
				}
				//Check for error conditions; if des is not in the request, the requested test list is invalid.
				if(des == null)
				{
					out.write("Missing Test: " + s + ": " +Moire.getDescriptions(request.getRunner()) +"\n");
					out.flush();
					out.close();
					os.close();
					System.exit(42);
				}
				Request tempRequest = request.filterWith(des);
				Result result = core.run(tempRequest);
				//Write the names of the tests that failed.
				for(Failure f: result.getFailures())
				{
					out.write(f.getDescription().getDisplayName() + "\n");
				}
			}
			//Flush the buffer and close the file streams before exiting.
			out.flush();
			out.close();
			os.close();
			System.exit(0);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(2);
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			System.exit(3);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			System.exit(4);
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(5);
		}
		catch (Error e)
		{
			e.printStackTrace();
			System.exit(6);
		}
		
	}
}
