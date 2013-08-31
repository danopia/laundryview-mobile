package net.danopia.mobile.laundryview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import net.danopia.mobile.laundryview.data.AssistClient;
import net.danopia.mobile.laundryview.data.LvClient;

/**
 * Created by daniel on 8/30/13.
 */
public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Cache.location = loc;

        if (getIntent().getData() == null) {
            // TODO: Check for wifi, otherwise, show FindCampusActivity
            startActivity(new Intent(this, FindCampusActivity.class));
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cache.bust();
                    LvClient.resetCookies();
                    LvClient.getPage(getIntent().getData().getPath());
                    AssistClient.submitPath(getIntent().getData().getPath(), Cache.location);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LaunchActivity.this, RoomListActivity.class);
                            intent.putExtra("path", getIntent().getData().getPath());
                            startActivity(intent);
                        }
                    });
                }
            }).start();

            // TODO: Auth as link, see what it offers
            System.out.println(getIntent().getData().getPath());
        }
    }
}
