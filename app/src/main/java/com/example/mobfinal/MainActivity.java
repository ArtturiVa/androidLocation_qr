package com.example.mobfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.os.Bundle;
import android.content.Intent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanButton;
    private TextView barcode;

    private TextView textLatitude;
    private TextView textLongitude;
    private FusedLocationProviderClient fusedLocationClient;
    int REQUEST_CODE = 1;
    double lat1;
    double long1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViews();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }


    private void initViews() {
        textLatitude = (TextView) findViewById(R.id.text_latitude);
        textLongitude = (TextView) findViewById(R.id.text_longitude);
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
        barcode = (TextView) findViewById(R.id.barCode);

    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);

        startActivityForResult(intent, REQUEST_CODE);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                if(data != null){
                    String resultData = data.getStringExtra("message_key");


                    int n = resultData.indexOf("=");
                    int end = resultData.indexOf(",");
                    String Latitude = resultData.substring(n+1,end);
                    String Longitude = resultData.substring(end+1);



                    double lat2 = Double.parseDouble(Latitude); // Latitude of location 2
                    double lon2 = Double.parseDouble(Longitude); // Longitude of location 2

                    double distance = LocationUtils.calculateDistance(lat1, long1, lat2, lon2);

// You can now use the 'distance' variable, which contains the distance in kilometers
                    String distStr = String.valueOf(distance);

                    barcode.setText("Distance from current location to destination: "+distStr + " km");
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        textLatitude.setText("61.50");
        textLongitude.setText("23.75");


        System.out.println("about to check coarse and fine location permissions");

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[]permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, 42);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            textLatitude.setText(Double.toString(location.getLatitude()));
                            lat1 = location.getLatitude();
                            long1 = location.getLongitude();
                            textLongitude.setText(Double.toString(location.getLongitude()));
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    System.out.println("onRequestPermissionsResult: requestCode = " + requestCode);
    for (int i = 0; i < permissions.length; i++){
        System.out.println(permissions[i] + " : " + Integer.toString(grantResults[i]));
    }
    }






}
