package com.herring.pmds.superuser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;


/* this class helps read and write to files. Even system files.
 * Android is not going to like this solution and can throw System errors, such as "can't kill process"
 * the app will still work, the system will still works.
 * basically the only noticeable thing about this is a big log entry in logcat and granted su priviliges
 */
public class SUHandler 
{
	
	public static final String NA = "not available";
	private static final String NEW_LINE = "\n";
	
	
	/* try to write given value to a specific file
	 * file - file object to modify
	 * value - what we want to write to the file
	 */
	public static boolean writeFile(File file, String value) 
	{
		//check if file exists, if not return false
		if (!file.exists()) 
		{
			Log.w("PMDS SUHandler", file.getAbsolutePath() + " does not exist!");
			return false;
		}
		
		//if it does exist read the file
		String readFile = readFile(file);
		
		//check if the value is correct first (not null, not already there)
		if (value == null || value.equals(readFile) || SUHandler.NA.equals(readFile)) 
		{
			return false;
		}
		
		//if it's correct set that file to given value
		synchronized (file) 
		{
			String path = file.getAbsolutePath();
			Log.w("PMDS SUHandler", "Writing " + value + " to " + path);
			return SUHandler.execute("echo " + value + " > " + path);
		}
	}
	
	/* read the given file
	 * 
	 */
	public static String readFile(File file) 
	{
		//check if the file is either null or doesn't exist
		if (!file.exists() || file == null) 
		{
			return NA;
		}
		
		//if it's correct, begin reading
		//synchronized is a must here 
		synchronized (file) 
		{
			StringBuilder returnedValue = new StringBuilder();
			BufferedReader reader = null;
			
			try {
				//if file can be read, use BufferedReader
				if (file.canRead()) 
				{
					reader = new BufferedReader(new FileReader(file), 256);
					String line = reader.readLine();
					while (line != null && !line.trim().equals("")) 
					{
						if (returnedValue.length() > 0) 
						{
							returnedValue.append("\n");
						}
						returnedValue.append(line);
						line = reader.readLine();
					}
					reader.close();
				} 
				else 
				{
					Log.w("PMDS SUHandler", "Cannot read from file >" + file + "< it does not exist.");
				}
			} 
			//catch ALL the errors!
			catch (Throwable e) 
			{
					Log.e("PMDS SUHandler", "Can't open file", e);
			} 
			finally 
			{
				if (reader != null) 
				{
					try 
					{
						reader.close();
					} 
					catch (IOException e) 
					{
						Log.w("PMDS SUHandler", "Can't close reader", e);
					}
					reader = null;
				}
			}
			String readValue = returnedValue.toString();
			if (readValue.trim().equals("")) 
			{
				readValue = NA;
			}
			Log.w("PMDS SUHandler", "Reading file " + file.getAbsolutePath() + " returned: " + readValue);
			return readValue;
		}
	}
	
	/* execute a command as root
	 * cmd - command string
	 * 
	 * this SHOULD read stdout and stderr, will add it later
	 */
	public static boolean execute(String cmd) 
	{
		Process p = null;
		boolean success = false;
		try 
		{
			Log.w("PMDS SUHandler", "Trying to run: " + cmd);
			p = new ProcessBuilder()
					.command("su")
					.redirectErrorStream(false)
					.start();
			
			DataOutputStream os = new DataOutputStream(p.getOutputStream());

			//add new line at the end if it's not there
			if (!cmd.endsWith(NEW_LINE)) 
			{
				cmd += NEW_LINE;
			}
			
			os.writeBytes(cmd);

			os.writeBytes("exit\n");
			os.flush();

			p.waitFor();
			
			Log.i("PMDS SUHandler", "Command :" + cmd.trim() + ", returned " + p.exitValue());
				
			if (p.exitValue() != 255) 
			{
				success = true;
			}
		} 
		catch (InterruptedException e) 
		{
			Log.e("PMDS SUHandler", "Interrupt while executing:" + cmd, e);
		} 
		catch (FileNotFoundException e) 
		{
			Log.e("PMDS SUHandler", "File not found, executed:" + cmd);
		} 
		catch (IOException e) 
		{
			Log.e("PMDS SUHandler", "IO error from: " + cmd, e);
		} 
		finally 
		{
			if (p != null) 
			{
				p.destroy();
			}
		}
		return success;
	}

}
