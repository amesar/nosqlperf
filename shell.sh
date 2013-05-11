
#*************************************************************************
#
# shell.sh - Run NoSQL KeyValue Shell program.
#
# Options:
#   --maxToDisplay $NUM_CHARS - maximum number of value chars to display
#
#*************************************************************************

. ./common.env

PGM=com.amm.nosql.cli.KeyValueShell


if [ $# -gt 0 ] ; then
  hosts=$1
  shift `expr $OPTIND - 1`
  fi

echo ">> hosts=$hosts"
echo ">> PROPS=$PROPS"

java $PROPS -cp $CPATH $PGM $* | tee log-shell.txt

