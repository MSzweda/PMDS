package com.herring.pmds.tools;

public class ChildItem 
{

	public int id;
	public String day_str;
	public int hour;
	public int minute;
	public String setTo;
	public int isActive;
	public int devType;
		
		public ChildItem(int id, String day_str, int hour, int minute, String setTo, int devType, int isActive)
		{
			this.id=id;
			this.hour=hour;
			this.minute=minute;
			this.day_str = day_str;
			this.setTo = setTo;
			this.isActive = isActive;
			this.devType = devType;
		}
	
}
