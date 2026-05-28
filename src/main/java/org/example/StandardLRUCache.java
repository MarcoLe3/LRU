package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

public class StandardLRUCache {
    private static class Node {
        int key;
        int value;
        StandardLRUCache.Node prev;
        StandardLRUCache.Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class LRUCache {
        private final Map<Integer, Node> cache = new HashMap<>();
        private final Node dummyHead = new Node(-1, -1);
        private final Node dummyTail = new Node(-1, -1);

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
            unlinkNode(node);
            this.dummyTail.prev.next = node;
            node.prev = this.dummyTail.prev;
            node.next = this.dummyTail;
            this.dummyTail.prev = node;
        }

        private void unlinkNode(Node node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            node.prev = null;
            node.next = null;
        }
    }

    private final int cacheSizeLimit;
    private final LRUCache[] cacheArray;

    public StandardLRUCache(int capacity, int cacheArraySize) {
        this.cacheSizeLimit = capacity / cacheArraySize;
        cacheArray = new StandardLRUCache.LRUCache[cacheArraySize];
        for (int i = 0; i < cacheArraySize; i++) {
            cacheArray[i] = new StandardLRUCache.LRUCache();
        }
    }

    private LRUCache getCache(int key) {
        int cacheHit = key ^ (key >>> 16);
        return cacheArray[Math.abs(cacheHit % cacheArray.length)];
    }

    private boolean cacheContainsKey(Map<Integer, Node> cache, int key) {
        return cache.containsKey(key);
    }

    private void updateExistingNode(LRUCache LRUCache, int value, int key) {
        Node existingNode = LRUCache.cache.get(key);
        existingNode.value = value;
        LRUCache.moveToTail(existingNode);
    }

    private boolean cacheSizeReachedLimit(int cacheSize) {
        return cacheSize >= cacheSizeLimit;
    }

    private void updateCache(StandardLRUCache.LRUCache LRUCache, int key, int value) {
        Node newNode = new Node(key, value);
        LRUCache.cache.put(key, newNode);
        LRUCache.moveToTail(newNode);
    }

    private Optional<Integer> validateGetValue(int value) {
        if (value == -1) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    public synchronized void put(int key, int value) {
        LRUCache LRUCache = getCache(key);
        Map<Integer, Node> cache = LRUCache.cache;

        if (cacheContainsKey(cache, key)) {
            updateExistingNode(LRUCache, value, key);
        } else {
            if (cacheSizeReachedLimit(cache.size())) {
                LRUCache.removeNode();
            }
            updateCache(LRUCache, key, value);
        }
    }

    public synchronized Optional<Integer> get(int key) {
        LRUCache LRUCache = getCache(key);
        Node node = LRUCache.cache.get(key);
        int value = node != null ? node.value : -1;
        return validateGetValue(value);
    }

    synchronized public void printLinkedList() {
        for (int i = 0; i < cacheArray.length; i++) {
            System.out.println("LRUCache LinkedList " + i + ":");
            Node temp = cacheArray[i].dummyHead.next;
            while (temp != null && temp.key != -1) {
                System.out.println(temp.key);
                temp = temp.next;
            }
        }
    }
}
