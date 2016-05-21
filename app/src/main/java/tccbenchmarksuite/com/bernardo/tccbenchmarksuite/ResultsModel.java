package tccbenchmarksuite.com.bernardo.tccbenchmarksuite;

/**
 * Created by bernardorochasalgueiro on 21/05/16.
 */
public class ResultsModel {

    private int id;
    private long timestamp;
    private int memory;
    private long clockTicks;

    public ResultsModel () {}

    public ResultsModel(int id, long timestamp, int memory, long clockTicks) {
        this.id = id;
        this.timestamp = timestamp;
        this.memory = memory;
        this.clockTicks = clockTicks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public long getClockTicks() {
        return clockTicks;
    }

    public void setClockTicks(long clockTicks) {
        this.clockTicks = clockTicks;
    }
}
