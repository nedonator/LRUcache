import java.util.*;

public class SimpleLRUCache<T> implements LRUCache<T> {
    private final int capacity;
    private final T defaultValue;

    private final Map<T, Node> set = new HashMap<>();
    Node begin = new Node(null, null);
    Node end = begin;

    private class Node{
        T value;
        Node previous;
        Node next;

        public Node(T value, Node previous) {
            this.value = value;
            this.previous = previous;
            if(previous != null)
                previous.next = this;
            next = null;
        }
    }

    public SimpleLRUCache(int capacity) {
        this(capacity, null);
    }

    public SimpleLRUCache(int capacity, T defaultValue) {
        this.capacity = capacity;
        this.defaultValue = defaultValue;
    }

    @Override
    public void add(T x){
        if(get(x) != defaultValue){
            end.value = x;
            return;
        }
        put(x);
        if(set.size() == capacity + 1){
            set.remove(remove().value);
        }
    }

    @Override
    public T get(T x) {
        Node e = set.get(x);
        if(e == null) return defaultValue;
        put(x);
        remove(e);
        return e.value;
    }

    private Node remove(){
        Node n = begin.next;
        remove(n);
        return n;
    }

    private void remove(Node n){
        n.previous.next = n.next;
        n.next.previous = n.previous;
    }

    private void put(T x){
        Node n = new Node(x, end);
        end = n;
        set.put(x, n);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node n = begin.next;
        while(n != null){
            sb.append("%s, ".formatted(n.value));
            n = n.next;
        }
        return "[%s]".formatted(sb.length() == 0 ? "" :
                sb.subSequence(0, sb.length() - 2).toString());
    }
}
