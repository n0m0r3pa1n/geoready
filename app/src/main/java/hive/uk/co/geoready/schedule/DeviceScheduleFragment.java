package hive.uk.co.geoready.schedule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hive.uk.co.geoready.R;
import hive.uk.co.geoready.devices.Day;
import hive.uk.co.geoready.devices.Heating;

public class DeviceScheduleFragment extends Fragment {
    public static final String TAG = DeviceScheduleFragment.class.getSimpleName();

    private Heating mHeating;
    private List<Day> mSchedule;

    private RecyclerView mRecyclerView;
    private TextView mSuggestedEndTime;
    private TextView mSuggestedStartTime;
    private TextView tvNotReachable;
    private LinearLayout mContainerStartTime;
    private LinearLayout mContainerEndTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSchedule = new ArrayList<>();

        LocalDate now = new LocalDate();
        Date monday = now.withDayOfWeek(DateTimeConstants.MONDAY).toDate();
        Date tuesday = now.withDayOfWeek(DateTimeConstants.TUESDAY).toDate();
        Date wednesday = now.withDayOfWeek(DateTimeConstants.WEDNESDAY).toDate();
        Date thursday = now.withDayOfWeek(DateTimeConstants.THURSDAY).toDate();
        Date friday = now.withDayOfWeek(DateTimeConstants.FRIDAY).toDate();
        Date saturday = now.withDayOfWeek(DateTimeConstants.SATURDAY).toDate();
        Date sunday = now.withDayOfWeek(DateTimeConstants.SUNDAY).toDate();

        LocalTime startTime = LocalTime.now().withFieldAdded(DurationFieldType.hours(), 1);
        LocalTime endTime = LocalTime.now().withFieldAdded(DurationFieldType.hours(), 5);
        mSchedule.add(new Day(LocalDate.now().dayOfWeek().getAsText(Locale.US), monday, startTime, endTime));
//        mSchedule.add(new Day("Tuesday", tuesday, startTime, endTime));
//        mSchedule.add(new Day("Wednesday", wednesday, startTime, endTime));
//        mSchedule.add(new Day("Thursday", thursday, startTime, endTime));
//        mSchedule.add(new Day("Friday", friday, startTime, endTime));
//        mSchedule.add(new Day("Saturday", saturday, startTime, endTime));
//        mSchedule.add(new Day("Sunday", sunday, startTime, endTime));

        mHeating = new Heating("My Hive Heating", mSchedule);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_schedule, container, false);
        mRecyclerView = view.findViewById(R.id.rv_schedule);
        mSuggestedStartTime = view.findViewById(R.id.tv_suggested_start_date);
        mSuggestedEndTime = view.findViewById(R.id.tv_suggested_end_date);
        mContainerStartTime = view.findViewById(R.id.container_start_date);
        mContainerEndTime = view.findViewById(R.id.container_end_date);
        tvNotReachable = view.findViewById(R.id.tv_not_reachable);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ScheduleAdapter(mSchedule));

        return view;
    }

    public Day getCurrentScheduleTime() {
        return mSchedule.get(0);
    }

    public void hideSuggestedTime() {
        mContainerStartTime.setVisibility(View.GONE);
        mContainerEndTime.setVisibility(View.GONE);
    }

    public void showSuggestedTime() {
        mContainerStartTime.setVisibility(View.VISIBLE);
        mContainerEndTime.setVisibility(View.VISIBLE);
    }

    public void setSuggestedTime(String startTime, String endTime) {
        mSuggestedStartTime.setText(startTime);
        mSuggestedEndTime.setText(endTime);
    }

    public void showTargetTempUnreachable() {
        tvNotReachable.setVisibility(View.VISIBLE);
    }

    public void hideTargetTempUnreachable() {
        tvNotReachable.setVisibility(View.GONE);
    }
}
