package hive.uk.co.geoready;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class GeoReadyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
