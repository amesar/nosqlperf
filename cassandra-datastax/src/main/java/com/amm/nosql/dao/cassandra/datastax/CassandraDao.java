package com.amm.nosql.dao.cassandra.datastax;

import java.util.*;
import java.nio.ByteBuffer;
import com.amm.nosql.data.NoSqlEntity;
import com.amm.nosql.dao.NoSqlDao;
import com.amm.mapper.ObjectMapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster;

/**
 * Cassandra 2.0 implementation.
 * @author andre
 */
public class CassandraDao<T extends NoSqlEntity> implements NoSqlDao<T> {
	private final Cluster cluster ;
	private final Session session ;
	private final String columnFamily ;
	private ObjectMapper<T> objectMapper;

	public CassandraDao(String hostname, String keyspace, String columnFamily, ObjectMapper<T> objectMapper) {
		cluster = Cluster.builder()
			.addContactPoint(hostname)
			.build();
		this.session = cluster.connect(keyspace);
		this.columnFamily = columnFamily ;
		this.objectMapper = objectMapper;
		//debug("CTOR: keyspace="+keyspace+" columnFamily="+columnFamily);
	}

	public void put(T obj) throws Exception { 
		String key = getKey(obj);
		byte [] bvalue = objectMapper.toBytes(obj);
		ByteBuffer buffer = ByteBuffer.allocate(bvalue.length);
		buffer.put(bvalue);
		buffer.rewind();
		PreparedStatement stmt = session.prepare("INSERT INTO "+columnFamily+" (key, value)" + "VALUES (?,?);");
		BoundStatement bstmt = new BoundStatement(stmt);
		session.execute(bstmt.bind(key, buffer));
	}

	public T get(String key) throws Exception {
		Statement select = QueryBuilder.select().all().from(columnFamily).where(QueryBuilder.eq("key", key));

		ResultSet results = session.execute(select);
		if (results.isExhausted()) {
			return null;
		}

		Row row = results.one();
		ByteBuffer buffer = row.getBytes("value");
		byte [] bvalue = buffer.array();

		int len = buffer.limit() - buffer.position();
		byte [] bvalue2 = new byte[len];
		for (int j=0 ; j < len ; j++) {
			bvalue2[j] = bvalue[j+buffer.position()];
		}
		T obj = objectMapper.toObject(bvalue2);
		obj.setKey(key);
		return obj ;
	}

	public Map<String,T> getBulk(Collection<String> keys) throws Exception {
		Map<String,T> map = new HashMap<String,T>();
		for (String key : keys) {
			T obj = get(key);
			map.put(key,obj);
		}
		return map;
	}

	public void delete(String id) throws Exception {
		Statement delete = QueryBuilder.delete().from(columnFamily).where(QueryBuilder.eq("key", id));
		ResultSet results = session.execute(delete);
	}

	private String getKey(T obj) {
		return obj.getKey().toString();
	}

	@Override 
	public String toString() {
		return 
			"columnFamily="+columnFamily;
	}

}
