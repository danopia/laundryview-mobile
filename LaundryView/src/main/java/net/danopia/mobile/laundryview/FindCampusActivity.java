package net.danopia.mobile.laundryview;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.danopia.mobile.laundryview.data.AssistClient;
import net.danopia.mobile.laundryview.data.LvClient;
import net.danopia.mobile.laundryview.structs.Assist.Campus;

import java.util.Collections;
import java.util.List;

/**
 * Created by daniel on 8/29/13.
 */
public class FindCampusActivity extends ListActivity {
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        location = loc;

        getListView().addHeaderView(View.inflate(this, R.layout.activity_find_campus_top, null));
        getListView().addFooterView(View.inflate(this, R.layout.activity_find_campus_bottom, null));

        ((ImageButton) findViewById(R.id.wifiButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        final Button pathButton = (Button) findViewById(R.id.pathButton);
        final EditText pathText = (EditText) findViewById(R.id.pathText);
        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String entry = pathText.getText().toString();

                if (entry.length() == 0) {
                    pathText.setError("Please enter a link which you were given");
                    String text = "Please enter a link which you were given";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    pathText.requestFocus();
                } else if (entry.replaceAll("[a-zA-Z]", "").length() > 0) {
                    String text = "That link doesn't look right. It should be all letters, like 'upenn'";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    pathText.requestFocus();
                } else {
                    pathButton.setClickable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LvClient.resetCookies();
                            LvClient.getPage(entry.toLowerCase());
                            AssistClient.submitPath(entry, loc);

                            // check if worked

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pathButton.setClickable(true);

                                    startActivity(new Intent(FindCampusActivity.this, RoomListActivity.class));
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        pathText.setImeActionLabel("Go", EditorInfo.IME_ACTION_DONE);
        pathText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    pathButton.performClick();

                    return true;
                }

                return false;
            }
        });

        if (location == null) {
            findViewById(R.id.spinner).setVisibility(View.GONE);
            //findViewById(R.id.campusList).setVisibility(View.GONE);
            findViewById(R.id.textNoLoc).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            //findViewById(R.id.campusList).setVisibility(View.GONE);
            findViewById(R.id.textNoLoc).setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<Campus> campuses = AssistClient.getCampuses(loc);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCampuses(campuses);
                        }
                    });
                }
            }).start();
        }
    }

    void updateCampuses(List<Campus> campuses) {
        for (Campus campus : campuses) {
            campus.location = new Location("server");
            campus.location.setLatitude(campus.coords[0]);
            campus.location.setLongitude(campus.coords[1]);

            campus.distance = (int) (location.distanceTo(campus.location) / 1000);
        }
        Collections.sort(campuses);

        findViewById(R.id.spinner).setVisibility(View.GONE);
        ListView list = getListView(); // (ListView) findViewById(R.id.campusList);
        //list.setVisibility(View.VISIBLE);

        ArrayAdapter<Campus> adapter = new ArrayAdapter<Campus>(this, 0, campuses.subList(0, 5)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                Campus campus = getItem(position);

                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.campus_list_item, parent, false);
                }

                ((TextView) view.findViewById(R.id.textName)).setText(campus.name);
                ((TextView) view.findViewById(R.id.textDist)).setText(campus.distance + "km");
                return view;
            }
        };
        list.setAdapter(adapter);

        /*for (Campus campus : campuses.subList(0, 3)) {
            View item = inflater.inflate(R.layout.campus_list_item, parent, false);
            ((TextView) item.findViewById(R.id.textName)).setText(campus.name);
            ((TextView) item.findViewById(R.id.textDist)).setText(campus.distance + "km");
            parent.addView(item);
        }*/
    }

}
