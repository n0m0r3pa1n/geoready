package hive.uk.co.geoready.setup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLngBounds;

import hive.uk.co.geoready.R;

public class SetupLocationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_locations);


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            if (selectedPlace != null) {
                builder.setLatLngBounds(new LatLngBounds(selectedPlace.getLatLng(), selectedPlace.getLatLng()));
            }

            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            int playServicesErrorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (playServicesErrorCode != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.showErrorDialogFragment(playServicesErrorCode, this, null, 12312, null);
            }
            e.printStackTrace();
        }


    }
}
