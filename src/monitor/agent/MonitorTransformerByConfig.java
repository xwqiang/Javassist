package monitor.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class MonitorTransformerByConfig implements ClassFileTransformer {

	final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
	final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";
	final static String split = ";";

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		MonitorConfig configure = new MonitorConfigImpl();
		String methodlist = configure.getStringValue("methodList", null);
		System.out.println(methodlist);
		className = className.replace("/", ".");
		CtClass ctclass = null;
		try {
			ctclass = ClassPool.getDefault().get(className);
			for (String method : methodlist.split(split)) {
				String methodName = method.substring(
						method.lastIndexOf('.') + 1, method.length());
				String outputStr = "\ncom.kuyun.shadowNet.matics.MatricsUtil.regist(\"time\", (endTime - startTime) );";
				String timeStr = "\ncom.kuyun.shadowNet.matics.core.Counter c = (com.kuyun.shadowNet.matics.core.Counter)com.kuyun.shadowNet.matics.MatricsUtil.get(\"times\");"
						+ "if (c == null ){ "
						+ "c = new com.kuyun.shadowNet.matics.core.Counter();"
						+ "c.name(\"times\");"
						+ " com.kuyun.shadowNet.matics.MatricsUtil.regist(\"times\",c);"
						+ "}";
				String timeStr1 = "\nc.inc();";
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

				bodyStr.append("}");
				// 替换新方法
				System.out.println(bodyStr.toString());
				newMethod.setBody(bodyStr.toString());
				// 增加新方法
				ctclass.addMethod(newMethod);
			}
			return ctclass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}