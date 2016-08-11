package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter that shows the list of lights in display
 */
public class LightbulbArrayAdapter extends ArrayAdapter<String> {

    private final Context context;

    public LightbulbArrayAdapter(Context context, String[] values) {
        super(context, R.layout.content_lightbulb_array_adapter, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.content_lightbulb_array_adapter, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText("Light " + (position+1));
        return rowView;
    }

}
