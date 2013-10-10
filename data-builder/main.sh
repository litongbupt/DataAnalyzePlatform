#!/bin/bash

DATE=$1
if [ -z $DATE ]; then
    DATE=`date +%Y%m%d -d"1 days ago"`
fi

echo `date` "Starting to fetch VR list"
if [ -e 'vrtype.txt' ] ; then
    mv vrtype.txt vrtype.txt.old
fi
java -cp multivr.jar:mysql-connector-java-5.0.8-bin.jar:log4j-1.2.15.jar com.sogou.uigs.multvr.entity.VrTypeFetcher
if [ $? -ne 0 ]; then
    echo `date` "Failed to fetch VR list"
    exit 1
fi

#save diff vrtype
diff vrtype.txt vrtype.txt.old >> vrtype.diff

#filter vrtype.txt by vrid != struct_id
./filter.sh vrtype.txt struct_type.txt

# append temp local vr to vrtype list
cat ./local_vrtype.txt >> vrtype.txt

hadoop dfs -rm /tmp/vrtype.txt
hadoop dfs -put vrtype.txt /tmp/vrtype.txt

echo `date` "Starting to process VR related data for $DATE"
RESULTBASE="result/$DATE"
rm -rf "$RESULTBASE"
echo $RESULTBASE

echo `date` "Starting to calculate multivrPC for $DATE"
echo /usr/local/loghandler/bin/loghandle  -d "$DATE" -p multivrPC.pig -s web:/logdata/uigs/web  -r "$RESULTBASE/vr" --lzo  --pig multivrPCdebug.pig
/usr/local/loghandler/bin/loghandle  -d "$DATE" -p multivrPC.pig -s web:/logdata/uigs/web  -r "$RESULTBASE/vr" --lzo  --pig multivrPCdebug.pig
if [ $? -ne 0 ]; then
   echo `date` "Failed to process the multivrPC.pig for $DATE"
    exit 1
else
    echo `date` "Successfully processed the multivrPC.pig for $DATE"
fi
echo cat $RESULTBASE/vr/title_vr/part-r-000?? > $RESULTBASE/vr/title_vr/part-all
cat $RESULTBASE/vr/title_vr/part-r-000?? > $RESULTBASE/vr/title_vr/part-all
echo sh sload.sh $DATE $RESULTBASE/vr/title_vr/part-all multivr_pc
sh sload.sh $DATE $RESULTBASE/vr/title_vr/part-all multivr_pc

rm -rf $DATE $DATE $RESULTBASE/vr
#add other category which like vr to datebase
#cd ./translate/
#pwd
#./daily.sh $DATE
#cd ..
#pwd

exit 0
