package de.ovgu.spldev.pclocator;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Measurement {
    private static final int MEGABYTE = 1024 * 1024;
    private MemoryMXBean _memoryBean = ManagementFactory.getMemoryMXBean();
    long heapMemory, time;

    Measurement() {
        MemoryUsage heapUsage = _memoryBean.getHeapMemoryUsage();
        heapMemory = heapUsage.getUsed();
        time = System.currentTimeMillis();
    }

    private Measurement(long _heapMemory, long _time) {
        heapMemory = _heapMemory;
        time = _time;
    }

    public Measurement difference(Measurement other) {
        return new Measurement(heapMemory - other.heapMemory, time - other.time);
    }

    public String heapMemory() {
        boolean moreUsedMemory = heapMemory > 0;
        String sign = moreUsedMemory ? "+" : "-";
        return sign + Math.abs(heapMemory) / MEGABYTE + "MB";
    }

    public String time() {
        return time + "ms";
    }

    public String toString() {
        return heapMemory() + ", " + time();
    }
}
