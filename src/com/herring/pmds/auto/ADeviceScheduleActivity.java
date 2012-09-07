package com.herring.pmds.auto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.herring.pmds.R;
import com.herring.pmds.tools.ChildItem;
import com.herring.pmds.tools.Constants;
import com.herring.pmds.tools.GroupItem;
import com.herring.pmds.tools.PMDSExpendableListAdapter;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class ADeviceScheduleActivity extends ExpandableListActivity 
{
	private AutoSchedulesDBManager db; 

	private static Context context;
	
	private ArrayList<ArrayList<ChildItem>> children;
	private ArrayList<GroupItem> groups;
	
	private static final int DELETE_ITEM = 0;
	private static final int DEACTIVATE_ALL = 1;
	private static final int ACTIVATE_ALL = 2;
	private static final int DELETE_ALL = 3;
	
	private PMDSExpendableListAdapter adapter;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        context = getApplicationContext();
	        
	        fillData();
	        
	        registerForContextMenu(getExpandableListView());
	   
	        /*   
	        SimpleExpandableListAdapter expListAdapter =
	    			new SimpleExpandableListAdapter(
	    				this,
	    				createGroupList(groups),	// groupData describes the first-level entries
	    				//R.layout.group_item,	// Layout for the first-level entries
	    				android.R.layout.simple_expandable_list_item_1,
	    				new String[] { "deviceName" },	// Key in the groupData maps to display
	    				//new int[] { R.id.giTW1 },
	    				new int[] { android.R.id.text1 },// Data under "colorName" key goes into this TextView
	    				createChildList(groups),	// childData describes second-level entries
	    				R.layout.list_item,	// Layout for second-level entries
	    				new String[] { "dateString", "setTo" },	// Keys in childData maps to display
	    				new int[] { R.id.liTW1, R.id.liTW2 }	// Data under the keys above go into these TextViews
	    			);
	    		setListAdapter( expListAdapter );
	    		*/
	
	}
	
	/*
	 * prepare the data and fill the adapter with it
	 */
	private void fillData()
	{
		db = new AutoSchedulesDBManager(context);
		groups = convertGroupCursorToList(db.getDevices());
		children = prepareList(groups);
		adapter = new PMDSExpendableListAdapter(this, groups, children);
		setListAdapter(adapter);
		db.closeDB();
	}
	
	/*
	 * creating context menu for list(non-Javadoc)
	 * @see android.app.ExpandableListActivity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);

		//Context menu for children
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) 
		{
			menu.setHeaderTitle(getString(R.string.options));
			menu.add(0, DELETE_ITEM, 0, getString(R.string.delete));
		}
		//Context menu for groups
		if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) 
		{
			menu.setHeaderTitle(getString(R.string.options));
			menu.add(0, DEACTIVATE_ALL, 0, getString(R.string.deactivate_all));
			menu.add(0, ACTIVATE_ALL, 0, getString(R.string.activate_all));
			menu.add(0, DELETE_ALL, 0, getString(R.string.delete_all));
		}
	}
	
	/*
	 * what happens after we click the item(non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	public boolean onContextItemSelected(MenuItem menuItem) 
	{
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuItem.getMenuInfo();
		
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		
		int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
		
		boolean retVal = false;
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) 
		{
			ChildItem kid = children.get(group).get(child);
			int delId = kid.id;
			switch (menuItem.getItemId()) 
			{
				case DELETE_ITEM:
					db = new AutoSchedulesDBManager(context);
					db.deleteScheduleItem(delId);
					db.closeDB();
					fillData();
					retVal =  true;
					break;
				default:
					retVal = super.onContextItemSelected(menuItem);
					break;
			}
		}
		else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) 
		{
			GroupItem gi = groups.get(group);
			int devType = gi.id;
			switch (menuItem.getItemId()) 
			{
				case DEACTIVATE_ALL:
					db = new AutoSchedulesDBManager(context);
					int changeCount1 = db.changeActiveStateForDeviceSchedules(devType, Constants.INACTIVE_SCHEDULE_MODE);
					db.closeDB();
					Log.i("PMDS", "Changed "+changeCount1+" items");
					fillData();
					retVal =  true;
					break;
				case ACTIVATE_ALL:
					db = new AutoSchedulesDBManager(context);
					int changeCount = db.changeActiveStateForDeviceSchedules(devType, Constants.ACTIVE_SCHEDULE_MODE);
					db.closeDB();
					Log.i("PMDS", "Changed "+changeCount+" items");
					fillData();
					retVal =  true;
					break;
				case DELETE_ALL:
					db = new AutoSchedulesDBManager(context);
					int delCount = db.deleteScheduleItemsForDevice(devType);
					db.closeDB();
					Log.i("PMDS", "Deleted "+delCount+" items");
					fillData();
					retVal =  true;
					break;
				default:
					retVal = super.onContextItemSelected(menuItem);
					break;
			}
		}

		return retVal;
	}


	
	/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List createChildList(List<GroupItem> groups) 
	{
		ArrayList result = new ArrayList();
		
		for(int i = 0; i < groups.size(); ++i)
		{
			ArrayList secList = new ArrayList();
			List<ChildItem> childrenList = convertChildrenCursorToList(groups.get(i).id);
			for(int j = 0; j < childrenList.size(); ++j)
			{
				HashMap child = new HashMap();
				child.put( "schedID", childrenList.get(j).id);
				child.put( "dateString",childrenList.get(j).day_str+", "+ childrenList.get(j).hour+":"+childrenList.get(j).minute);
			    child.put( "setTo", getString(R.string.set_to)+" "+childrenList.get(j).setTo );
				secList.add( child );
			}
			result.add( secList );
		}
		return result;
	  }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List createGroupList(List<GroupItem> groups) 
	{
		  ArrayList result = new ArrayList();
		  for( int i = 0 ; i < groups.size() ; ++i ) 
		  {
			HashMap m = new HashMap();
		    m.put( "deviceName",groups.get(i).nameString );
			result.add( m );
		  }
		  return (List)result;
	    }
	*/
	
	/*
	 * convert given group cursor to ArrayList
	 */
	private ArrayList<GroupItem> convertGroupCursorToList(Cursor cursor)
	{
		ArrayList<GroupItem> groups = new ArrayList<GroupItem>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			int pom = cursor.getInt(cursor.getColumnIndex(AutoSchedulesDBManager.DEVICE_TYPE));
			String name = getDeviceName(pom);
			groups.add(new GroupItem(pom, name));
			cursor.moveToNext();
				
		}
		cursor.close();
		return groups;
	}
	
	/*
	 * get device name from its id
	 */
	private String getDeviceName(int dev)
	{
		if(dev==Constants.WIFI_DEVICE) return getString(R.string.wifi);
		else if(dev==Constants.BLUETOOTH_DEVICE) return getString(R.string.bluetooth);
		else if(dev==Constants.AUTOROTATION) return getString(R.string.autorotation);
		else if(dev==Constants.SCREEN_DEVICE) return getString(R.string.screen);
		else if(dev==Constants.CPU_DEVICE) return getString(R.string.cpu);
		else return null;
	}
	
	/*
	 * prepare final list that will be fed to the adapter
	 */
	private ArrayList<ArrayList<ChildItem>> prepareList(List<GroupItem> groups)
	{
		ArrayList<ArrayList<ChildItem>> kids = new ArrayList<ArrayList<ChildItem>>();

		for(int i = 0; i < groups.size(); ++i)
		{
			ArrayList<ChildItem> children = convertChildrenCursorToList(groups.get(i).id);
			if (kids.size() < i + 1) 
			{
	            kids.add(new ArrayList<ChildItem>());
	        }

			for(int j = 0; j < children.size(); ++j)
			{
				kids.get(i).add(children.get(j));
			}
		}
		return kids;
	}

	
	/*
	 * convert the children cursor of given group to ArrayList
	 */
	private ArrayList<ChildItem> convertChildrenCursorToList(int devType)
	{
		Cursor cur = db.getAllSchedulesByDevice(devType);
		ArrayList<ChildItem> children = new ArrayList<ChildItem>();
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			//Log.i("PMDS","time: "+cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.HOUR_OF_DAY))+":"+cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.MINUTE_OF_HOUR))+" Is active: " + cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.IS_ACTIVE)));
			children.add(new ChildItem(
					cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.SCHEDULE_ROWID)),
					getDayString(cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.WEEKDAY))),
					cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.HOUR_OF_DAY)),
					cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.MINUTE_OF_HOUR)),
					getSetToString(cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.ACTION_TYPE)),devType),
					devType,
					cur.getInt(cur.getColumnIndex(AutoSchedulesDBManager.IS_ACTIVE))
					));
			cur.moveToNext();
		}
		cur.close();
		return children;
	}
	
	/*
	 * get name of day of given day id
	 */
	private String getDayString(int day)
	{
		if(day==Calendar.MONDAY) return getString(R.string.monday);
		else if(day==Calendar.TUESDAY) return getString(R.string.tuesday);
		else if(day==Calendar.WEDNESDAY) return getString(R.string.wednesday);
		else if(day==Calendar.THURSDAY) return getString(R.string.thursday);
		else if(day==Calendar.FRIDAY) return getString(R.string.friday);
		else if(day==Calendar.SATURDAY) return getString(R.string.saturday);
		else if(day==Calendar.SUNDAY) return getString(R.string.sunday);
		else return null;
	}
	
	/*
	 * create the Set to: string
	 */
	private String getSetToString(int setTo, int dev)
	{
		String ret = "";
		if(dev==Constants.BLUETOOTH_DEVICE || dev==Constants.WIFI_DEVICE || dev==Constants.AUTOROTATION)
		{
			if(setTo==0) ret = getString(R.string.off_);
			if(setTo==1) ret = getString(R.string.on_);
		}
		if(dev==Constants.CPU_DEVICE)
		{
			if(setTo==Constants.CONSERVATIVE_MODE) ret = Constants.CONSERVATIVE_MODE_STR;
			if(setTo==Constants.POWERSAVE_MODE) ret = Constants.POWERSAVE_MODE_STR;
			if(setTo==Constants.ONDEMAND_MODE) ret = Constants.ONDEMAND_MODE_STR;
		}
		if(dev==Constants.SCREEN_DEVICE)
		{
			ret = String.valueOf(setTo);
		}
		
		return ret;
	}
	



}
