package de.dfki.falldetector;

/**
 * The same as {@link de.dfki.falldetector.RingBufferFloat},
 * just for timestamps and without min/max/average
 */
public class RingBufferLong {

    private final long[] buf;
    private int size = 0;
    private int start = -1;

    public RingBufferLong(int size) {
        this.buf = new long[size];
    }

    /**
     * Peeks at the head of the queue
     *
     * @return The head element of the queue
     */
    public long getFirst() {
        if (start < 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        return buf[start];
    }

    public void add(long n) {
        //full queue
        if (size >= buf.length) {
            buf[start] = n;
            start++;
            if (start >= buf.length) {
                start = start - buf.length;
            }
        } else {
            buf[size] = n;
            size++;
        }
    }

    public long get(int i){
        if(i >= size){
            throw new IllegalArgumentException("Index out of bounds. Index: "+i+", Size: "+size);
        }
        if(start + i >= buf.length){
            i = i-buf.length; // wrap index
        }
        return buf[start+i];
    }

    public long dequeue() {
        if (start < 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        long ret = buf[start];
        start++;
        if (start >= buf.length) {
            start = start - buf.length;
        }
        size--;
        return ret;
    }

    public void clear() {
        start = 0;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
