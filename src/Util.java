import java.time.Instant;
import java.time.temporal.TemporalField;

public class Util {
    public static void print(Object str) {
        System.out.println( "\n" + "[" + (System.nanoTime() / 1000000L - Main.startTime / 1000000L) + " ms] " + str);
    }
}
