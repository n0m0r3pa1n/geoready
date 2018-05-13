package hive.uk.co.geoready;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.LocalDateTime;

import hive.uk.co.geoready.devices.Day;
import hive.uk.co.geoready.schedule.DeviceScheduleFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoingHomeActivity extends AppCompatActivity {
    public static final String TAG = GoingHomeActivity.class.getSimpleName();

    private static final String KEY_MAIN = "main";
    private static final String KEY_HOME = "HOME";
    private static final String KEY_WORK = "WORK";
    private static final String KEY_TARGET_TEMP = "TARGET_TEMP";

    private Button btnGoHome;
    private TextView tvTime;
    private TextView tvHint;
    private SharedPreferences sharedPreferences;
    private Location homeLocation, workLocation;
    private MapsApiService mapsApiService;
    private TemperatureApi temperatureApi;
    private String mTravelMode;
    private String mTransitMode;
    private DeviceScheduleFragment mDeviceScheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_going_home);

        sharedPreferences = getSharedPreferences(KEY_MAIN, MODE_PRIVATE);

        tvTime = findViewById(R.id.tv_time);
        tvHint = findViewById(R.id.tv_hint);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnGoHome.setOnClickListener(v -> getLocationExpectedTime(mTravelMode, mTransitMode));

        setupRetrofit();
        setupLocations();
        setupSpinners();

        getLocationExpectedTime("driving", "");

        mDeviceScheduleFragment = new DeviceScheduleFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mDeviceScheduleFragment, DeviceScheduleFragment.TAG)
                .commit();
    }

    private void setupSpinners() {
        Spinner spTravelMode = findViewById(R.id.sp_travel_mode);
        Spinner spTransitMode = findViewById(R.id.sp_transit_mode);
        spTransitMode.setEnabled(false);

        ArrayAdapter<CharSequence> travelModesArray = ArrayAdapter.createFromResource(this, R.array.travel_modes, android.R.layout.simple_spinner_item);
        travelModesArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTravelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    spTransitMode.setEnabled(true);
                } else {
                    spTransitMode.setEnabled(false);
                    String[] stringArray = getResources().getStringArray(R.array.travel_modes);
                    mTravelMode = stringArray[position];
                    mTransitMode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTravelMode.setAdapter(travelModesArray);


        ArrayAdapter<CharSequence> transitModesArray = ArrayAdapter.createFromResource(this, R.array.transit_mode, android.R.layout.simple_spinner_item);
        transitModesArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTransitMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] stringArray = getResources().getStringArray(R.array.transit_mode);
                mTravelMode = "transit";
                if (position == 0) {
                    mTransitMode = "";
                } else {
                    mTransitMode = stringArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTransitMode.setAdapter(transitModesArray);

    }

    private void setupLocations() {
        String homeLocationString = sharedPreferences.getString(KEY_HOME, "");
        String workLocationString = sharedPreferences.getString(KEY_WORK, "");

        Gson gson = new Gson();

        homeLocation = gson.fromJson(homeLocationString, Location.class);
        workLocation = gson.fromJson(workLocationString, Location.class);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mapsApiService = retrofit.create(MapsApiService.class);

        Retrofit retrofitLocal = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        temperatureApi = retrofitLocal.create(TemperatureApi.class);
    }

    private void getLocationExpectedTime(String travelMode, String transitMode) {
        String origin = String.format("%s,%s", workLocation.getLatitude(), workLocation.getLongitude());
        String destination = String.format("%s,%s", homeLocation.getLatitude(), homeLocation.getLongitude());

        Call<JsonElement> timeRequest = mapsApiService.getTravelTime("imperial", origin, destination, travelMode, transitMode);
        timeRequest.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject responseObject = response.body()
                        .getAsJsonObject();

                JsonObject durationObject = responseObject
                        .get("rows")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("elements")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("duration")
                        .getAsJsonObject();

                String destinationAddress = responseObject.get("destination_addresses").getAsJsonArray().get(0).getAsString();
                String originAddress = responseObject.get("origin_addresses").getAsJsonArray().get(0).getAsString();
                tvHint.setText(String.format("How do you plan to go from %s to %s?", originAddress, destinationAddress));

                String durationText = durationObject
                        .get("text")
                        .getAsString();

                int travelTimeInMinutes = durationObject.get("value").getAsInt() / 60;
                Log.d(TAG, "onResponse: " + travelTimeInMinutes);

                int targetTemp = sharedPreferences.getInt(KEY_TARGET_TEMP, 20);
                temperatureApi.getMinutesToHeat(targetTemp).enqueue(
                        new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                int minutesToHeat = response.body().getAsJsonObject().get("minutes").getAsInt();
                                tvTime.setText("Travel will take you around " + durationText + "\n\nYour home can be heated up to " + targetTemp + " degress in " +
                                        minutesToHeat + " minutes");

                                if (minutesToHeat < travelTimeInMinutes) {
                                    String startTime = LocalDateTime.now().plusMinutes(travelTimeInMinutes).minusMinutes(minutesToHeat).toString("E HH:mm");
                                    mDeviceScheduleFragment.setSuggestedTime(startTime, "--");
                                    mDeviceScheduleFragment.hideTargetTempUnreachable();
                                    mDeviceScheduleFragment.showSuggestedTime();
                                } else {
                                    mDeviceScheduleFragment.hideSuggestedTime();
                                    mDeviceScheduleFragment.showTargetTempUnreachable();
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {

                            }
                        });

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }
}
