package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class DiscoverLightsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> str2 = getIntent().getStringArrayListExtra("list");
        int i = 0;
        String[] str = new String[str2.size()];
        i=0;

        if(str2 != null) {
            for (String s : str2) {
                str[i++] = s;
            }
        }
        setListAdapter(new LightbulbArrayAdapter(this, str, R.mipmap.lifx_bulb, Color.rgb(119, 0, 255)));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        Intent intent = new Intent(this, LightBulbAction.class);

        intent.putExtra("ip", l.getItemAtPosition(position).toString());
        startActivity(intent);
    }
}
