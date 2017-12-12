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
		(ti1.transaction_type_id in (7) and ti1.stock_effect='C') or 
		(ti1.transaction_type_id in (4) and ti1.store2_id=1)
	)  
) as InQty,
(select sum(ti2.item_qty) from view_transaction_item ti2 where 
	ti2.item_id=s.item_id and 
	(
		(ti2.transaction_type_id in (3,2,4) and ti2.store_id=1) or 
		(ti2.transaction_type_id in (7) and ti2.stock_effect='D') 
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