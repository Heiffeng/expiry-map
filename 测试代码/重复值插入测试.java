import java.util.concurrent.TimeUnit;

public class 重复值插入测试 {

    public static void main(String[] args) throws InterruptedException {
        ExpiryMap<String,String> map = new ExpiryMap<>();
        map.put("haha","嘻嘻",3, TimeUnit.SECONDS); // 第一次失效时间为3秒
        map.put("haha","嘿嘿",5, TimeUnit.SECONDS); // 第二次失效时间为5秒
        Thread.sleep(3100);
        System.out.println(map.get("haha")); // 3秒后去获取值，能获取到。
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 5秒后再次获取，为null
    }
}
