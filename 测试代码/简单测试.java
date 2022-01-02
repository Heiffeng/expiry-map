import java.util.concurrent.TimeUnit;

public class 简单测试 {

    public static void main(String[] args) throws InterruptedException {
        ExpiryMap<String,String> map = new ExpiryMap<>();
        map.put("haha","嘻嘻",3, TimeUnit.SECONDS);
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 这次可以拿到值
        Thread.sleep(2000);
        System.out.println(map.get("haha")); // 2+2=4，大于3秒了，所以获取为null
    }

}
