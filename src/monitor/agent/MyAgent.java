package monitor.agent;


import java.lang.instrument.Instrumentation;
 
public class MyAgent {
   
    public static void premain(String agentArgs, Instrumentation inst){
        inst.addTransformer(new MonitorTransformerByAnnotation());
    }
  
}