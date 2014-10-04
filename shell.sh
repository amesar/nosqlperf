
#*************************************************************************
#
# shell.sh - Run NoSQL KeyValue Shell program.
#
# Options:
#   --maxToDisplay $NUM_CHARS - maximum number of value chars to display
#
#*************************************************************************

. ./common.env
CPATH="$CPATH:conf/$provider"

PGM=com.amm.nosql.cli.KeyValueShell

opts="h:p:"
while getopts $opts opt
  do
  case $opt in
    p) provider=$OPTARG ;;
    h) hosts=$OPTARG ;;
    \?) echo $USAGE " Error"
        exit;;
    esac
  done
shift `expr $OPTIND - 1`

#CPATH="$CPATH:conf/$provider"
addProviderToClasspath $provider

echo "PROVIDER: $provider"
echo "HOSTS: $hosts"
#echo ">> PROPS=$PROPS"

java $PROPS -cp $CPATH $PGM $* | tee log-shell.txt

