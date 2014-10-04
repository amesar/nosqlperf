package com.amm.nosql.dao.noop;

import com.amm.nosql.dao.KeyValueDao;
import com.amm.nosql.data.KeyValue;

public class NoopKeyValueDao extends NoopNoSqlDao<KeyValue> implements KeyValueDao {
	public NoopKeyValueDao() throws Exception {
	}
}
