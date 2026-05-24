package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class LRUCustomCache {
    private class Node {
        int key;
        int value;
        Node prev;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private class LRUCache {
        private final Map<Integer,Node> cache = new HashMap<>();
        private final StampedLock lock = new StampedLock();
        private final Node dummyHead = new Node(-1,-1);
        private final Node dummyTail  = new Node(-1,-1);

        public LRUCache() {
            dummyHead.next = dummyTail;
            dummyTail.prev = dummyHead;
        }

        public void removeNode() {
            cache.remove(dummyHead.next.key);
            this.dummyHead.next = this.dummyHead.next.next;
            this.dummyHead.next.prev = this.dummyHead;
        }

        public void moveToTail(Node node) {
            this.dummyTail.prev.next = node;
            node.prev = this.dummyTail.prev;
            node.next = this.dummyTail;
            this.dummyTail.prev = node;
        }

    }

    private final int cacheSizeLimit;
    private final LRUCache[] cacheArray;

    public LRUCustomCache(int capacity, int cacheArraySize) {
        this.cacheSizeLimit = capacity/cacheArraySize;
        cacheArray = new LRUCache[cacheArraySize];
        for (int i = 0; i < cacheArraySize; i++) {
            cacheArray[i] = new LRUCache();
        }
    }

    private LRUCache getCache(int key) {
        int cacheHit = key ^ (key >>> 16);
        return cacheArray[Math.abs(cacheHit % cacheArray.length)];
    }

    private boolean cacheContainsKey(Map<Integer,Node> cache,int key) {
        return cache.containsKey(key);
    }

    private void updateExistingNode(LRUCache LRUCache,int value, int key) {
        Node existingNode = LRUCache.cache.get(key);
        existingNode.value = value;
        LRUCache.moveToTail(existingNode);
    }

    private boolean cacheSizeReachedLimit(int cacheSize) {
        return cacheSize >= cacheSizeLimit;
    }

    private void updateCache(LRUCache LRUCache, int key, int value) {
        Node newNode = new Node(key, value);
        LRUCache.cache.put(key, newNode);
        LRUCache.moveToTail(newNode);
    }

    private int validateGetValue(int value){
        if (value == -1){
            System.out.println("Value not found");
        }
        return value;
    }

    public void put (int key, int value) {
        LRUCache LRUCache = getCache(key);
        Map<Integer,Node> cache = LRUCache.cache;
        StampedLock lock = LRUCache.lock;
        long stamp = lock.writeLock();
        try {
            if (cacheContainsKey(cache,key)) {
                updateExistingNode(LRUCache, value, key);
            } else {
                if (cacheSizeReachedLimit(cache.size())) {
                    LRUCache.removeNode();
                }
                updateCache(LRUCache, key, value);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int get (int key) {
        LRUCache LRUCache = getCache(key);
        long stamp = LRUCache.lock.tryOptimisticRead();
        Node node = LRUCache.cache.get(key);
        int value = node != null ? node.value : -1;

        if (LRUCache.lock.validate(stamp)) {
            return validateGetValue(value);
        }

        stamp = LRUCache.lock.readLock();
        try {
            node = LRUCache.cache.get(key);
            value = node != null ? node.value : -1;
            return validateGetValue(value);
        } finally {
            LRUCache.lock.unlockRead(stamp);
        }
    }

    synchronized public void printLinkedList() {
        for (int i = 0; i < cacheArray.length; i++) {
            int LRUCacheNumber = i;
            System.out.println("LRUCache LinkedList " + LRUCacheNumber + ":");
            Node temp = cacheArray[i].dummyHead.next;
            while (temp != null && temp.key != -1) {
                System.out.println(temp.key);
                temp = temp.next;
            }
        }
    }
}
