update transactor set transactor_names=replace(transactor_names,'a','e') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'A','E') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'i','o') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'I','O') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'u','a') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'U','A') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'$','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'&','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,"'","") where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,"/","_") where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'_','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'.','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'-','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'7','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'1','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'4','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'(','') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,')','') where transactor_id>0;
update transactor set transactor_names=UCASE(transactor_names) where transactor_id>0;
update transactor set transactor_names=TRIM(transactor_names) where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'B','H') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'C','K') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'D','M') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'F','T') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'G','Y') where transactor_id>0;
update transactor set transactor_names=replace(transactor_names,'LTM','LTD') where transactor_id>0;