import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadExecutor {

    private final ThreadPoolExecutor executor;

    public ThreadExecutor(Factory factory){
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15,factory);
    }

    public void ejecutar(Runnable Task){
        executor.execute(Task);
    }
}