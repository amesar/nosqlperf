package com.amm.nosql.dao.ehcacherest;

import com.amm.httpclient.ByteHttpClient ;
import com.amm.nosql.dao.KeyValueDao;
import com.amm.nosql.data.KeyValue;
import com.amm.mapper.ObjectMapper;

public class RestEhcacheKeyValueDao extends RestEhcacheDao<KeyValue> implements KeyValueDao {
	public RestEhcacheKeyValueDao(String url, ObjectMapper<KeyValue> entityMapper, ByteHttpClient byteHttpClient) {
		super(url, entityMapper, byteHttpClient) ;
	}
}
