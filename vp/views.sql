CREATE OR REPLACE VIEW view_pay AS
	SELECT pa.*,concat(ud.first_name,' ',ud.second_name) AS add_user_names,
	concat(ud2.first_name,' ',ud2.second_name) AS edit_user_names,
	tc.transactor_names AS bill_transactor_names,st.store_name,
	tt.transaction_type_name,pm.pay_method_name 
	FROM pay pa 
	INNER JOIN store st ON pa.store_id=st.store_id 
	INNER JOIN transaction_type tt ON pa.transaction_type_id=tt.transaction_type_id 
	INNER JOIN user_detail ud ON pa.add_user_detail_id=ud.user_detail_id 
	INNER JOIN pay_method pm ON pa.pay_method_id=pm.pay_method_id 
	LEFT JOIN user_detail ud2 ON pa.edit_user_detail_id=ud2.user_detail_id 
	LEFT JOIN transactor tc ON pa.bill_transactor_id=tc.transactor_id;

CREATE OR REPLACE VIEW view_stock_in AS
	SELECT i.*,s.store_id,s.batchno,s.currentqty,s.item_mnf_date,s.item_exp_date,
	t.store_name,c.category_name,u.unit_name,sc.sub_category_name 
	FROM stock s 
	INNER JOIN item i ON s.item_id=i.item_id 
	INNER JOIN store t ON s.store_id=t.store_id 
	INNER JOIN category c ON i.category_id=c.category_id 
	INNER JOIN unit u ON i.unit_id=u.unit_id 
	LEFT JOIN sub_category sc ON i.sub_category_id=sc.sub_category_id;

CREATE OR REPLACE VIEW view_stock_total AS 
	select t.store_id,i.item_id,i.category_id,i.sub_category_id,i.unit_cost_price,i.unit_retailsale_price,i.unit_wholesale_price,
	IFNULL(sum(s.currentqty),0) as currentqty,
	IFNULL(sum(s.currentqty)*i.unit_cost_price,0) as cost_value,
	IFNULL(sum(s.currentqty)*i.unit_wholesale_price,0) as wholesale_value,
	IFNULL(sum(s.currentqty)*i.unit_retailsale_price,0) as retailsale_value,
	i.reorder_level,
	t.store_name,i.description,u.unit_id,u.unit_name,c.category_name,sc.sub_category_name from item i 
	inner join unit u on i.unit_id=u.unit_id 
	inner join category c on i.category_id=c.category_id 
	left join sub_category sc on i.sub_category_id=sc.sub_category_id 
	left join stock s on i.item_id=s.item_id 
	left join store t on s.store_id=t.store_id 
	group by t.store_id,i.item_id 
	order by t.store_name,i.description;

CREATE OR REPLACE VIEW view_stock_all AS 
	select i.item_id,i.category_id,i.sub_category_id,
	IFNULL(sum(s.currentqty),0) as currentqty,i.reorder_level,
	i.description,u.unit_id,u.unit_name,c.category_name,sc.sub_category_name from item i 
	inner join unit u on i.unit_id=u.unit_id 
	inner join category c on i.category_id=c.category_id 
	left join sub_category sc on i.sub_category_id=sc.sub_category_id 
	left join stock s on i.item_id=s.item_id 
	group by i.item_id 
	order by i.description;

CREATE OR REPLACE VIEW view_item AS
	SELECT i.*,c.category_name,sc.sub_category_name,u.unit_symbol 
	FROM item i 
	INNER JOIN category c ON i.category_id=c.category_id 
	INNER JOIN unit u ON i.unit_id=u.unit_id 
	LEFT JOIN sub_category sc ON i.sub_category_id=sc.sub_category_id;

CREATE OR REPLACE VIEW view_transaction AS 
	SELECT  t.*,s.store_name,s2.store_name AS store_name2,
	concat(ud.first_name,' ',ud.second_name) AS add_user_detail_name,
	concat(ud2.first_name,' ',ud2.second_name) AS edit_user_detail_name,
	concat(ud3.first_name,' ',ud3.second_name) AS transaction_user_detail_name,
	tr.transactor_names,tr2.transactor_names AS bill_transactor_names,
	tr3.transactor_names AS scheme_transactor_names,tt.transaction_type_name,
	tn.transaction_reason_name 
	FROM transaction t 
	INNER JOIN store s ON t.store_id=s.store_id 
	INNER JOIN transaction_type tt ON t.transaction_type_id=tt.transaction_type_id 
	INNER JOIN transaction_reason tn ON t.transaction_reason_id=tn.transaction_reason_id 
	INNER JOIN user_detail ud ON t.add_user_detail_id=ud.user_detail_id 
	LEFT JOIN store s2 ON t.store2_id=s2.store_id 
	LEFT JOIN user_detail ud2 ON t.edit_user_detail_id=ud2.user_detail_id 
	LEFT JOIN user_detail ud3 ON t.transaction_user_detail_id=ud3.user_detail_id
	LEFT JOIN transactor tr ON t.transactor_id=tr.transactor_id 
	LEFT JOIN transactor tr2 ON t.bill_transactor_id=tr2.transactor_id 
	LEFT JOIN transactor tr3 ON t.scheme_transactor_id=tr3.transactor_id;

CREATE OR REPLACE VIEW view_transaction_item AS 
	SELECT  ti.*,i.description,ut.unit_symbol,c.*,
	t.princ_scheme_member,t.scheme_card_number,t.grand_total,
	t.store_id,store2_id,t.transaction_date,t.add_date,t.edit_date,t.scheme_transactor_id,
	t.transactor_id,t.bill_transactor_id,t.transaction_type_id,t.transaction_reason_id,
	t.add_user_detail_id,t.edit_user_detail_id,t.transaction_user_detail_id,
	s.store_name,s2.store_name AS store_name2,
	concat(ud.first_name,' ',ud.second_name) AS add_user_detail_name,
	concat(ud2.first_name,' ',ud2.second_name) AS edit_user_detail_name,
	concat(ud3.first_name,' ',ud3.second_name) AS transaction_user_detail_name,
	tr.transactor_names,tr2.transactor_names AS bill_transactor_names,
	tr3.transactor_names AS scheme_transactor_names,
	tt.transaction_type_name,tn.transaction_reason_name 
	FROM transaction_item ti 
	INNER JOIN transaction t ON ti.transaction_id=t.transaction_id 
	INNER JOIN store s ON t.store_id=s.store_id 
	INNER JOIN transaction_type tt ON t.transaction_type_id=tt.transaction_type_id 
	INNER JOIN transaction_reason tn ON t.transaction_reason_id=tn.transaction_reason_id 
	INNER JOIN user_detail ud ON t.add_user_detail_id=ud.user_detail_id 
	INNER JOIN item i ON ti.item_id=i.item_id 
	INNER JOIN unit ut ON i.unit_id=ut.unit_id 
	INNER JOIN category c ON i.category_id=c.category_id 
	LEFT JOIN store s2 ON t.store2_id=s2.store_id 
	LEFT JOIN user_detail ud2 ON t.edit_user_detail_id=ud2.user_detail_id 
	LEFT JOIN user_detail ud3 ON t.transaction_user_detail_id=ud3.user_detail_id 
	LEFT JOIN transactor tr ON t.transactor_id=tr.transactor_id 
	LEFT JOIN transactor tr2 ON t.bill_transactor_id=tr2.transactor_id 
	LEFT JOIN transactor tr3 ON t.scheme_transactor_id=tr3.transactor_id;

CREATE OR REPLACE VIEW view_location AS 
	SELECT l.*,s.store_name FROM location l INNER JOIN store s ON l.store_id=s.store_id;

CREATE OR REPLACE VIEW view_item_location AS 
	SELECT il.*,l.store_id,l.location_name,s.store_name,i.description,u.* FROM item_location il 
	INNER JOIN location l ON il.location_id=l.location_id 
	INNER JOIN item i ON il.item_id=i.item_id 
	INNER JOIN store s ON l.store_id=s.store_id 
	INNER JOIN unit u ON i.unit_id=u.unit_id;

CREATE OR REPLACE VIEW view_sub_category AS 
	SELECT s.*,c.category_name FROM sub_category s INNER JOIN category c ON s.category_id=c.category_id;

create or replace view transaction_item_bi as 
select ti.*, 
(
t.cash_discount/
(select count(ti2.transaction_id) from transaction_item ti2 where ti2.transaction_id=ti.transaction_id)
) as unit_cash_discount 
from transaction_item ti 
inner join transaction t on ti.transaction_id=t.transaction_id;


create or replace view view_stock_dif_stg1 as 
select 
s.item_id,
(select i.description from item i where i.item_id=s.item_id) as Description,
(select u.unit_name from item i2,unit u where i2.item_id=s.item_id and i2.unit_id=u.unit_id) as Unit,
s.currentqty as CurrentQty,
(select sum(ti1.item_qty) from view_transaction_item ti1 where 
	ti1.item_id=s.item_id and 
	(
		(ti1.transaction_type_id in (1) and ti1.store_id=1) or 
		(ti1.transaction_type_id in (7) and ti1.stock_effect='C' and ti1.store_id=1) or 
		(ti1.transaction_type_id in (4) and ti1.store2_id=1)
	)  
) as InQty,
(select sum(ti2.item_qty) from view_transaction_item ti2 where 
	ti2.item_id=s.item_id and 
	(
		(ti2.transaction_type_id in (3,2,4) and ti2.store_id=1) or 
		(ti2.transaction_type_id in (7) and ti2.stock_effect='D' and ti2.store_id=1) 
	)  
) as OutQty 
from stock s 
where s.store_id=1 
order by s.item_id DESC;

create or replace view view_stock_dif_stg2 as 
SELECT stg1.*,(InQty-OutQty) as ExpectedCurrentQty FROM view_stock_dif_stg1 stg1;

create or replace view view_stock_dif_stg3 as 
SELECT stg2.*, (CurrentQty-ExpectedCurrentQty) as ExcessQty FROM view_stock_dif_stg2 stg2;

create or replace view view_stock_difference as 
SELECT * FROM view_stock_dif_stg3 sd where ExcessQty!=0;

