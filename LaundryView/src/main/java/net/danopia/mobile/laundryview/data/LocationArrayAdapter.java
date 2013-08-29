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
    public LocationArrayAdapter(Context context, List<Location> data)
    {
        super(context, 0, new ArrayList<Union>());

        for (Location location : data) {
            if (data.size() > 1) // handle single-location case in a more visually-pleasing way
                this.add(new Union(location));

            for (Room room : location.rooms) {
                this.add(new Union(room));
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;// = convertView;

        //if (row == null)
        //{
            // ROW INFLATION
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (isEnabled(position)) {
                row = inflater.inflate(R.layout.room_list_item, parent, false);
            } else {
                row = inflater.inflate(R.layout.location_header_item, parent, false);
            }
        //}

        Union union = getItem(position);

        if (isEnabled(position)) {
            ((TextView) row.findViewById(R.id.name)).setText(union.room.name);
            ((TextView) row.findViewById(R.id.washer_avail)).setText(union.room.w + " W");
            ((TextView) row.findViewById(R.id.dryer_avil)).setText(union.room.d + " D");
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