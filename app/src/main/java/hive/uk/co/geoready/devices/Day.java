package hive.uk.co.geoready.devices;

import org.joda.time.LocalTime;

import java.util.Date;

public class Day {
    private String name;
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Day(String name, Date date, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
