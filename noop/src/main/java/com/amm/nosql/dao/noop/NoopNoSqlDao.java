package com.amm.nosql.dao.noop;

import java.util.*;
import com.amm.nosql.NoSqlException;
import com.amm.nosql.data.NoSqlEntity;
import com.amm.nosql.dao.NoSqlDao;

/**
 * Noop implementation - optionally will sleep a bit.
 * @author amesarovic
 */
public class NoopNoSqlDao<T extends NoSqlEntity> implements NoSqlDao<T> {
	private long millis ;

	public NoopNoSqlDao() {
	}

	public NoopNoSqlDao(long millis) {
		this.millis = millis ;
	}

	public T get(String id) throws Exception {
		sleep();
		return null;
	}

	public Map<String,T> getBulk(Collection<String> keys) throws Exception {
		sleep();
		return null;
	}

	public void put(T entity) throws Exception {
		sleep();
	}

	public void delete(String id) throws Exception {
		sleep();
	}

	private void sleep() {
		if (millis > 0) {	
			try {
	  			Thread.currentThread().sleep(millis);
			} catch (InterruptedException ignoreme) {
			}
		}
	}
}
