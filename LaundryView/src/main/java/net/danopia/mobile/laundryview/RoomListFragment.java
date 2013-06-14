package net.danopia.mobile.laundryview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;

import net.danopia.mobile.laundryview.data.Client;
import net.danopia.mobile.laundryview.data.LocationArrayAdapter;
import net.danopia.mobile.laundryview.structs.Provider;

/**
 * A list fragment representing a list of Rooms. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link RoomDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RoomListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private AlertDialog mAD = null;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mAuthTask != null) {
            return;
        }

        if (Cache.provider == null) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            //showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute();
        } else {
            getActivity().setTitle(Cache.provider.name);
            setListAdapter(new LocationArrayAdapter(getActivity(), Cache.provider.locations));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

        //this.getListView().addOnLayoutChangeListener(e -> {System.out.println("hi"); });
        //button.addActionListener(e -> { ui.dazzle(e.getModifiers()); });

    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (Cache.provider == null) return;

        LocationArrayAdapter.Union union = (LocationArrayAdapter.Union) listView.getAdapter().getItem(position);
        if (union.room == null) return; // weird muckery going on here...
        
        mCallbacks.onItemSelected(Long.toString(union.room.id));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick() {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Provider> {
        long startTime = 0;

        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Provider doInBackground(Void... params) {
            return Client.getLocations();
        }

        @Override
        protected void onPostExecute(final Provider data) {
            mAuthTask = null;

            if (data == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.no_network)
                       .setMessage(R.string.error_no_network)
                       .setNeutralButton(R.string.close_button, null);

                mAD = builder.create();
                mAD.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        getActivity().finish();
                    }
                });
                mAD.show();
                return;
            }
            Cache.provider = data;

            if (Cache.provider.isDemo && mAD == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.no_campus)
                       .setMessage(R.string.error_no_campus)
                       .setNeutralButton(R.string.continue_button, null);

                mAD = builder.create();
                mAD.show();
            }

            // TODO: make sure we still exist
            getActivity().setTitle(Cache.provider.name);
            setListAdapter(new LocationArrayAdapter(getActivity(), Cache.provider.locations));

            EasyTracker.getTracker().sendEvent("dataLoad", "provider", Cache.provider.name + " - " + Cache.provider.locations.size() + " locs", 0L );
            EasyTracker.getTracker().sendTiming("dataLoad", System.currentTimeMillis() - startTime, "provider", Cache.provider.name);

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
