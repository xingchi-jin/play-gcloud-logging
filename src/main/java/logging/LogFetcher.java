package logging;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;

import java.time.Duration;
import java.time.Instant;


public class LogFetcher {
    private static Logging logging;

    public static void main(String[] args) {
        final int pageSize = Integer.parseInt(args[0]);
        final String query = args[1];
        Instant start = Instant.now();

        logging = LoggingOptions.getDefaultInstance().getService();
        var entries = logging.listLogEntries(Logging.EntryListOption.pageSize(pageSize),
            Logging.EntryListOption.filter(query));
        int callCount = 0;
        long totalBytes = 0;
        long logLineCount = 0;
        do {
            for (LogEntry logEntry : entries.iterateAll()) {
                totalBytes += logEntry.toString().length();
                logLineCount ++;
                //System.out.println(logEntry);
            }
            callCount ++;
            entries = entries.getNextPage();
        } while (entries != null);
        Instant finish = Instant.now();
        Duration timeElapsed = Duration.between(start, finish);

        System.out.printf("log-size(kb): %d, log-lines: %d, api-calls: %d, Elapse: %sh-%sm-%ss%n",
        totalBytes/1024,
        logLineCount,
        callCount,
        timeElapsed.toHoursPart(),
        timeElapsed.toMinutesPart(),
        timeElapsed.toSecondsPart());
    }
}
