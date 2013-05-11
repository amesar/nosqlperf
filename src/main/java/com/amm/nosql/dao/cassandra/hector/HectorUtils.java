package com.amm.nosql.dao.cassandra.hector;

import me.prettyprint.hector.api.beans.HColumn;

/**
 * Hector utilities.
 * @author andre
 */
public class HectorUtils {

	public static String format(HColumn column) {
		if (column==null) return null;
		return 
			"[Name="+column.getName()
			+ " Clock="+ column.getClock()
			+ " Ttl="+ column.getTtl()
			+ "]"
			;
	}
	
}
