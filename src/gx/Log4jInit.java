package gx;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Ridiculous on 2016/8/24.
 */
public class Log4jInit extends HttpServlet {
    private static Logger logger = Logger.getLogger(Log4jInit.class);
    public Log4jInit() {
        super();
    }
    public void init(ServletConfig config) throws ServletException {
        String prefix = config.getServletContext().getRealPath("/");
        String file = config.getInitParameter("log4j");
        String filePath = prefix + file;
        //兼容windows和linux
        filePath = filePath.replace('/', File.separatorChar);

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(filePath));
            String logDFile = prefix + File.separator + "logs" + File.separator
                    + props.getProperty("log4j.appender.D.File");// 设置路径
            props.setProperty("log4j.appender.D.File", logDFile);
            String logIFile = prefix + File.separator + "logs" + File.separator
                    + props.getProperty("log4j.appender.I.File");// 设置路径
            props.setProperty("log4j.appender.I.File", logIFile);
            String logEFile = prefix + File.separator + "logs" + File.separator
                    + props.getProperty("log4j.appender.E.File");// 设置路径
            props.setProperty("log4j.appender.E.File", logEFile);
            PropertyConfigurator.configure(props);// 装入log4j配置信息
        } catch (IOException e) {
            logger.error("Could not read configuration file [" + filePath + "].");
            logger.error("Ignoring configuration file [" + filePath + "].");
            return;
        }
    }

}