package monitor.agent;
 
/**
 * 读取系统设置的参数，已经从参数中解析监测配置
 * @author yongkang.qiyk
 *
 */
public interface MonitorConfig {
    
    public String getStringValue(String key,String defaultValue);
    
    public boolean getBooleanValue(String key,boolean defautValue);
    
    public Integer getIntegerValue(String key,Integer defaultValue);
}