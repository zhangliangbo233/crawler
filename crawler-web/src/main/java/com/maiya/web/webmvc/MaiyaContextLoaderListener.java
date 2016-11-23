package com.maiya.web.webmvc;

import com.maiya.common.constants.MaiyaCrawlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 重写ContextLoaderListener
 * Created by zhanglb on 2016/10/8.
 */
public class MaiyaContextLoaderListener extends ContextLoaderListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaiyaContextLoaderListener.class);

    @Override
    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            // The application context id is still set to its original default value
            // -> assign a more useful id based on available information
            String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
            if (idParam != null) {
                wac.setId(idParam);
            } else {
                // Generate default id...
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
                        ObjectUtils.getDisplayString(sc.getContextPath()));
            }
        }

        wac.setServletContext(sc);
        String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
        if (isExecuteTask()) {
            String dynamicXmlName = System.getProperty("line.separator") + "classpath:spring/spring_task.xml";
            configLocationParam = configLocationParam + dynamicXmlName;
        }

        if (configLocationParam != null) {
            wac.setConfigLocation(configLocationParam);
        }


        // The wac environment's #initPropertySources will be called in any case when the context
        // is refreshed; do it eagerly here to ensure servlet property sources are in place for
        // use in any post-processing or initialization that occurs below prior to #refresh
        ConfigurableEnvironment env = wac.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
        }

        customizeContext(sc, wac);
        wac.refresh();
    }

    /**
     * 是否执行定时任务
     *
     * @return
     */
    private boolean isExecuteTask() {
        boolean isExecuteTask = false;
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("/application.properties");
            Properties prop = new Properties();
            prop.load(in);
            String execute = prop.getProperty("is.execute.task").trim();
            if (execute.equals(MaiyaCrawlConstants.DEFAULT_YES_TRUE_FLAG)) {
                isExecuteTask = true;
            }
        } catch (IOException e) {
            LOGGER.error("加载配置文件出错", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        LOGGER.info("isExecuteTask is {}", isExecuteTask);
        return isExecuteTask;
    }


}
