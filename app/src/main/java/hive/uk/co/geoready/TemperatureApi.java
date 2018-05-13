package hive.uk.co.geoready;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TemperatureApi {
    @GET("/?internal_temp=18&external_temp=15")
    Call<JsonElement> getMinutesToHeat(@Query("target") int target);
}
