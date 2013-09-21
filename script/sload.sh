if [ $# != 3 ]; then 
echo sh sload.sh "<date>" "<path>" "<database>"
exit 1
fi
date=$1
path=$2
database=$3
timestr=`date "+%Y-%m-%d %H:%M:%S"`
echo start $timestr loading
echo sed -ie 's/"//g' $path
sed -ie 's/"//g' $path
echo sh load.sh tb_detail_$date $database $path
errorline=$(sh load.sh tb_detail_$date $database $path 2>&1 | grep -i "Wrong data or column definition." | cut -d ',' -f 1|cut -d ':' -f 3|sed 's/^ //;s/ $//')
count=0;
while [ -n "$errorline" ]
do
	timestr=`date "+%Y-%m-%d %H:%M:%S"`
	echo "time "$timestr
	let "count=$count+1"
	echo "has deleted "$count
	echo "current errorline: "$errorline
	output=$(echo ${errorline}p | xargs -I {} sed -n {} part-all)
	echo $output
	echo ${errorline}d | xargs -I {} sed -in {} part-all
	echo "loading again"
	errorline=$(./load.sh tb_detail_20130809 multivr_pc /search/infobright/part-all 2>&1 | grep -i "Wrong data or column definition." | cut -d ',' -f 1|cut -d ':' -f 3|sed 's/^ //;s/ $//')
done
