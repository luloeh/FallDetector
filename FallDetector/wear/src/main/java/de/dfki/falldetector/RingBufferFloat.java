package de.dfki.falldetector;

/**
 * This class implements a circular buffer for floats.
 * Since this application also needs the maximum, minimum and
 * average values in the buffer, those are also tracked with as few
 * iterations over the data as possible.
 */
public class RingBufferFloat {

    private final float[] buf;
    private int size = 0;
    private int start = -1;
    private float max = Float.MIN_VALUE;
    private float min = Float.MAX_VALUE;
    private float sum = 0;

    public RingBufferFloat(int size) {
        this.buf = new float[size];
    }

    /**
     * Peeks at the head of the queue
     *
     * @return The head element of the queue
     */
    public float getFirst() {
        if (start < 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        return buf[start];
    }

    /**
     * add an element to the end of the queue
     *
     * @param n the value to add
     */
    public void add(float n) {
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
        sum += n;
        if (n > max) {
            max = n;
        }
        if (n < min) {
            min = n;
        }
    }

    /**
     * get a value with index between 0 and the <code>size</code> of this buffer
     * for index 0, use {@link #getFirst()} instead as it is faster
     *
     * @param i the index to get
     * @return the value at index i
     */
    public float get(int i) {
        if (i >= size) {
            throw new IllegalArgumentException("Index out of bounds. Index: " + i + ", Size: " + size);
        }
        if (start + i >= buf.length) {
            i = i - buf.length; // wrap index
        }
        return buf[start + i];
    }

    /**
     * remove the head of the queue
     *
     * @return the value of the removed head
     */
    public float dequeue() {
        if (start < 0) {
            throw new IllegalStateException("Buffer is empty");
        }
        float ret = buf[start];
        start++;
        if (start >= buf.length) {
            start = start - buf.length;
        }
        size--;
        sum -= ret;
        if (ret == min) {
            updateMin();
        }
        if (ret == max) {
            updateMax();
        }
        return ret;
    }

    /**
     * empty the queue
     */
    public void clear() {
        start = 0;
        size = 0;
        sum = 0;
        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
    }

    /**
     * find a new maximum if the old one got removed
     */
    private void updateMax() {
        if (size < 1) {
            max = Float.MIN_VALUE;
        } else {
            int l = buf.length;
            float c;
            for (int i = start; i < size; i++) {
                if (i >= l) {
                    c = buf[i - l];
                } else {
                    c = buf[i];
                }
                if (c > max) {
                    max = c;
                }
            }
        }
    }

    /**
     * find a new minimum if the old one got removed
     */
    private void updateMin() {
        if (size < 1) {
            min = Float.MAX_VALUE;
        } else {
            int l = buf.length;
            float c;
            for (int i = start; i < size; i++) {
                if (i >= l) {
                    c = buf[i - l];
                } else {
                    c = buf[i];
                }
                if (c < min) {
                    min = c;
                }
            }
        }
    }

    /**
     * retrieve the maximum value in the buffer, do not call on empty buffer
     *
     * @return the maximum value
     */
    public float getMax() {
        if (size < 1) {
            throw new IllegalStateException("Buffer is empty");
        }
        return max;
    }

    /**
     * retrieve the minimum value in the buffer, do not call on empty buffer
     *
     * @return the minimum value
     */
    public float getMin() {
        if (size < 1) {
            throw new IllegalStateException("Buffer is empty");
        }
        return min;
    }

    /**
     * get the average value currently in the buffer, do not call on empty buffer
     *
     * @return the average
     */
    public float getAverage() {
        if (size < 1) {
            throw new IllegalStateException("Buffer is empty");
        }
        return sum / size;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * current size of the buffer,
     * not the capacity of the buffer
     *
     * @return the number of elements in the buffer
     */
    public int size() {
        return size;
    }

    public int capacity() {
        return buf.length;
    }
}
