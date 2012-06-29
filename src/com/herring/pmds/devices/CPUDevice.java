package com.herring.pmds.devices;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

import android.util.Log;

import com.herring.pmds.superuser.SUHandler;

public class CPUDevice 
{
	
	//constants

	//dirs
	private static final String BASE_DIR = "/sys/devices/system/cpu/";
	private static final String CPUFREQ_DIR = "/cpufreq/";
	
	private static final String SCALING_GOVERNOR = "scaling_governor";
	
	//values
	private final String[] CPUs;
	private final Map<String, File[]> fileMap = new WeakHashMap<String, File[]>();
	
	/* 
	 * this constructor will check how many cpus are there and will save them in the CPUs array
	 */
	public CPUDevice()
	{
		File cpuBase = new File(BASE_DIR);
		String[] CPUs = cpuBase.list(new FilenameFilter() 
		{

			@Override
			public boolean accept(File dir, String filename) 
			{
				File file = new File(dir, filename);
				if(file.isDirectory() && filename.matches("cpu\\d"))
				{
					return true;
				}
				else return false;
			}
		});
		this.CPUs = CPUs;
	}
	
	/*
	 * mode setting function, the Strings used will be "ondemand", "powersave" or "conservative"
	 */
	public boolean setMode(String mode) 
	{
		Log.i("PMDS CPUDevice", "Setting mode to " + mode);
		return writeFiles(getFiles(SCALING_GOVERNOR), mode);
	}
	
	public String getMode() 
	{
		File[] files = getFiles(SCALING_GOVERNOR);
		return SUHandler.readFile(files[0]);
	}
	
	/*
	 * write all the files with the help of SUHandler writeFile function
	 * we're dealing with system files here
	 */
	public boolean writeFiles(File[] files, String value) 
	{
		boolean retVal = false;
		for (int i = 0; i < files.length; i++) 
		{
			if (SUHandler.writeFile(files[i], value)) 
			{
				retVal = true;
			}
		}
		return retVal;
	}


	/*
	 * get the needed files
	 */
	public File[] getFiles(String name) 
	{
		String mapName = name;
		File[] files = fileMap.get(mapName);
		if (files == null) 
		{
			ArrayList<File> fileList = new ArrayList<File>(CPUs.length);
			File file;
			file = new File(BASE_DIR + CPUs[0], name);
			if (checkFile(file)) 
			{
				// get the other cpu files
				fileList.add(0, file);
				for (int i = 1; i < CPUs.length; i++) 
				{
					file = new File(BASE_DIR + CPUs[i], name);
					if (checkFile(file)) 
					{
						fileList.add(i, file);
					}
				}
			} 
			else 
			{
				file = new File(BASE_DIR + CPUFREQ_DIR, name);
				if (checkFile(file)) 
				{
					fileList.add(0, file);
				} 
				else 
				{
					file = new File(BASE_DIR, name);
					if (checkFile(file)) 
					{
						fileList.add(0, file);
					} 
					else 
					{
						file = new File(BASE_DIR + CPUs[0] + CPUFREQ_DIR, name);
						if (checkFile(file)) 
						{
							// get the other cpu files
							fileList.add(0, file);
							for (int i = 1; i < CPUs.length; i++) 
							{
								try 
								{
									file = new File(BASE_DIR + CPUs[i] + CPUFREQ_DIR, name);
									if (checkFile(file)) 
									{
										fileList.add(i, file);
									}
								} 
								catch (IndexOutOfBoundsException e) 
								{
									Log.w("PMDS CPUDevice", "Cpu index " + i, e);
								}
							}
						}

					}
				}
			}

			files = new File[fileList.size()];
			files = fileList.toArray(files);

			if (files == null) 
			{
				files = new File[0];
			}

			fileMap.put(mapName, files);
		}

		String s = "";
		for (int i = 0; i < files.length; i++) 
		{
			s = s + " " + files[i].getAbsolutePath();
		}
		Log.w("PMDS CPUDevice", "Files for " + name + ": " + s);
		
		return files;
	}
	
	/*
	 * check if the file is ok (exists, not null, not root directory)
	 */
	private boolean checkFile(File file) 
	{
		if (file == null || !file.exists()) 
		{
			return false;
		}
		return !"/".equals(file.getAbsolutePath());
	}

}
