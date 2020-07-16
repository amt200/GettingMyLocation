package sg.edu.rp.webservices.gettingmylocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartDetect, btnStopDetect, btnCheck;
    TextView tvLatLng;
    private GoogleMap map;
    private Marker lastKnownLocation;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        tvLatLng = findViewById(R.id.tvLatLng);
        btnStartDetect = findViewById(R.id.btnStart);
        btnStopDetect = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheck);

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(100);

        mLocationCallback = new LocationCallback(){};

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                boolean isGranted = checkPermission();
                if (isGranted) {
                    map.setMyLocationEnabled(true);
                }
                 else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                LatLng lastKnownLatLng = new LatLng(1.350057, 103.934452);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 10));

                UiSettings ui = map.getUiSettings();
                ui.setZoomControlsEnabled(true);

                lastKnownLocation = map.addMarker(new
                        MarkerOptions()
                        .position(lastKnownLatLng)
                        .title("HQ North")
                        .snippet("Block 333, Admiralty Ave 3, 765654 "+"\nOperating Hours: 10am - 5pm\n65433456")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnStart){
            if(checkPermission()){
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        }
        else if(v.getId() == R.id.btnStop){
            client.removeLocationUpdates(mLocationCallback);
        }
        else{

        }
    }
    private boolean checkPermission(){
        int permissionCoarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCoarse == PermissionChecker.PERMISSION_GRANTED || permissionFine == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }
        else {
            String msg = "Permission not granted to retrieve location info";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}