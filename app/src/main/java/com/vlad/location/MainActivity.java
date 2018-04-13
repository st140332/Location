package com.vlad.location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnLoc,btnAuto,btnStop;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor sensorLight;
   Double level;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    AppDatabase db = App.getInstance().getDatabase();
    List<Light> lights = db.lightDao().getAll();
    String sec;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view) ;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        btnLoc = (Button) findViewById(R.id.btnGetLoc);
        btnAuto=(Button) findViewById(R.id.btnAuto);
        btnStop=(Button) findViewById(R.id.btnStop);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LightAdapter(lights);
        recyclerView.setAdapter(adapter);
        sensorManager.registerListener(listenerLight, sensorLight,
                SensorManager.SENSOR_DELAY_NORMAL);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessageNoGps();
        }



        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {



                GpsTracker gt = new GpsTracker(getApplicationContext());
                Location l = gt.getLocation();
                if (l == null)
                {
                    Toast.makeText(getApplicationContext(), "GPS unable to get Value please wait", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    Toast.makeText(getApplicationContext(), "GPS Lat = " + lat + "\n lon = "
                            + lon + " \n Date and time: " + date + " \n Light: " + level, Toast.LENGTH_SHORT).show();
                    db.lightDao().insertAll(new Light(lat,lon,level,date));
                    lights.add(new Light(lat,lon,level,date));
                    lights = db.lightDao().getAll();
                    adapter = new LightAdapter(lights);
                    recyclerView.setAdapter(adapter);

                }

             }
        });

        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

        final Runnable runnableCode = new Runnable() {
            public void run(){
                new CountDownTimer(delay, 100) {

                    public void onTick(long millisUntilFinished) {
                        btnAuto.setText(Double.toString(millisUntilFinished / 1000));
                        btnAuto.setEnabled(false);
                    }

                    public void onFinish() {
                        btnAuto.setText("AUTO");
                        btnAuto.setEnabled(true);
                    }
                }.start();
                GpsTracker gt = new GpsTracker(getApplicationContext());
                Location l = gt.getLocation();
                if (l == null)
                {
                    Toast.makeText(getApplicationContext(), "GPS unable to get Value please wait", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    Toast.makeText(getApplicationContext(), "GPS Lat = " + lat + "\n lon = "
                            + lon + " \n Date and time: " + date + " \n Light: " + level, Toast.LENGTH_SHORT).show();
                    db.lightDao().insertAll(new Light(lat,lon,level,date));
                    lights.add(new Light(lat,lon,level,date));
                    lights = db.lightDao().getAll();
                    adapter = new LightAdapter(lights);
                    recyclerView.setAdapter(adapter);

                }
                handler.postDelayed(this, delay);

            }
        };

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAuto.setEnabled(false);
                //popUpEditText();
               // no2=Integer.parseInt(sec);
                new CountDownTimer(delay, 100) {

                    public void onTick(long millisUntilFinished) {
                        btnAuto.setText(Double.toString(millisUntilFinished / 1000));
                        btnAuto.setEnabled(false);
                    }

                    public void onFinish() {
                        btnAuto.setText("AUTO");
                        btnAuto.setEnabled(true);
                    }

                }.start();
                handler.postDelayed(runnableCode,delay);


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnableCode);
                Toast.makeText(getApplicationContext(), "Stopped!", Toast.LENGTH_SHORT).show();
                btnAuto.setText("AUTO");
                btnAuto.setEnabled(true);

            }
        });

    }
    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    SensorEventListener listenerLight = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
           // tvText.setText(String.valueOf(event.values[0]));
             level = Double.valueOf(event.values[0]);


        }
    };

    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comments");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sec = input.getText().toString();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


}