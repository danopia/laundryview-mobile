package net.danopia.mobile.laundryview.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import net.danopia.mobile.laundryview.R;
import net.danopia.mobile.laundryview.structs.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danopia on 5/16/13.
 */
public class MachineArrayAdapter extends ArrayAdapter<Machine> {
    private static final String tag = "MachineArrayAdapter";
    private Context context;

    private TextView machineNum;
    private TextView machineState;
    private TextView machineStatus;
    private List<Machine> machines = new ArrayList<Machine>();

    public MachineArrayAdapter(Context context, List<Machine> machines)
    {
        super(context, 0, machines);
        this.context = context;
        this.machines = machines;
        //Collections.sort(buddies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        if (row == null)
        {
            // ROW INFLATION
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.machine_list_item, parent, false);
        }

        // Get item
        Machine machine = getItem(position);
        //machine.refresh();

        //buddyName = (TextView) row.findViewById(R.id.buddy_name);   //change this to textField1  from simple_list_item_2
        //buddyName.setText(buddy.toString());

        //buddyStatus = (TextView) row.findViewById(R.id.buddy_mood); //change this to textField2 from simple_list_item_2
        //buddyStatus.setText(buddy.getMood());
        //      Log.d(tag, buddy.getIdentity()+"'s mood is "+buddyStatus.getText());

        return row;
    }
}