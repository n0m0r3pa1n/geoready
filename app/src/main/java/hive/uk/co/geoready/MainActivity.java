package hive.uk.co.geoready;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private OnSuccessListener<Location> goingHome = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(KEY_HOME, getLocationAsString(location))
                        .apply();
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
                edit.putString(KEY_WORK, getLocationAsString(location))
                        .apply();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoingToWork = findViewById(R.id.btn_leaving);
        Button btnGoingHome = findViewById(R.id.btn_home);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
        gson = new Gson();

        btnGoingToWork.setOnClickListener(v -> MainActivityPermissionsDispatcher.getLastKnownLocationWithPermissionCheck(this, goingToWork));

        btnGoingHome.setOnClickListener(v -> MainActivityPermissionsDispatcher.getLastKnownLocationWithPermissionCheck(this, goingHome));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLastKnownLocation(OnSuccessListener<Location> onSuccessListener) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, onSuccessListener);
    }
}
