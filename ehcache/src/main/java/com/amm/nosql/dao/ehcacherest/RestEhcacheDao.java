package com.amm.nosql.dao.ehcacherest;

import java.util.*;
import com.amm.nosql.NoSqlException;
import com.amm.nosql.data.NoSqlEntity;
import com.amm.nosql.dao.NoSqlDao;
import com.amm.mapper.ObjectMapper;
import com.amm.httpclient.ByteHttpClient;

/**
 * Implementation uses RestEhcache's HTTP client.
 * @author amesarovic
 */
public class RestEhcacheDao<T extends NoSqlEntity> implements NoSqlDao<T> {
	//private static final Log logger = LogFactory.getLog(RestEhcacheDao.class);
	private ObjectMapper<T> objectMapper;
	private String baseUrl ;
	private ByteHttpClient byteHttpClient ;

	public RestEhcacheDao(String baseUrl, ObjectMapper<T> objectMapper, ByteHttpClient byteHttpClient) {
		this.objectMapper = objectMapper;
		this.byteHttpClient = byteHttpClient;
		this.baseUrl = baseUrl;
		//logger.debug("baseUrl="+baseUrl);
	}

	@SuppressWarnings("unchecked")
	public T get(String key) throws Exception {
		String url = makeUrl(key);
		byte value [] = byteHttpClient.get(url);
		if (value == null)
			return null;
		T object = objectMapper.toObject(value);
		object.setKey(key);
		return object;
	}

	public void put(T object) throws Exception {
		String key = getKey(object);
		//logger.debug("put: key="+key);
		String url = makeUrl(key);
		byte [] request = objectMapper.toBytes(object);
		byteHttpClient.put(url,request);
	}

	public void delete(String key) throws Exception {
		String url = makeUrl(key);
		byteHttpClient.delete(url);
	}

	public Map<String,T> getBulk(Collection<String> keys) throws Exception {
		throw new UnsupportedOperationException(); // TODO
	}

	private String makeUrl(String key) {
		return baseUrl + "/" + key;
	}

	private String getKey(T object) {
		return object.getKey().toString();
	}

	@Override 
	public String toString() {
		return
			"[class="+this.getClass().getName()
			+" objectMapper="+objectMapper
			+"]";
	}
}
