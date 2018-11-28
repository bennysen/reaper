package org.cabbage.crawler.reaper.worker.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * Cassandra 操作工具类
 * 
 * https://www.cnblogs.com/youzhibing/p/6607082.html 基本操作
 * https://zhaoyanblog.com/archives/499.html 索引查询和排序
 * http://www.cnblogs.com/youzhibing/p/6617986.html 高级操作之索引、排序以及分页
 * 
 * @author wkshen
 *
 */
public class CassandraUtils {

	public Cluster cluster;

	public Session session;

	private static String host;
	private static Integer port;
	private static String username;
	private static String password;
	private static String keyspace;

	public static String getKeyspace() {
		return keyspace;
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param keyspace
	 */
	public void connect(String host, int port, String username, String password, String keyspace) {
		// addContactPoints:cassandra节点ip withPort:cassandra节点端口 默认9042
		// withCredentials:cassandra用户名密码
		// 如果cassandra.yaml里authenticator：AllowAllAuthenticator 可以不用配置
		cluster = Cluster.builder().addContactPoints(host).withPort(port).withCredentials(username, password).build();
		session = cluster.connect(keyspace);
	}

	/**
	 * 
	 * @throws ReaperException
	 */
	public void connect() throws ReaperException {
		if (null == host || null == port || null == username || null == password || null == keyspace) {
			host = Configure.getInstance(false).getProperty("cassandraHost");
			port = Configure.getInstance(false).getPropertyInteger("cassandraPort");
			username = Configure.getInstance(false).getProperty("cassandraUsername");
			password = Configure.getInstance(false).getProperty("cassandraPassword");
			keyspace = Configure.getInstance(false).getProperty("cassandraKeySpace");
		}
		try {
			cluster = Cluster.builder().addContactPoints(host).withPort(port).withCredentials(username, password)
					.build();
			session = cluster.connect(keyspace);
		} catch (Exception e) {
			throw new ReaperException("Connect to cassandra error!", e);
		}
	}

	public void close() {
		if (null == session) {

		}
		session.close();
		if (null == cluster) {

		}
		cluster.close();
	}

	/**
	 * 执行
	 */
	public void query() {
		ResultSet rs = session.execute(QueryBuilder.select("outlinks", "producelinks").from("reaper", "page_mark")
				.where(QueryBuilder.eq("domain", "qq.com")).and(QueryBuilder.eq("url", "http://news.qq.com/")));
		Iterator<Row> rsIterator = rs.iterator();
		while (rsIterator.hasNext()) {
			Row row = rsIterator.next();
			Set<String> outlinks = row.getSet("outlinks", String.class);
			if (null == outlinks || outlinks.size() == 0) {
				System.err.println("Get nothing!");
			} else {
				int i = 0;
				for (String outlink : outlinks) {
					System.out.println(outlink);
					if (i > 5) {
						break;
					}
				}
			}
			System.err.println("========================");
		}
	}

	/**
	 * 执行
	 */
	public void insert(Insert insert) {
		session.execute(insert);
	}

	public static void main(String[] args) throws ReaperException {
		CassandraUtils ct = new CassandraUtils();
		Set<String> outlinks = new HashSet<String>();
		outlinks.add("https://new.qq.com/omn/20181102A1Y83200");
		outlinks.add("https://new.qq.com/omn/20181102/20181102A241JM.html");
		outlinks.add("https://new.qq.com/omn/20181103/20181103A040WC.html");
		Set<String> producelinks = new HashSet<String>();
		producelinks.add("https://new.qq.com/omn/20181102/20181102A241JM.html");
		producelinks.add("https://new.qq.com/omn/20181103/20181103A040WC.html");
		try {
			ct.connect();
			ct.query();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ct.close();
		}
	}

}
