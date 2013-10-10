register /usr/local/loghandler/lib/commonlib.jar;

define UrlDecode com.sogou.loghandle.eval.string.UrlDecode();
define EXTRACTVR com.sogou.loghandle.eval.string.ExtractVR();
define ISVRLOG com.sogou.loghandle.eval.string.IsVRLog();
define ISBASEVR com.sogou.loghandle.eval.string.IsBaseVR();
define EXTRACTJHID com.sogou.loghandle.eval.string.ExtractJHIDFromCLLog();

@weblog = loadtemplate uigs web;
--find vr log
hitlog = filter weblog by vr is not null and pbquery is not null and rn is not null and rn !=0 and stype is not null and uuid is not null and uigs_refer is not null and uigs_refer!='' and suv is not null and suv !='-'; 
--caculate pv
filter_vr_pvlog = filter hitlog by type =='pv' and ISVRLOG(vr);
temp_vr_pvlog  = foreach filter_vr_pvlog generate UrlDecode(pbquery,'gbk') as pvquery, (abtestid is null ? 0 : abtestid) as pvabtestid,jhid as wuyong, FLATTEN(EXTRACTVR(vrdetail)),SUBSTRING(manualtime,12,14) as pvhour, SUBSTRING(manualtime,0,11) as pvday;
vr_pvlog = filter temp_vr_pvlog by ISBASEVR(vrid);
--vr_pvlog = filter temp_vr_pvlog by vrid != 'null';
group_vrpvlog = group vr_pvlog by (pvabtestid, pvhour, pvquery, vrpos, vrid,pvday,jhid);
vrpv = foreach group_vrpvlog generate  flatten(group) as (pvabtestid, pvhour, pvquery, pvvrpos:int, pvvrid,pvday,jhid) , COUNT(vr_pvlog) as pv;
--describe vrpv;
--lvrpv = limit vrpv 10;
--dump lvrpv;

--caculate endcl
filter_cllog = filter hitlog by type =='cl';
cllog= foreach filter_cllog generate timestamp, uuid, UrlDecode(pbquery,'gbk') as cllquery, (abtestid is null? 0 : abtestid) as cllabtestid, SUBSTRING(manualtime,12,14) as cllhour,SUBSTRING(manualtime,0,11) as cllday, uigs_cl_clickinfo , uigs_cl_pos as cllvrpos, vrdetail as cllvrdetail;

group_cllog = group cllog by uuid;
filter_group_log = foreach group_cllog {
    order_cllog = order cllog by timestamp DESC;
    l_cllog = limit order_cllog 1;
    endclick_log = filter l_cllog by uigs_cl_clickinfo matches 'sogou_vr_.*' and ISBASEVR((REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1)));
    --endclick_log = filter l_cllog by uigs_cl_clickinfo matches 'sogou_vr_.*' and (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1)) != 'null';
    generate  flatten(endclick_log);
}
temp_endclick_log = foreach filter_group_log generate (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1) is null ? 'title' :REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1)) as cllickid, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1)) as cllvrid,cllquery,cllabtestid,cllhour,cllvrpos,cllday,cllvrdetail;
endclick_log = foreach temp_endclick_log generate cllickid,cllvrid,cllquery,cllabtestid,cllhour,cllvrpos,cllday,EXTRACTJHID(cllvrdetail,cllvrid,cllvrpos) as clljhid;
tgroup_endcllog = group endclick_log by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllday,clljhid);
tendclpv = foreach tgroup_endcllog generate flatten(group) as (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllday,clljhid),'all' as cllickid, COUNT(endclick_log) as eclpv;
egroup_endcllog = group endclick_log by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid,cllday,clljhid); 
eendclpv = foreach egroup_endcllog generate flatten(group) as (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid,cllday,clljhid), COUNT(endclick_log) as eclpv;
endclpv = UNION  ONSCHEMA tendclpv,eendclpv;
--describe endclpv;
--lendclpv = limit endclpv 10;
--dump lendclpv;
--caculate cl
filter_vr_cllog = filter cllog by uigs_cl_clickinfo matches 'sogou_vr_.*';
temp_vr_cllog = foreach filter_vr_cllog generate cllquery as clquery, cllabtestid as clabtestid, cllhour as clhour,cllday as clday, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1) is null ? 'title' : REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1))  as clickid, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1))as clvrid, cllvrpos as clvrpos,cllvrdetail as clvrdetail;
filter_vr_cllog2 = filter temp_vr_cllog by ISBASEVR(clvrid);
vr_cllog = foreach filter_vr_cllog2 generate clickid,clvrid,clquery,clabtestid,clhour,clvrpos,clday,EXTRACTJHID(clvrdetail,clvrid,clvrpos) as cljhid;
tgroup_vrcllog= group vr_cllog by (clabtestid,clhour,clquery,clvrpos,clvrid,clday,cljhid);
tvrcl = foreach tgroup_vrcllog generate flatten(group) as (clabtestid,clhour,clquery,clvrpos,clvrid,clday,cljhid),'all' as clickid, COUNT(vr_cllog) as cl;
egroup_vrcllog= group vr_cllog by (clabtestid,clhour,clquery,clvrpos,clvrid,clday,cljhid,clickid);
evrcl = foreach egroup_vrcllog generate flatten(group) as (clabtestid,clhour,clquery,clvrpos,clvrid,clday,cljhid,clickid), COUNT(vr_cllog) as cl;
vrcl = UNION ONSCHEMA tvrcl,evrcl;
--describe vrcl;
--lvrcl = limit vrcl 10;
--dump lvrcl;
joinedcl = join vrcl by (clabtestid,clhour,clquery,clvrpos,clvrid,clickid,clday,cljhid) LEFT, endclpv by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid,cllday,clljhid);
--describe joinedcl;
--dump joinedcl;
joinedvr = join vrpv by (pvabtestid,pvhour,pvquery,pvvrpos,pvvrid,pvday,jhid) LEFT, joinedcl by (clabtestid,clhour,clquery,clvrpos,clvrid,clday,cljhid);
--describe joinedvr;
--dump joinedvr;
temp_title_vr = foreach joinedvr generate  pvabtestid as abtest,(eclpv is null?0:eclpv) as eclpv,(cl is null ? 0 : cl) as click, pvvrpos as position,  (clickid is null?'all':clickid) as clickid, pvquery as keyword,(long)pvhour as hour, pvvrid as type, pv as pv,pvday as day,jhid;
temp2_title_vr = foreach temp_title_vr generate StringConcat('abtestid:', abtest,'_vrtype:',type,'_pos:', position, '_hour:', hour,'_keywords:', keyword, '_subclick:',clickid,'_jhid:',jhid)  as title,abtest,eclpv,click,position,clickid,keyword,hour,type,pv,day,jhid;
title_vr = order temp2_title_vr by type,pv;
--ltitle_vr = limit title_vr 10;
--describe title_vr;
--dump title_vr;
@output title_vr;
