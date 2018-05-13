package hive.uk.co.geoready;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final String KEY_HOME = "HOME";
    private static final String KEY_WORK = "WORK";
    private static final String KEY_TARGET_TEMP = "TARGET_TEMP";
    public static final String KEY_MAIN = "main";

    private boolean mRequestingLocationUpdates = true;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;
    private Location mLastLocation;
    private Gson gson;

    private OnSuccessListener<Location> goingHome = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(KEY_WORK, getLocationAsString(location))
                        .apply();

                Intent intent = new Intent(MainActivity.this, GoingHomeActivity.class);
                startActivity(intent);
            }
        }
    };

    private String getLocationAsString(Location location) {
        return gson.toJson(location);
    }

    private OnSuccessListener<Location> goingToWork = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(KEY_HOME, getLocationAsString(location))
                        .apply();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences(KEY_MAIN, MODE_PRIVATE);
        gson = new Gson();

        Button btnGoingToWork = findViewById(R.id.btn_leaving);
        Button btnGoingHome = findViewById(R.id.btn_home);
        Button btnStats = findViewById(R.id.btn_stats);
        NumberPicker npTargetTemp = findViewById(R.id.np_target_temperature);
        npTargetTemp.setMinValue(10);
        npTargetTemp.setMaxValue(30);
        npTargetTemp.setValue(sharedPreferences.getInt(KEY_TARGET_TEMP, 20));
        npTargetTemp.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (oldVal != newVal) {
                sharedPreferences.edit().putInt(KEY_TARGET_TEMP, newVal).apply();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        btnGoingToWork.setOnClickListener(v -> goingToWork.onSuccess(mLastLocation));
        btnGoingHome.setOnClickListener(v -> goingHome.onSuccess(mLastLocation));
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            MainActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressLint("NoCorrespondingNeedsPermission")
    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("We need permission to access location")
                .setPositiveButton("Approve", (dialog, button) -> request.proceed())
                .setNegativeButton("Deny", (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForCamera() {
        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForCamera() {
        Toast.makeText(this, "Never", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                null /* Looper */);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
