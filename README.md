
# Performance Framework for NoSQL key/value stores

Tool to execute performance tests for a variety on NoSQL key/value stores.

## Supported NoSQL providers
* cassandra - Both astyanax 1.56.34 and 1.1-3 hector client packages
* mongodb - Client driver 2.10.1
* couchbase - Client driver 1.1.8
* citrusleaf/aerospike - Aug. 2012
* hbase - 0.94.1
* oracle - Oracle's NoSQL store - 1.2.123
* redis - jedis client 2.1.0
* memcached - Uses standard spy.memcached client
* hashmap - Dummy in-memory hashmap implementation
* noop - Implementation does nothing at all

## Scripts

### vtest.sh 

Invokes the performance test tool.

Options:

  *   -r : number of requests
  *   -t : number of threads
  *   -i : number iterations for the test
  *   -p : provider per sub-directory in conf/
  *   -h : comma-separated list of server hosts

### shell.sh 

Shell to interactively manipulate items (put, get and delete).

## Running 

* First make changes in common.env which contains config settings for tests.

  Toggle two properties:
    * provider: NoSQL provider. Corresponds to the provider in conf/ - see above list. In the example below, running with "-p mongodb" will use the Spring configuration file mongodb/appContext-provider.xml.
    * hosts: URLs for server - one or more comma-delimited server names

* Run vtest.sh which produces metrics report as shown in the sample report below.  The default provider is the in-memory hashmap provider. 
```
    vtest.sh
    vtest.sh -r 100 -t 2 -i 5 -p mongodb put-get-update.job
    vtest.sh -r 100 -t 10 -i 1 -p cassandra put-get-update.task
```

## Sample reports

### Simple put/get/update 

```
    Test      Req/Sec    Total     Mean   50.0%   90.0%   99.0%   99.5%   99.9%     Max  Err  Fail
    Put       1439.21  5558608    6.943       7      11      13      13      19    8525    0     0
    Get       5522.51  1448616    1.798       2       2       5       8      17     703    0     0
    Update     983.83  8131446   10.159       7      11      86     126     283    8680    0     0
```

### Cassandra - testing get/update cycle

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

## Configuration
### Overview
* Tests are written in Java
* Configured and wired with Spring XML and set of externalized properties
* Two things to configure:
  * The vtest test (task/job) you want to run 
  * The provider-specific implementation of the KeyValueDao 

### Provider Configuration
* See conf/
* conf/appContext.xml imports provider-specific appContext-nosql.xml which is toggled by putting appropriate the directory into the classpath (common.env $provider)
* Each provider has a directory in conf containing:
  * appContext-nosql.xml - implementation of KeyValueDao
  * appContext-nosql.properties for above (optional)

### VTest Configuration
* See in conf/vtest/
* Files:
  * vtest.xml - core vtest bean configuration. Root app context file for vtest framework
  * vtest.properties - externalized properties for above
  * datagen.xml - Key and Value generator implementations
  * tasks-keyvalue.xml - Definitions of tasks (tests) to run
* Definitions:
  * task - a named test
  * job - a collection of tasks
