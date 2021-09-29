import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class SimplentLRUCache<T> implements LRUCache<T> {
    private final int capacity;
    private final AtomicInteger time = new AtomicInteger(0);

    private final Map<T, Entry> set = new ConcurrentHashMap<>();
    private final Map<Integer, T> rset = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<Integer> list = new ConcurrentSkipListSet<>();

    public SimplentLRUCache(int capacity) {
        this.capacity = capacity;
    }

    class Entry{
        int time;
        T value;

        public Entry(int time, T value) {
            this.time = time;
            this.value = value;
        }
    }

    @Override
    public void add(T x){
        put(x);
        if(set.size() > capacity){
            remove();
        }
    }

    @Override
    public T get(T x) {
        Entry e = set.get(x);
        if(e != null) {
            put(e.value);
            return e.value;
        }
        return null;
    }

    private void remove(){
        Integer t = list.pollFirst();
        set.remove(rset.remove(t));
    }

    private void put(T x){
        int t = time.getAndIncrement();
        list.add(t);
        Entry old = set.put(x, new Entry(t, x));
        rset.put(t, x);
        if(old != null) {
            list.remove(old.time);
            rset.remove(old.time);
        }
    }

    @Override
    public String toString() {
        return rset.toString() + "\n" + list.toString();
    }
}
