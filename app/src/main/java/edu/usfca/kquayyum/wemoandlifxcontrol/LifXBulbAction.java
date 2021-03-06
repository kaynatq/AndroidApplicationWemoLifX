package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

/**
 * This activity os lauched if a selected light is LIfX
 */
public class LifXBulbAction extends AppCompatActivity {

    /**
     * method to convert RGB color value to HSV
     * @param r
     * @param g
     * @param b
     * @return
     */
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

    /**
     * Since we need to represent the 2 bytes in little endian
     * @param str
     * @return
     */
    private String convertToLEndian(String str)
    {
        int i = 4 - str.length();

        while(i > 0){
            str = "0" + str;
            i--;
        }

        char[] chars = str.toCharArray();
        char temp = chars[0];
        chars[0] = chars[2];
        chars[2] = temp;
        temp = chars[1];
        chars[1] = chars[3];
        chars[3] = temp;
        str = new String(chars);
        return str;
    }

    /**
     * The pop up which is shown while we are selecting a color from color picker
     * @param view
     */
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
                String hue = "";
                String colorS = "";

                double[] requiredColor = RGBtoHSV(selectedColorR, selectedColorG, selectedColorB);
                requiredColor[0] = requiredColor[0] / 360 * 65535;
                requiredColor[1] = requiredColor[1] / 360 * 65535;
                requiredColor[2] = requiredColor[2] / 360 * 65535;
                hue = Integer.toHexString((int)requiredColor[0]);
                colorS += convertToLEndian(hue);
                System.out.println(colorS);
                LifXSetColor lifXSetColor = new LifXSetColor(getIntent().getStringExtra("ip"), colorS);
                lifXSetColor.execute();
                cp.dismiss();
            }
        });
    }

    /**
     * Set the bulb on
     * @param view
     */
    public void powerOn(View view){
        LifXSetPowerOn lifXSetPowerOn = new LifXSetPowerOn(getIntent().getStringExtra("ip"));
        lifXSetPowerOn.execute();
    }

    /**
     * Set the bulb off
     * @param view
     */
    public void powerOff(View view){
        LifXSetPowerOff lifXSetPowerOff = new LifXSetPowerOff(getIntent().getStringExtra("ip"));
        lifXSetPowerOff.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lif_xbulb_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
