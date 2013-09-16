
#*******************************************************
#
# vtest.sh - Run script for vtest 
#
# Examples:
#   vtest.sh -r 100 -t 2 -i 5 get.task
# Options:
#   -r : number of requests
#   -t : number of threads
#   -i : number iterations of the tests
#   -p : provider dir in conf/
#
#*******************************************************

CPATH="$CPATH:conf/vtest"

. ./common.env

PGM=com.amm.vtest.VTestDriver

tasksConfigFile=tasks-keyvalue.xml

job=put-get.job
iterations=1
requests=1
threadPoolSize=1

seedKey=1776
seedValue=1776

keySize=20
logModulo=50000

opts="r:t:i:s:S:p:h:d:"
while getopts $opts opt
  do
  case $opt in
    r) requests=$OPTARG ;;
    t) threadPoolSize=$OPTARG ;;
    i) iterations=$OPTARG ;;
    s) seedKey=$OPTARG ;;
    S) seedValue=$OPTARG ;;
    p) provider=$OPTARG ;;
    d) tasksConfigFile=$OPTARG.xml 
       PROPS="$PROPS -DtasksConfigFile=$tasksConfigFile"
       ;;
    h) hosts=$OPTARG ;;
    \?) echo $USAGE " Error"
        exit;;
    esac
  done
CPATH="$CPATH:conf/$provider"

shift `expr $OPTIND - 1`
if [ $# -gt 0 ] ; then
  job=$1
  fi

tstamp=`date "+%F_%H-%M-%S"` ; logdir=logs-$job-$tstamp-Req${requests}-Thr${threadPoolSize}-Iter${iterations} ; mkdir $logdir
echo "Log output=$logdir"

cycleMax=$requests

PROPS="$PROPS -Dcfg.requests=$requests"
PROPS="$PROPS -Dcfg.threadPoolSize=$threadPoolSize"
PROPS="$PROPS -Dcfg.keyGenerator.size=$keySize"
PROPS="$PROPS -Dcfg.key.seed=$seedKey"
PROPS="$PROPS -Dcfg.value.seed=$seedValue"
PROPS="$PROPS -Dcfg.randomGenerator.cycleMax=$cycleMax"
PROPS="$PROPS -DtasksConfigFile=$tasksConfigFile"
PROPS="$PROPS -Dcfg.logModulo=$logModulo"

echo "keySize=$keySize valueSize=$valueSize"

mem=1024m
XPROPS="-Xmx${mem} -Xms${mem}"

time -p java $XPROPS $PROPS -cp $CPATH $PGM $* \
  --iterations $iterations \
  --job $job \
  | tee log.txt

echo "provider=$provider" >> log.txt
echo "LOGDIR=$logdir" >> log.txt
echo "LOGDIR=$logdir"
echo "PROPS=$PROPS"
echo "provider=$provider"
echo "tasksConfigFile=$tasksConfigFile"

cp -p log.txt $logdir
mv log-*.json $logdir
