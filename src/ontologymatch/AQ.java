package ontologymatch;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Muhd Jibril Kazim
 */
public class AQ {
    static ExecutorService es = Executors.newCachedThreadPool();
    static String callingClass = null;
    public static void add(Runnable task) {
        String presentClass = new Throwable().getStackTrace()[1].getClassName();
        presentClass = "Thread " + Thread.currentThread().getId() + ": " + presentClass;
        if (callingClass == null) {
            callingClass = presentClass;
        } else {
            if (!callingClass.equals(presentClass)) {
                throw new ConcurrentModificationException("AQ.add called from multiple classes: " + callingClass + ", " + presentClass);
            }
        }
        es.execute(task);
    }
    /**
     * AQ.finish() must be called after your loop.  This waits until
     * all threads are finished before moving on to the rest of the code.
     */
    public static void finish() {
        // request all threads be completed
        es.shutdown();
        // letting threads complete
        try {
            es.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // resetting threads
        es = Executors.newCachedThreadPool();
        // forget the previous calling class
        callingClass = null;
    }
}
