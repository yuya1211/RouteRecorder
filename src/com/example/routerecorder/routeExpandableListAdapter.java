package com.example.routerecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class routeExpandableListAdapter extends BaseExpandableListAdapter{
	
	private Context context;
	int sendGroupPosition = 0;
	private FragmentActivity activity;
	private MyListFragment fragment = new MyListFragment();
	
	
	//List of groups
	List<List<String>> tripAttr = new ArrayList<List< String>>();
	//List of children
	List<List< String >> placeAttr = new ArrayList<List< String>>();
	
	public routeExpandableListAdapter( MyListFragment _fragment, Context context, 
										List< List< String >> tripGroup,
										List<List< String>> placeChild){
		this.fragment = _fragment;
		this.tripAttr = tripGroup;
		this.placeAttr = placeChild;
		this.context = context;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return placeAttr.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if(view == null){
			LayoutInflater inflater = (LayoutInflater) context.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_route_expanded, null);
		}
		
		TextView placeName = (TextView) view.findViewById(R.id.place_name);
		placeName.setText(  placeAttr.get(groupPosition).get(childPosition)  );
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return placeAttr.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return tripAttr.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return tripAttr.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		sendGroupPosition = groupPosition;
		
		View view = convertView;
		
		if (view == null){
			
			LayoutInflater inflater = (LayoutInflater) context.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_route_collapsed, null);
									
		}
		
		
		TextView tripId = (TextView) view.findViewById(R.id.route_1);
		TextView tripName = (TextView) view.findViewById(R.id.route_2);
		TextView tripDesc = (TextView) view.findViewById(R.id.route_3);
		
		
		//tripId.setText( tripAttr.get(groupPosition).get(0));
		Integer curPosition = groupPosition +1 ;
		tripId.setText( curPosition.toString() );
		tripName.setText(tripAttr.get(groupPosition).get(1));
		tripDesc.setText( tripAttr.get(groupPosition).get(2));	

		
		ImageButton btnShow = (ImageButton) view.findViewById(R.id.show_imagebutton);
		btnShow.setOnClickListener(  new OnClickListener(){
			public void onClick(View v){		      
				fragment.showOnMap(tripAttr.get(groupPosition).get(0));
				Log.d("QQ" , "the trip attr you clicked " + tripAttr.get(groupPosition).get(0));
			}
			
		} );
		
		ImageButton btnDel = (ImageButton) view.findViewById(R.id.delete_imagebutton);
		btnDel.setOnClickListener( new OnClickListener(){
			public void onClick(View v){
				
				AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(context);
				deleteAlertDialog.setTitle("Warning!");
				String deleteMessage = "You are going to delete one trip: " + 
								"\"" +   tripAttr.get(groupPosition).get(1) + "\"";
								
				deleteAlertDialog.setMessage( deleteMessage )
									.setCancelable(true)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											//if(fragment == null)
												Log.d("QQ", 
														tripAttr.get(groupPosition).get(0).toString() );
											fragment.deleteItem(tripAttr.get(groupPosition).get(0));
											
										}})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();											
										}
									});
				
				AlertDialog deleteAlert = deleteAlertDialog.create();
				deleteAlert.show();				
						
			}// end of onClick listener
		});// end of set onClick listener
		
		
		
		btnDel.setFocusable(false);
		btnShow.setFocusable(false);
	
		return view;
	}

	protected String getTripId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
}
