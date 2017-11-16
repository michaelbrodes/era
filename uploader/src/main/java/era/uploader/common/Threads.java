package era.uploader.common;

import java.util.function.Supplier;

/**
 * A utility class for Multithreaded classes.
 */
public class Threads {

    /**
     * Used for threadsafe singletons. It will double check that instance is
     * actually null. The first check is a fast check for 99% of the time as
     * it will only pass when the singleton hasn't been instantiated yet.
     * After we have checked that the instance is null we synchronize to make
     * sure that no other threads are creating it. We then check
     * <em>again</em> to make sure we weren't blocked by the synchronization
     * mutex and if that passes we finally create the instance with creator.
     */
    public static <T> T doubleCheck(
            T instance,
            Supplier<T> creator,
            Class<T> syncClass
    ) {
        if (instance == null) {
            synchronized (syncClass) {
                if (instance == null) {
                    instance = creator.get();
                }
            }
        }

        return instance;
    }
}
