package org.cabbage.crawler.reaper.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;

public class SolrTest {

	final String solrUrl = "http://192.168.80.101:8983/solr/reaperSample";

	public void querySolr() throws Exception {
		// [1]获取连接
		// 创建solrClient同时指定超时时间，不指定走默认配置
		HttpSolrClient client = null;
		try {
			client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
			// [2]封装查询参数
			Map<String, String> queryParamMap = new HashMap<String, String>();
			queryParamMap.put("q", "*:*");
			// [3]添加到SolrParams对象
			MapSolrParams queryParams = new MapSolrParams(queryParamMap);
			// [4]执行查询返回QueryResponse
			QueryResponse response = client.query(queryParams);
			// [5]获取doc文档
			SolrDocumentList documents = response.getResults();
			// [6]内容遍历
			for (SolrDocument doc : documents) {
				System.out.println("id:" + doc.get("id") + "\tproduct_name:" + doc.get("product_name")
						+ "\tproduct_catalog_name:" + doc.get("product_catalog_name") + "\tproduct_number:"
						+ doc.get("product_number") + "\tproduct_price:" + doc.get("product_price")
						+ "\tproduct_picture:" + doc.get("product_picture"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != client) {
				client.close();
			}
		}
	}

	/**
	 * 2、使用 SolrParams 的子类 SolrQuery,它提供了一些方便的方法,极大地简化了查询操作。
	 * 
	 * @throws Exception
	 */
	public void querySolr2() throws Exception {
		// [1]获取连接
		// 创建solrClient同时指定超时时间，不指定走默认配置
		HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000)
				.withSocketTimeout(60000).build();
		// [2]封装查询参数
		SolrQuery query = new SolrQuery("*:*");
		// [3]添加需要回显得内容
		query.addField("id");
		query.addField("product_name");
		query.setRows(20);// 设置每页显示多少条
		// [4]执行查询返回QueryResponse
		QueryResponse response = client.query(query);
		// [5]获取doc文档
		SolrDocumentList documents = response.getResults();
		// [6]内容遍历
		for (SolrDocument doc : documents) {
			System.out.println("id:" + doc.get("id") + "\tproduct_name:" + doc.get("product_name") + "\tname:"
					+ doc.get("name") + "\tproduct_catalog_name:" + doc.get("product_catalog_name")
					+ "\tproduct_number:" + doc.get("product_number") + "\tproduct_price:" + doc.get("product_price")
					+ "\tproduct_picture:" + doc.get("product_picture"));
		}
		client.close();
	}

	public void solrAdd() throws Exception {
		// [1]获取连接
		// 创建solrClient同时指定超时时间，不指定走默认配置
		HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000)
				.withSocketTimeout(60000).build();
		// [2]创建文档doc
		SolrInputDocument doc = new SolrInputDocument();
		// [3]添加内容
		String str = UUID.randomUUID().toString();
		System.out.println(str);
		doc.addField("id", str);
		doc.addField("SPIDER_TITLE", "两广固废非法转移链条调查：水路提供便利，非法中介充当帮凶");
		doc.addField("SPIDER_DOMAIN", "new.qq.com");
		doc.addField("SPIDER_ROOTDOMAIN", "qq.com");
		doc.addField("SPIDER_DATASOURCE_TYPE", 1);
		doc.addField("SPIDER_DATASOURCE_TYPE_NEW", 1);
		doc.addField("SPIDER_NET_URL", "https://new.qq.com/omn/20180906/20180906A09CK4.html");
		doc.addField("createtime", System.currentTimeMillis()/1000);
		// [4]添加到client
		UpdateResponse updateResponse = client.add(doc);
		System.out.println(updateResponse.getElapsedTime());
		// [5] 索引文档必须commit
		client.commit();
	}

	public void solrDelete() throws Exception {
		// [1]获取连接
		// 创建solrClient同时指定超时时间，不指定走默认配置
		HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000)
				.withSocketTimeout(60000).build();
		// [2]通过id删除
		client.deleteById("30000");
		// [3]提交
		client.commit();
		// [4]关闭资源
		client.close();
	}

	public void solrDeleteList() throws Exception {
		// [1]获取连接
		// 创建solrClient同时指定超时时间，不指定走默认配置
		HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000)
				.withSocketTimeout(60000).build();
		// [2]通过id删除
		ArrayList<String> ids = new ArrayList<String>();
		ids.add("30000");
		ids.add("1");
		client.deleteById(ids);
		// [3]提交
		client.commit();
		// [4]关闭资源
		client.close();
	}

	public static void main(String[] args) throws Exception {
		SolrTest test = new SolrTest();
		test.solrAdd();

	}

}
