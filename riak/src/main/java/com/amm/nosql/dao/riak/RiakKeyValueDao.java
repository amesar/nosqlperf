package com.amm.nosql.dao.riak;

import com.amm.nosql.dao.KeyValueDao;
import com.amm.nosql.data.KeyValue;
import com.amm.mapper.ObjectMapper;

public class RiakKeyValueDao extends RiakDao<KeyValue> implements KeyValueDao {
	public RiakKeyValueDao(String url, String bucket, ObjectMapper<KeyValue> objectMapper) throws Exception {
		super(url, bucket, objectMapper) ;
	}
}
