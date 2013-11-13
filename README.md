
# Performance tests for key/value NoSQL stores

Tool to execute performance tests for key/value NoSQL stores.

## Supported providers
* cassandra - Both astyanax 1.56.34 and 1.1-3 hector client packages
* mongodb - Client driver 2.10.1
* couchbase - Client driver 1.1.8
* citrusleaf
* hbase - 0.94.1
* oracle - Oracle's NoSQL store - 1.2.123
* redis - jedis client 2.1.0
* memcached - Uses standard spy.memcached client
* hashmap - Dummy in-memory hashmap implementation
* noop - Implementation does nothing at all

## Scripts
* vtest.sh - Invokes the performance test

  Options:
  *   -r : number of requests
  *   -t : number of threads
  *   -i : number iterations for the test
  *   -p : provider dir in conf/
  *   -h : comma-separated list of server hosts
* shell.sh - Shell to interactively manipulate items (put, get and delete)

## Running 

1) First make changes in common.env which contains config settings for tests.

  Toggle two properties:

    provider: NoSQL provider. Corresponds to the provider in conf/ - see above list
    hosts: URLs for server - one or more comma-delimited server names

2) Run vtest.sh and you well get a standard metrics report as shown in the sample below.

3) The default settings will run the hashmap provider. See vtest.sh for argument documentation.

    vtest.sh
    vtest.sh -r 100 -t 2 -i 5 -p cassandra get.task
    vtest.sh -r 100 -t 2 -i 5 -p mongodb put-get-update.job

## Sample report

    Test      Req/Sec    Total     Mean   50.0%   90.0%   99.0%   99.5%   99.9%     Max  Err  Fail
    Put       1439.21  5558608    6.943       7      11      13      13      19    8525    0     0
    Get       5522.51  1448616    1.798       2       2       5       8      17     703    0     0
    Update     983.83  8131446   10.159       7      11      86     126     283    8680    0     0

## Configuration
* Tests are written in Java
* Configured and wired with Spring XML and set of externalized properties
* Two things to configure:
  * The vtest test (task/job) you want to run 
  * The provider-specific implementation of the KeyValueDao 

### Provider Configuration
* See conf/
* conf/appContext.xml imports provider-specific appContext-nosql.xml which is toggled by putting appropriate directory into classpath (common.env $provider)
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
