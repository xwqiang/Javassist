package monitor.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class MonitorTransformer implements ClassFileTransformer {
   
    final  static String prefix ="\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix ="\nlong endTime = System.currentTimeMillis();\n";
//    final static List<String> methodList =new ArrayList<String>();
//    static{
//        methodList.add("monitor.agent.MyTest.sayHello");
//        methodList.add("monitor.agent.MyTest.sayHello2");
//    }
 
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,byte[] classfileBuffer) throws IllegalClassFormatException {
        //先判断下现在加载的class的包路径是不是需要监控的类，通过instrumentation进来的class路径用‘/’分割
//    	if(classBeingRedefined.getAnnotation(Aop.class) == null){
//    		return null;
//    	}
    	MonitorConfig configure = new MonitorConfigImpl();
    	String methodlist = configure.getStringValue("methodList",null);
    	System.out.println(methodlist);
        if(className.startsWith("monitor/agent")){
            className = className.replace("/",".");
            CtClass ctclass = null;
            try {
                //用于取得字节码类，必须在当前的classpath中，使用全称 ,这部分是关于javassist的知识
                ctclass = ClassPool.getDefault().get(className);
                for(String method :methodlist.split(";")){
                	
		               if( ctclass.getAnnotation(Moitor.class) == null){
		            	   CtMethod cmm = ctclass.getDeclaredMethod(method);
		            	   if(cmm.getAnnotation(Aop.class) != null){

		                		//获取方法名
		                        String methodName = method.substring(method.lastIndexOf('.')+1, method.length());
		                        //metric --> registry
//		                        String outputStr ="\nSystem.out.println(\"this method "+methodName+" cost:\" +(endTime - startTime) +\"ms.\");";
		                        String outputStr ="\ncom.kuyun.shadowNet.matics.MatricsUtil.regist(\"time\", (endTime - startTime) );";
		                        String timeStr ="\ncom.kuyun.shadowNet.matics.core.Counter c = (com.kuyun.shadowNet.matics.core.Counter)com.kuyun.shadowNet.matics.MatricsUtil.get(\"times\");" +
		                        		"if (c == null ){ " +
			                        		"c = new com.kuyun.shadowNet.matics.core.Counter();" +
			                        		"c.name(\"times\");" +
			                        		" com.kuyun.shadowNet.matics.MatricsUtil.regist(\"times\",c);"+
		                        		"}"  ;
		                        String timeStr1 ="\nc.inc();";
		                        //得到这方法实例
		                        
		                        CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);
		                        //新定义一个方法叫做比如sayHello$impl 
		                        String newMethodName = methodName +"$impl";
		                     //原来的方法改个名字 
		                        ctmethod.setName(newMethodName);
		                       
		                      //创建新的方法，复制原来的方法 ，名字为原来的名字
		                        CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctclass,null);
		                        //构建新的方法体
		                        StringBuilder bodyStr =new StringBuilder();
		                        bodyStr.append("{");
		                        bodyStr.append(prefix); 
		                        //调用原有代码，类似于method();($$)表示所有的参数 
		                        bodyStr.append(newMethodName +"($$);\n"); 
		                 
		                        bodyStr.append(postfix);
		                        bodyStr.append(outputStr);
		                        bodyStr.append(timeStr);
		                        bodyStr.append(timeStr1);
		                 
		                        bodyStr.append("}"); 
		                        //替换新方法 
		                        System.out.println(bodyStr.toString());
		                        newMethod.setBody(bodyStr.toString());
		                        //增加新方法 
		                        ctclass.addMethod(newMethod); 
		                
		            	   }
		               }
                }
            //循环一下，看看哪些方法需要加时间监测
//            for(String method :methodlist.split(";")){
//                if (method.equals(className)){
//                		//获取方法名
//                        String methodName = method.substring(method.lastIndexOf('.')+1, method.length());
//                        //metric --> registry
////                        String outputStr ="\nSystem.out.println(\"this method "+methodName+" cost:\" +(endTime - startTime) +\"ms.\");";
//                        String outputStr ="\ncom.kuyun.shadowNet.matics.MatricsUtil.regist(\"time\", (endTime - startTime) );";
//                        String timeStr ="\ncom.kuyun.shadowNet.matics.core.Counter c = (com.kuyun.shadowNet.matics.core.Counter)com.kuyun.shadowNet.matics.MatricsUtil.get(\"times\");" +
//                        		"if (c == null ){ " +
//	                        		"c = new com.kuyun.shadowNet.matics.core.Counter();" +
//	                        		"c.name(\"times\");" +
//	                        		" com.kuyun.shadowNet.matics.MatricsUtil.regist(\"times\",c);"+
//                        		"}"  ;
//                        String timeStr1 ="\nc.inc();";
//                        //得到这方法实例
//                        
//                        CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);
//                        //新定义一个方法叫做比如sayHello$impl 
//                        String newMethodName = methodName +"$impl";
//                     //原来的方法改个名字 
//                        ctmethod.setName(newMethodName);
//                       
//                      //创建新的方法，复制原来的方法 ，名字为原来的名字
//                        CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctclass,null);
//                        //构建新的方法体
//                        StringBuilder bodyStr =new StringBuilder();
//                        bodyStr.append("{");
//                        bodyStr.append(prefix); 
//                        //调用原有代码，类似于method();($$)表示所有的参数 
//                        bodyStr.append(newMethodName +"($$);\n"); 
//                 
//                        bodyStr.append(postfix);
//                        bodyStr.append(outputStr);
//                        bodyStr.append(timeStr);
//                        bodyStr.append(timeStr1);
//                 
//                        bodyStr.append("}"); 
//                        //替换新方法 
//                        System.out.println(bodyStr.toString());
//                        newMethod.setBody(bodyStr.toString());
//                        //增加新方法 
//                        ctclass.addMethod(newMethod); 
//                }
//            }    
                return ctclass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return null;
    }
 
}