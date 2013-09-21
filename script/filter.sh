#!/bin/sh

structids=( $(cat $2) )
filte_num=0
IFS=$'\n'
for line in `cat $1`
do
    id=`echo $line | cut -f 1`
    append=true
    for structid in "${structids[@]}"
    do
        #echo vrid:$structid == $id
        if [ "vrid:"$structid == $id ]
        then
            echo delete $line because having $structid
            append=false
            filte_num=$((filte_num+1))
            break  
        fi
    done

    if [ $append == true ]
    then
        echo $line >> filter.txt 
    fi
done
echo total filte $filte_num struct type id 
rm -f $1
mv filter.txt $1
