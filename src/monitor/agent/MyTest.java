
package monitor.agent;
/**
 * 8960
 * @author yongkang.qiyk
 */
@Aop(value = "aaa")
public class MyTest {
	private static int sum ;
	private static int sum1 ;
    public static void main(String[] args) {
    	while(true){
	        sayHello();
	        
	        sayHello2("hello world");
	        System.out.println(sum + "   " + sum1);
    	}
    }
    
    public static void sayHello() {
        try {
            Thread.sleep(2000);
            System.out.println("hello world!!");
            sum ++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void sayHello2(String hello) {
        try {
            Thread.sleep(1000);
            sum1++;
            System.out.println(hello);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}