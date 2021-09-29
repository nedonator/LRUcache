import java.util.Random;

public class Main {
    public static void main(String[] args) {
        LRUCache<Integer> cache = new SimpleLRUCache<>(5, -1);
        Random r = new Random();
        for(int i = 0; i < 100; i++){
            int v = r.nextInt(10);
            if(r.nextBoolean()){
                System.out.printf("add %d%n", v);
                cache.add(v);
            }
            else{
                System.out.printf("get %d, result is %d%n", v, cache.get(v));
            }
            System.out.println(cache);
        }
    }
}
