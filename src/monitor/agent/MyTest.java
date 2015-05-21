
package monitor.agent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import monitor.annotation.Aop;

/**
 * 8960
 * @author yongkang.qiyk
 */
public class MyTest {
    public static void main(String[] args) throws Exception {
    	String day = "2014-02-23 23:22:12";
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = dateFormat.parse(day);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.MONTH,-1);
    	cal.getTime();
    	String dddd = day.substring(0, 7);
    	System.err.println(cal.getTime());
    	System.exit(0);
    	while(true){
	        sayHello();
	        
	        sayHello2("hello world");
    	}
    }
    @Aop(value = { "aa" })
    public static void sayHello() {
        try {
            Thread.sleep(2000);
            System.out.println("hello world!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Aop(value = { "aa" })
    public static void sayHello2(String hello) {
        try {
            Thread.sleep(1000);
            System.out.println(hello);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}