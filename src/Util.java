import java.time.Instant;
import java.time.temporal.TemporalField;

public class Util {
    public static void print(Object str) {
        System.out.println( "\n" + "[" + (Instant.now().getNano() / 1000000L - Main.startTime.getNano() / 1000000L) + " ms] " + str);
    }
}
