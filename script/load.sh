# VERIFY INPUT PARAMETERS3if [ $# != 7 ]; then
if [ $# != 3 ]; then
    echo "            <table name> "
    echo "            <target Infobright database> "
    echo "            <source file>"
    exit 2 
fi

# assign parameters
  table=$1
  TGTDB=$2
  OFILE=$3	

# MYSQL CONNECTION STRINGS
TGTMYSQL="mysql -h 10.16.135.22 -u root -pyang1290 -P 5029"

#
echo ICE Breaker for MySQL - V 0.1 ALPHA
echo

# VERIFY CONNECTIVITY TO INFOBRIGHT
echo "show tables" | $TGTMYSQL -D $TGTDB >> /dev/null
if [ $? != 0 ]; then
   echo Unable to connect to target database
   exit 3
fi


echo Processing the following tables from the source file
echo 
echo Starting - date 
echo DROP TABLE IF EXISTS " $table "
echo DROP TABLE IF EXISTS " $table "| $TGTMYSQL -D $TGTDB

echo "CREATE TABLE  $table  (title varchar(1500) DEFAULT NULL, abtest int(10) DEFAULT 0,eclpv int(10) DEFAULT 0,click int(10) DEFAULT 0,position int(10) DEFAULT 0, clickid varchar(20) DEFAULT NULL comment 'lookup', keyword varchar(1400) DEFAULT NULL, hour int(10) DEFAULT 0, type varchar(20) DEFAULT NULL, pv int(10) DEFAULT 0) ENGINE=BRIGHTHOUSE DEFAULT CHARSET=utf8;"

echo "CREATE TABLE  $table  (title varchar(1500) DEFAULT NULL, abtest int(10) DEFAULT 0,eclpv int(10) DEFAULT 0,click int(10) DEFAULT 0,position int(10) DEFAULT 0, clickid varchar(20) DEFAULT NULL comment 'lookup', keyword varchar(1400) DEFAULT NULL, hour int(10) DEFAULT 0, type varchar(20) DEFAULT NULL, pv int(10) DEFAULT 0) ENGINE=BRIGHTHOUSE DEFAULT CHARSET=utf8;"| $TGTMYSQL -D $TGTDB


# start loader
echo "load data infile '"${OFILE}"'  IGNORE into table" $table " fields terminated by '\t' ; "
echo "SHOW VARIABLES like 'character_set_database'" | $TGTMYSQL -D $TGTDB 
echo $TGTMYSQL -D $TGTDB 
#echo "set character_set_database=utf8;set character_set_client=utf8; set character_set_connection=utf8; set names utf8; load data infile '"${OFILE}"'  IGNORE into table" $table " fields terminated by '\t' ; " | $TGTMYSQL -D $TGTDB 
echo "load data LOCAL infile '"${OFILE}"' into table" $table " fields terminated by '\t' ; " | $TGTMYSQL -D $TGTDB 
#rm $OFILE

# get record count from source 
TGTCNT=$TGTMYSQL -D $TGTDB --skip-column_names << EOF5
 select count(*) 
   from $table ;
EOF5
echo $table $TGTCNT " rows written to target" - date
echo ICE Breaker Complete
