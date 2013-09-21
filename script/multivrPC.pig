register /usr/local/loghandler/loghandler.jar;
register /usr/local/loghandler/lib/commonlib.jar;
register /usr/local/loghandler/lib/myudf.jar;

define UrlDecode com.sogou.loghandle.eval.string.UrlDecode();
define EXTRACTVR com.sogou.loghandle.eval.string.ExtractVR();
define ISVRLOG com.sogou.loghandle.eval.string.IsVRLog();
define ISBASEVR com.sogou.loghandle.eval.string.IsBaseVR();

@weblog = loadtemplate uigs web;
hitlog = filter weblog by vr is not null and pbquery is not null and rn is not null and rn !=0 and stype is not null and uuid is not null and uigs_refer is not null and uigs_refer!='' and suv is not null and suv !='-'; 
--count pv
filter_vr_pvlog = filter hitlog by type =='pv' and ISVRLOG(vr);
temp_vr_pvlog  = foreach filter_vr_pvlog generate UrlDecode(pbquery,'gbk') as pvquery, (abtestid is null ? 0 : abtestid) as pvabtestid, FLATTEN(EXTRACTVR(vr)),SUBSTRING(manualtime,12,14) as pvhour;
vr_pvlog = filter temp_vr_pvlog by ISBASEVR(vrid);
--vr_pvlog = filter temp_vr_pvlog by 1==1;
--groupkey_pvlog = group vr_pvlog by pvquery;
--groupall_pvlog = group groupkey_pvlog all;
--keyvrpv = foreach groupall_pvlog generate 'keywordnum' as title, COUNT(groupkey_pvlog) as keywordnumuv;
--dump keyvrpv;
--@output keyvrpv;
--@exit;
group_vrpvlog = group vr_pvlog by (pvabtestid, pvhour, pvquery, vrpos, vrid);
vrpv = foreach group_vrpvlog generate  flatten(group) as (pvabtestid, pvhour, pvquery, pvvrpos, pvvrid) , COUNT(vr_pvlog) as pv;
--dump vrpv;

--count endcl
filter_cllog = filter hitlog by type =='cl';
cllog= foreach filter_cllog generate timestamp, uuid, UrlDecode(pbquery,'gbk') as cllquery, (abtestid is null? 0 : abtestid) as cllabtestid, SUBSTRING(manualtime,12,14) as cllhour, uigs_cl_clickinfo , (chararray)uigs_cl_pos as cllvrpos;

group_cllog = group cllog by uuid;
filter_group_log = foreach group_cllog {
    order_cllog = order cllog by timestamp DESC;
    l_cllog = limit order_cllog 1;
    endclick_log = filter l_cllog by uigs_cl_clickinfo matches 'sogou_vr_.*' and ISBASEVR((REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1)));
    generate  flatten(endclick_log);
}
endclick_log = foreach filter_group_log generate REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1) as cllickid, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1)) as cllvrid,cllquery,cllabtestid,cllhour,cllvrpos;
tgroup_endcllog = group endclick_log by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid);
tendclpv = foreach tgroup_endcllog generate flatten(group) as (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid),'all' as cllickid, COUNT(endclick_log) as eclpv;
eendclick_log = filter endclick_log by cllickid is not null;
egroup_endcllog = group eendclick_log by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid); 
eendclpv = foreach egroup_endcllog generate flatten(group) as (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid), COUNT(eendclick_log) as eclpv;
endclpv = UNION  ONSCHEMA tendclpv,eendclpv;
--dump eclpv;
--count cl
--filter_vr_cllog = filter hitlog by type =='cl' and uigs_cl_clickinfo matches 'sogou_vr_.*';
--vr_cllog= foreach filter_vr_cllog generate UrlDecode(pbquery,'gbk') as clquery, abtestid as clabtestid, SUBSTRING(manualtime,12,14) as clhour, REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_.*_(.*)', 1) as clickid, REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1) as clvrid, (chararray)uigs_cl_pos as clvrpos;
filter_vr_cllog = filter cllog by uigs_cl_clickinfo matches 'sogou_vr_.*';
temp_vr_cllog = foreach filter_vr_cllog generate cllquery as clquery, cllabtestid as clabtestid, cllhour as clhour, REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_[0-9]+_(.*)', 1) as clickid, (REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1) is null ? REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)', 1): REGEX_EXTRACT(uigs_cl_clickinfo , 'sogou_vr_(.*)_.*', 1))as clvrid, cllvrpos as clvrpos;
vr_cllog = filter temp_vr_cllog by ISBASEVR(clvrid);
tgroup_vrcllog= group vr_cllog by (clabtestid,clhour,clquery,clvrpos,clvrid);
tvrcl = foreach tgroup_vrcllog generate flatten(group) as (clabtestid,clhour,clquery,clvrpos,clvrid),'all' as clickid, COUNT(vr_cllog) as cl;
evr_cllog = filter vr_cllog by clickid is not null; 
egroup_vrcllog= group evr_cllog by (clabtestid,clhour,clquery,clvrpos,clvrid,clickid);
evrcl = foreach egroup_vrcllog generate flatten(group) as (clabtestid,clhour,clquery,clvrpos,clvrid,clickid), COUNT(evr_cllog) as cl;
vrcl = UNION ONSCHEMA tvrcl,evrcl;
--describe vrcl;

--dump vrcl;

joinedcl = join vrcl by (clabtestid,clhour,clquery,clvrpos,clvrid,clickid) LEFT, endclpv by (cllabtestid,cllhour,cllquery,cllvrpos,cllvrid,cllickid);
joinedvr = join vrpv by (pvabtestid,pvhour,pvquery,pvvrpos,pvvrid) LEFT, joinedcl by (clabtestid,clhour,clquery,clvrpos,clvrid);
--title_vr = foreach joinedvr generate (clickid is null ? StringConcat('abtestid:', pvabtestid,'_vrtype:',pvvrid,'_pos:', (chararray)pvvrpos, '_hour:', pvhour,'_keywords:', pvquery)  : StringConcat('abtestid:', pvabtestid,'_vrtype',pvvrid,'_pos:', (chararray)pvvrpos, '_hour:',pvhour,'_keywords:', pvquery, '_subclick:',clickid))  as title, pvabtestid as abtest,(eclpv is null?0:eclpv) as eclpv,(cl is null ? 0 : cl) as click, (long)pvvrpos as position,(clickid is null ? 'all' : clickid) as clickid, pvquery as keyword,(long)pvhour as hour, pvvrid as type, pv as pv;
temp_title_vr = foreach joinedvr generate  pvabtestid as abtest,(eclpv is null?0:eclpv) as eclpv,(cl is null ? 0 : cl) as click, (long)pvvrpos as position,(clickid is null ? 'all' : clickid) as clickid, pvquery as keyword,(long)pvhour as hour, pvvrid as type, pv as pv;
temp2_title_vr = foreach temp_title_vr generate StringConcat('abtestid:', abtest,'_vrtype:',type,'_pos:', (chararray)position, '_hour:', hour,'_keywords:', keyword, '_subclick',clickid)  as title,abtest,eclpv,click,position,clickid,keyword,hour,type,pv;
title_vr = order temp2_title_vr by type,pv;
@output title_vr;
--describe title_vr;
--@output title_vr;
--@output eclpv;
