package com.ubirouting.ubimapdemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ubirouting.ubimapdemo.view.HorizontalSwitchListView;
import com.ubirouting.ubimapdemo.view.HorizontalSwitchListView.HorizontalSwitchClickListener;
import com.ubirouting.ubimapdemo.view.StoreDetailView;
import com.ubirouting.ubimapdemo.view.StoreDetailView.StoreDetailClickListener;
import com.ubirouting.ubimaplib.UbiMapListener;
import com.ubirouting.ubimaplib.UbiMapView;
import com.ubirouting.ubimaplib.model.map.Area;
import com.ubirouting.ubimaplib.model.map.Floor;
import com.ubirouting.ubimaplib.model.map.MapModel;
import com.ubirouting.ubimaplib.model.map.Mark;

public class MapActivity extends Activity {

	// UbiMapView object
	private UbiMapView mMap;

	// Floor Switching Widget
	private HorizontalSwitchListView mFloorSwitcher;

	// Pop-up storeDetail Widget
	private StoreDetailView mPoiDetailView;
	private ArrayList<String> floorStrList;
	private MapModel currentModel = null;

	private MapModel startModel = null;
	private MapModel endModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Intent i = getIntent();
		long mapId = i.getLongExtra("mapId", -1);
		mMap = (UbiMapView) findViewById(R.id.mapview);

		// load the map via mapId
		mMap.load(mapId);

		mFloorSwitcher = (HorizontalSwitchListView) findViewById(R.id.hslvSwitchList);
		floorStrList = new ArrayList<String>();

		mPoiDetailView = (StoreDetailView) findViewById(R.id.sdvDetailView);
		mPoiDetailView.setListener(new StoreDetailClickListener() {

			@Override
			public void onClickStart() {
				if (currentModel != null) {

					// mark start
					mMap.markAsStart(currentModel);
					startModel = currentModel;
				}
			}

			@Override
			public void onClickEnd() {
				if (currentModel != null) {
					// mark end
					mMap.markAsEnd(currentModel);

					if (startModel != null && currentModel != null)
						// navigate
						mMap.navigate(startModel, currentModel);
				}
			}

			@Override
			public void onClickDetail() {
				// TODO
			}
		});

		mMap.setOnMapListener(new UbiMapListener() {

			@Override
			public void onSwitchFloor(int area) {

			}

			// When map loading completed, the floor information will be
			// returned by this method. You can access floors via 'floorList'.
			// Each element in 'floorList' is a instance of
			// com.ubirouting.ubimaplib.model.map.Floor class.
			@Override
			public void onLoadData(List floorList) {
				Iterator<Floor> itr = floorList.iterator();
				while (itr.hasNext()) {
					Floor f = itr.next();
					
					Log.d("UbiMapDemo", f.toString());
					floorStrList.add(f.getName());
				}

				mFloorSwitcher.setAdapter(floorStrList, MapActivity.this);
				mFloorSwitcher.setOnHorizontalSwitchListener(new HorizontalSwitchClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO
					}
				});
			}

			@Override
			public void onFailedLoadData() {
				Toast.makeText(MapActivity.this, "Load data failed", Toast.LENGTH_LONG).show();
			}

			// Called when clicking on the map view, returning the x and y
			// coordinate of click point.
			@Override
			public void onClickMap(float x, float y) {
				if (mPoiDetailView != null) {
					mPoiDetailView.hide();
				}
			}

			// Called when clicking on certain area
			@Override
			public void onClickArea(Area area) {
				if (mPoiDetailView != null) {
					mPoiDetailView.show();
				}
				currentModel = area;
				mPoiDetailView.setTitle(area.getName().trim().equals("") ? "Other" : area.getName());
				mPoiDetailView.setIcon(area.getIcon());
			}

			// Called when clicking on certain mark
			@Override
			public void onClickMark(Mark mark) {
				if (mPoiDetailView != null) {
					mPoiDetailView.show();
				}
				currentModel = mark;
				mPoiDetailView.setTitle("Other");
				mPoiDetailView.setIcon(mark.getIcon());
			}

		});
	}
}
