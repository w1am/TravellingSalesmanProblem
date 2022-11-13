package geneticAlgorithm;

import java.time.Duration;

public class Timer {
    private long startTime;
    private long durationInNanoseconds;
    private float durationInMilliseconds;
    private float durationInSeconds;

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void stop() {
        long endTime = System.nanoTime();
        this.durationInNanoseconds = Duration.ofNanos(endTime - this.startTime).getNano();
        this.durationInMilliseconds = (float) this.durationInNanoseconds / 1_000_000;
        this.durationInSeconds = (float) this.durationInNanoseconds / 1_000_000_000;
    }

    public long getDurationInNanos() {
        return this.durationInNanoseconds;
    }

    public float getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public float getDurationInSeconds() {
        return durationInSeconds;
    }

    public void renderTime() {
        System.out.format("+-----------------+-------------------------+%n");
        System.out.format("| Unit            | Output                  |%n");
        System.out.format("+-----------------+-------------------------+%n");
        System.out.format("| %-15s | %d \t\t\t\t|%n", "nanoseconds", this.durationInNanoseconds);
        System.out.format("| %-15s | %.10f \t\t\t|%n", "milliseconds", this.durationInMilliseconds);
        System.out.format("| %-15s | %.10f \t\t\t|%n", "seconds", this.durationInSeconds);
        System.out.format("+-----------------+-------------------------+%n");
    }
}
