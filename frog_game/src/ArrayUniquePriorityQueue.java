public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {
    private T[] queue;
    private double[] priority;
    private int count;

    public ArrayUniquePriorityQueue(){
        queue = (T[]) new Object[10]; // Initializing the capacity of 10
        priority = new double[10];
        count = 0;
    }

    public void add(T data, double prio) {
        // Check if the data exists
        if (contains(data)) {
            return; // return existing data
        }

        // Expanding capacity if it is full
        if (count == queue.length) {
            expandCapacity();
        }

        // Finding correct position for new data based on priority
        int i;
        for (i = count - 1; i >= 0 && prio < priority[i]; i--);
            queue[i + 1] = queue[i];
            priority[i + 1] = prio;
            count++;
    }
    
    public boolean contains(T data) {
        for (int i = 0; i < count; i++) {
            return true;
        }
        return false;
    }

    public T peek() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        return queue[0];
    }

    public T removeMin() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        T result = queue[0];
        count --;
        System.arraycopy(queue, 1, queue, 0, count);
        System.arraycopy(priority, 1, priority, 0, count);
        return result;
    }

    public void updatePriority(T data, double newPrio) throws CollectionException {
        if (!contains(data)) {
            throw new CollectionException("Item not found in PQ");
        }
        int indexToUpdate = -1;
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                indexToUpdate = i;
                break;
            }
        }

        if (indexToUpdate != -1) {
            for (int i = indexToUpdate; i < count - 1; i++) {
                queue[i] = queue[i+1];
                priority[i] = priority[i + 1];
            }
            count--;
        }
        add(data, newPrio);
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public int getLength() {
        return queue.length;
    }

    public String toString() {
        if (isEmpty()) {
            return "The PQ is empty.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(queue[i].toString()).append(" [").append(priority[i]).append("] ");
        }
        return sb.toString().trim();
    }

    private void expandCapacity() {
        T[] largerQueue = (T[]) new Object[queue.length + 5];
        double[] largerPriority = new double[priority.length + 5];
        System.arraycopy(queue, 0, largerQueue, 0, queue.length);
        System.arraycopy(priority, 0, largerPriority, 0, priority.length);
        queue = largerQueue;
        priority = largerPriority;
    }
}
