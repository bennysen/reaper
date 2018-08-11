package org.cabbage.crawler.reaper.test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraTest {

	public Cluster cluster;

	public Session session;

	public void connect() {
		// addContactPoints:cassandra节点ip withPort:cassandra节点端口 默认9042
		// withCredentials:cassandra用户名密码
		// 如果cassandra.yaml里authenticator：AllowAllAuthenticator 可以不用配置
		// cluster = Cluster.builder().addContactPoints("192.168.80.101").withPort(9042)
		// .withCredentials("cassandra", "cassandra").build();
		cluster = Cluster.builder().addContactPoints("192.168.80.101").build();
		session = cluster.connect("reaper");
	}

	public void connect2() {
		PoolingOptions poolingOptions = new PoolingOptions();
		// 每个连接的最大请求数 2.0的驱动好像没有这个方法
		poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
		// 表示和集群里的机器至少有2个连接 最多有4个连接
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 2).setMaxConnectionsPerHost(HostDistance.LOCAL, 4)
				.setCoreConnectionsPerHost(HostDistance.REMOTE, 2).setMaxConnectionsPerHost(HostDistance.REMOTE, 4);

		// addContactPoints:cassandra节点ip withPort:cassandra节点端口 默认9042
		// withCredentials:cassandra用户名密码
		// 如果cassandra.yaml里authenticator：AllowAllAuthenticator 可以不用配置
		// cluster = Cluster.builder().addContactPoints("192.168.3.89").withPort(9042)
		// .withCredentials("cassandra",
		// "cassandra").withPoolingOptions(poolingOptions).build();
		cluster = Cluster.builder().addContactPoints("192.168.80.101").withPort(9042).build();
		// 建立连接
		// session = cluster.connect("test");连接已存在的键空间
		session = cluster.connect();

	}

	/**
	 * 创建键空间
	 */
	public void createKeyspace() {
		// 单数据中心 复制策略 ：1
		String cql = "CREATE KEYSPACE if not exists mydb WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}";
		session.execute(cql);
	}

	/**
	 * 创建表
	 */
	public void createTable() {
		// a,b为复合主键 a：分区键，b：集群键
		String cql = "CREATE TABLE if not exists mydb.test (a text,b int,c text,d int,PRIMARY KEY (a, b))";
		session.execute(cql);
	}

	/**
	 * 插入
	 */
	public void insert() {
		String cql = "INSERT INTO reaper.point (domain, url, perpoint,outpoint,  touchcount ) VALUES ( '163.com','http://news.163.com/',{'http://ent.163.com/','http://money.163.com/','http://www.163.com/'},{'http://news.163.com/18/0809/07/DOOJI7VR0001875P.html','http://news.163.com/18/0809/10/DOOR7KEN0001875P.html','http://news.163.com/18/0809/14/DOPB8FPA0001899N.html'},6);";
		session.execute(cql);
	}

	/**
	 * 修改
	 */
	public void update() {
		// a,b是复合主键 所以条件都要带上，少一个都会报错，而且update不能修改主键的值，这应该和cassandra的存储方式有关
		String cql = "UPDATE mydb.test SET d = 1234 WHERE a='aa' and b=2;";
		// 也可以这样 cassandra插入的数据如果主键已经存在，其实就是更新操作
		String cql2 = "INSERT INTO mydb.test (a,b,d) VALUES ( 'aa',2,1234);";
		// cql 和 cql2 的执行效果其实是一样的
		session.execute(cql);
	}

	/**
	 * 删除
	 */
	public void delete() {
		// 删除一条记录里的单个字段 只能删除非主键，且要带上主键条件
		String cql = "DELETE d FROM mydb.test WHERE a='aa' AND b=2;";
		// 删除一张表里的一条或多条记录 条件里必须带上分区键
		String cql2 = "DELETE FROM mydb.test WHERE a='aa';";
		session.execute(cql);
		session.execute(cql2);
	}

	/**
	 * 查询
	 */
	public void query() {
		String cql = "SELECT * FROM reaper.point;";

		ResultSet resultSet = session.execute(cql);
		System.out.print("这里是字段名：");
		for (Definition definition : resultSet.getColumnDefinitions()) {
			System.out.print(definition.getName() + " ");
		}
		System.out.println();
		for (Row row : resultSet) {
			System.out.println(row.getString("domain"));
		}

		session.close();
	}

	public static void main(String[] args) {
		CassandraTest ct = new CassandraTest();
		ct.connect();
//		ct.query();
		ct.session.execute(
				"update reaper.point set outpoint=outpoint+{'http://news.163.com/18/0809/14/DOPB0F4G0001899N.html'} where domain = '163.com'");
	}

}
