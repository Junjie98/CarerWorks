package com.example.carertrackingapplication;

//import static androidx.core.location.LocationManagerCompat.Api28Impl.isLocationEnabled;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.carertrackingapplication.helperClass.GoogleDirectionAPIFetchURL;
import com.example.carertrackingapplication.helperClass.CallbackOnTaskDone;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
//import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
//import com.example.carertrackingapplication.databinding.ActivityMapsTrackerBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsTrackerActivity extends FragmentActivity implements OnMapReadyCallback, CallbackOnTaskDone {// NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    Location mLastLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Polyline currentPolyline;

    MarkerOptions place1,place2;
    DatabaseReference onlineReference, currentUserReference, carerLocationReference;
    GeoFire geoFire;
    FirebaseDatabase database;
    Address myAddress;
    LatLng myLatLng;
    private static String address, postcode, carer_id, carerName;
    DatabaseReference dataRef, dataRefCarerTravelMode;
    ArrayList<String> listCarerLocation = new ArrayList<>();
    LatLng carerLatLng;
    Location carerLocation;
    String travelModeLoaded="walking"; //default

    public MapsTrackerActivity(String address, String postcode, String carer_id, String carerName){
        this.address = address;
        this.postcode = postcode;
        this.carer_id = carer_id;
        this.carerName = carerName;
    }
    public MapsTrackerActivity(){

    }

    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists())
                currentUserReference.onDisconnect().removeValue();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineReference.removeEventListener(onlineValueEventListener);
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        registerOnlineSystem();
    }

    private void registerOnlineSystem() {
        onlineReference.addValueEventListener(onlineValueEventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_tracker);
        CreateLocationRequest();
        getCarerLocationRequest();


        // polylines = new ArrayList<>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myAddress = geoLocation();
        myLatLng = new LatLng(myAddress.getLatitude(),myAddress.getLongitude());

        place1 = new MarkerOptions().position(new LatLng(myLatLng.latitude,myLatLng.longitude)).title("Provided Address");


    }


    private String getUrl(LatLng origin, LatLng destination, String directionMode){
        //origin route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        //destination of route
        String str_dest = "destination=" + destination.latitude + "," +destination.longitude;
        //mode
        String mode = "mode=" + directionMode;
        //building the parameter to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        //output
        String output = "json";
        //Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private Address geoLocation(){
        String locationName = address + "," +postcode;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);

            if(addressList.size()>0){ //if addressList is not empty, get the address of index 0
                Address address = addressList.get(0);
                System.out.println(address.getLatitude() + "," + address.getLongitude());
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;

    }

    public void getCarerLocationRequest(){
        database = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app");
        dataRef = database.getReference("CarerLocation").child(carer_id).child("l");
        database = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app");
        dataRefCarerTravelMode = database.getReference("CarerLocation").child(carer_id);
        dataRefCarerTravelMode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshots : snapshot.getChildren()) {
//                    travelModeLoaded = snapshots.child("TravelMode").getValue(String.class);
                    //System.out.println(snapshots.child("TravelMode").getValue(String.class) + "AYEEEEEEEEEEEEEEEEEEEE");
                    if(snapshots.getValue().equals("driving")){
                        travelModeLoaded = "driving";
                    }
                    if(snapshots.getValue().equals("walking")){
                        travelModeLoaded = "walking";
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Reading failed");
            }
        });

        // Attach a listener to read the data at our posts reference
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listCarerLocation.clear(); //not used
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    carerLatLng = new LatLng(locationLat,locationLng);


                    carerLocation = new Location("");
                    carerLocation.setLatitude(carerLatLng.latitude);
                    carerLocation.setLongitude(carerLatLng.longitude);

                    System.out.println(carerLocation.getLatitude()+ "," + carerLocation.getLongitude() + " YES I AM HERE");

                    place2 = new MarkerOptions().position(new LatLng(carerLocation.getLatitude(),carerLocation.getLongitude()));
                    mMap.clear();
                    mMap.addMarker(place2);
                    mMap.addMarker(place1);
                    String url = getUrl(carerLatLng,myLatLng,travelModeLoaded);
                    new GoogleDirectionAPIFetchURL(MapsTrackerActivity.this).execute(url,travelModeLoaded);
                    //float distance = loc1.distanceTo(loc2);
//                    place2 = new MarkerOptions().position(new LatLng(carerLocation.getLatitude(),carerLocation.getLongitude()));
//                    //mMap.addMarker(place2);
////                    String url = getUrl(myLatLng,carerLatLng,"walking");
////                    new GoogleDirectionAPIFetchURL(MapsTrackerActivity.this).execute(url,"walking");

                    //listCarerLocation.add(snapshot.getValue().toString());
                    //System.out.println("VALIDATE = " + travelModeLoaded);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void CreateLocationRequest() {
        onlineReference = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child(".info/connected");
        carerLocationReference = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference("CarerLocation");
        currentUserReference = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference("PatientLocation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        geoFire = new GeoFire(currentUserReference);

        registerOnlineSystem();

        locationRequest = LocationRequest.create();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                LatLng newCurPosition = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCurPosition, 18f));

                geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if(error != null){
                            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                        }else{
                            Snackbar.make(mapFragment.getView(), "Success connected to realtime dtb & upload location", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(place1);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return false;
                                }
                                mFusedLocationClient.getLastLocation()
                                        .addOnFailureListener(e -> Toast.makeText(MapsTrackerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                                        .addOnSuccessListener(location -> {
                                            if(location!= null) {
                                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18f));
                                            }
                                        });

                                return true;
                            }
                        });


//                        //layout btn
                        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        params.setMargins(0,0,0,50);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MapsTrackerActivity.this, "Please provide access to location service.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();

        try{
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapdesign));
            if(!success)
                Toast.makeText(MapsTrackerActivity.this, "Map style failed to load.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MapsTrackerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        // Add a marker in Sydney and move the camera
//        LatLng glasgow = new LatLng(55.869641, -4.297166);
//        mMap.addMarker(new MarkerOptions().position(glasgow).title("Marker in Glasgow"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(glasgow));
    }


    @Override
    public void onTaskFinished(Object... values) {
        if(currentPolyline!=null){
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}



/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera. In this case,
 * we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to install
 * it inside the SupportMapFragment. This method will only be triggered once the user has
 * installed Google Play services and returned to the app.
 */