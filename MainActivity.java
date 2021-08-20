package com.example.gettouchxy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    boolean isStart = false;
    String filename, myPathXY, myPathMagnetic;
    private File myFileXY, myFileMagnetic;
    FileWriter myFWXY, myFWMagnetic;
    BufferedWriter myBWXY, myBWMagnetic;

    Button btnstart,btnend;
    EditText fname;

    private Sensor Magnetic;
    private SensorManager sensorManager;
    private int indicator = 0;
    private MySensorEventListenr sensorEventListenr;

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final TextView xy = (TextView)findViewById(R.id.XY);
        btnstart = (Button)findViewById(R.id.btnstart);
        btnend = (Button)findViewById(R.id.btnend);
        fname = (EditText)findViewById(R.id.fname);
    //    final Button btnstart = (Button)findViewById(R.id.btnstart);
        final LinearLayout parent = (LinearLayout) findViewById(R.id.parent);

        int number = 0;
        final String setfilename = "zy_1"+"-"+number;
        fname.setText(setfilename);
        btnend.setEnabled(false);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnstart.setEnabled(false);
                btnend.setEnabled(true);

                filename = fname.getText().toString();

                String externaldir = "";
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    externaldir = Environment.getExternalStorageDirectory().getAbsolutePath();
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                }
                /*
                if (-1 == ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                */
                if (!"".equals(externaldir)){
                    myPathXY = externaldir + File.separator + "yan" + File.separator + filename + "_XY.txt";
                    myFileXY = new File(myPathXY);
                    myPathMagnetic = externaldir + File.separator + "yan" + File.separator + filename + "_Magnetic.txt";
                    myFileMagnetic = new File(myPathMagnetic);

                    if (myFileXY.exists()){
                        myFileXY.delete();
                    }

                    if (!myFileXY.exists()){
                        try {
                            String filedir = myPathXY.substring(0, myPathXY.lastIndexOf(File.separator));

                            File my_file_dir = new File(filedir);
                            if (!my_file_dir.exists()) {
                                boolean ll = my_file_dir.mkdirs();
                                System.out.print(ll);
                            }
                            boolean tt = myFileXY.createNewFile();
                            System.out.print(tt);

                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "file exists", Toast.LENGTH_LONG).show();
                    }

                    if (!myFileMagnetic.exists()){
                        try {
                            String filedir = myPathMagnetic.substring(0, myPathMagnetic.lastIndexOf(File.separator));

                            File my_file_dir = new File(filedir);
                            if (!my_file_dir.exists()) {
                                boolean ll = my_file_dir.mkdirs();
                                System.out.print(ll);
                            }
                            boolean tt = myFileMagnetic.createNewFile();
                            System.out.print(tt);

                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "file exists", Toast.LENGTH_LONG).show();
                    }
                }

                try {
                    myFWXY = new FileWriter(myFileXY, true);
                    myBWXY = new BufferedWriter(myFWXY);
                } catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    myFWMagnetic = new FileWriter(myFileMagnetic, true);
                    myBWMagnetic = new BufferedWriter(myFWMagnetic);
                } catch (IOException e){
                    e.printStackTrace();
                }

                sensorEventListenr = new MySensorEventListenr();
                Magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener(sensorEventListenr,Magnetic,SensorManager.SENSOR_DELAY_FASTEST);

                parent.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float x = event.getX();
                        float y = event.getY();
                        xy.setText("Touch at: " + "X: " + x + "\n" + "Y: " + y);
                        try{
                            myBWXY.write(System.currentTimeMillis() + "\t" + x + "\t" + y + "\n");
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
            }
        });

        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnstart.setEnabled(true);
                btnend.setEnabled(false);

                int index = filename.indexOf('-');
                String Num = filename.substring(index+1,filename.length());
                int newint = Integer.parseInt(Num)+1;

                String newfilename = filename.substring(0,index) + '-' + Integer.toString(newint);
                fname.setText(newfilename);

                try{
                    myBWXY.close();
                    myBWMagnetic.close();
                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }

    private final class MySensorEventListenr implements SensorEventListener
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            indicator = 1;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                try {
                    myBWMagnetic.write(System.currentTimeMillis() + "\t" + x + "\t"+y+"\t"+z+"\n");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy){

        }
    }
}
