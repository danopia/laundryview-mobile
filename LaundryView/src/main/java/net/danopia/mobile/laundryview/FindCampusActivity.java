package net.danopia.mobile.laundryview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.danopia.mobile.laundryview.data.Client;

/**
 * Created by daniel on 8/29/13.
 */
public class FindCampusActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_campus);

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
                    String text = "Please enter a link which you were given";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    pathText.requestFocus();
                } else if (entry.replaceAll("[a-zA-Z]", "").length() > 0) {
                    String text = "That link doesn't look right. It should be all letters, like 'pitt'";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    pathText.requestFocus();
                } else {
                    pathButton.setClickable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Client.resetCookies();
                            Client.getPage(entry.toLowerCase());

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
    }
}
