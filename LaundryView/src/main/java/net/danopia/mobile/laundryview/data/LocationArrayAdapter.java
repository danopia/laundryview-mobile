package net.danopia.mobile.laundryview.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.danopia.mobile.laundryview.R;
import net.danopia.mobile.laundryview.structs.Location;
import net.danopia.mobile.laundryview.structs.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danopia on 5/16/13.
 */
public class LocationArrayAdapter extends ArrayAdapter<LocationArrayAdapter.Union> {
    //private static final String tag = "LocationArrayAdapter";
    //private final Context context;

    public LocationArrayAdapter(Context context, List<Location> data)
    {
        super(context, 0, new ArrayList<Union>());

        for (Location location : data) {
            if (data.size() > 1) // handle single-location case more visually-pleasingly
                this.add(new Union(location));

            for (Room room : location.rooms) {
                this.add(new Union(room));
            }
        }

        //this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;// = convertView;

        //if (row == null)
        //{
            // ROW INFLATION
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (isEnabled(position)) {
                row = inflater.inflate(R.layout.room_list_item, parent, false);
            } else {
                row = inflater.inflate(R.layout.location_header_item, parent, false);
            }
        //}

        // Get item
        Union union = getItem(position);

        if (isEnabled(position)) {
            ((TextView) row.findViewById(R.id.name)).setText(union.room.name);
            ((TextView) row.findViewById(R.id.washer_avail)).setText(union.room.w + " W");
            ((TextView) row.findViewById(R.id.dryer_avil)).setText(union.room.d + " D");

            //buddyName = (TextView) row.findViewById(R.id.buddy_name);   //change this to textField1  from simple_list_item_2
            //buddyName.setText(buddy.toString());

            //buddyStatus = (TextView) row.findViewById(R.id.buddy_mood); //change this to textField2 from simple_list_item_2
            //buddyStatus.setText(buddy.getMood());
            //      Log.d(tag, buddy.getIdentity()+"'s mood is "+buddyStatus.getText());
        } else {
            ((TextView) row.findViewById(R.id.list_header_title)).setText(union.location.name);
        }

        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).room != null;
    }

    public class Union {
        public final Location location;
        public final Room room;

        public Union(Location location) {
            this.location = location;
            this.room = null;
        }

        public Union(Room room) {
            this.location = null;
            this.room = room;
        }
    }
}