# expiry-map
 Key值可以失效的HashMap，LRU机制的HashMap


# 使用示例

## 简单测试

```java
    public static void main(String[] args) throws InterruptedException {
        ExpiryMap<String,String> map = new ExpiryMap<>();
        map.put("haha","嘻嘻",3, TimeUnit.SECONDS);
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 这次可以拿到值
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 2+2=4，大于3秒了，所以获取为null
    }
```

## 重复值插入测试

```java
    public static void main(String[] args) throws InterruptedException {
        ExpiryMap<String,String> map = new ExpiryMap<>();
        map.put("haha","嘻嘻",3, TimeUnit.SECONDS); // 第一次失效时间为3秒
        map.put("haha","嘿嘿",5, TimeUnit.SECONDS); // 第二次失效时间为5秒
        Thread.sleep(3100);
        System.out.println(map.get("haha")); // 3秒后去获取值，能获取到。
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 5秒后再次获取，为null
    }
```