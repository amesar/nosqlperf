package com.amm.nosql.util.mongodb;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.ReadPreference;

/**
 * Spring config-friendly way to create Mongo DBCollection
 */
public class MongoConnection {
	private static final Logger logger = Logger.getLogger(MongoConnection.class);
	private final String databaseName ;
	private final String collectionName ;

	public MongoConnection(Mongo mongo, String databaseName, String collectionName) {
		this.mongo = mongo ;
		this.databaseName = databaseName ;
		this.collectionName = collectionName ;
		database = mongo.getDB(databaseName);
		collection = database.getCollection(collectionName);
		logger.info("mongo.getReadPreference="+mongo.getReadPreference());
	}

	private Mongo mongo; 
	public Mongo getMongo() { return mongo; }
	public void setMongo(Mongo mongo) { this.mongo = mongo; } 
 
	private DBCollection collection; 
	public DBCollection getCollection() { return collection; }
	public void setCollection(DBCollection collection) { this.collection = collection; } 
 
	private DB database; 
	public DB getDatabase() { return database; }
	public void setDatabase(DB database) { this.database = database; } 

	@Override 
	public String toString() {
		return
			"databaseName="+databaseName
			+" collectionName="+collectionName
			+" mongo=["+mongo+"]"
			+" mongo.readPreference="+mongo.getReadPreference()
			;
	}
}
