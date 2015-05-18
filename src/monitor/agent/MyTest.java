
package monitor.agent;
/**
 * 
 * @author yongkang.qiyk
 */
@Aop(value = "aaa")
public class MyTest {
    public static void main(String[] args) {
    	
        sayHello();
        
        sayHello2("hello world");
    }
    
    public static void sayHello() {
        try {
            Thread.sleep(2000);
            System.out.println("hello world!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void sayHello2(String hello) {
        try {
            Thread.sleep(1000);
            System.out.println(hello);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}