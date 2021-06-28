package biz.dto;

/**
 * @author tizi
 */
public class AgeInterval {
    public int intervalMin = 0;
    public int intervalMax = 0;
    public String intervalName = "";

    public AgeInterval(int intervalMin, int intervalMax, String intervalName) {
        this.intervalMin = intervalMin;
        this.intervalMax = intervalMax;
        this.intervalName = intervalName;
    }

    public int getIntervalMin() {
        return intervalMin;
    }

    public void setIntervalMin(int intervalMin) {
        this.intervalMin = intervalMin;
    }

    public int getIntervalMax() {
        return intervalMax;
    }

    public void setIntervalMax(int intervalMax) {
        this.intervalMax = intervalMax;
    }

    public String getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(String intervalName) {
        this.intervalName = intervalName;
    }

    @Override
    public String toString() {
        String res = "";
        res = String.format("age is %d to %d %s", this.intervalMin, this.intervalMax, this.intervalName);
        return res;
    }
}
