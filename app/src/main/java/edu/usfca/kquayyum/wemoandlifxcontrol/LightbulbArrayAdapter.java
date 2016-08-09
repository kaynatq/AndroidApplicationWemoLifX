package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LightbulbArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resId;
    private final int color;

    public LightbulbArrayAdapter(Context context, String[] values, int resId, int color) {
        super(context, R.layout.content_lightbulb_array_adapter, values);
        this.context = context;
        this.resId = resId;
        this.color = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.content_lightbulb_array_adapter, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        textView.setText("Light " + (position+1));
        imageView.setImageResource(resId);
        rowView.setBackgroundColor(color);

        return rowView;
    }

}
