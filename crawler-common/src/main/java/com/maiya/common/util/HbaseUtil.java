package com.maiya.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Cluster;
import org.apache.hadoop.hbase.rest.client.RemoteHTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author xiangdf
 */
public class HbaseUtil implements InitializingBean {

	public static final Logger LOG = LoggerFactory.getLogger(HbaseUtil.class);

	@Value("${hbase.ip}")
	private String hbaseIp;

	@Value("${hbase.port}")
	private Integer hbasePort;

	private Cluster cluster;

	public void putData(String table, String rowKey, String family, String qualifiers[], String data)
			throws IOException {

		Client client = new Client(cluster);
		RemoteHTable hTable = new RemoteHTable(client, table); // 表名

		Put put = new Put(Bytes.toBytes(rowKey)); // rowkey
		for (String qualifier : qualifiers) {
			// 列族 列名 插入的数据
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(data));
		}

		try {
			hTable.put(put);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			try {
				hTable.close();
			} catch (IOException e) {
				LOG.error("close RemoteHTable exception:" + e.getMessage());
			}
		}

	}

	public String getData(String table, String rowKey) {

		Client client = new Client(cluster);
		RemoteHTable hTable = new RemoteHTable(client, table); // 表名

		Get get = new Get(rowKey.getBytes());

		Result rs = null;
		try {
			rs = hTable.get(get);
		} catch (IOException e) {
			LOG.error("get data from hbase exception:" + e.getMessage());
		} finally {
			try {
				hTable.close();
			} catch (IOException e) {
				LOG.error("close RemoteHTable  exception:" + e.getMessage());
			}

		}

		StringBuffer sb = new StringBuffer();
		for (KeyValue kv : rs.raw()) {
			sb.append(kv.getKey() + ":" + kv.getValue() + ",");

		}

		return sb.toString();

	}

	public String getData(String table, String rowKey, String qualifier) throws IOException {

		Client client = new Client(cluster);
		RemoteHTable hTable = new RemoteHTable(client, table); // 表名

		// 查询单行记录
		try {
			Get get = new Get(rowKey.getBytes());
			Result rs = hTable.get(get);
			if (rs == null || rs.isEmpty()) {
				return null;
			}

			for (KeyValue kv : rs.raw()) {
				if (StringUtils.equals(Bytes.toString(kv.getQualifier()), qualifier)) {
					return Bytes.toString(kv.getValue());
				}
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			hTable.close();
		}
		return "";
	}

	/**
	 * 根据rowkey的前缀查询数据 
	 * @param table
	 * @param rowKeyPrefix
	 * @param qualifier
	 * @return
	 * @throws IOException
	 */
	public List<String> getDataByPrefix(String table, String rowKeyPrefix, String qualifier) throws IOException {

		List<String> resultList=new ArrayList<String>();
		Client client = new Client(cluster);
		RemoteHTable hTable = new RemoteHTable(client, table); // 表名
		Scan scan = new Scan();
		scan.setFilter(new PrefixFilter(rowKeyPrefix.getBytes()));
		try {
			ResultScanner scanner = hTable.getScanner(scan);
			for (Result rs : scanner) {

				for (KeyValue kv : rs.raw()) {
					if (StringUtils.equals(Bytes.toString(kv.getQualifier()), qualifier)) {
						resultList.add(Bytes.toString(kv.getValue()));
					}
				}
			}
			
			return resultList;
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			hTable.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		cluster = new Cluster();
		cluster.add(hbaseIp, hbasePort);
	}
}
