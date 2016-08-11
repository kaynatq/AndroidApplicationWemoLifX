package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * This activity is launched if a list of lights is discovered in our network
 */
public class DiscoverLightsActivity extends ListActivity {
    ArrayList<String> listOfLights = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOfLights = getIntent().getStringArrayListExtra("list");
        int i = 0;
        String[] str = new String[listOfLights.size()];
        if(listOfLights != null) {
            for (String s : listOfLights) {
                str[i++] = s;
            }
        }
        setListAdapter(new LightbulbArrayAdapter(this, str));
    }

    //get selected items
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intentLifX = new Intent(this, LifXBulbAction.class);
        Intent intentWeMo = new Intent(this, WeMoBulbAction.class);
        String name = l.getItemAtPosition(position).toString();

        if (name.contains(".")){
            intentLifX.putExtra("ip", name);
            startActivity(intentLifX);
        } else {
            intentWeMo.putExtra("lightName", name);
            startActivity(intentWeMo);
        }
    }
}
