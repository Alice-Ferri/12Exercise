package p12;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {

    Map<Q, Queue<T>> marketMap = new TreeMap<>();

    @Override
    public Set<Q> availableQueues() {
        return marketMap.keySet();
    }

    @Override
    public void openNewQueue(Q queue) {
        if (marketMap.containsKey(queue)) {
            throw new IllegalArgumentException("Queue " + queue + " already exists");
        }
        marketMap.put(queue, new LinkedList<>());
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        if(!marketMap.containsKey(queue)){
            throw new IllegalArgumentException("Queue " + queue + " doesn't exist");
        }
        return marketMap.get(queue).isEmpty();
    }

    @Override
    public void enqueue(T elem, Q queue) {
        if(!marketMap.containsKey(queue)){
            throw new IllegalArgumentException("Queue " + queue + " doesn't exist, can't add to it");
        }
        marketMap.get(queue).add(elem);
    }

    @Override
    public T dequeue(Q queue) {
        if(!marketMap.containsKey(queue)){
            throw new IllegalArgumentException("Queue " + queue + " doesn't exist, can't remove from it");
        }
        return marketMap.get(queue).poll();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        Map<Q, T> elementsQueue = new TreeMap<>();

        for (Q elem : marketMap.keySet()) {
            elementsQueue.put(elem, marketMap.get(elem).poll());
        }

        return elementsQueue;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        Set<T> enqueuedElements = new LinkedHashSet<>();

        for(Q queue : marketMap.keySet()){
            for(T elem : marketMap.get(queue))
                enqueuedElements.add(elem);
        }

        return enqueuedElements;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        if(!marketMap.containsKey(queue)){
            throw new IllegalArgumentException("Queue " + queue + " doesn't exist, can't remove all from it");
        }
        if (marketMap.get(queue) == null) {
            throw new IllegalArgumentException("Queue " + queue + " does not exist");
        }

        List<T> queueList = new LinkedList<>();
        
        while(!marketMap.get(queue).isEmpty()){
            queueList.add(marketMap.get(queue).poll());
        }
        
        return queueList;

    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        if(!marketMap.containsKey(queue)){
            throw new IllegalArgumentException("Queue " + queue + " doesn't exist, can't close it");
        }
        if(marketMap.size() == 1){
            throw new IllegalStateException("Can't close" + queue + "there's no other queue");
        }

        List<T> queueClosed = dequeueAllFromQueue(queue);
        Set<Q> queueOpen = availableQueues();

        queueOpen.remove(queue);
        Q queueToPut = queueOpen.iterator().next();
        
        for(T elem : queueClosed){
            enqueue(elem, queueToPut);
        }
    
        marketMap.remove(queue);

    }

}
