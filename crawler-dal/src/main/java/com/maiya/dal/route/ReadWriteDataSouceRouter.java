package com.maiya.dal.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 读写分离操作
 * Created by zhanglb on 16/11/2016.
 */
@Component("dataSource")
public class ReadWriteDataSouceRouter extends AbstractRoutingDataSource {

    @Autowired
    private DataSource crawlReadMysqlDataSource,crawlWriteMysqlDataSource;

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("read", crawlReadMysqlDataSource);
        targetDataSources.put("write", crawlWriteMysqlDataSource);
        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(crawlWriteMysqlDataSource);
        super.afterPropertiesSet();
    }

    /**
     * 获取数据源
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSouceHolder.getDataSourceName();
    }

    /**
     *
     */
    public static class DataSouceHolder {

        static final ThreadLocal<String> holder = new ThreadLocal<>();

        public static void setDataSouceName(String dataSouceName) {
            holder.set(dataSouceName);
        }

        static String getDataSourceName() {
            return holder.get();
        }

    }
}
