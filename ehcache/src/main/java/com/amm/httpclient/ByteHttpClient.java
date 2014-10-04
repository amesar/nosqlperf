package com.amm.httpclient;

import java.util.*;
import java.io.*;
import com.amm.nosql.NoSqlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.StatusLine;

/**
 * Byte array HTTP client.
 * @author amesar
 */
public class ByteHttpClient {
	private static final Log logger = LogFactory.getLog(ByteHttpClient.class);

	private MultiThreadedHttpConnectionManager httpConnectionManager;
	private HttpClient httpClient ;
	private static final int SOCKET_TIMEOUT = 5000;
	private static final int CONNECTION_TIMEOUT = 5000 ;
	private int socketTimeoutMillis = SOCKET_TIMEOUT;
	private int connectionTimeoutMillis = CONNECTION_TIMEOUT;
	private int maxTotalConnections = 50;
	private int maxConnectionsPerHost = 10;

	public ByteHttpClient() {
		init();
	}

	public ByteHttpClient(int socketTimeoutMillis, int connectionTimeoutMillis,
			int maxTotalConnections, int maxConnectionsPerHost) {
		this.socketTimeoutMillis = socketTimeoutMillis;
		this.connectionTimeoutMillis = connectionTimeoutMillis;
		this.maxTotalConnections = maxTotalConnections;
		this.maxConnectionsPerHost = maxConnectionsPerHost;
		init();
	}

	private void init() {
		httpConnectionManager = new MultiThreadedHttpConnectionManager();
		httpConnectionManager.getParams().setConnectionTimeout(connectionTimeoutMillis);
		httpConnectionManager.getParams().setSoTimeout(socketTimeoutMillis);
		httpConnectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
		//httpConnectionManager.getParams().setDefaultMaxPerRoute(
		//httpConnectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
		httpClient = new HttpClient(httpConnectionManager);
	}

	public byte [] get(String url) throws IOException {
		GetMethod method = new GetMethod(url);
		try {
			//logger.debug("url="+url);
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == 404)
				return null;
			if (isError(statusCode))
				throw new NoSqlException("GET failed for "+url+". HTTP StatusCode="+statusCode);
			InputStream stream = method.getResponseBodyAsStream();
			if (stream == null)
				throw new IOException("NoSqlException response for "+url);
			byte [] bytes = IOUtils.toByteArray(stream);
			//debug("GET: value="+new String(bytes));
			return bytes;
		} finally {
			method.releaseConnection();
		}
	}

	public void put(String url, byte [] value) throws IOException {
		logger.debug("put: url="+url);
		PutMethod method = new PutMethod(url);
		try {
			RequestEntity rentity = new ByteArrayRequestEntity(value);
			method.setRequestEntity(rentity);
			//logger.debug("url="+url);
			int statusCode = httpClient.executeMethod(method);
			checkStatus2(method, url, "PUT");
			//if (isError(statusCode))
				//throw new NoSqlException("PUT failed for "+url+". StatusLine: "+method.getStatusLine());
			InputStream stream = method.getResponseBodyAsStream();
			if (stream == null)
				throw new NoSqlException("Put for url="+url+" failed. HTTP StatusCode="+statusCode);
			byte [] bytes = IOUtils.toByteArray(stream);
		} finally {
			method.releaseConnection();
		}
	}

	public void delete(String url) throws IOException {
		DeleteMethod method = new DeleteMethod(url);
		try {
			int statusCode = httpClient.executeMethod(method);
			checkStatus(statusCode, url, "DELETE");
		} finally {
			method.releaseConnection();
		}
	}

	private boolean isError(int statusCode) {
		return statusCode < 200 || statusCode > 299 ;
	}

	private void checkStatus(int statusCode, String url, String method) {
		if (isError(statusCode))
			throw new NoSqlException(method+" failed for "+url+". HTTP StatusCode="+statusCode);
	}

	private void checkStatus2(HttpMethodBase method, String url, String methodName) {
		if (isError(method.getStatusCode()))
			throw new NoSqlException(methodName+" failed for "+url+". StatusLine: "+method.getStatusLine());
	}


	void debug(Object o) { System.out.println(">> ByteHttpClient: "+o) ; }
}
