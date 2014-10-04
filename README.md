
# Performance framework for NoSQL key/value stores

Tool to execute performance tests for a variety of NoSQL key/value stores.

The tool uses the [vtest](http://amesar.wordpress.com/2010/04/12/vtest-testing-framework/) testing framework.

Note that most of this work was done between 2011 and 2013.

## Supported NoSQL providers

Each provider is located in its own module.

* cassandra:
	* cassandra-datastax - Datastax Cassandra driver 2.1.1
	* cassandra-astyanax - Netflix's astyanax 1.56.34 
	* cassandra-hector - Hector 1.1-3 
* mongodb - Client driver 2.11.4
* couchbase - Client driver 1.1.8
* aerospike - 3.0.29 - (Aug. 2012 server)
* hbase - 0.99.0
* oracle-nosql - Oracle's NoSQL store - kvclient_3.0.5
* redis - jedis client 2.1.0
* riak - riak-client 1.1.4 (Oct. 2012)
* memcached - spymemcached 2.11.4 
* ehcache - echache REST - 2.8.4
* hashmap - Dummy in-memory hashmap implementation
* noop - Implementation does nothing at all


## Building and Installing

The project is implemented as a multi-module maven project.

The core module contains common shared logic.
Other modules correspond to provider implementations.

To build the project:
```
mvn clean install
```

Each module also has distribution tarball containing all jars and files needed to run the module without maven.
For example:
```
core/target/nosqlperf-distribution.tar.gz
cassandra-datastax/target/cassandra-datastax-distribution.tar.gz
mongodb/target/mongodb-distribution.tar.gz
hashmap/target/hashmap-distribution.tar.gz
```

## Running 

### common.env

common.env contains settings shared between all scripts. You can either change this file or use script options to override
the default values.

Key properties to toggle per environment:

* provider: NoSQL provider. Corresponds to the provider module. In the example below, running with "-p mongodb" will use the Spring configuration file mongodb/conf/appContext-provider.xml.
* hosts: URLs for server - one or more comma-delimited server names. Default is localhost.


### vtest.sh

Invokes the performance test tool.

Options:

  *   -r : number of requests
  *   -t : number of threads
  *   -i : number iterations for the test
  *   -p : provider (module name)
  *   -h : comma-separated list of server hosts

Instructions

* vtest.sh produces metrics report as shown in the sample report below.  The default provider is the in-memory hashmap provider. 
```
    vtest.sh
    vtest.sh -r 100 -t 2 -i 5 -p mongodb put-get-update.job
    vtest.sh -r 100 -t 10 -i 1 -p cassandra put-get-update.task
```

#### Sample reports

##### Simple put/get/update 

```
    Test      Req/Sec    Total     Mean   50.0%   90.0%   99.0%   99.5%   99.9%     Max  Err  Fail
    Put       1439.21  5558608    6.943       7      11      13      13      19    8525    0     0
    Get       5522.51  1448616    1.798       2       2       5       8      17     703    0     0
    Update     983.83  8131446   10.159       7      11      86     126     283    8680    0     0
```

##### Cassandra - advanced get/update cycle

Testing how get-s operate after a series of updates. Notice the mean get starts at 5.8, drops to 17.5 after the first batch of updates,
dropts to 11.9 and ends at 32.6.

Key parameters:

* Number requests per 5,000,000 test - 40,000,000 total
* 10 threads
* key size: 20 bytes
* Mean value size: 10,000 bytes
* Value size variation: 10% - randomly distributed between 9,000 and 11,000 bytes
* All keys and values are randomly generated
* Cluster: 3 nodes
* Hardware (physical machine):
	* RAM: 24 GB
	* Intel Xeon CPU E5645 @ 2.40GHz - 24 processors

```
Test      Req/Sec     Total     Mean   50.0%   90.0%   99.0%   99.5%   99.9%     Max  Err  Fail
Put       2896.13   1726443    3.449       3       6       8       8      12    5918    0     0
Get0      1710.66   2922842    5.842       1       8      98     145     283    6076    0     0
Update1   2534.60   1972696    3.942       3       6       8       8      12   49424    0     0
Get1       572.19   8738376   17.473       1      49     220     286     582    8947    0     0
Update2   2529.22   1976892    3.948       3       6       8       8      12   41970    0     0
Get2       841.50   5941766   11.873       1      27     185     243     568    9318    0     0
PutOrGet   299.51  16693792   33.384       6      85     256     343     683  114062    0     0
Get3       306.77  16298600   32.594      20      74     207     229     363   14583    0     0
```

### shell.sh

Invokes a simple shell to interactively manipulate items (put, get and delete).

Options:

  *   -p : provider (module name)
  *   -h : comma-separated list of server hosts


Sample session:
```
+----- Key Value Shell -------------------------------------+
|  ?               - Help                                   |
|  get KEY         - get KEY                                |
|  delete KEY      - delete KEY                             |
|  put KEY VALUE   - put KEY VALUE                          |
|  putf KEY FILE   - put KEY FILE contents                  |
|  status          - status                                 |
|  q               - Quit shell                             |
+-----------------------------------------------------------+
Status:
  keyValueDao.class: com.amm.nosql.dao.hashmap.HashMapKeyValueDao
  keyValueDao:       cache.size=0 cache.class=java.util.Collections$SynchronizedMap
  maxToDisplay:      100

>> put foo nosql_rocks
put foo nosql_rocks

>> get foo
get foo
KeyValue:
  key=foo
  keySize=3 valueSize=11
  value=nosql_rocks

>> get bar
get bar
KeyValue: null

```

## Configuration
### Overview
* Tests are written in Java.
* Configured and wired with Spring XML and set of externalized properties.
* Two things to configure:
  * The vtest test task you want to run.
  * The provider implementation of the KeyValueDao (e.g. mongo, cassandra).

### Provider Configuration
* The directory [core/conf](core/conf) contains configuration for the core module.
  * appContext.xml is the root Spring configuration file. It import appContext-nosql.xml.
  * appContext-nosql.xml - imports appContext-provider.xml which is determined by the appropriate provider directory in the classpath.

* Each provider module also has a conf directory containing:
  * appContext-provider.xml - provider-specific configuration of KeyValueDao
  * provider.properties 

For example, here's what the MongoDB configuration looks like:

  * [mongodb/conf/appContext-provider.xml](mongodb/conf/appContext-provider.xml)
  * [mongodb/conf/provider.properties](mongodb/conf/provider.properties)

### VTest Configuration
* [core/conf/vtest](core/conf/vtest):
  * [vtest.xml](core/conf/vtest/vtest.xml) - core vtest bean configuration. Root application context file for vtest framework.
  * [vtest.properties](core/conf/vtest/vtest.properties) - externalized properties for above.
  * [datagen.xml](core/conf/vtest/datagen.xml) - Key and Value generator implementations.
  * [tasks-keyvalue.xml](core/conf/vtest/tasks-keyvalue.xml) - Definitions of tasks (tests) to run.
* Definitions:
  * task - a named test.
  * job - a collection of tasks.

## Provider Shell Samples

### MongoDB 

The database and collection are defined in properties found in [mongodb/conf/provider.properties](mongodb/conf/provider.properties).
```
  mongodb.database=nosql
  mongodb.collection.keyValue=kv
```

You do not have to manually create the the database and collection since the MongoDB driver will automatically create them when you run code to insert new items.

Inside the mongo shell:
```
> use nosql;

> db.kv.count();
1000000

> db.kv.find().limit(2);
{ "_id" : "NXcJUSxWhjZtEFqSRQXF", "value" : BinData(0,"emFXQ0xq...eHdCUg==") }
{ "_id" : "gylYclzdQyopewDZjlIv", "value" : BinData(0,"bE5tRGFF...eEVEcXo=") }

```

### Cassandra

From the cqlsh.sh.

```
select key from kv limit 10;

 key
----------------------
 CpGsMGSGkSEmeaXzloif
 XxIqEnhhVWwUmJLakMii
 ZToBrfOOiAPECQHcpolF
 QFHBmqlMiWgaeCQPlewK
 eodunAkGSUJxlKjODkUS
 pBgHeFhyWVycwNHtouaX
 eSgAthydojUUZFISErmH
 sHWShKFWYEHRLrrCfuve
 OhoxERPoeKMMOGjGjqNw
 LPDfDHKekRZVJKUvtMuO
```

select key,value from kv limit 3;
```
 key                  | value
----------------------+-------------------------------------------

 CpGsMGSGkSEmeaXzloif | 0x715a4a746c4a725773594d7777416a4255657061
 XxIqEnhhVWwUmJLakMii | 0x554b6c68767046424b594f6f79706649616e434a
 ZToBrfOOiAPECQHcpolF | 0x42596d4b6c45576c4a55616d767563637879416e
```

## TODOS

* Rewrite the shell using [jline](http://jline.sourceforge.net/).
* Create a provider for the new official [Cassandra Java Driver](http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/whatsNew2.html).

