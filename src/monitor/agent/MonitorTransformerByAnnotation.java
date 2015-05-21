package monitor.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import monitor.annotation.Aop;

public class MonitorTransformerByAnnotation implements ClassFileTransformer {

	final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
	final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		className = className.replace("/", ".");
		CtClass ctclass = null;
		try {
			// 用于取得字节码类，必须在当前的classpath中，使用全称 ,这部分是关于javassist的知识
			ctclass = ClassPool.getDefault().get(className);
			CtMethod[] methods = ctclass.getMethods();
			for (CtMethod ctMethod : methods) {
				if (ctMethod.getAnnotation(Aop.class) != null) {
					// 获取方法名
					String methodName = ctMethod.getName();
					final String key_time = "time:" + ctclass.getName() + "."
							+ methodName;
					final String key_times = "times:" + ctclass.getName() + "."
							+ methodName;
					// String outputStr =
					// "\ncom.kuyun.shadowNet.matics.MatricsUtil.regist(\""+key_time+"\", (endTime - startTime) );";
					String outputStr = "\ncom.kuyun.shadowNet.matics.core.Gauges g = (com.kuyun.shadowNet.matics.core.Gauges)com.kuyun.shadowNet.matics.MatricsUtil.get(\""
							+ key_time
							+ "\");\n"
							+ "if (g == null ){ \n"
							+ "g = new com.kuyun.shadowNet.matics.core.Gauges();\n"
							+ "g.name(\""
							+ key_time
							+ "\");\n"
							+ "g.set(endTime - startTime);\n"

							+ " com.kuyun.shadowNet.matics.MatricsUtil.regist(\""
							+ key_time + "\",g);\n" 
							+ "}";
					String timeStr = "\ncom.kuyun.shadowNet.matics.core.Counter c = (com.kuyun.shadowNet.matics.core.Counter)com.kuyun.shadowNet.matics.MatricsUtil.get(\""
							+ key_times
							+ "\");\n"
							+ "if (c == null ){ \n"
							+ "c = new com.kuyun.shadowNet.matics.core.Counter();\n"
							+ "c.name(\""
							+ key_times
							+ "\");\n"
							+ " com.kuyun.shadowNet.matics.MatricsUtil.regist(\""
							+ key_times + "\",c);\n" + "}";
					String timeStr1 = "\nc.inc();\n";
					String timeStr2 = "\ncom.kuyun.shadowNet.matics.MatricsUtil.start();\n";
					// 得到这方法实例
					CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);
					// 新定义一个方法叫做比如sayHello$impl
					String newMethodName = methodName + "$impl";
					// 原来的方法改个名字
					ctmethod.setName(newMethodName);

					// 创建新的方法，复制原来的方法 ，名字为原来的名字
					CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName,
							ctclass, null);
					// 构建新的方法体
					StringBuilder bodyStr = new StringBuilder();
					bodyStr.append("{");
					bodyStr.append(prefix);
					// 调用原有代码，类似于method();($$)表示所有的参数
					bodyStr.append(newMethodName + "($$);\n");

					bodyStr.append(postfix);
					bodyStr.append(outputStr);
					bodyStr.append(timeStr);
					bodyStr.append(timeStr1);
					bodyStr.append(timeStr2);

					bodyStr.append("}");
					// 替换新方法
					System.out.println(bodyStr.toString());
					newMethod.setBody(bodyStr.toString());
					// 增加新方法
					ctclass.addMethod(newMethod);

				}
			}
			ctclass.writeFile("d:/usr/");
			return ctclass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}