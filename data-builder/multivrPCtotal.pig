register /usr/local/loghandler/loghandler.jar;


register /usr/local/loghandler/loghandler.jar;


register /usr/local/loghandler/loghandler.jar;
register /usr/local/loghandler/lib/commonlib.jar;
register /usr/local/loghandler/lib/myudf.jar;

define UrlDecode com.sogou.loghandle.eval.string.UrlDecode();
define EXTRACTVR com.sogou.loghandle.eval.string.ExtractVR();
define ISVRLOG com.sogou.loghandle.eval.string.IsVRLog();
define EXTRACTJHID com.sogou.loghandle.eval.string.ExtractJHIDFromCLLog();

tmp1_rawlog = load '/logdata/uigs/web/201310/20131009' using PigStorage('\u0000') as (line : chararray);
tmp2_rawpblog = foreach tmp1_rawlog generate flatten(com.sogou.loghandle.eval.ExtractPBLog(line)),line;
weblog = foreach tmp2_rawpblog generate ip, manualtime, timestamp, type, url, useragent, suv, yyid,line,flatten(com.sogou.loghandle.eval.ExtractPBStringMap(pbstring)), flatten(com.sogou.loghandle.eval.ExtractPBReferURLMap(url));
--find vr log
hitlog = filter weblog by vr is not null and pbquery is not null and rn is not null and rn !=0 and stype is not null and uuid is not null and uigs_refer is not null and uigs_refer!='' and suv is not null and suv !='-' and jhid != 'null'; 
--caculate pv
filter_vr_pvlog = filter hitlog by type =='pv' and ISVRLOG(vr);
vr_pvlog  = foreach filter_vr_pvlog generate jhid, SUBSTRING(manualtime,0,11) as pvday;
group_vrpvlog = group vr_pvlog by (jhid,pvday);
vrpv = foreach group_vrpvlog generate  flatten(group) as (jhid,pvday), COUNT(vr_pvlog) as pv;
--describe vrpv;
--lvrpv = limit vrpv 10;
--dump lvrpv;

--caculate endcl
filter_cllog = filter hitlog by type =='cl';
cllog= foreach filter_cllog generate timestamp, uuid, uigs_cl_clickinfo ,SUBSTRING(manualtime,0,11) as cllday, uigs_cl_pos as cllvrpos, vrdetail as cllvrdetail;

group_cllog = group cllog by uuid;
filter_group_log = foreach group_cllog {
    order_cllog = order cllog by timestamp DESC;
    l_cllog = limit order_cllog 1;
    endclick_log = filter l_cllog by uigs_cl_clickinfo matches 'sogou_vr_.*'  and (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1)) != 'null' ;
    generate  flatten(endclick_log);
}
temp_endclick_log = foreach filter_group_log generate (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1)) as cllvrid,cllvrpos,cllvrdetail,cllday;
endclick_log = foreach temp_endclick_log generate cllday,EXTRACTJHID(cllvrdetail,cllvrid,cllvrpos) as clljhid;
tgroup_endcllog = group endclick_log by (clljhid,cllday);
endclpv = foreach tgroup_endcllog generate flatten(group) as (clljhid,cllday),COUNT(endclick_log) as eclpv;
--caculate cl
filter_vr_cllog = filter cllog by uigs_cl_clickinfo matches 'sogou_vr_.*';
temp_vr_cllog = foreach filter_vr_cllog generate cllday as clday, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(\\d+)_.*', 1))as clvrid, cllvrpos as clvrpos,cllvrdetail as clvrdetail;
vr_cllog = foreach temp_vr_cllog generate clday,EXTRACTJHID(clvrdetail,clvrid,clvrpos) as cljhid;

tgroup_vrcllog= group vr_cllog by (cljhid,clday);
vrcl = foreach tgroup_vrcllog generate flatten(group) as (cljhid,clday), COUNT(vr_cllog) as cl;
--describe vrcl;
--lvrcl = limit vrcl 10;
--dump lvrcl;
joinedcl = join vrcl by (clday,cljhid) LEFT, endclpv by (cllday,clljhid);
--describe joinedcl;
--dump joinedcl;
joinedvr = join vrpv by (pvday,jhid) LEFT, joinedcl by (clday,cljhid);
--describe joinedvr;
--dump joinedvr;
title_vr = foreach joinedvr generate pvday as day,jhid, pv as pv,(cl is null ? 0 : cl) as click, (eclpv is null?0:eclpv) as eclpv;
--ltitle_vr = limit title_vr 10;
--describe title_vr;
--dump title_vr;

describe title_vr;
store title_vr into '/root/CustomResult/tmp/loghandle/tmp1381393055138/title_vr';

