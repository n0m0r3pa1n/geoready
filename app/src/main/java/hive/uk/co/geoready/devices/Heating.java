package hive.uk.co.geoready.devices;

import java.util.List;

public class Heating {
    private String name;
    private List<Day> schedule;

    public Heating(String name, List<Day> schedule) {
        this.name = name;
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public List<Day> getSchedule() {
        return schedule;
    }
}
