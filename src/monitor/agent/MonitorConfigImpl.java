package monitor.agent;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
/**
 * TODO Comment of MonitorConfigImpl
 * @author yongkang.qiyk
 *
 */
public class MonitorConfigImpl implements MonitorConfig {
//	-Dmonitor.config=C:\Users\admin\Desktop\jars\profile.txt
    private String MONITOR_CONF = "monitor.config";
    private Properties prop   = null;
    
    public MonitorConfigImpl() {
        prop = System.getProperties();
        String monitorConf = prop.getProperty(MONITOR_CONF);
        File monitorFile = new File(monitorConf);
        try {
			InputStream inputStream = new FileInputStream(monitorFile);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    @Override
    public String getStringValue(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
 
    @Override
    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = prop.getProperty(key, null);
        
        return (null!=value) ? Boolean.valueOf(value) : defaultValue;
    }
 
    @Override
    public Integer getIntegerValue(String key, Integer defaultValue) {
        String value = prop.getProperty(key, null);
        return (null!=value) ? Integer.valueOf(value) : defaultValue;
    }
 
}