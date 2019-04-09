package lock.model;

/**
 * @Author xiongyx
 * on 2019/4/9.
 *
 * 锁的内容
 */
public class LockContent {

    /**
     * 客户端唯一id
     * */
    private String requestID;

    /**
     * 重入锁 count
     * */
    private int lockCount;

    public LockContent(String requestID, int lockCount) {
        this.requestID = requestID;
        this.lockCount = lockCount;
    }

    public int lockCountInc(){
        this.lockCount++;
        return this.lockCount;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public int getLockCount() {
        return lockCount;
    }

    public void setLockCount(int lockCount) {
        this.lockCount = lockCount;
    }
}
