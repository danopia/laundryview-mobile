package net.danopia.mobile.laundryview.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.danopia.mobile.laundryview.R;

/**
 * Created by daniel on 8/30/13.
 */
public class LoadingAdapter extends ArrayAdapter<String> {
    public LoadingAdapter(Context context) {
        super(context, 0, new String[] {""});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_loading, parent, false);
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
