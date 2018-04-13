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
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnLoc,btnAuto;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor sensorLight;
   Double level;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    AppDatabase db = App.getInstance().getDatabase();
    List<Light> lights = db.lightDao().getAll();
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




        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                final Handler handler = new Handler();
                final int delay = 5000; //milliseconds

                handler.postDelayed(new Runnable(){
                    public void run(){
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
                }, delay);
         /*       Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
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
                }, 0, 30, TimeUnit.SECONDS); */
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
}