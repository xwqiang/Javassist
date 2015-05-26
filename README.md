# Javassist

采用javassist方式对方法执行时间做统计

运行方式：启动时增加javaagent=XXX 的方式进行运行

demo:
-javaagent:C:\Users\admin\Desktop\jars\MyAgent.jar 
-cp .;C:\Users\admin\Desktop\jars\Metrics.jar
-Dmonitor.config=C:\Users\admin\Desktop\jars\profile.txt
