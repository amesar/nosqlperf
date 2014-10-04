package com.amm.nosql.dao.cassandra.datastax;

import com.amm.nosql.dao.KeyValueDao;
import com.amm.nosql.data.KeyValue;
import com.amm.mapper.ObjectMapper;

public class CassandraKeyValueDao extends CassandraDao<KeyValue> implements KeyValueDao {
	public CassandraKeyValueDao(String hostname, String keyspace, String columnFamily, ObjectMapper<KeyValue> objectMapper) throws Exception {
		super(hostname,keyspace,columnFamily,objectMapper);
	}
}
