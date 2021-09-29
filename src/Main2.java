import java.util.Random;

public class Main2 {
    public static void main(String[] args) {
        LRUCache<Integer> cache = new SimplentLRUCache<>(5);
        for (int j = 0; j < 5; j++) {
            new Thread(() -> {
                Random r = new Random();
                while(true) {
                    int v = r.nextInt(10);
                    if (r.nextBoolean()) {
                        System.out.printf("add %d%n", v);
                        cache.add(v);
                    } else {
                        System.out.printf("get %d, result is %d%n", v, cache.get(v));
                    }
                }
            }
            ).start();
        }
    }
}

