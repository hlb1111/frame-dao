package com.hu.wxky.frame.dao.multi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.CollectionUtils;
/**
 * 
 * @author hulb
 *
 */
public class MultiDataSource extends AbstractDataSource implements InitializingBean {

	private DataSource writeDataSource;
    private Map<String, DataSource> readDataSourceMap;
    
    
    private String[] readDataSourceNames;
    private DataSource[] readDataSources;
    private int readDataSourceCount;
    /**
     * 设置读库（name, DataSource）
     * @param readDataSourceMap
     */
    public void setReadDataSourceMap(Map<String, DataSource> readDataSourceMap) {
        this.readDataSourceMap = readDataSourceMap;
    }
    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }
    
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if(writeDataSource == null) {
            logger.fatal("property 'writeDataSource' is required");
            throw new IllegalArgumentException("property 'writeDataSource' is required");
        }
        if(CollectionUtils.isEmpty(readDataSourceMap)) {
            logger.fatal("property 'readDataSourceMap' is required");
            throw new IllegalArgumentException("property 'readDataSourceMap' is required");
        }
        readDataSourceCount = readDataSourceMap.size();
        
        readDataSources = new DataSource[readDataSourceCount];
        readDataSourceNames = new String[readDataSourceCount];
        
        int i = 0;
        for(Entry<String, DataSource> e : readDataSourceMap.entrySet()) {
            readDataSources[i] = e.getValue();
            readDataSourceNames[i] = e.getKey();
            i++;
        }
        
        
    }
    
    
    private DataSource determineDataSource() {
        if(MultiDataSourceDecision.isChoiceWrite()) {
            //log.trace("current determine write datasource");
            return writeDataSource;
        }
        
        /*if(MultiDataSourceDecision.isChoiceNone()) {
            log.trace("no choice read/write, default determine write datasource");
            return writeDataSource;
        } */
        return determineReadDataSource();
    }
    
    private DataSource determineReadDataSource() {
        int index = (int)Thread.currentThread().getId() % readDataSourceCount;
        if(index < 0) {
            index = - index;
        }
        //String dataSourceName = readDataSourceNames[index];
        //log.trace("current determine read datasource : {"+dataSourceName+"}");
        return readDataSources[index];
    }
    
    @Override
    public Connection getConnection() throws SQLException {
    	Connection conn = determineDataSource().getConnection();
    	if(!MultiDataSourceDecision.isChoiceWrite()){
    		conn.setReadOnly(true);
    	}
        return conn;
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }

}
