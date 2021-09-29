import java.util.Objects;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimestampSkipList<T> {
    Node begin;
    Node lastBegin;
    AtomicInteger time;
    AtomicInteger minTime;
    Random random = new Random();

    public TimestampSkipList(int capacity, T defaultValue) {
        int depth = (int) (Math.log(capacity) / Math.log(2)) + 1;
        minTime = new AtomicInteger(Integer.MIN_VALUE);
        time = new AtomicInteger(Integer.MIN_VALUE);
        Node end = null;
        while (depth-- > 0) {
            end = new Node(defaultValue, Integer.MAX_VALUE, null, end);
            begin = new Node(defaultValue, Integer.MIN_VALUE, end, begin);
            if (lastBegin == null) lastBegin = begin;
        }
    }

    public TimestampSkipList(int capacity) {
        this(capacity, null);
    }

    private class Node {
        T value;
        int time;
        AtomicReference<Node> next;
        Node down;

        public Node(T value, int time, Node next, Node down) {
            this.value = value;
            this.time = time;
            this.next = new AtomicReference<>(next);
            this.down = down;
        }
    }

    public int add(T x) {
        int t = time.incrementAndGet();
        Stack<Node> stack = find(t);
        Node n1 = null;
        for (Node n = stack.pop(); !stack.isEmpty(); n = stack.pop()) {
            do {
                n1 = new Node(x, t, n.next.get(), n1);
            } while (!n.next.compareAndSet(n1.next.get(), n1));
            if (random.nextBoolean()) break;
        }
        return t;
    }

    public int skip() {
        int t = time.get();
        int t1 = minTime.get();
        if (t1 < t) {
            if(minTime.compareAndSet(t1, t1 + 1)){
                return t1;
            }
        }
        return Integer.MIN_VALUE;
    }

    public T get(int time) {
        Stack<Node> stack = find(time);
        Node node = stack.peek();
        return node.time == time ? node.value : null;
    }

    private void compact() {
        for (Node n = begin; n != null; n = n.down) {
            for (Node n1 = n; n1 != null && n1.next.get() != null; n1 = n1.next.get()) {
                Node n2 = n1.next.get();
                if (n2.time <= minTime.get()) {
                    n1.next.compareAndSet(n2, n2.next.get());
                }
            }
        }
    }

    private Stack<Node> find(int time) {
        Stack<Node> stack = new Stack<>();
        Node n = begin;
        while (n != null) {
            Node n2 = n.next.get();
            if (n2.time <= minTime.get()) {
                n.next.compareAndSet(n2, n2.next.get());
            }
            if (n2.time > time) {
                stack.add(n);
                n = n.down;
            } else {
                n = n2;
            }
        }
        return stack;
    }

    @Override
    public String toString() {
        compact();
        return Stream.iterate(begin, Objects::nonNull, x -> x.down).map(beg -> "[%s]".formatted(
                Stream.iterate(beg, Objects::nonNull, x -> x.next.get())
                        .map(x -> x.value.toString()).collect(Collectors.joining(", "))))
                .collect(Collectors.joining("\n"));
    }
}
