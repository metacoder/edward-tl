package de.metacoder.edwardthreadlocal;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TraceReceiver {

    private static final ThreadLocal<Boolean> traceEnabledForThisThread = ThreadLocal.withInitial(() -> false);

    private static final Set<Thread> activeTracedThreads = ConcurrentHashMap.newKeySet();

    public static void activateTracingForThread(){
        assert activeTracedThreads.add(Thread.currentThread());
        System.out.println("Activated thread local tracing for thread " + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + ")");
    }

    public static void deactivateTracingForThread(){
        try {
            // analysis
        } finally {
            assert activeTracedThreads.remove(Thread.currentThread());
            System.out.println("Deactivated thread local tracing for thread " + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + ")");
        }
    }

    public static void trackSet(ThreadLocal<?> affectedThreadLocal, Object objectToSet){
        System.out.println("This is edward: set called =) - your system hashcode for your TL is: " + System.identityHashCode(affectedThreadLocal));
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //new RuntimeException().printStackTrace();
        // TODO record
    }

    public static void trackRemove(ThreadLocal<?> affectedThreadLocal){
        System.out.println("This is edward: remove called =) - your system hashcode for your TL is: " + System.identityHashCode(affectedThreadLocal));
        //new RuntimeException().printStackTrace();
        // TODO record
    }

}
