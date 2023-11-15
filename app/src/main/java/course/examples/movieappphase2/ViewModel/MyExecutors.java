package course.examples.movieappphase2.ViewModel;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyExecutors {

    /* i understood executors and implemented code by following youTube Channel
    https://www.youtube.com/watch?v=6Oo-9Can3H8
    Java ExecutorService
     */

    private static final Object LOCK = new Object();
    private static MyExecutors mExecutors;
    private final Executor thread1;
    private final Executor thread2;
    private final Executor thread3;


    private MyExecutors(Executor thread1, Executor thread2, Executor thread3) {
        this.thread1 = thread1;
        this.thread3 = thread2;
        this.thread2 = thread3;
    }

    public static MyExecutors getInstance() {
        if (mExecutors == null) {
            synchronized (LOCK) {
                mExecutors = new MyExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(   3),
                        new MainThreadExecutor());
            }
        }
        return mExecutors;
    }

    public Executor getThread1() {
        return thread1;
    }


    public Executor getThread2() {
        return thread2;
    }

    public Executor getThread3() {
        return thread3;
    }


    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
