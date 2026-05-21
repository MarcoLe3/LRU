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
        private Node dummyHead = new Node(-1,-1);
        private Node dummyTail  = new Node(-1,-1);

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

    private final int cacheSize;
    private final LRUCache[] cacheArray;

    public LRUCustomCache(int capacity, int cacheArraySize) {
        this.cacheSize = capacity/cacheArraySize;
        cacheArray = new LRUCache[cacheArraySize];
        for (int i = 0; i < cacheArraySize; i++) {
            cacheArray[i] = new LRUCache();
        }
    }

    private LRUCache getCache(int key) {
        int cacheHit = key ^ (key >>> 16);
        return cacheArray[Math.abs(cacheHit % cacheArray.length)];
    }

    public void put (int key, int value) {
        LRUCache LRUCache = getCache(key);
        long stamp = LRUCache.lock.writeLock();
        try {
            if (LRUCache.cache.containsKey(key)) {
                Node existingNode = LRUCache.cache.get(key);
                existingNode.value = value;
                LRUCache.moveToTail(existingNode);
            } else {
                if (LRUCache.cache.size() == cacheSize) {
                    LRUCache.removeNode();
                }
                Node newNode = new Node(key, value);
                LRUCache.moveToTail(newNode);
                LRUCache.cache.put(key, newNode);
            }
        } finally {
            LRUCache.lock.unlockWrite(stamp);
        }
    }

    public int get (int key) {
        LRUCache LRUCache = getCache(key);
        long stamp = LRUCache.lock.tryOptimisticRead();
        Node node = LRUCache.cache.get(key);
        int value = node != null ? node.value : -1;

        if (LRUCache.lock.validate(stamp)) {
            if (value == -1) {
                System.out.println("Value not found");
            }
            return value;
        }

        stamp = LRUCache.lock.readLock();
        try {
            node = LRUCache.cache.get(key);
            value = node != null ? node.value : -1;
            if (value == -1) {
                System.out.println("Value not found");
            }
            return value;
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
