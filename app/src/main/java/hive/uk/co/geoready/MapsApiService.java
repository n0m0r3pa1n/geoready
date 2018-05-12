package hive.uk.co.geoready;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapsApiService {

    @GET("distancematrix/json?key=AIzaSyC-BaBwhrzgjay8qcj1-7HCT4gwFKwEVDU")
    Call<JsonElement> getTravelTime(@Query("units") String units, @Query("origins") String origins,
            @Query("destinations") String destinations, @Query("mode") String travelMode, @Query("transit_mode") String transitMode);
}
