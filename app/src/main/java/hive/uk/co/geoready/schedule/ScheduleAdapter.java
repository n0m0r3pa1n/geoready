package hive.uk.co.geoready.schedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hive.uk.co.geoready.R;
import hive.uk.co.geoready.devices.Day;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<Day> mSchedule;

    public ScheduleAdapter(List<Day> schedule) {
        mSchedule = schedule;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Day day = mSchedule.get(position);
        holder.tvName.setText(day.getName());
        holder.tvStartTime.setText(day.getStartTime().toString("HH:mm"));
        holder.tvEndTime.setText(day.getEndTime().toString("HH:mm"));
    }

    @Override
    public int getItemCount() {
        return mSchedule.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvStartTime;
        TextView tvEndTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStartTime = itemView.findViewById(R.id.tv_start_date);
            tvEndTime = itemView.findViewById(R.id.tv_end_date);
        }
    }
}
