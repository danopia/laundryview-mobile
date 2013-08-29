package net.danopia.mobile.laundryview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import net.danopia.mobile.laundryview.data.LvClient;
import net.danopia.mobile.laundryview.data.MachineComparator;
import net.danopia.mobile.laundryview.structs.Machine;
import net.danopia.mobile.laundryview.structs.Room;
import net.danopia.mobile.laundryview.util.Helpers;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a single Room detail screen.
 * This fragment is either contained in a {@link RoomListActivity}
 * in two-pane mode (on tablets) or a {@link RoomDetailActivity}
 * on handsets.
 */
public class RoomDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "room_id";

    /**
     * The room this fragment is presenting.
     */
    private Room mRoom = null;


    private UserLoginTask mAuthTask = null;
    private UserLoginTask2 mAuthTask2 = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            if (Cache.provider == null) return; // TODO: wat

            mRoom = Cache.provider.getRoom(Long.parseLong(getArguments().getString(ARG_ITEM_ID)));

            if (mRoom.machines == null) {
                mAuthTask = new UserLoginTask();
                mAuthTask.execute(mRoom);
            } else {
                mAuthTask2 = new UserLoginTask2();
                mAuthTask2.execute(mRoom);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void doTheThing(String title) {
        getActivity().getActionBar().setSubtitle(title);
    }

    private View rootView=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_room_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mRoom != null) {
            if (getActivity().getTitle() != Cache.provider.name) {
                getActivity().setTitle(mRoom.name);

                String subtitle = Helpers.titleCase(Cache.provider.getRoomLocation(mRoom.id).name);
                if (subtitle.equals(mRoom.name)) subtitle = Cache.provider.name;

                if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.HONEYCOMB) {
                    doTheThing(subtitle);
                }
            }

            if (mRoom.machines != null) {
                fillTable();
            }
        }

        return rootView;
    }

    protected void fillTable() {
        TableLayout grid = (TableLayout) rootView.findViewById(R.id.machine_grid);
        grid.removeAllViews();
        if (mRoom == null || mRoom.machines == null) return; // bail if nothing to work with
        if (getActivity() == null) {
            System.out.println("LaundryView: null activity, bailing");
            return;
        }

        // categorize into columns
        ArrayList<Machine> washers = new ArrayList<Machine>();
        ArrayList<Machine> driers = new ArrayList<Machine>();

        for (Machine machine : mRoom.machines) {
            if (machine.type.equals("washer")) {
                washers.add(machine);
            } else if (machine.type.equals("dryer")) {
                driers.add(machine);
            } else {
                System.out.println("LaundryView: got funky machine type '" + machine.type + "', ignoring");
            }
        }

        Collections.sort(washers, new MachineComparator());
        Collections.sort(driers,  new MachineComparator());

        // how many rows?
        int rowCount = washers.size();
        if (rowCount < driers.size())
            rowCount = driers.size();

        for (int i = 0; i < rowCount; i++) {
            TableRow row = new TableRow(getActivity());

            if (washers.size() > i) {
                View mV = getMachineView(washers.get(i), row);
                mV.setLayoutParams(new TableRow.LayoutParams(0));
                row.addView(mV);
            }

            if (driers.size() > i) {
                View mV = getMachineView(driers.get(i), row);
                mV.setLayoutParams(new TableRow.LayoutParams(1));
                row.addView(mV);
            }

            grid.addView(row);
        }
    }

    public View getMachineView(Machine machine, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.machine_list_item, parent, false);

        TextView machineNum = (TextView) row.findViewById(R.id.machine_number);
        TextView machineStatus = (TextView) row.findViewById(R.id.machine_status);
        TextView machineMessage = (TextView) row.findViewById(R.id.machine_message);

        machineNum.setText(machine.number);

        TextView bgL = (TextView) row.findViewById(R.id.bg_dark);
        TextView bgR = (TextView) row.findViewById(R.id.bg_light);

        bgL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));
        bgR.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        if (machine.message != null && machine.message.startsWith("cycle has ended")) {
            machine.status = 2;
        }

        switch (machine.status) {
            case 0:
                machineMessage.setText("");
                if (machine.message == null) {
                    machineStatus.setText(machine.timeLeft + " minutes left");
                } else if (machine.message.startsWith("extended cycle")) {
                    machineStatus.setText("extended cycle");
                    machineMessage.setText(machine.message.substring(14));
                } else {
                    machineStatus.setText(machine.message);
                }

                bgL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, machine.cycleLength - machine.timeLeft));
                bgR.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, machine.timeLeft));

                bgL.setBackgroundColor(Color.parseColor("#ff8080"));
                bgR.setBackgroundColor(Color.parseColor("#ffc0c0"));
                break;

            case 1:
                machineMessage.setText("");
                if (machine.message == null) {
                    machineStatus.setText(machine.message);
                } else if (machine.message.startsWith("cycle ended")) {
                    machineStatus.setText("cycle ended");
                    machineMessage.setText(machine.message.substring(11));
                } else {
                    machineStatus.setText("");
                }

                bgR.setBackgroundColor(Color.parseColor("#c0ffc0"));
                break;

            case 2:
                machineStatus.setText("cycle has ended");
                machineMessage.setText("door still closed");

                bgR.setBackgroundColor(Color.parseColor("#ffffc0"));
                break;

            default:
                machineStatus.setText(machine.message);
                machineMessage.setText("");
                bgR.setBackgroundColor(Color.parseColor("#c0c0c0"));
        }

        machineStatus.setVisibility (( machineStatus.getText() == "") ? TextView.GONE : TextView.VISIBLE);
        machineMessage.setVisibility((machineMessage.getText() == "") ? TextView.GONE : TextView.VISIBLE);

        return row;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.room_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_option:
                if (mAuthTask == null && mAuthTask2 == null) {
                    if (mRoom == null) {
                        return true;
                    } else if (mRoom.machines == null) {
                        mAuthTask = new UserLoginTask();
                        mAuthTask.execute(mRoom);
                    } else {
                        mAuthTask2 = new UserLoginTask2();
                        mAuthTask2.execute(mRoom);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class UserLoginTask extends AsyncTask<Room, Void, Room> {
        long startTime = 0;

        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Room doInBackground(Room... params) {
            LvClient.getRoom(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(final Room data) {
            mAuthTask = null;

            fillTable();

            if (data.machines == null) return; // probably no network
            mAuthTask2 = new UserLoginTask2();
            mAuthTask2.execute(mRoom);

            EasyTracker.getTracker().sendEvent("dataLoad", "room", Cache.provider.name + " - " + data.id + ": " + data.name, 0L );
            EasyTracker.getTracker().sendTiming("dataLoad", System.currentTimeMillis() - startTime, "roomStatic", data.id + ": " + data.name);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private class UserLoginTask2 extends AsyncTask<Room, Void, Room> {
        long startTime = 0;

        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Room doInBackground(Room... params) {
            LvClient.updateRoom(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(final Room data) {
            mAuthTask2 = null;

            fillTable();

            EasyTracker.getTracker().sendTiming("dataLoad", System.currentTimeMillis() - startTime, "roomDynamic", data.id + ": " + data.name);
        }

        @Override
        protected void onCancelled() {
            mAuthTask2 = null;
        }
    }
}
