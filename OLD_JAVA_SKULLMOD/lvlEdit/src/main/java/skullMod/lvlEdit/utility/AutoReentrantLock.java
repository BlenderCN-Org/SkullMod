package skullMod.lvlEdit.utility;

import java.util.concurrent.locks.ReentrantLock;

/**
 * No idea if this is considered a hack, but it seems nice
 * it can be used in try-with statments
 */

public class AutoReentrantLock extends ReentrantLock implements AutoCloseable{
    public AutoReentrantLock(){
        super();
    }

    public AutoCloseable autoLock(){
        lock();
        return this;
    }

    public AutoReentrantLock(boolean fair){
        super(fair);
    }
    public void close(){
        unlock();
    }
}
