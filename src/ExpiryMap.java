import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ExpiryMap<K,V> implements Map<K,V> {

    DelayQueue<Ele<K>> queue;

    ConcurrentHashMap<K,V> map;

    public ExpiryMap() {
        queue = new DelayQueue<>();
        map = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        removeExpire();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        removeExpire();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        removeExpire();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        removeExpire();
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        removeExpire();
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key,value);
    }

    // 带失效时间的put方法
    public V put(K key,V value,long expire,TimeUnit unit){
        Ele<K> ele = new Ele<>(key, System.currentTimeMillis() + unit.toMillis(expire));
        synchronized (this){
            if(queue.contains(ele)){
                queue.remove(ele);
            }
            queue.add(ele);
            return map.put(key,value);
        }
    }

    @Override
    public synchronized V remove(Object key) {
        Ele<K> eleKey = queue.stream().filter(ele -> ele.getKey().equals(key)).findFirst().orElse(null);
        if(eleKey!=null) queue.remove(eleKey);
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public synchronized void clear() {
        queue.clear();
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        removeExpire();
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        removeExpire();
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        removeExpire();
        return map.entrySet();
    }

    private synchronized void removeExpire(){
        Ele ele = null;
        while((ele = queue.poll()) != null){
            map.remove(ele.getKey());
        }
    }

    private class Ele<K> implements Delayed {

        private K key;
        private long expireTime;

        public Ele(K key, long expireTime) {
            this.key = key;
            this.expireTime = expireTime;
        }

        public K getKey() {
            return key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expireTime - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if(o == null) return 1;
            long otherDelay = o.getDelay(TimeUnit.MILLISECONDS);
            long thisDelay = this.getDelay(TimeUnit.MILLISECONDS);
            if(otherDelay > thisDelay){
                return -1;
            }else if(otherDelay < thisDelay){
                return 1;
            }else{
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ele<?> ele = (Ele<?>) o;
            return key.equals(ele.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
