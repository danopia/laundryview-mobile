package net.danopia.mobile.laundryview.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.danopia.mobile.laundryview.R;

/**
 * Adapter to show a static message.
 * Created by daniel on 8/30/13.
 */
public class MessageAdapter extends ArrayAdapter<String> {
    public MessageAdapter(Context context, String message) {
        super(context, 0, new String[] {message});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String message = getItem(position);
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_message, parent, false);
        }

        if (view == null) return null;
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(message);

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}