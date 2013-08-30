package net.danopia.mobile.laundryview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by daniel on 8/30/13.
 */
public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() == null) {
            // TODO: Check for wifi, otherwise, show FindCampusActivity
            startActivity(new Intent(this, FindCampusActivity.class));
        } else {
            // TODO: Auth as link, see what it offers
            System.out.println(getIntent().getData().getPath());
        }
    }
}
