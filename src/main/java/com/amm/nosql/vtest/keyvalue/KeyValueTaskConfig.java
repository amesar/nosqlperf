package com.amm.nosql.vtest.keyvalue;

import java.util.*;
import org.apache.log4j.Logger;
import com.amm.nosql.vtest.NoSqlTaskConfig;
import com.amm.nosql.dao.KeyValueDao;

public class KeyValueTaskConfig extends NoSqlTaskConfig {
	private KeyValueDao keyValueDao ;

	public KeyValueTaskConfig(KeyValueDao keyValueDao) throws Exception {
		this.keyValueDao = keyValueDao ;
	}
	public KeyValueDao getKeyValueDao() { return keyValueDao ; }
}
