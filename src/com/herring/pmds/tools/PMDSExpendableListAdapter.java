package com.herring.pmds.tools;

import java.util.ArrayList;

import com.herring.pmds.R;
import com.herring.pmds.manual.ManualSchedulesDBManager;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PMDSExpendableListAdapter extends BaseExpandableListAdapter 
{

	private Context context;

    private ArrayList<GroupItem> groups;

    private ArrayList<ArrayList<ChildItem>> children;

    public PMDSExpendableListAdapter(Context context, ArrayList<GroupItem> groups, ArrayList<ArrayList<ChildItem>> children) 
    {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    
	@Override
	public Object getChild(int groupPosition, int childPosition) 
	{
		return children.get(groupPosition).get(childPosition);

	}
	
	public void removeChild(int groupPosition, int childPosition)
	{
		children.get(groupPosition).remove(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) 
	{
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
	{
		final ChildItem child = (ChildItem) getChild(groupPosition, childPosition);
        if (convertView == null) 
        {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        
        TextView tv = (TextView) convertView.findViewById(R.id.liTW1);
        tv.setText(child.day_str+", "+ child.hour+":"+child.minute);
        
        TextView tv2 = (TextView) convertView.findViewById(R.id.liTW2);
        tv2.setText(context.getString(R.string.set_to)+" "+child.setTo);

        ImageView iv = (ImageView)convertView.findViewById(R.id.liIV);
        if (child.devType == Constants.WIFI_DEVICE) 
        {
            iv.setImageResource(R.drawable.wifi_ico_32);
        } 
        else if (child.devType == Constants.BLUETOOTH_DEVICE) 
        {
            iv.setImageResource(R.drawable.bt_ico_32);
        }
        else if (child.devType == Constants.SCREEN_DEVICE) 
        {
            iv.setImageResource(R.drawable.screen_ico_32);
        }
        else if (child.devType == Constants.AUTOROTATION) 
        {
            iv.setImageResource(R.drawable.rot_ico_32);
        }
        else if (child.devType == Constants.CPU_DEVICE) 
        {
            iv.setImageResource(R.drawable.cpu_ico_32);
        }
       
        ToggleButton tb = (ToggleButton)convertView.findViewById(R.id.activeTB);
        if(child.isActive == 1)
        {
        	tb.setChecked(true);
        }
        else tb.setChecked(false);
        
        
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				ManualSchedulesDBManager db = new ManualSchedulesDBManager(context);
				if(isChecked)
				{
					db.changeActiveState(child.id, Constants.ACTIVE_SCHEDULE_MODE);
				}
				else
				{
					db.changeActiveState(child.id, Constants.INACTIVE_SCHEDULE_MODE);
				}
				db.closeDB();
				//Log.i("PMDSExpendableAdapter","Clicked toggle button for id: "+child.id);
				if(isChecked) child.isActive = 1;
				else child.isActive = 0;
				
			}

        });
        
       
        return convertView;

	}
	

	@Override
	public int getChildrenCount(int groupPosition) 
	{
		return children.get(groupPosition).size();

	}

	@Override
	public Object getGroup(int groupPosition) 
	{
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() 
	{
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) 
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
	{
		GroupItem groupI = (GroupItem) getGroup(groupPosition);
		String group = groupI.nameString;
        if (convertView == null) 
        {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            //convertView = infalInflater.inflate(R.layout.device_row, null);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        //TextView tv = (TextView) convertView.findViewById(R.id.giTW1);
        tv.setText(group);
        return convertView;

	}

	@Override
	public boolean hasStableIds() 
	{
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) 
	{
		return false;
	}

}
