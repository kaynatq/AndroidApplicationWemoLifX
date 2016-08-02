package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.Bundle;
import android.renderscript.Double2;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class LifXBulbAction extends AppCompatActivity {

    public static double[] RGBtoHSV(double r, double g, double b){

        double h, s, v;

        double min, max, delta;

        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);

        // V
        v = max;

        delta = max - min;

        // S
        if( max != 0 )
            s = delta / max;
        else {
            s = 0;
            h = -1;
            return new double[]{h,s,v};
        }

        // H
        if( r == max )
            h = ( g - b ) / delta; // between yellow & magenta
        else if( g == max )
            h = 2 + ( b - r ) / delta; // between cyan & yellow
        else
            h = 4 + ( r - g ) / delta; // between magenta & cyan

        h *= 60;    // degrees

        if( h < 0 )
            h += 360;

        return new double[]{h,s,v};
    }
    public void changeColor(View view){
        final ColorPicker cp = new ColorPicker(LifXBulbAction.this, 0, 0, 0);
        cp.show();


        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedColorR = cp.getRed();
                int selectedColorG = cp.getGreen();
                int selectedColorB = cp.getBlue();


                double[] requiredColor = RGBtoHSV(selectedColorR, selectedColorG, selectedColorB);
                requiredColor[0] = requiredColor[0] / 360 * 65535;
                requiredColor[1] = requiredColor[1] / 360 * 65535;
                requiredColor[2] = requiredColor[2] / 360 * 65535;
                String colorString = "";
                colorString += Integer.toHexString((int)requiredColor[0]);

                colorString += Integer.toHexString((int)requiredColor[1]);

                colorString += Integer.toHexString((int)requiredColor[2]);

                LifXSetColor lifXSetColor = new LifXSetColor(colorString);
                lifXSetColor.execute();
                cp.dismiss();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lif_xbulb_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
