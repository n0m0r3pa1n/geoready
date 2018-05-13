package hive.uk.co.geoready.stats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

import hive.uk.co.geoready.R;

public class StatsActivity extends AppCompatActivity {

    private static final String KEY_MAIN = "main";
    private static final String KEY_SAVED = "SAVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar =   findViewById(R.id.toolbar);
        TextView tvMinutes = findViewById(R.id.tv_minutes_count);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.DAY_OF_MONTH, yesterday.get(Calendar.DAY_OF_MONTH) - 1);
        Date d1 = yesterday.getTime();

        Calendar calendar = Calendar.getInstance();
        Date d2 = calendar.getTime();

        GraphView graph = (GraphView) findViewById(R.id.graph);

        int saved = getSharedPreferences(KEY_MAIN, MODE_PRIVATE).getInt(KEY_SAVED, 0);
        tvMinutes.setText((saved + 45) + "");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 45),
                new DataPoint(d2, saved)
        });

        graph.addSeries(series);

// set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(2); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(1);
        graph.getViewport().setMaxY(500);


        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
