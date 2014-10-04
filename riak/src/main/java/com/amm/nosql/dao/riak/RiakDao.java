package com.amm.nosql.dao.riak;

import java.util.*;
import org.apache.log4j.Logger;

import com.amm.nosql.NoSqlException;
import com.amm.nosql.data.NoSqlEntity;
import com.amm.nosql.dao.NoSqlDao;
import com.amm.mapper.ObjectMapper;
import com.basho.riak.client.RiakClient;
import com.basho.riak.client.RiakObject;
import com.basho.riak.client.response.StoreResponse;
import com.basho.riak.client.response.FetchResponse;
import com.basho.riak.client.response.BucketResponse;
import com.basho.riak.client.response.HttpResponse;
import com.basho.riak.client.RiakBucketInfo;
import com.basho.riak.client.request.RequestMeta;

/**
 * Implementation uses Riak's HTTP client.
 * @author amesarovic
 */
public class RiakDao<T extends NoSqlEntity> implements NoSqlDao<T> {
	private static final Logger logger = Logger.getLogger(RiakDao.class);
	private ObjectMapper<T> entityMapper;
	private RiakClient riakClient ;
	private RequestMeta requestMeta;
	private String bucket ;
	private String url ;

	public RiakDao(String url, String bucket, ObjectMapper<T> entityMapper) {
		this.riakClient = new RiakClient(url);
		this.entityMapper = entityMapper;
		this.bucket = bucket;
		this.url = url;
		logger.debug("url="+url+" bucket="+bucket);
	}

	@SuppressWarnings("unchecked")
	public T get(String id) throws Exception {
		FetchResponse response = riakClient.fetch(bucket, id) ;
		int statusCode = response.getStatusCode();
		logger.debug("get: id="+id+" response="+format(response));
		//if (isError(statusCode))
			//throw new NoSqlException("Get for id="+id+" failed. HTTP StatusCode="+statusCode);
		RiakObject robj = response.getObject() ;
		if (robj == null)
			return null;
		T entity = entityMapper.toObject(robj.getValueAsBytes());
		entity.setKey(id);
		return entity;
	}

	public void put(T entity) throws Exception {
		String key = getKey(entity);
		logger.debug("put: id="+key);
		byte [] value = entityMapper.toBytes(entity);
		RiakObject robj = new RiakObject(riakClient, bucket, key, value) ;
		StoreResponse response = riakClient.store(robj);
		int statusCode = response.getStatusCode();
		if (isError(statusCode))
			throw new NoSqlException("Put for id="+key+" failed. HTTP StatusCode="+statusCode);
	}

	public void delete(String id) throws Exception {
		HttpResponse response = riakClient.delete(bucket,id);
		logger.debug("delete: id="+id+" response="+format(response));
		int statusCode = response.getStatusCode();
		if (isError(statusCode))
			throw new NoSqlException("Delete for id="+id+" failed. HTTP StatusCode="+statusCode);
	}

	public Map<String,T> getBulk(Collection<String> keys) throws Exception {
		throw new UnsupportedOperationException(); // TODO
	}

	private String getKey(T entity) {
		return entity.getKey().toString();
	}

	private boolean isError(int statusCode) {
		return statusCode > 299 ;
	}
	private String format(HttpResponse response) {
		return
			"[StatusCode="+ response.getStatusCode()
			+" isError="+response.isError()
			+"]"
			;
	}

	@Override 
	public String toString() {
		return
			"[class="+this.getClass().getName()
			+" bucket="+bucket
			+"]";
	}
}
