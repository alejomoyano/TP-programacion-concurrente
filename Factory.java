import java.util.concurrent.ThreadFactory;
import java.util.List;

public class Factory implements ThreadFactory{
    
    private int counter;
    private List<String> nombres;
    public Factory(){
        this.counter=0;
        
    }

    public Thread newThread(Runnable task){
            Thread hilo = new Thread(task);
            hilo.setName("Thread T"+counter);
            counter++;
            return hilo;
    }

    
    
}