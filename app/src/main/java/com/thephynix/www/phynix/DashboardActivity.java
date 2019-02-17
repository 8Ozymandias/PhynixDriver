package com.thephynix.www.phynix;

import android.Manifest;
import android.app.AutomaticZenRule;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FirebaseFirestore db;
    private TextView result;

    protected LatLng myLatLng;
    private String DriverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash);
        result = findViewById(R.id.textView9);
        db = FirebaseFirestore.getInstance();
        grantPermission();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DriverId = randomId();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        if (mLocationPermissionGranted == false) {
            grantPermission();
        }else{
            getCurrentLocation();
        }
    }

    private void grantPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation(){

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(myLocation).title("Me"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
                            myLatLng = myLocation;
                        }else{
                            Toast.makeText(DashboardActivity.this, "Sorry Location Was Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Legit", "Error trying to get last GPS location");
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    mLocationPermissionGranted = false;
                }
                return;
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("lane").whereEqualTo("Accepted", false)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                           result.setText("Latitude: "+documentSnapshot.get("Latitude").toString()+"\nLongitude: "+documentSnapshot.get("Longitude")+"\nIsAccepted: "+documentSnapshot.get("Accepted\n\n"));
                        }

                        if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()){

                            db.collection("lane").whereEqualTo("DriverId", DriverId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                result.setText("Rider And Driver Is In Dialouge");
                                            }

                                            if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()){
                                                result.setText("Another Driver Has Already Accepted");
                                            }
                                        }
                                    });

                            result.setText("Someone Already Accepted");
                        }
                    }
                });
    }

    public String randomId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    public void LaneInfo(View v){

        if(mLocationPermissionGranted){
            getCurrentLocation();
            if(myLatLng != null){
                db.collection("lane").document("user_lane").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getString("DriverId").equals(DriverId)){
                            Toast.makeText(DashboardActivity.this, "You Already Accepted", Toast.LENGTH_SHORT).show();
                        }else if(task.getResult().getString("DriverId").equals("")){
                            Map<String, Object> info = new HashMap<>();
                            info.put("DriverLatitude", myLatLng.latitude);
                            info.put("DriverLongitude", myLatLng.longitude);
                            info.put("Accepted", true);
                            info.put("DriverId", DriverId);
                            mMap.addMarker(new MarkerOptions().position(myLatLng).title("Rider Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            db.collection("lane").document("user_lane").set(info, SetOptions.merge());
                        }else{
                            Toast.makeText(DashboardActivity.this, "Another Driver Already Accepted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }else{
                Toast.makeText(this, "My Lattitude and Logitude is Empty My G", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Location Not Granted My G", Toast.LENGTH_SHORT).show();
        }

    }


    }
