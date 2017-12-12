DROP PROCEDURE IF EXISTS sp_get_current_system_datetime;
DELIMITER //
CREATE PROCEDURE sp_get_current_system_datetime
(
	OUT out_current_system_datetime datetime 
)
BEGIN 
		set out_current_system_datetime=Now(); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_get_current_system_datetime2;
DELIMITER //
CREATE PROCEDURE sp_get_current_system_datetime2
()
BEGIN 
		set @cur_sys_datetime=Now(); 
		SELECT @cur_sys_datetime AS cur_sys_datetime;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_get_new_card_number;
DELIMITER //
CREATE PROCEDURE sp_get_new_card_number
(
	OUT out_new_card_number varchar(10)
)
BEGIN 
	set @new_no=out_new_card_number;

	set @sql_text = concat('SELECT ',
	lpad(FLOOR(1+RAND()*9),1,'0'),
	lpad(FLOOR(10+RAND()*90),2,'0'),
	lpad(FLOOR(100+RAND()*899),3,'0'),
	lpad(FLOOR(1000+RAND()*8999),4,'0'),
	') INTO @new_no');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
	set out_new_card_number=@new_no;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_get_new_id;
DELIMITER //
CREATE PROCEDURE sp_get_new_id
(
	IN in_table_name varchar(50),
	IN in_id_column_name varchar(50),
	OUT out_new_id bigint
)
BEGIN 
	set @table=in_table_name;
	set @column=in_id_column_name;
	set @max_id=out_new_id;
	set @sql_text = concat('SELECT MAX(',@column,') INTO @max_id',' FROM ',@table);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
	if (@max_id is not null) then
		set out_new_id=(@max_id+1);
	else
		set out_new_id=1;
	end if;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_category;
DELIMITER //
CREATE PROCEDURE sp_insert_category
(
	IN in_category_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("category","category_id",@new_id);
	INSERT INTO category
	(
		category_id,
		category_name
	) 
    VALUES
	(
		@new_id,
		in_category_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_category;
DELIMITER //
CREATE PROCEDURE sp_update_category
(
	IN in_category_id int,
	IN in_category_name varchar(50)
) 
BEGIN 
	UPDATE category SET 
		category_name=in_category_name
	WHERE category_id=in_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_sub_category;
DELIMITER //
CREATE PROCEDURE sp_insert_sub_category
(
	IN in_category_id int,
	IN in_sub_category_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("sub_category","sub_category_id",@new_id);
	INSERT INTO sub_category
	(
		sub_category_id,
		category_id,
		sub_category_name
	) 
    VALUES
	(
		@new_id,
		in_category_id,
		in_sub_category_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_sub_category;
DELIMITER //
CREATE PROCEDURE sp_update_sub_category
(
	IN in_sub_category_id int,
	IN in_category_id int,
	IN in_sub_category_name varchar(50)
) 
BEGIN 
	UPDATE sub_category SET 
		category_id=in_category_id,
		sub_category_name=in_sub_category_name
	WHERE sub_category_id=in_sub_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_unit;
DELIMITER //
CREATE PROCEDURE sp_insert_unit
(
	IN in_unit_name varchar(50),
	IN in_unit_symbol varchar(5)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("unit","unit_id",@new_id);
	INSERT INTO unit
	(
		unit_id,
		unit_name,
		unit_symbol
	) 
    VALUES
	(
		@new_id,
		in_unit_name,
		in_unit_symbol
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_unit;
DELIMITER //
CREATE PROCEDURE sp_update_unit
(
	IN in_unit_id int,
	IN in_unit_name varchar(50),
	IN in_unit_symbol varchar(5)
) 
BEGIN 
	UPDATE unit SET 
		unit_name=in_unit_name,
		unit_symbol=in_unit_symbol
	WHERE unit_id=in_unit_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_store;
DELIMITER //
CREATE PROCEDURE sp_insert_store
(
	IN in_store_name varchar(20)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("store","store_id",@new_id);
	INSERT INTO store
	(
		store_id,
		store_name
	) 
    VALUES
	(
		@new_id,
		in_store_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_store;
DELIMITER //
CREATE PROCEDURE sp_update_store
(
	IN in_store_id int,
	IN in_store_name varchar(20)
) 
BEGIN 
	UPDATE store SET 
		store_name=in_store_name
	WHERE store_id=in_store_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_store_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_store_by_id
(
	IN in_store_id int
) 
BEGIN 
	SELECT * FROM store 
	WHERE store_id=in_store_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_store_by_name_equal;
DELIMITER //
CREATE PROCEDURE sp_search_store_by_name_equal
(
	IN in_store_name varchar(20)
) 
BEGIN 
	SELECT * FROM store 
	WHERE store_name=in_store_name; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_store_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_store_by_name
(
	IN in_store_name varchar(20)
) 
BEGIN 
	SELECT * FROM store 
	WHERE store_name LIKE concat('%',in_store_name,'%') 
	ORDER BY store_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_store_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_store_by_none() 
BEGIN 
	SELECT * FROM store ORDER BY store_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_store_by_user_detail;
DELIMITER //
CREATE PROCEDURE sp_search_store_by_user_detail
(
	IN in_user_detail_id int
) 
BEGIN 
	SELECT * FROM store WHERE store_id IN(
		SELECT gr.store_id FROM group_right gr WHERE gr.group_detail_id IN(
			SELECT gu.group_detail_id FROM group_user gu WHERE gu.user_detail_id=in_user_detail_id
		)
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_user_detail;
DELIMITER //
CREATE PROCEDURE sp_insert_user_detail
(
	IN in_user_name varchar(20),
	IN in_user_password varchar(255),
	IN in_first_name varchar(100),
	IN in_second_name varchar(100),
	IN in_third_name varchar(100),
	IN in_is_user_locked varchar(3),
	IN in_is_user_gen_admin varchar(3),
	In in_user_category_id int
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("user_detail","user_detail_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO user_detail
	(
		user_detail_id,
		user_name,
		user_password,
		first_name,
		second_name,
		third_name,
		is_user_locked,
		is_user_gen_admin,
		user_category_id,
		add_date,
		edit_date
		
	) 
    VALUES
	(
		@new_id,
		in_user_name,
		in_user_password,
		in_first_name,
		in_second_name,
		in_third_name,
		in_is_user_locked,
		in_is_user_gen_admin,
		in_user_category_id,
		@cur_sys_datetime,
		@cur_sys_datetime
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_user_detail;
DELIMITER //
CREATE PROCEDURE sp_update_user_detail
(
	IN in_user_detail_id int,
	IN in_user_name varchar(20),
	IN in_user_password varchar(255),
	IN in_first_name varchar(100),
	IN in_second_name varchar(100),
	IN in_third_name varchar(100),
	IN in_is_user_locked varchar(3),
	IN in_is_user_gen_admin varchar(3),
	IN in_user_category_id int
) 
BEGIN 

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE user_detail SET 
		user_name=in_user_name,
		user_password=in_user_password,
		first_name=in_first_name,
		second_name=in_second_name,
		third_name=in_third_name,
		is_user_locked=in_is_user_locked,
		is_user_gen_admin=in_is_user_gen_admin,
		user_category_id=in_user_category_id,
		edit_date=@cur_sys_datetime 
	WHERE user_detail_id=in_user_detail_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_detail_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_user_detail_by_id
(
	IN in_user_detail_id int
) 
BEGIN 
	SELECT * FROM user_detail 
	WHERE user_detail_id=in_user_detail_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_detail_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_user_detail_by_none
() 
BEGIN 
	SELECT * FROM user_detail ORDER BY user_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_detail_by_names;
DELIMITER //
CREATE PROCEDURE sp_search_user_detail_by_names
(
	IN in_names varchar(100) 
) 
BEGIN 
	SELECT * FROM user_detail 
	WHERE user_name LIKE concat('%', in_names, '%') 
	OR first_name LIKE concat('%', in_names, '%') 
	OR second_name LIKE concat('%', in_names, '%') 
	OR third_name LIKE concat('%', in_names, '%') 
	ORDER BY first_name,second_name,third_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_detail_by_username;
DELIMITER //
CREATE PROCEDURE sp_search_user_detail_by_username
(
	IN in_user_name varchar(20) 
) 
BEGIN 
	SELECT * FROM user_detail 
	WHERE user_name=in_user_name;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_detail_all_active;
DELIMITER //
CREATE PROCEDURE sp_search_user_detail_all_active() 
BEGIN 
	SELECT COUNT(user_detail_id) as total_user_count 
	FROM user_detail 
	WHERE is_user_locked='No' AND user_name<>'system';
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_user_right;
DELIMITER //
CREATE PROCEDURE sp_insert_user_right
(
	IN in_store_id int,
	IN in_user_detail_id int,
	IN in_function_name varchar(50),
	IN in_allow_view varchar(3),
	IN in_allow_add varchar(3),
	IN in_allow_edit varchar(3),
	IN in_allow_delete varchar(3)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("user_right","user_right_id",@new_id);
	INSERT INTO user_right
	(
		user_right_id,
		store_id,
		user_detail_id,
		function_name,
		allow_view,
		allow_add,
		allow_edit,
		allow_delete
	) 
    VALUES
	(
		@new_id,
		in_store_id,
		in_user_detail_id,
		in_function_name,
		in_allow_view,
		in_allow_add,
		in_allow_edit,
		in_allow_delete
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_user_right;
DELIMITER //
CREATE PROCEDURE sp_update_user_right
(
	IN in_user_right_id int,
	IN in_store_id int,
	IN in_user_detail_id int,
	IN in_function_name varchar(50),
	IN in_allow_view varchar(3),
	IN in_allow_add varchar(3),
	IN in_allow_edit varchar(3),
	IN in_allow_delete varchar(3)
) 
BEGIN 
	UPDATE user_right SET 
		store_id=in_store_id,
		user_detail_id=in_user_detail_id,
		function_name=in_function_name,
		allow_view=in_allow_view,
		allow_add=in_allow_add,
		allow_edit=in_allow_edit,
		allow_delete=in_allow_delete
	WHERE user_right_id=in_user_right_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_item;
DELIMITER //
CREATE PROCEDURE sp_insert_item
(
	IN in_item_code varchar(50),
	IN in_description varchar(100),
	IN in_category_id int,
	IN in_sub_category_id integer,
	IN in_unit_id int,
	IN in_reorder_level int,
	IN in_unit_cost_price float,
	IN in_unit_retailsale_price float,
	IN in_unit_wholesale_price float,
	IN in_is_suspended varchar(3),
	IN in_vat_rated varchar(10),
	IN in_item_img_url varchar(100),
	IN in_item_type varchar(7)
) 
BEGIN 
	SET @new_id=0;
	SET @sub_category_id=NULL;
	if (in_sub_category_id!=0) then
		set @sub_category_id=in_sub_category_id;
	end if;
	CALL sp_get_new_id("item","item_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO item
	(
		item_id,
		item_code,
		description,
		category_id,
		sub_category_id,
		unit_id,
		reorder_level,
		unit_cost_price,
		unit_retailsale_price,
		unit_wholesale_price,
		is_suspended,
		vat_rated,
		item_img_url,
		add_date,
		edit_date,
		item_type
	) 
    VALUES
	(
		@new_id,
		in_item_code,
		in_description,
		in_category_id,
		@sub_category_id,
		in_unit_id,
		in_reorder_level,
		in_unit_cost_price,
		in_unit_retailsale_price,
		in_unit_wholesale_price,
		in_is_suspended,
		in_vat_rated,
		in_item_img_url,
		@cur_sys_datetime,
		@cur_sys_datetime,
		in_item_type
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_item;
DELIMITER //
CREATE PROCEDURE sp_update_item
(
	IN in_item_id int,
	IN in_item_code varchar(50),
	IN in_description varchar(100),
	IN in_category_id int,
	IN in_sub_category_id integer,
	IN in_unit_id int,
	IN in_reorder_level int,
	IN in_unit_cost_price float,
	IN in_unit_retailsale_price float,
	IN in_unit_wholesale_price float,
	IN in_is_suspended varchar(3),
	IN in_vat_rated varchar(10),
	IN in_item_img_url varchar(100),
	IN in_item_type varchar(7)
) 
BEGIN 
	SET @sub_category_id=NULL;
	if (in_sub_category_id!=0) then
		set @sub_category_id=in_sub_category_id;
	end if;

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE item SET 
		item_code=in_item_code,
		description=in_description,
		category_id=in_category_id,
		sub_category_id=@sub_category_id,
		unit_id=in_unit_id,
		reorder_level=in_reorder_level,
		unit_cost_price=in_unit_cost_price,
		unit_retailsale_price=in_unit_retailsale_price,
		unit_wholesale_price=in_unit_wholesale_price,
		is_suspended=in_is_suspended,
		vat_rated=in_vat_rated,
		item_img_url=in_item_img_url,
		edit_date=@cur_sys_datetime,
		item_type=in_item_type 
	WHERE item_id=in_item_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_pay_method;
DELIMITER //
CREATE PROCEDURE sp_insert_pay_method
(
	IN in_pay_method_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("pay_method","pay_method_id",@new_id);
	INSERT INTO pay_method
	(
		pay_method_id,
		pay_method_name
	) 
    VALUES
	(
		@new_id,
		in_pay_method_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_pay_method;
DELIMITER //
CREATE PROCEDURE sp_update_pay_method
(
	IN in_pay_method_id int,
	IN in_pay_method_name varchar(50)
) 
BEGIN 
	UPDATE pay_method SET 
		pay_method_name=in_pay_method_name
	WHERE pay_method_id=in_pay_method_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transaction_type;
DELIMITER //
CREATE PROCEDURE sp_insert_transaction_type
(
	IN in_transaction_type_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_type","transaction_type_id",@new_id);
	INSERT INTO transaction_type
	(
		transaction_type_id,
		transaction_type_name
	) 
    VALUES
	(
		@new_id,
		in_transaction_type_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction_type;
DELIMITER //
CREATE PROCEDURE sp_update_transaction_type
(
	IN in_transaction_type_id int,
	IN in_transaction_type_name varchar(50),
	IN in_transactor_label varchar(50),
	IN in_transaction_number_label varchar(50),
	IN in_transaction_output_label varchar(50),
	IN in_bill_transactor_label varchar(50),
	IN in_transaction_ref_label varchar(50),
	IN in_transaction_date_label varchar(50),
	IN in_transaction_user_label varchar(50),
	IN in_is_transactor_mandatory varchar(3),
	IN in_is_transaction_user_mandatory varchar(3),
	IN in_is_transaction_ref_mandatory varchar(3),
	IN in_is_authorise_user_mandatory varchar(3),
	IN in_is_authorise_date_mandatory varchar(3),
	IN in_is_delivery_address_mandatory varchar(3),
	IN in_is_delivery_date_mandatory varchar(3),
	IN in_is_pay_due_date_mandatory varchar(3),
	IN in_is_expiry_date_mandatory varchar(3)
) 
BEGIN 
	UPDATE transaction_type SET 
		transactor_label=in_transactor_label,
		transaction_number_label=in_transaction_number_label,
		transaction_output_label=in_transaction_output_label,
		bill_transactor_label=in_bill_transactor_label,
		transaction_ref_label=in_transaction_ref_label,
		transaction_date_label=in_transaction_date_label,
		transaction_user_label=in_transaction_user_label,
		is_transactor_mandatory=in_is_transactor_mandatory,
		is_transaction_user_mandatory=in_is_transaction_user_mandatory,
		is_transaction_ref_mandatory=in_is_transaction_ref_mandatory,
		is_authorise_user_mandatory=in_is_authorise_user_mandatory,
		is_authorise_date_mandatory=in_is_authorise_date_mandatory,
		is_delivery_address_mandatory=in_is_delivery_address_mandatory,
		is_delivery_date_mandatory=in_is_delivery_date_mandatory,
		is_pay_due_date_mandatory=in_is_pay_due_date_mandatory,
		is_expiry_date_mandatory =in_is_expiry_date_mandatory 
	WHERE transaction_type_id=in_transaction_type_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transaction_reason;
DELIMITER //
CREATE PROCEDURE sp_insert_transaction_reason
(
	IN in_transaction_type_id int,
	IN in_transaction_reason_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_reason","transaction_reason_id",@new_id);
	INSERT INTO transaction_reason
	(
		transaction_reason_id,
		transaction_type_id,
		transaction_reason_name
	) 
    VALUES
	(
		@new_id,
		in_transaction_type_id,
		in_transaction_reason_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction_reason;
DELIMITER //
CREATE PROCEDURE sp_update_transaction_reason
(
	IN in_transaction_reason_id int,
	IN in_transaction_type_id int,
	IN in_transaction_reason_name varchar(50)
) 
BEGIN 
	UPDATE transaction_reason SET 
		transaction_type_id=in_transaction_type_id,
		transaction_reason_name=in_transaction_reason_name
	WHERE transaction_reason_id=in_transaction_reason_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transactor;
DELIMITER //
CREATE PROCEDURE sp_insert_transactor
(
	IN in_transactor_type varchar(20),
	IN in_transactor_names varchar(100),
	IN in_phone varchar(100),
	IN in_email varchar(100),
	IN in_website varchar(100),
	IN in_cpname varchar(100),
	IN in_cptitle varchar(100),
	IN in_cpphone varchar(100),
	IN in_cpemail varchar(100),
	IN in_physical_address varchar(255),
	IN in_tax_identity varchar(100),
	IN in_account_details varchar(255),
	IN in_card_number varchar(10),
	IN in_dob date,
	IN in_is_suspended varchar(3),
	IN in_suspended_reason varchar(50),
	IN in_category varchar(20),
	IN in_sex varchar(10),
	IN in_occupation varchar(50),
	IN in_loc_country varchar(100),
	IN in_loc_district varchar(100),
	IN in_loc_town varchar(100),
	IN in_first_date date,
	IN in_file_reference varchar(100),
	IN in_id_type varchar(50),
	IN in_id_number varchar(50),
	IN in_id_expiry_date date 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transactor","transactor_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @in_dob=NULL;
	if (in_dob is not null) then
		set @in_dob=in_dob;
	end if;

	INSERT INTO transactor
	(
		transactor_id,
		transactor_type,
		transactor_names,
		phone,
		email,
		website,
		cpname,
		cptitle,
		cpphone,
		cpemail,
		physical_address,
		tax_identity,
		account_details,
		card_number,
		add_date,
		edit_date,
		dob,
		is_suspended,
		suspended_reason,
		category,
		sex,
		occupation,
		loc_country,
		loc_district,
		loc_town,
		first_date,
		file_reference,
		id_type,
		id_number,
		id_expiry_date
	) 
    VALUES
	(
		@new_id,
		in_transactor_type,
		in_transactor_names,
		in_phone,
		in_email,
		in_website,
		in_cpname,
		in_cptitle,
		in_cpphone,
		in_cpemail,
		in_physical_address,
		in_tax_identity,
		in_account_details,
		in_card_number,
		@cur_sys_datetime,
		@cur_sys_datetime,
		@in_dob,
		in_is_suspended,
		in_suspended_reason,
		in_category,
		in_sex,
		in_occupation,
		in_loc_country,
		in_loc_district,
		in_loc_town,
		in_first_date,
		in_file_reference,
		in_id_type,
		in_id_number,
		in_id_expiry_date
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transactor;
DELIMITER //
CREATE PROCEDURE sp_update_transactor
(
	IN in_transactor_id int,
	IN in_transactor_type varchar(20),
	IN in_transactor_names varchar(100),
	IN in_phone varchar(100),
	IN in_email varchar(100),
	IN in_website varchar(100),
	IN in_cpname varchar(100),
	IN in_cptitle varchar(100),
	IN in_cpphone varchar(100),
	IN in_cpemail varchar(100),
	IN in_physical_address varchar(255),
	IN in_tax_identity varchar(100),
	IN in_account_details varchar(255),
	IN in_card_number varchar(10),
	IN in_dob date,
	IN in_is_suspended varchar(3),
	IN in_suspended_reason varchar(50),
	IN in_category varchar(20),
	IN in_sex varchar(10),
	IN in_occupation varchar(50),
	IN in_loc_country varchar(100),
	IN in_loc_district varchar(100),
	IN in_loc_town varchar(100),
	IN in_first_date date,
	IN in_file_reference varchar(100),
	IN in_id_type varchar(50),
	IN in_id_number varchar(50),
	IN in_id_expiry_date date 
) 
BEGIN 

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @in_dob=NULL;
	if (in_dob is not null) then
		set @in_dob=in_dob;
	end if;

	UPDATE transactor SET 
		transactor_type=in_transactor_type,
		transactor_names=in_transactor_names,
		phone=in_phone,
		email=in_email,
		website=in_website,
		cpname=in_cpname,
		cptitle=in_cptitle,
		cpphone=in_cpphone,
		cpemail=in_cpemail,
		physical_address=in_physical_address,
		tax_identity=in_tax_identity,
		account_details=in_account_details,
		card_number=in_card_number,
		edit_date=@cur_sys_datetime,
		dob=@in_dob,
		is_suspended=in_is_suspended,
		suspended_reason=in_suspended_reason,
		category=in_category,
		sex=in_sex,
		occupation=in_occupation,
		loc_country=in_loc_country,
		loc_district=in_loc_district,
		loc_town=in_loc_town,
		first_date=in_first_date,
		file_reference=in_file_reference,
		id_type=in_id_type,
		id_number=in_id_number,
		id_expiry_date=in_id_expiry_date 
	WHERE transactor_id=in_transactor_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_by_name
(
	IN in_transactor_names varchar(100) 
) 
BEGIN 
	SELECT * FROM transactor t 
	WHERE t.transactor_names LIKE concat('%',in_transactor_names,'%') 
	ORDER BY t.transactor_names ASC LIMIT 10; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_by_name_type;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_by_name_type
(
	IN in_transactor_names varchar(100),
	IN in_transactor_type varchar(20)
) 
BEGIN 
	SELECT * FROM transactor t 
	WHERE t.transactor_type=in_transactor_type AND 
	t.transactor_names LIKE concat('%',in_transactor_names,'%') 
	ORDER BY t.transactor_names ASC LIMIT 10; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_by_id
(
	IN in_transactor_id bigint
) 
BEGIN 
	SELECT * FROM transactor t 
	WHERE t.transactor_id=in_transactor_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transaction_item;
DELIMITER //
CREATE PROCEDURE sp_insert_transaction_item
(
	IN in_transaction_id bigint,
	IN in_item_id bigint,
	IN in_batchno varchar(100),
	IN in_item_qty float,
	IN in_unit_price float,
	IN in_unit_trade_discount float,
	IN in_unit_vat float,
	IN in_amount float,
	IN in_item_expiry_date date,
	IN in_item_mnf_date date,
	IN in_vat_rated varchar(10),
	IN in_vat_perc float,
	IN in_unit_price_inc_vat float,
	IN in_unit_price_exc_vat float,
	IN in_amount_inc_vat float,
	IN in_amount_exc_vat float,
	IN in_stock_effect varchar(1),
	IN in_is_trade_discount_vat_liable varchar(3),
	IN in_unit_cost_price float,
	IN in_unit_profit_margin float,
	IN in_earn_perc float,
	IN in_earn_amount float
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_item","transaction_item_id",@new_id);

	SET @item_expiry_date=NULL;
	if (in_item_expiry_date is not null) then
		set @item_expiry_date=in_item_expiry_date;
	end if;

	SET @item_mnf_date=NULL;
	if (in_item_mnf_date is not null) then
		set @item_mnf_date=in_item_mnf_date;
	end if;

	SET @in_batchno='';
	if (in_batchno is not null) then
		set @in_batchno=in_batchno;
	end if;

	INSERT INTO transaction_item
	(
		transaction_item_id,
		transaction_id,
		item_id,
		batchno,
		item_qty,
		unit_price,
		unit_trade_discount,
		unit_vat,
		amount,
		item_expiry_date,
		item_mnf_date,
		vat_rated,
		vat_perc,
		unit_price_inc_vat,
		unit_price_exc_vat,
		amount_inc_vat,
		amount_exc_vat,
		stock_effect,
		is_trade_discount_vat_liable,
		unit_cost_price,
		unit_profit_margin,
		earn_perc,
		earn_amount
	) 
    VALUES
	(
		@new_id,
		in_transaction_id,
		in_item_id,
		@in_batchno,
		in_item_qty,
		in_unit_price,
		in_unit_trade_discount,
		in_unit_vat,
		in_amount,
		@item_expiry_date,
		@item_mnf_date,
		in_vat_rated,
		in_vat_perc,
		in_unit_price_inc_vat,
		in_unit_price_exc_vat,
		in_amount_inc_vat,
		in_amount_exc_vat,
		in_stock_effect,
		in_is_trade_discount_vat_liable,
		in_unit_cost_price,
		in_unit_profit_margin,
		in_earn_perc,
		in_earn_amount
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction_item;
DELIMITER //
CREATE PROCEDURE sp_update_transaction_item
(
	IN in_transaction_item_id bigint,
	IN in_transaction_id bigint,
	IN in_item_id bigint,
	IN in_batchno varchar(100),
	IN in_item_qty float,
	IN in_unit_price float,
	IN in_unit_trade_discount float,
	IN in_unit_vat float,
	IN in_amount float,
	IN in_item_expry_date date,
	IN in_item_mnf_date date,
	IN in_vat_rated varchar(10),
	IN in_vat_perc float,
	IN in_unit_price_inc_vat float,
	IN in_unit_price_exc_vat float,
	IN in_amount_inc_vat float,
	IN in_amount_exc_vat float,
	IN in_stock_effect varchar(1),
	IN in_is_trade_discount_vat_liable varchar(3),
	IN in_unit_cost_price float,
	IN in_unit_profit_margin float,
	IN in_earn_perc float,
	IN in_earn_amount float
) 
BEGIN 
	SET @item_expiry_date=NULL;
	if (in_item_expiry_date is not null) then
		set @item_expiry_date=in_item_expiry_date;
	end if;

	SET @item_mnf_date=NULL;
	if (in_item_mnf_date is not null) then
		set @item_mnf_date=in_item_mnf_date;
	end if;

	SET @in_batchno='';
	if (in_batchno is not null) then
		set @in_batchno=in_batchno;
	end if;

	UPDATE transaction_item SET 
		transaction_id=in_transaction_id,
		item_id=in_item_id,
		batchno=@in_batchno,
		item_qty=in_item_qty,
		unit_price=in_unit_price,
		unit_trade_discount=in_unit_trade_discount,
		unit_vat=in_unit_vat,
		amount=in_amount,
		item_expry_date=@item_expry_date,
		item_mnf_date=@item_mnf_date,
		vat_rated=in_vat_rated,
		vat_perc=in_vat_perc,
		unit_price_inc_vat =in_unit_price_inc_vat,
		unit_price_exc_vat =in_unit_price_exc_vat,
		amount_inc_vat =in_amount_inc_vat,
		amount_exc_vat =in_amount_exc_vat,
		stock_effect=in_stock_effect,
		is_trade_discount_vat_liable=in_is_trade_discount_vat_liable,
		unit_cost_price=in_unit_cost_price,
		unit_profit_margin=in_unit_profit_margin,
		earn_perc=in_earn_perc,
		earn_amount=in_earn_amount 
	WHERE transaction_item_id=in_transaction_item_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction_item2;
DELIMITER //
CREATE PROCEDURE sp_update_transaction_item2
(
	IN in_transaction_item_id bigint,
	IN in_item_qty float,
	IN in_unit_price float,
	IN in_unit_trade_discount float,
	IN in_unit_vat float,
	IN in_amount float,
	IN in_unit_price_inc_vat float,
	IN in_unit_price_exc_vat float,
	IN in_amount_inc_vat float,
	IN in_amount_exc_vat float,
	IN in_unit_cost_price float,
	IN in_unit_profit_margin float,
	IN in_earn_perc float,
	IN in_earn_amount float
) 
BEGIN 
	UPDATE transaction_item SET 
		item_qty=in_item_qty,
		unit_price=in_unit_price,
		unit_trade_discount=in_unit_trade_discount,
		unit_vat=in_unit_vat,
		amount=in_amount,
		unit_price_inc_vat =in_unit_price_inc_vat,
		unit_price_exc_vat =in_unit_price_exc_vat,
		amount_inc_vat =in_amount_inc_vat,
		amount_exc_vat =in_amount_exc_vat,
		unit_cost_price=in_unit_cost_price,
		unit_profit_margin=in_unit_profit_margin,
		earn_perc=in_earn_perc,
		earn_amount=in_earn_amount 
	WHERE transaction_item_id=in_transaction_item_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_item_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_item_by_id
(
	IN in_transaction_item_id bigint 
) 
BEGIN 
		SELECT * FROM transaction_item ti 
		WHERE ti.transaction_item_id=in_transaction_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_item_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_item_by_transaction_id
(
	IN in_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM transaction_item ti 
		WHERE ti.transaction_id=in_transaction_id ORDER BY ti.transaction_item_id ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_item_by_transaction_number;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_item_by_transaction_number
(
	IN in_transaction_number varchar(50) 
) 
BEGIN 
		SELECT ti.* FROM transaction_item ti,transaction t 
		WHERE ti.transaction_id=t.transaction_id and t.transaction_number=in_transaction_number ORDER BY ti.transaction_item_id ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_item_by_transaction_id2;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_item_by_transaction_id2
(
	IN in_transaction_id bigint 
) 
BEGIN 
		SELECT ti.* FROM transaction_item ti, item i,category c 
		WHERE ti.transaction_id=in_transaction_id AND 
		ti.item_id=i.item_id AND c.category_id=i.category_id 
		ORDER BY c.category_name ASC,ti.transaction_item_id ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transaction;
DELIMITER //
CREATE PROCEDURE sp_insert_transaction
(
	IN in_transaction_date date,
	IN in_store_id int,
	IN in_store2_id int,
	IN in_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_cash_discount float,
	IN in_total_vat float,
	IN in_transaction_comment varchar(255),
	IN in_add_user_detail_id int,
	IN in_add_date datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date datetime,
	IN in_transaction_ref varchar(100),
	OUT out_transaction_id bigint,
	IN in_sub_total float,
	IN in_grand_total float,
	IN in_total_trade_discount float,
	IN in_points_awarded float,
	IN in_card_number varchar(10),
	IN in_total_std_vatable_amount float,
	IN in_total_zero_vatable_amount float,
	IN in_total_exempt_vatable_amount float,
	IN in_vat_perc float,
	IN in_amount_tendered float,
	IN in_change_amount float,
	IN in_is_cash_discount_vat_liable varchar(3),
	IN in_total_profit_margin float,
	IN in_transaction_user_detail_id int,
	IN in_bill_transactor_id bigint,
	IN in_scheme_transactor_id bigint,
	IN in_princ_scheme_member varchar(100),
	IN in_scheme_card_number varchar(100),
	IN in_transaction_number varchar(50),
	IN in_delivery_date date,
	IN in_delivery_address varchar(250),
	IN in_pay_terms varchar(250),
	IN in_terms_conditions varchar(250),
	IN in_authorised_by_user_detail_id int,
	IN in_authorise_date date,
	IN in_pay_due_date date,
	IN in_expiry_date date
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction","transaction_id",@new_id);
	
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @edit_datetime=null;
	SET @edit_user_detail_id=null;
	
	SET @store2_id=NULL;
	if (in_store2_id!=0) then
		set @store2_id=in_store2_id;
	end if;

	SET @transactor_id=NULL;
	if (in_transactor_id!=0) then
		set @transactor_id=in_transactor_id;
	end if;

	SET @transaction_user_detail_id=NULL;
	if (in_transaction_user_detail_id!=0) then
		set @transaction_user_detail_id=in_transaction_user_detail_id;
	end if;

	SET @bill_transactor_id=NULL;
	if (in_bill_transactor_id!=0) then
		set @bill_transactor_id=in_bill_transactor_id;
	end if;

	SET @scheme_transactor_id=NULL;
	if (in_scheme_transactor_id!=0) then
		set @scheme_transactor_id=in_scheme_transactor_id;
	end if;

	SET @scheme_transactor_id=NULL;
	if (in_scheme_transactor_id!=0) then
		set @scheme_transactor_id=in_scheme_transactor_id;
	end if;

	SET @authorised_by_user_detail_id=NULL;
	if (in_authorised_by_user_detail_id!=0) then
		set @authorised_by_user_detail_id=in_authorised_by_user_detail_id;
	end if;

	SET @transaction_number=@new_id;
	if (in_transaction_number!='') then
		set @transaction_number=in_transaction_number;
	end if;

	INSERT INTO transaction
	(
		transaction_id,
		transaction_date,
		store_id,
		store2_id,
		transactor_id,
		transaction_type_id,
		transaction_reason_id,
		cash_discount,
		total_vat,
		transaction_comment,
		add_user_detail_id,
		add_date,
		edit_user_detail_id,
		edit_date,
		transaction_ref,
		sub_total,
		grand_total,
		total_trade_discount,
		points_awarded,
		card_number,
		total_std_vatable_amount,
		total_zero_vatable_amount,
		total_exempt_vatable_amount,
		vat_perc,
		amount_tendered,
		change_amount,
		is_cash_discount_vat_liable,
		total_profit_margin,
		transaction_user_detail_id,
		bill_transactor_id,
		scheme_transactor_id,
		princ_scheme_member,
		scheme_card_number,
		transaction_number,
		delivery_date,
		delivery_address,
		pay_terms,
		terms_conditions,
		authorised_by_user_detail_id,
		authorise_date,
		pay_due_date,
		expiry_date
	) 
    VALUES
	(
		@new_id,
		in_transaction_date,
		in_store_id,
		@store2_id,
		@transactor_id,
		in_transaction_type_id,
		in_transaction_reason_id,
		in_cash_discount,
		in_total_vat,
		in_transaction_comment,
		in_add_user_detail_id,
		@cur_sys_datetime,
		@edit_user_detail_id,
		@edit_datetime,
		in_transaction_ref,
		in_sub_total,
		in_grand_total,
		in_total_trade_discount,
		in_points_awarded,
		in_card_number,
		in_total_std_vatable_amount,
		in_total_zero_vatable_amount,
		in_total_exempt_vatable_amount,
		in_vat_perc,
		in_amount_tendered,
		in_change_amount,
		in_is_cash_discount_vat_liable,
		in_total_profit_margin,
		@transaction_user_detail_id,
		@bill_transactor_id,
		@scheme_transactor_id,
		in_princ_scheme_member,
		in_scheme_card_number,
		@transaction_number,
		in_delivery_date,
		in_delivery_address,
		in_pay_terms,
		in_terms_conditions,
		@authorised_by_user_detail_id,
		in_authorise_date,
		in_pay_due_date,
		in_expiry_date
	); 
SET out_transaction_id=@new_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction;
DELIMITER //
CREATE PROCEDURE sp_update_transaction
(
	IN in_transaction_id bigint,
	IN in_transaction_date date,
	IN in_store_id int,
	IN in_store2_id int,
	IN in_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_cash_discount float,
	IN in_total_vat float,
	IN in_transaction_comment varchar(255),
	IN in_edit_user_detail_id int,
	IN in_transaction_ref varchar(100),
	IN in_sub_total float,
	IN in_grand_total float,
	IN in_total_trade_discount float,
	IN in_points_awarded float,
	IN in_card_number varchar(10),
	IN in_total_std_vatable_amount float,
	IN in_total_zero_vatable_amount float,
	IN in_total_exempt_vatable_amount float,
	IN in_vat_perc float,
	IN in_amount_tendered float,
	IN in_change_amount float, 
	IN in_is_cash_discount_vat_liable varchar(3),
	IN in_total_profit_margin float,
	IN in_transaction_user_detail_id int,
	IN in_bill_transactor_id bigint,
	IN in_scheme_transactor_id bigint,
	IN in_princ_scheme_member varchar(100),
	IN in_scheme_card_number varchar(100),
	IN in_transaction_number varchar(50),
	IN in_delivery_date date,
	IN in_delivery_address varchar(250),
	IN in_pay_terms varchar(250),
	IN in_terms_conditions varchar(250),
	IN in_authorised_by_user_detail_id int,
	IN in_authorise_date date,
	IN in_pay_due_date date,
	IN in_expiry_date date
) 
BEGIN 

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @store2_id=NULL;
	if (in_store2_id!=0) then
		set @store2_id=in_store2_id;
	end if;

	SET @transactor_id=NULL;
	if (in_transactor_id!=0) then
		set @transactor_id=in_transactor_id;
	end if;

	SET @transaction_user_detail_id=NULL;
	if (in_transaction_user_detail_id!=0) then
		set @transaction_user_detail_id=in_transaction_user_detail_id;
	end if;

	SET @bill_transactor_id=NULL;
	if (in_bill_transactor_id!=0) then
		set @bill_transactor_id=in_bill_transactor_id;
	end if;

	SET @scheme_transactor_id=NULL;
	if (in_scheme_transactor_id!=0) then
		set @scheme_transactor_id=in_scheme_transactor_id;
	end if;

	SET @authorised_by_user_detail_id=NULL;
	if (in_authorised_by_user_detail_id!=0) then
		set @authorised_by_user_detail_id=in_authorised_by_user_detail_id;
	end if;

	UPDATE transaction SET 
		transaction_date=in_transaction_date,
		store_id=in_store_id,
		store2_id=@store2_id,
		transactor_id=@transactor_id,
		transaction_type_id=in_transaction_type_id,
		transaction_reason_id=in_transaction_reason_id,
		cash_discount=in_cash_discount,
		total_vat=in_total_vat,
		transaction_comment=in_transaction_comment,
		edit_user_detail_id=in_edit_user_detail_id,
		edit_date=@cur_sys_datetime,
		transaction_ref=in_transaction_ref,
		sub_total=in_sub_total,
		grand_total=in_grand_total,
		total_trade_discount=in_total_trade_discount,
		points_awarded=in_points_awarded,
		card_number=in_card_number,
		total_std_vatable_amount=in_total_std_vatable_amount,
		total_zero_vatable_amount=in_total_zero_vatable_amount,
		total_exempt_vatable_amount=in_total_exempt_vatable_amount,
		in_vat_perc=in_vat_perc,
		amount_tendered=in_amount_tendered,
		change_amount=in_change_amount,
		is_cash_discount_vat_liable=in_is_cash_discount_vat_liable,
		total_profit_margin=in_total_profit_margin,
		transaction_user_detail_id=in_transaction_user_detail_id,
		bill_transactor_id=@bill_transactor_id,
		scheme_transactor_id=@scheme_transactor_id,
		princ_scheme_member=in_princ_scheme_member,
		scheme_card_number=in_scheme_card_number,
		transaction_number=in_transaction_number,
		delivery_date=in_delivery_date,
		delivery_address=in_delivery_address,
		pay_terms=in_pay_terms,
		terms_conditions=in_terms_conditions,
		authorised_by_user_detail_id=@authorised_by_user_detail_id,
		authorise_date=in_authorise_date,
		pay_due_date=in_pay_due_date,
		expiry_date=in_expiry_date 
	WHERE transaction_id=in_transaction_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transaction2;
DELIMITER //
CREATE PROCEDURE sp_update_transaction2
(
	IN in_transaction_id bigint,
	IN in_cash_discount float,
	IN in_total_vat float,
	IN in_edit_user_detail_id int,
	IN in_sub_total float,
	IN in_grand_total float,
	IN in_total_trade_discount float,
	IN in_points_awarded float,
	IN in_card_number varchar(10),
	IN in_total_std_vatable_amount float,
	IN in_total_zero_vatable_amount float,
	IN in_total_exempt_vatable_amount float,
	IN in_amount_tendered float,
	IN in_change_amount float,
	IN in_total_profit_margin float 
) 
BEGIN 

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE transaction SET 
		cash_discount=in_cash_discount,
		total_vat=in_total_vat,
		edit_user_detail_id=in_edit_user_detail_id,
		edit_date=@cur_sys_datetime,
		sub_total=in_sub_total,
		grand_total=in_grand_total,
		total_trade_discount=in_total_trade_discount,
		points_awarded=in_points_awarded,
		card_number=in_card_number,
		total_std_vatable_amount=in_total_std_vatable_amount,
		total_zero_vatable_amount=in_total_zero_vatable_amount,
		total_exempt_vatable_amount=in_total_exempt_vatable_amount,
		amount_tendered=in_amount_tendered,
		change_amount=in_change_amount,
		total_profit_margin=in_total_profit_margin 
	WHERE transaction_id=in_transaction_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_id
(
	IN in_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_id=in_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_id_type;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_id_type
(
	IN in_transaction_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_id=in_transaction_id AND t.transaction_type_id=in_transaction_type_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_number_type;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_number_type
(
	IN in_transaction_number varchar(50),
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_number=in_transaction_number AND t.transaction_type_id=in_transaction_type_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_store_id_type;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_store_id_type
(
	IN in_store_id int,
	IN in_transaction_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_id=in_transaction_id AND t.transaction_type_id=in_transaction_type_id AND t.store_id=in_store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_store_number_type;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_store_number_type
(
	IN in_store_id int,
	IN in_transaction_number varchar(50),
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_number=in_transaction_number AND t.transaction_type_id=in_transaction_type_id AND t.store_id=in_store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_transactor_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_transactor_id
(
	IN in_transactor_id bigint 
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transactor_id=in_transactor_id 
		ORDER BY t.transaction_id DESC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_transactor_transtype;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_transactor_transtype
(
	IN in_transactor_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transactor_id=in_transactor_id AND t.transaction_type_id=in_transaction_type_id 
		ORDER BY t.transaction_id DESC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_bill_transactor_transtype;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_bill_transactor_transtype
(
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.bill_transactor_id=in_bill_transactor_id AND t.transaction_type_id=in_transaction_type_id 
		ORDER BY t.transaction_id DESC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_number;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_number
(
	IN in_transaction_number varchar(50) 
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.transaction_number=in_transaction_number;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_by_id
(
	IN in_item_id bigint 
) 
BEGIN 
		SELECT * FROM view_item 
		WHERE item_id=in_item_id 
		ORDER BY description ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_by_id_and_code;
DELIMITER //
CREATE PROCEDURE sp_search_item_by_id_and_code
(
	IN in_item_id bigint,
	IN in_item_code varchar(50)  
) 
BEGIN 
		SELECT * FROM view_item 
		WHERE item_id=in_item_id OR item_code=in_item_code 
		ORDER BY description ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_by_code_desc;
DELIMITER //
CREATE PROCEDURE sp_search_item_by_code_desc
(
	IN in_code_desc varchar(100) 
) 
BEGIN  
		SELECT * FROM view_item 
		WHERE description LIKE concat('%',in_code_desc,'%') OR item_code=in_code_desc 
		ORDER BY description ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_active_by_code_desc_very_old;
DELIMITER //
CREATE PROCEDURE sp_search_item_active_by_code_desc_very_old
(
	IN in_code_desc varchar(100) 
) 
BEGIN  
		SELECT * FROM view_item 
		WHERE is_suspended='No' AND (description LIKE concat('%',in_code_desc,'%') OR item_code=in_code_desc) 
		ORDER BY description ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_active_by_code_desc_old;
DELIMITER //
CREATE PROCEDURE sp_search_item_active_by_code_desc_old
(
	IN in_code_desc varchar(100) 
) 
BEGIN  
		SELECT * FROM item 
		WHERE is_suspended='No' AND (description LIKE concat('%',in_code_desc,'%') OR item_code=in_code_desc) 
		ORDER BY description ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_active_by_code_desc;
DELIMITER //
CREATE PROCEDURE sp_search_item_active_by_code_desc
(
	IN in_code_desc varchar(100) 
) 
BEGIN  
		SELECT * FROM item 
		WHERE is_suspended='No' AND (description LIKE concat('%',in_code_desc,'%') OR item_code LIKE concat('%',in_code_desc,'%')) 
		ORDER BY description ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_active_by_code_desc2;
DELIMITER //
CREATE PROCEDURE sp_search_item_active_by_code_desc2
(
	IN in_code_desc varchar(100) 
) 
BEGIN  
		SELECT i.*,c.category_name,sc.sub_category_name,u.unit_symbol 
		FROM item i 
		INNER JOIN category c ON i.category_id=c.category_id 
		INNER JOIN unit u ON i.unit_id=u.unit_id 
		LEFT JOIN sub_category sc ON i.sub_category_id=sc.sub_category_id 
		WHERE i.is_suspended='No' AND (i.description LIKE concat('%',in_code_desc,'%') OR i.item_code=in_code_desc) 
		ORDER BY description ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_by_code;
DELIMITER //
CREATE PROCEDURE sp_search_item_by_code
(
	IN in_item_code varchar(255)
) 
BEGIN 
		SELECT * FROM view_item 
		WHERE item_code=in_item_code 
		ORDER BY description ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_active_by_code;
DELIMITER //
CREATE PROCEDURE sp_search_item_active_by_code
(
	IN in_item_code varchar(255)
) 
BEGIN 
		SELECT * FROM view_item 
		WHERE item_code=in_item_code AND is_suspended='No' 
		ORDER BY description ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stock_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_stock_by_id
(
	IN in_stock_id bigint 
) 
BEGIN 
		SELECT * FROM stock s 
		WHERE s.stock_id=in_stock_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stock_by_store_item;
DELIMITER //
CREATE PROCEDURE sp_search_stock_by_store_item
(
		IN in_store_id int,
		IN in_item_id bigint 
) 
BEGIN 
		SELECT * FROM stock s 
		WHERE s.store_id=in_store_id AND s.item_id=in_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stock_by_store_id;
DELIMITER //
CREATE PROCEDURE sp_search_stock_by_store_id
(
		IN in_store_id int
) 
BEGIN 
		SELECT * FROM stock s 
		WHERE s.store_id=in_store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stock_by_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_stock_by_item_id
(
		IN in_item_id bigint 
) 
BEGIN 
		SELECT * FROM stock s 
		WHERE s.item_id=in_item_id ORDER BY s.store_id,s.item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_zero_qty_stock;
DELIMITER //
CREATE PROCEDURE sp_delete_zero_qty_stock() 
BEGIN 
		DELETE FROM stock WHERE currentqty=0;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stock_by_store_item_batch;
DELIMITER //
CREATE PROCEDURE sp_search_stock_by_store_item_batch
(
		IN in_store_id int,
		IN in_item_id bigint,
		IN in_batchno varchar(100) 
) 
BEGIN 

		SET @BatchNo='';
		if (in_batchno is not null) then 
			SET @BatchNo=in_batchno;
		end if;
		SELECT * FROM stock s 
		WHERE s.store_id=in_store_id AND s.item_id=in_item_id AND s.batchno=@BatchNo;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_stock;
DELIMITER //
CREATE PROCEDURE sp_insert_stock
(
		IN in_store_id int,
		IN in_item_id bigint,
		IN in_batchno varchar(100),
		IN in_currentqty float,
		IN in_item_mnf_date date,
		IN in_item_exp_date date  
) 
BEGIN 
		SET @new_id=0;
		CALL sp_get_new_id("stock","stock_id",@new_id);

		SET @in_item_exp_date=NULL;
		if (in_item_exp_date is not null) then
			set @in_item_exp_date=in_item_exp_date;
		end if;

		SET @in_item_mnf_date=NULL;
		if (in_item_mnf_date is not null) then
			set @in_item_mnf_date=in_item_mnf_date;
		end if;

		INSERT INTO stock
		(
		stock_id,
		store_id,
		item_id,
		batchno,
		currentqty,
		item_mnf_date,
		item_exp_date 
	) 
    VALUES
	(
		@new_id,
		in_store_id,
		in_item_id,
		in_batchno,
		in_currentqty,
		@in_item_mnf_date,
		@in_item_exp_date 
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_add_stock_by_store_item_batch;
DELIMITER //
CREATE PROCEDURE sp_add_stock_by_store_item_batch
(
		IN in_store_id int,
		IN in_item_id bigint,
		IN in_batchno varchar(100),
		IN in_qty float 
) 
BEGIN 
		UPDATE Stock s SET currentqty=currentqty+in_qty 
		WHERE s.store_id=in_store_id AND s.item_id=in_item_id AND s.batchno=in_batchno;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_subtract_stock_by_store_item_batch;
DELIMITER //
CREATE PROCEDURE sp_subtract_stock_by_store_item_batch
(
		IN in_store_id int,
		IN in_item_id bigint,
		IN in_batchno varchar(100),
		IN in_qty float 
) 
BEGIN 
		UPDATE Stock s SET currentqty=currentqty-in_qty 
		WHERE s.store_id=in_store_id AND s.item_id=in_item_id AND s.batchno=in_batchno;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_pay;
DELIMITER //
CREATE PROCEDURE sp_insert_pay
(
	IN in_transaction_id bigint,
	IN in_pay_date date,
	IN in_paid_amount float,
	IN in_pay_method_id int,
	IN in_add_user_detail_id int,
	IN in_edit_user_detail_id int,
	IN in_add_date datetime,
	IN in_edit_date datetime,
	IN in_points_spent float,
	IN in_points_spent_amount float,
	IN in_delete_pay_id bigint,
	IN in_pay_ref_no varchar(100),
	IN in_pay_category varchar(10),
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_store_id int,
	OUT out_pay_id bigint 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("pay","pay_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @edit_datetime=null;
	SET @edit_user_detail_id=null;
	
	SET @delete_pay_id=0;
	if (in_delete_pay_id=0) then 
		SET @delete_pay_id=@new_id;
	end if;
	if (in_delete_pay_id!=0) then 
		SET @delete_pay_id=in_delete_pay_id;
	end if;
	SET @transaction_id=null;
	if (in_transaction_id!=0) then 
		SET @transaction_id=in_transaction_id;
	end if;

	INSERT INTO pay
	(
		pay_id,
		transaction_id,
		pay_date,
		paid_amount,
		pay_method_id,
		add_user_detail_id,
		edit_user_detail_id,
		add_date,
		edit_date,
		points_spent,
		points_spent_amount,
		delete_pay_id,
		pay_ref_no,
		pay_category,
		bill_transactor_id,
		transaction_type_id,
		transaction_reason_id,
		store_id
	) 
    VALUES
	(
		@new_id,
		@transaction_id,
		in_pay_date,
		in_paid_amount,
		in_pay_method_id,
		in_add_user_detail_id,
		@edit_user_detail_id,
		@cur_sys_datetime,
		@edit_datetime,
		in_points_spent,
		in_points_spent_amount,
		@delete_pay_id,
		in_pay_ref_no,
		in_pay_category,
		in_bill_transactor_id,
		in_transaction_type_id,
		in_transaction_reason_id,
		in_store_id
	); 
SET out_pay_id=@new_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_pay;
DELIMITER //
CREATE PROCEDURE sp_update_pay
(
	IN in_pay_id bigint,
	IN in_transaction_id bigint,
	IN in_pay_date date,
	IN in_paid_amount float,
	IN in_pay_method_id int,
	IN in_edit_user_detail_id int,
	IN in_edit_date datetime,
	IN in_points_spent float,
	IN in_points_spent_amount float,
	IN in_delete_pay_id bigint,
	IN in_pay_ref_no varchar(100),
	IN in_pay_category varchar(10),
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_store_id int
) 
BEGIN 
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	SET @delete_pay_id=0;
	if (in_delete_pay_id=0) then 
		SET @delete_pay_id=@new_id;
	end if;
	if (in_delete_pay_id!=0) then 
		SET @delete_pay_id=in_delete_pay_id;
	end if;
	SET @transaction_id=null;
	if (in_transaction_id!=0) then 
		SET @transaction_id=in_transaction_id;
	end if;

	UPDATE pay SET 
		transaction_id=@transaction_id,
		pay_date=in_pay_date,
		paid_amount=in_paid_amount,
		pay_method_id=in_pay_method_id,
		edit_user_detail_id=in_edit_user_detail_id,
		edit_date=@cur_sys_datetime,
		points_spent=in_points_spent,
		points_spent_amount=in_points_spent_amount,
		delete_pay_id=@delete_pay_id,
		pay_ref_no=in_pay_ref_no,
		pay_category=in_pay_category,
		bill_transactor_id=in_bill_transactor_id,
		transaction_type_id=in_transaction_type_id,
		transaction_reason_id=in_transaction_reason_id,
		store_id=in_store_id 
	WHERE pay_id=in_pay_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_pay_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_pay_id
(
	IN in_pay_id bigint
) 
BEGIN 
	SELECT * FROM pay 
	WHERE pay_id=in_pay_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transaction_id
(
	IN in_transaction_id bigint
) 
BEGIN 
	SELECT * FROM pay 
	WHERE transaction_id=in_transaction_id ORDER BY pay_id ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transaction_id_type;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transaction_id_type
(
	IN in_transaction_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
	SELECT * FROM pay p,transaction t 
	WHERE p.transaction_id=in_transaction_id AND p.transaction_id=t.transaction_id AND t.transaction_type_id=in_transaction_type_id 
	ORDER BY pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transaction_id_paycat;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transaction_id_paycat
(
	IN in_transaction_id bigint,
	IN in_pay_category varchar(10)
) 
BEGIN 
	SELECT * FROM pay p,transaction t 
	WHERE p.transaction_id=in_transaction_id AND p.transaction_id=t.transaction_id AND p.pay_category=in_pay_category 
	ORDER BY pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transaction_number_paycat;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transaction_number_paycat
(
	IN in_transaction_number varchar(50),
	IN in_pay_category varchar(10)
) 
BEGIN 
	SELECT * FROM pay p,transaction t 
	WHERE t.transaction_number=in_transaction_number AND p.transaction_id=t.transaction_id AND p.pay_category=in_pay_category 
	ORDER BY pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_first_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_first_by_transaction_id
(
	IN in_transaction_id bigint
) 
BEGIN 
	SELECT * FROM pay 
	WHERE transaction_id=in_transaction_id having min(pay_id);
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_first_by_transaction_number;
DELIMITER //
CREATE PROCEDURE sp_search_pay_first_by_transaction_number
(
	IN in_transaction_number varchar(50)
) 
BEGIN 
	SELECT p.* FROM pay p,transaction t 
	WHERE p.transaction_id=t.transaction_id and t.transaction_number=in_transaction_number 
	having min(p.pay_id);
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transactor_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transactor_id
(
	IN in_transactor_id bigint
) 
BEGIN 
	SELECT * FROM pay p 
	INNER JOIN transaction t ON p.transaction_id=t.transaction_id AND t.transactor_id=in_transactor_id 
	ORDER BY p.pay_id DESC,p.transaction_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_transactor_transtype;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_transactor_transtype
(
	IN in_transactor_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
	SELECT * FROM pay p 
	INNER JOIN transaction t ON p.transaction_id=t.transaction_id AND t.transactor_id=in_transactor_id AND t.transaction_type_id=in_transaction_type_id 
	ORDER BY p.transaction_id DESC,p.pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_bill_transactor_transtype;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_bill_transactor_transtype
(
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int
) 
BEGIN 
	SELECT * FROM pay p 
	INNER JOIN transaction t ON p.transaction_id=t.transaction_id AND t.bill_transactor_id=in_bill_transactor_id AND t.transaction_type_id=in_transaction_type_id 
	ORDER BY p.transaction_id DESC,p.pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_by_bill_transactor_paycat;
DELIMITER //
CREATE PROCEDURE sp_search_pay_by_bill_transactor_paycat
(
	IN in_bill_transactor_id bigint,
	IN in_pay_category varchar(10)
) 
BEGIN 
	SELECT * FROM pay p 
	WHERE bill_transactor_id=in_bill_transactor_id AND pay_category=in_pay_category 
	ORDER BY p.pay_id DESC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_total_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_total_by_transaction_id
(
	IN in_transaction_id bigint
) 
BEGIN 
	SELECT sum(paid_amount+points_spent_amount) AS sum_paid_amount FROM pay 
	WHERE transaction_id=in_transaction_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_count_by_delete_pay_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_count_by_delete_pay_id
(
	IN in_delete_pay_id bigint
) 
BEGIN 
	SELECT count(*) AS count_delete_pay FROM pay 
	WHERE delete_pay_id=in_delete_pay_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_branch;
DELIMITER //
CREATE PROCEDURE sp_insert_branch
(
	IN in_branch_code varchar(20),
	IN in_branch_name varchar(100) 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("branch","branch_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO branch
	(
		branch_id,
		branch_code,
		branch_name,
		add_date,
		edit_date
	) 
    VALUES
	(
		@new_id,
		in_branch_code,
		in_branch_name,
		@cur_sys_datetime,
		@cur_sys_datetime
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_branch;
DELIMITER //
CREATE PROCEDURE sp_update_branch
(
	IN in_branch_id bigint,
	IN in_branch_code varchar(20),
	IN in_branch_name varchar(100)  
) 
BEGIN 
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE branch SET 
		branch_id=in_branch_id,
		branch_code=in_branch_code,
		branch_name=in_branch_name,
		edit_date=@cur_sys_datetime 
	WHERE branch_id=in_branch_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_branch_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_branch_by_id
(
	IN in_branch_id bigint 
) 
BEGIN 
		SELECT * FROM branch b 
		WHERE b.branch_id=in_branch_id 
		ORDER BY branch_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_branch_by_code;
DELIMITER //
CREATE PROCEDURE sp_search_branch_by_code
(
	IN in_branch_code varchar(20)
) 
BEGIN 
		SELECT * FROM branch b 
		WHERE b.branch_code=in_branch_code 
		ORDER BY branch_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_branch_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_branch_by_name
(
	IN in_branch_name varchar(100)
) 
BEGIN 
		SELECT * FROM branch b 
		WHERE b.branch_name=in_branch_name 
		ORDER BY branch_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_branch_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_branch_by_none()
BEGIN 
		SELECT * FROM branch b 
		ORDER BY branch_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_points_card;
DELIMITER //
CREATE PROCEDURE sp_insert_points_card
(
	IN in_card_number varchar(10),
	IN in_card_holder varchar(100),
	IN in_email varchar(100),
	IN in_phone varchar(100),
	IN in_physical_address varchar(255),
	IN in_tax_identity varchar(100),
	IN in_account_details varchar(255),
	IN in_website varchar(100),
	IN in_cpname varchar(100),
	IN in_cptitle varchar(100),
	IN in_cpphone varchar(100),
	IN in_cpemail varchar(100),
	IN in_reg_branch_id int,
	IN in_points_balance float,
	IN in_add_date datetime,
	IN in_add_user varchar(100),
	IN in_edit_date datetime,
	IN in_edit_user varchar(100)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("points_card","points_card_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO points_card
	(
		points_card_id,
		card_number,
		card_holder,
		email,
		phone,
		website,
		cpname,
		cptitle,
		cpphone,
		cpemail,
		physical_address,
		tax_identity,
		account_details,
		reg_branch_id,
		points_balance,
		add_date,
		add_user,
		edit_date,
		edit_user
	) 
    VALUES
	(
		@new_id,
		in_card_number,
		in_card_holder,
		in_email,
		in_phone,
		in_website,
		in_cpname,
		in_cptitle,
		in_cpphone,
		in_cpemail,
		in_physical_address,
		in_tax_identity,
		in_account_details,
		in_reg_branch_id,
		in_points_balance,
		@cur_sys_datetime,
		in_add_user,
		@cur_sys_datetime,
		in_edit_user
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_points_card;
DELIMITER //
CREATE PROCEDURE sp_update_points_card
(
	IN in_points_card_id bigint,
	IN in_card_number varchar(10),
	IN in_card_holder varchar(100),
	IN in_email varchar(100),
	IN in_phone varchar(100),
	IN in_physical_address varchar(255),
	IN in_tax_identity varchar(100),
	IN in_account_details varchar(255),
	IN in_website varchar(100),
	IN in_cpname varchar(100),
	IN in_cptitle varchar(100),
	IN in_cpphone varchar(100),
	IN in_cpemail varchar(100),
	IN in_reg_branch_id int,
	IN in_points_balance float,
	IN in_edit_date datetime,
	IN in_edit_user varchar(100)
) 
BEGIN 
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE points_card SET 
		points_card_id=in_points_card_id,
		card_number=in_card_number,
		card_holder=in_card_holder,
		email=in_email,
		phone=in_phone,
		website=in_website,
		cpname=in_cpname,
		cptitle=in_cptitle,
		cpphone=in_cpphone,
		cpemail=in_cpemail,
		physical_address=in_physical_address,
		tax_identity=in_tax_identity,
		account_details=in_account_details,
		reg_branch_id=in_reg_branch_id,
		points_balance=in_points_balance,
		edit_date=@cur_sys_datetime,
		edit_user=in_edit_user
	WHERE points_card_id=in_points_card_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_points_card_balance;
DELIMITER //
CREATE PROCEDURE sp_update_points_card_balance
(
	IN in_card_number varchar(10),
	IN in_points float 
) 
BEGIN 
		UPDATE points_card p 
		SET p.points_balance=in_points 
		WHERE p.card_number=in_card_number;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_add_points_card_balance_by_card_no;
DELIMITER //
CREATE PROCEDURE sp_add_points_card_balance_by_card_no
(
	IN in_card_number varchar(10),
	IN in_points float 
) 
BEGIN 
		UPDATE points_card p 
		SET p.points_balance=p.points_balance + in_points 
		WHERE p.card_number=in_card_number;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_add_points_card_balance_by_card_id;
DELIMITER //
CREATE PROCEDURE sp_add_points_card_balance_by_card_id
(
	IN in_points_card_id bigint,
	IN in_points float 
) 
BEGIN 
		UPDATE points_card p 
		SET p.points_balance=p.points_balance + in_points 
		WHERE p.points_card_id=in_points_card_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_subtract_points_card_balance;
DELIMITER //
CREATE PROCEDURE sp_subtract_points_card_balance
(
	IN in_card_number varchar(10),
	IN in_points float 
) 
BEGIN 
		UPDATE points_card p 
		SET p.points_balance=p.points_balance - in_points 
		WHERE p.card_number=in_card_number;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_card_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_points_card_by_id
(
	IN in_points_card_id bigint 
) 
BEGIN 
		SELECT * FROM points_card p 
		WHERE p.points_card_id=in_points_card_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_card_by_card_number;
DELIMITER //
CREATE PROCEDURE sp_search_points_card_by_card_number
(
	IN in_card_number varchar(10) 
) 
BEGIN 
		SELECT * FROM points_card p 
		WHERE p.card_number=in_card_number;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_card_by_card_number_holder_names;
DELIMITER //
CREATE PROCEDURE sp_search_points_card_by_card_number_holder_names
(
	IN in_card_number varchar(10) 
) 
BEGIN 
		SELECT * FROM points_card p 
		WHERE p.card_number=in_card_number OR p.card_holder LIKE concat('%',in_card_number,'%') 
		ORDER BY card_holder ASC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_card_by_holder_names;
DELIMITER //
CREATE PROCEDURE sp_search_points_card_by_holder_names
(
	IN in_card_holder varchar(100) 
) 
BEGIN 
		SELECT * FROM points_card p 
		WHERE p.card_holder=in_card_holder;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_points_transaction;
DELIMITER //
CREATE PROCEDURE sp_insert_points_transaction
(
	IN in_points_card_id bigint,
	IN in_transaction_date date,
	IN in_points_awarded float,
	IN in_points_spent float,
	IN in_transaction_id bigint,
	IN in_trans_branch_id int,
	IN in_add_date datetime,
	IN in_add_user varchar(100),
	IN in_edit_date datetime,
	IN in_edit_user varchar(100),
	IN in_points_spent_amount float
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("points_transaction","points_transaction_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO points_transaction
	(
		points_transaction_id,
		points_card_id,
		transaction_date,
		points_awarded,
		points_spent,
		transaction_id,
		trans_branch_id,
		add_date,
		add_user,
		edit_date,
		edit_user,
		points_spent_amount 
	) 
    VALUES
	(
		@new_id,
		in_points_card_id,
		in_transaction_date,
		in_points_awarded,
		in_points_spent,
		in_transaction_id,
		in_trans_branch_id,
		@cur_sys_datetime,
		in_add_user,
		@cur_sys_datetime,
		in_edit_user,
		in_points_spent_amount
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_points_transaction;
DELIMITER //
CREATE PROCEDURE sp_update_points_transaction
(
	IN in_points_transaction_id bigint,
	IN in_points_card_id bigint,
	IN in_transaction_date date,
	IN in_points_awarded float,
	IN in_points_spent float,
	IN in_transaction_id bigint,
	IN in_trans_branch_id int,
	IN in_edit_date datetime,
	IN in_edit_user varchar(100),
	IN in_points_spent_amount float
) 
BEGIN 
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE points_transaction SET 
		points_card_id=in_points_card_id,
		transaction_date=in_transaction_date,
		points_awarded=in_points_awarded,
		points_spent=in_points_spent,
		transaction_id=in_transaction_id,
		trans_branch_id=in_trans_branch_id,
		edit_date=@cur_sys_datetime,
		edit_user=@cur_sys_datetime,
		points_spent_amount=in_points_spent_amount 
	WHERE points_transaction_id=in_points_transaction_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_transaction_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_points_transaction_by_id
(
	IN in_points_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM points_transaction p 
		WHERE p.points_transaction_id=in_points_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_points_transaction_by_card_id;
DELIMITER //
CREATE PROCEDURE sp_search_points_transaction_by_card_id
(
	IN in_points_card_id bigint 
) 
BEGIN 
		SELECT * FROM points_transaction p 
		WHERE p.points_card_id=in_points_card_id 
		ORDER BY points_transaction_id DESC;
END//
DELIMITER ;


DROP PROCEDURE IF EXISTS sp_insert_group_detail;
DELIMITER //
CREATE PROCEDURE sp_insert_group_detail
(
	IN in_group_name varchar(50),
	IN in_is_active varchar(3) 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("group_detail","group_detail_id",@new_id);

	INSERT INTO group_detail
	(
		group_detail_id,
		group_name,
		is_active 
	) 
    VALUES
	(
		@new_id,
		in_group_name,
		in_is_active
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_group_detail;
DELIMITER //
CREATE PROCEDURE sp_update_group_detail
(
	IN in_group_detail_id bigint,
	IN in_group_name varchar(50),
	IN in_is_active varchar(3)  
) 
BEGIN 
	UPDATE group_detail SET 
		group_detail_id=in_group_detail_id,
		group_name=in_group_name,
		is_active=in_is_active 
	WHERE group_detail_id=in_group_detail_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_detail_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_group_detail_by_id
(
	IN in_group_detail_id int 
) 
BEGIN 
		SELECT * FROM group_detail g 
		WHERE g.group_detail_id=in_group_detail_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_detail_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_group_detail_by_name
(
	IN in_group_name varchar(20)
) 
BEGIN 
		SELECT * FROM group_detail g 
		WHERE g.group_name=in_group_name;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_detail_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_group_detail_by_none() 
BEGIN 
		SELECT * FROM group_detail g 
		ORDER BY group_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_group_user;
DELIMITER //
CREATE PROCEDURE sp_insert_group_user
(
	IN in_group_detail_id int,
	IN in_user_detail_id int 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("group_user","group_user_id",@new_id);

	INSERT INTO group_user
	(
		group_user_id,
		group_detail_id,
		user_detail_id 
	) 
    VALUES
	(
		@new_id,
		in_group_detail_id,
		in_user_detail_id
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_group_right;
DELIMITER //
CREATE PROCEDURE sp_insert_group_right
(
	IN in_store_id int,
	IN in_group_detail_id int,
	IN in_function_name varchar(50),
	IN in_allow_view varchar(3),
	IN in_allow_add varchar(3),
	IN in_allow_edit varchar(3),
	IN in_allow_delete varchar(3)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("group_right","group_right_id",@new_id);
	INSERT INTO group_right
	(
		group_right_id,
		store_id,
		group_detail_id,
		function_name,
		allow_view,
		allow_add,
		allow_edit,
		allow_delete
	) 
    VALUES
	(
		@new_id,
		in_store_id,
		in_group_detail_id,
		in_function_name,
		in_allow_view,
		in_allow_add,
		in_allow_edit,
		in_allow_delete
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_group_right;
DELIMITER //
CREATE PROCEDURE sp_update_group_right
(
	IN in_group_right_id int,
	IN in_store_id int,
	IN in_group_detail_id int,
	IN in_function_name varchar(50),
	IN in_allow_view varchar(3),
	IN in_allow_add varchar(3),
	IN in_allow_edit varchar(3),
	IN in_allow_delete varchar(3)
) 
BEGIN 
	UPDATE group_right SET 
		store_id=in_store_id,
		group_detail_id=in_group_detail_id,
		function_name=in_function_name,
		allow_view=in_allow_view,
		allow_add=in_allow_add,
		allow_edit=in_allow_edit,
		allow_delete=in_allow_delete
	WHERE group_right_id=in_group_right_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_by_id
(
	IN in_group_right_id int
) 
BEGIN 
	SELECT * FROM group_right 
	WHERE group_right_id=in_group_right_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_by_group_id;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_by_group_id
(
	IN in_group_detail_id int
) 
BEGIN 
	SELECT * FROM group_right 
	WHERE group_detail_id=in_group_detail_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_by_store_id;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_by_store_id
(
	IN in_store_id int
) 
BEGIN 
	SELECT * FROM group_right 
	WHERE store_id=in_store_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_by_group_id_store_id;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_by_group_id_store_id
(
	IN in_group_detail_id int,
	IN in_store_id int
) 
BEGIN 
	SELECT * FROM group_right 
	WHERE store_id=in_store_id AND group_detail_id=in_group_detail_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_by_group_id_store_id_function;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_by_group_id_store_id_function
(
	IN in_group_detail_id int,
	IN in_store_id int,
	IN in_function_name varchar(50) 
) 
BEGIN 
	SELECT * FROM group_right 
	WHERE store_id=in_store_id AND group_detail_id=in_group_detail_id AND function_name=in_function_name; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_group_right_get_active_rights_by_store_user;
DELIMITER //
CREATE PROCEDURE sp_search_group_right_get_active_rights_by_store_user
(
	IN in_store_id int,
	IN in_user_detail_id bigint
) 
BEGIN 
	SELECT * FROM group_right gr 
	INNER JOIN group_detail gd ON gr.group_detail_id=gd.group_detail_id 
	AND gr.store_id=in_store_id AND gd.is_active='Yes' AND gd.group_detail_id IN
	(
		SELECT gu.group_detail_id FROM group_user gu 
		INNER JOIN user_detail ud ON gu.user_detail_id=ud.user_detail_id 
		AND ud.is_user_gen_admin='No' AND ud.is_user_locked='No' AND ud.user_detail_id=in_user_detail_id
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_item_map;
DELIMITER //
CREATE PROCEDURE sp_insert_item_map
(
	IN in_big_item_id bigint,
	IN in_small_item_id bigint,
	IN in_fraction_qty float,
	IN in_position int,
	IN in_map_group_id bigint 
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("item_map","item_map_id",@new_id);
	INSERT INTO item_map
	(
		item_map_id,
		big_item_id,
		small_item_id,
		fraction_qty,
		position,
		map_group_id 
	) 
    VALUES
	(
		@new_id,
		in_big_item_id,
		in_small_item_id,
		in_fraction_qty,
		in_position,
		in_map_group_id
	);  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_item_map;
DELIMITER //
CREATE PROCEDURE sp_update_item_map
(
	IN in_item_map_id bigint,
	IN in_big_item_id bigint,
	IN in_small_item_id bigint,
	IN in_fraction_qty float,
	IN in_position int,
	IN in_map_group_id bigint 
) 
BEGIN 
	UPDATE item_map SET 
	big_item_id=in_big_item_id,
	small_item_id=in_small_item_id,
	fraction_qty=in_fraction_qty,
	position=in_position,
	map_group_id=in_map_group_id 
	WHERE item_map_id=in_item_map_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_item_map;
DELIMITER //
CREATE PROCEDURE sp_delete_item_map
(
	IN in_item_map_id bigint
) 
BEGIN 
	DELETE FROM item_map 
	WHERE item_map_id=in_item_map_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_id
(
	IN in_item_map_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE item_map_id=in_item_map_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_small_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_small_item_id
(
	IN in_small_item_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE small_item_id=in_small_item_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_big_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_big_item_id
(
	IN in_big_item_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE big_item_id=in_big_item_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_small_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_small_item_id
(
	IN in_small_item_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE small_item_id=in_small_item_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_big_small_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_big_small_item_id
(
	IN in_big_item_id bigint,
	IN in_small_item_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE in_big_item_id IN(big_item_id,small_item_id) OR in_small_item_id IN(small_item_id,big_item_id) 
	ORDER BY position ASC;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_by_map_group_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_by_map_group_id
(
	IN in_map_group_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE map_group_id=in_map_group_id 
	ORDER BY position ASC;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_map_get_count_map_group_id;
DELIMITER //
CREATE PROCEDURE sp_search_item_map_get_count_map_group_id
(
	IN in_map_group_id bigint
) 
BEGIN 
	SELECT * FROM item_map 
	WHERE map_group_id=in_map_group_id;  
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_discount_package;
DELIMITER //
CREATE PROCEDURE sp_insert_discount_package
(
	IN in_package_name varchar(50),
	IN in_start_date datetime,
	IN in_end_date datetime
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("discount_package","discount_package_id",@new_id);

	INSERT INTO discount_package
	(
		discount_package_id,
		package_name,
		start_date,
		end_date
	) 
    VALUES
	(
		@new_id,
		in_package_name,
		in_start_date,
		in_end_date
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_discount_package;
DELIMITER //
CREATE PROCEDURE sp_update_discount_package
(
	IN in_discount_package_id int,
	IN in_package_name varchar(50),
	IN in_start_date datetime,
	IN in_end_date datetime
) 
BEGIN 
	UPDATE discount_package SET 
		package_name=in_package_name,
		start_date=in_start_date,
		end_date=in_end_date 
	WHERE discount_package_id=in_discount_package_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_by_id
(
	IN in_discount_package_id int 
) 
BEGIN 
		SELECT * FROM discount_package d 
		WHERE d.discount_package_id=in_discount_package_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_by_name
(
	IN in_discount_package_name varchar(50)
) 
BEGIN 
		SELECT * FROM discount_package d 
		WHERE d.package_name=in_package_name OR d.package_name LIKE concat('%',in_package_name,'%') 
		ORDER BY d.package_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_by_none() 
BEGIN 
		SELECT * FROM discount_package 
		ORDER BY end_date DESC,package_name ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_discount_package_item;
DELIMITER //
CREATE PROCEDURE sp_insert_discount_package_item
(
	IN in_discount_package_id bigint,
	IN in_store_id int,
	IN in_item_id int,
	IN in_item_qty float,
	IN in_wholesale_discount_amt float,
	IN in_retailsale_discount_amt float
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("discount_package_item","discount_package_item_id",@new_id);

	INSERT INTO discount_package_item
	(
		discount_package_item_id,
		discount_package_id,
		store_id,
		item_id,
		item_qty,
		wholesale_discount_amt,
		retailsale_discount_amt
	) 
    VALUES
	(
		@new_id,
		in_discount_package_id,
		in_store_id,
		in_item_id,
		in_item_qty,
		in_wholesale_discount_amt,
		in_retailsale_discount_amt
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_discount_package_item;
DELIMITER //
CREATE PROCEDURE sp_update_discount_package_item
(
	IN in_discount_package_item_id bigint,
	IN in_discount_package_id int,
	IN in_store_id int,
	IN in_item_id int,
	IN in_item_qty float,
	IN in_wholesale_discount_amt float,
	IN in_retailsale_discount_amt float
) 
BEGIN 
	UPDATE discount_package_item SET 
		discount_package_id=in_discount_package_id,
		store_id=in_store_id,
		item_id=in_item_id,
		item_qty=in_item_qty,
		wholesale_discount_amt=in_wholesale_discount_amt,
		retailsale_discount_amt=in_retailsale_discount_amt 
	WHERE discount_package_item_id=in_discount_package_item_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_id
(
	IN in_discount_package_item_id bigint 
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.discount_package_item_id=in_discount_package_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_package_id;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_package_id
(
	IN in_discount_package_id int 
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.discount_package_id=in_discount_package_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_package_store;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_package_store
(
	IN in_discount_package_id int,
	In in_store_id int
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.discount_package_id=in_discount_package_id AND d.store_id=in_store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_store_item;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_store_item
(
	In in_store_id int,
	IN in_item_id bigint
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.store_id=in_store_id AND d.item_id=in_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_package_store_item;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_package_store_item
(
	IN in_discount_package_id int,
	In in_store_id int,
	IN in_item_id bigint
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.discount_package_id=in_discount_package_id AND d.store_id=in_store_id AND d.item_id=in_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_item_id
(
	IN in_item_id bigint 
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.item_id=in_item_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_store_id;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_store_id
(
	IN in_store_id int 
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.store_id=in_store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_store_item_qty_all;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_store_item_qty_all
(
	IN in_store_id int,
	IN in_item_id bigint,
	IN in_item_qty float
) 
BEGIN 
		SELECT * FROM discount_package_item d 
		WHERE d.store_id=in_store_id AND d.item_id=in_item_id AND d.item_qty=in_item_qty;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_discount_package_item_by_store_item_qty_active;
DELIMITER //
CREATE PROCEDURE sp_search_discount_package_item_by_store_item_qty_active
(
	IN in_store_id int,
	IN in_item_id bigint,
	IN in_item_qty float
) 
BEGIN 
		SELECT d.*,dd.start_date,dd.end_date FROM discount_package_item d 
		INNER JOIN discount_package dd ON d.discount_package_id=dd.discount_package_id 
		AND d.store_id=in_store_id AND d.item_id=in_item_id AND d.item_qty=in_item_qty AND 
		Now() BETWEEN dd.start_date AND dd.end_date;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_company_setting_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_company_setting_by_id
(
	IN in_company_setting_id int
) 
BEGIN 
		SELECT * FROM company_setting c 
		WHERE c.company_setting_id=in_company_setting_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_company_setting;
DELIMITER //
CREATE PROCEDURE sp_update_company_setting
(
	IN in_ECompanyName varchar(100),
	IN in_EPhysicalAddress varchar(250),
	IN in_EPhone varchar(100),
	IN in_EFax varchar(100),
	IN in_EEmail varchar(100),
	IN in_EWebsite varchar(100),
	IN in_ELogoUrl varchar(100),
	IN in_ESloghan varchar(100),
	IN in_ECurrencyUsed varchar(10),
	IN in_EVatPerc float,
	IN in_EIsAllowDiscount varchar(3),
	IN in_EIsAllowDebt varchar(3),
	IN in_EIsCustomerMandatory varchar(3),
	IN in_EIsSupplierMandatory varchar(3),
	IN in_EIsVatInclusive varchar(3),
	IN in_EIsTradeDiscountVatLiable varchar(3),
	IN in_EIsCashDiscountVatLiable varchar(3),
	IN in_EIsMapItemsActive varchar(3),
	IN in_EBranchCode varchar(20),
	IN in_EBranchId int,
	IN in_EAwardAmountPerPoint float,
	IN in_ESpendAmountPerPoint float,
	IN in_ETaxIdentity varchar(50),
	IN in_ESalesReceiptName varchar(20),
	IN in_EIsShowDeveloper varchar(3),
	IN in_EDeveloperEmail varchar(100),
	IN in_EDeveloperPhone varchar(100),
	IN in_EShowLogoInvoice varchar(3),
	IN in_EIsAllowAutoUnpack varchar(3),
	IN in_ETimeZone varchar(20),
	IN in_EDateFormat varchar(20),
	IN in_ELicenseKey varchar(254),
	IN in_ESalesReceiptVersion int,
	IN in_EEnforceTransUserSelect varchar(3),
	IN in_EShowBranchInvoice varchar(3),
	IN in_EShowStoreInvoice varchar(3),
	IN in_EShowVatAnalysisInvoice varchar(3),
	IN in_EStoreEquivName varchar(20)
) 
BEGIN 
	UPDATE company_setting SET 
		company_name=in_ECompanyName,
		physical_address=in_EPhysicalAddress,
		phone=in_EPhone,
		fax=in_EFax,
		email=in_EEmail,
		website=in_EWebsite,
		logo_url=in_ELogoUrl,
		sloghan=in_ESloghan,
		currency_used=in_ECurrencyUsed,
		vat_perc=in_EVatPerc,
		is_allow_discount=in_EIsAllowDiscount,
		is_allow_debt=in_EIsAllowDebt,
		is_customer_mandatory=in_EIsCustomerMandatory,
		is_supplier_mandatory=in_EIsSupplierMandatory,
		is_vat_inclusive=in_EIsVatInclusive,
		is_trade_discount_vat_liable=in_EIsTradeDiscountVatLiable,
		is_cash_discount_vat_liable=in_EIsCashDiscountVatLiable,
		is_map_items_active=in_EIsMapItemsActive,
		branch_code=in_EBranchCode,
		branch_id=in_EBranchId,
		award_amount_per_point=in_EAwardAmountPerPoint,
		spend_amount_per_point=in_ESpendAmountPerPoint,
		tax_identity=in_ETaxIdentity,
		sales_receipt_name=in_ESalesReceiptName,
		is_show_developer=in_EIsShowDeveloper,
		developer_email=in_EDeveloperEmail,
		developer_phone=in_EDeveloperPhone,
		show_logo_invoice=in_EShowLogoInvoice,
		show_branch_invoice=in_EShowBranchInvoice,
		show_store_invoice=in_EShowStoreInvoice,
		is_allow_auto_unpack=in_EIsAllowAutoUnpack,
		time_zone=in_ETimeZone,
		date_format=in_EDateFormat,
		license_key=in_ELicenseKey,
		sales_receipt_version=in_ESalesReceiptVersion,
		enforce_trans_user_select=in_EEnforceTransUserSelect,
		show_vat_analysis_invoice=in_EShowVatAnalysisInvoice,
		store_equiv_name=in_EStoreEquivName 
	WHERE company_setting_id=1; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_company_setting_logo;
DELIMITER //
CREATE PROCEDURE sp_update_company_setting_logo
(
	IN in_logo blob 
) 
BEGIN 
	UPDATE company_setting SET logo=in_logo 
	WHERE company_setting_id=1; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_stage_points_transaction;
DELIMITER //
CREATE PROCEDURE sp_insert_stage_points_transaction
(
	IN in_points_card_id bigint,
	IN in_transaction_date date,
	IN in_points_awarded float,
	IN in_points_spent float,
	IN in_transaction_id bigint,
	IN in_trans_branch_id int,
	IN in_add_date datetime,
	IN in_add_user varchar(100),
	IN in_edit_date datetime,
	IN in_edit_user varchar(100),
	IN in_points_spent_amount float
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("stage_points_transaction","stage_points_transaction_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO stage_points_transaction
	(
		stage_points_transaction_id,
		points_card_id,
		transaction_date,
		points_awarded,
		points_spent,
		transaction_id,
		trans_branch_id,
		add_date,
		add_user,
		edit_date,
		edit_user,
		points_spent_amount 
	) 
    VALUES
	(
		@new_id,
		in_points_card_id,
		in_transaction_date,
		in_points_awarded,
		in_points_spent,
		in_transaction_id,
		in_trans_branch_id,
		@cur_sys_datetime,
		in_add_user,
		@cur_sys_datetime,
		in_edit_user,
		in_points_spent_amount
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stage_points_transaction_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_stage_points_transaction_by_none() 
BEGIN 
		SELECT * FROM stage_points_transaction; 
END// 
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stage_points_transaction_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_stage_points_transaction_by_id
(
	IN in_stage_points_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM stage_points_transaction 
		WHERE stage_points_transaction_id=in_stage_points_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_stage_points_transaction_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_search_stage_points_transaction_by_transaction_id
(
	IN in_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM stage_points_transaction 
		WHERE transaction_id=in_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_stage_points_transaction_by_id;
DELIMITER //
CREATE PROCEDURE sp_delete_stage_points_transaction_by_id
(
	IN in_stage_points_transaction_id bigint 
) 
BEGIN 
		DELETE FROM stage_points_transaction 
		WHERE stage_points_transaction_id=in_stage_points_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_stage_points_transaction_by_transaction_id;
DELIMITER //
CREATE PROCEDURE sp_delete_stage_points_transaction_by_transaction_id
(
	IN in_transaction_id bigint 
) 
BEGIN 
		DELETE FROM stage_points_transaction 
		WHERE transaction_id=in_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transaction_approve;
DELIMITER //
CREATE PROCEDURE sp_insert_transaction_approve
(
	IN in_transaction_id bigint,
	IN in_function_name varchar(50),
	IN in_user_detail_id int
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_approve","transaction_approve_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO transaction_approve
	(
		transaction_approve_id,
		transaction_id,
		function_name,
		user_detail_id,
		approve_date
	) 
    VALUES
	(
		@new_id,
		in_transaction_id,
		in_function_name,
		in_user_detail_id,
		@cur_sys_datetime
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transaction;
DELIMITER //
CREATE PROCEDURE sp_report_transaction
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_store2_id int,
	IN in_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_add_user_detail_id int,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date_from datetime,
	IN in_edit_date_to datetime,
	IN in_transaction_user_detail_id int,
	IN in_bill_transactor_id bigint 
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @ToStore='';
	SET @Transactor='';
	SET @TransType='';
	SET @TransReason='';
	SET @AddUser='';
	SET @AddDate='';
	SET @EditUser='';
	SET @EditDate='';
	SET @TransactionUser='';
	SET @BillTransactor='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (t.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (t.add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if ((in_edit_date_from is not null) and (in_edit_date_to is not null)) then 
		SET @EditDate=concat(" AND (t.edit_date BETWEEN '",in_edit_date_from,"' AND '",in_edit_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_store2_id!=0) then 
		SET @ToStore=concat(' AND t.store2_id=',in_store2_id);
	end if;
	if (in_transactor_id!=0) then 
		SET @Transactor=concat(' AND t.transactor_id=',in_transactor_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND t.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_transaction_reason_id!=0) then 
		SET @TransReason=concat(' AND t.transaction_reason_id=',in_transaction_reason_id);
	end if;
	if (in_add_user_detail_id!=0) then 
		SET @AddUser=concat(' AND t.add_user_detail_id=',in_add_user_detail_id);
	end if;
	if (in_edit_user_detail_id!=0) then 
		SET @EditUser=concat(' AND t.edit_user_detail_id=',in_edit_user_detail_id);
	end if;
	if (in_transaction_user_detail_id!=0) then 
		SET @TransactionUser=concat(' AND t.transaction_user_detail_id=',in_transaction_user_detail_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_transaction t WHERE 1=1 ',@TransDate,@AddDate,@EditDate,@FromStore,@ToStore,@AddUser,@EditUser,@TransactionUser,@Transactor,@BillTransactor,@TransType,@TransReason,' ORDER BY t.add_date DESC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transaction_summary;
DELIMITER //
CREATE PROCEDURE sp_report_transaction_summary
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_store2_id int,
	IN in_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_add_user_detail_id int,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date_from datetime,
	IN in_edit_date_to datetime,
	IN in_group_by_field varchar(100),
	IN in_transaction_user_detail_id int,
	IN in_bill_transactor_id bigint 
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @ToStore='';
	SET @Transactor='';
	SET @BillTransactor='';
	SET @TransType='';
	SET @TransReason='';
	SET @AddUser='';
	SET @AddDate='';
	SET @EditUser='';
	SET @TransactionUser='';
	SET @EditDate='';
	SET @GroupBy1='';
	SET @GroupBy2='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (t.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (t.add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if ((in_edit_date_from is not null) and (in_edit_date_to is not null)) then 
		SET @EditDate=concat(" AND (t.edit_date BETWEEN '",in_edit_date_from,"' AND '",in_edit_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_store2_id!=0) then 
		SET @ToStore=concat(' AND t.store2_id=',in_store2_id);
	end if;
	if (in_transactor_id!=0) then 
		SET @Transactor=concat(' AND t.transactor_id=',in_transactor_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND t.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_transaction_reason_id!=0) then 
		SET @TransReason=concat(' AND t.transaction_reason_id=',in_transaction_reason_id);
	end if;
	if (in_add_user_detail_id!=0) then 
		SET @AddUser=concat(' AND t.add_user_detail_id=',in_add_user_detail_id);
	end if;
	if (in_edit_user_detail_id!=0) then 
		SET @EditUser=concat(' AND t.edit_user_detail_id=',in_edit_user_detail_id);
	end if;
	if (in_transaction_user_detail_id!=0) then 
		SET @TransactionUser=concat(' AND t.transaction_user_detail_id=',in_transaction_user_detail_id);
	end if;
	if (in_group_by_field!='') then 
		SET @GroupBy1=concat('store_id,transaction_type_id,',in_group_by_field,' AS field_name,',
		'SUM(t.total_trade_discount) AS sum_total_trade_discount,',
		'SUM(t.total_vat) AS sum_total_vat,',
		'SUM(t.cash_discount) AS sum_cash_discount,',
		'SUM(t.grand_total) AS sum_grand_total,',
		'SUM(t.total_std_vatable_amount) AS sum_total_std_vatable_amount,',
		'SUM(t.total_zero_vatable_amount) AS sum_total_zero_vatable_amount,',
		'SUM(t.total_exempt_vatable_amount) AS sum_total_exempt_vatable_amount,',
		'SUM(t.total_profit_margin) AS sum_total_profit_margin');
		SET @GroupBy2=concat(' GROUP BY store_id,transaction_type_id,',in_group_by_field,' ORDER BY store_id,transaction_type_id,',in_group_by_field);
	else
		SET @GroupBy1=concat('store_id,transaction_type_id,',
		'SUM(t.total_trade_discount) AS sum_total_trade_discount,',
		'SUM(t.total_vat) AS sum_total_vat,',
		'SUM(t.cash_discount) AS sum_cash_discount,',
		'SUM(t.grand_total) AS sum_grand_total,',
		'SUM(t.total_std_vatable_amount) AS sum_total_std_vatable_amount,',
		'SUM(t.total_zero_vatable_amount) AS sum_total_zero_vatable_amount,',
		'SUM(t.total_exempt_vatable_amount) AS sum_total_exempt_vatable_amount,',
		'SUM(t.total_profit_margin) AS sum_total_profit_margin');
		SET @GroupBy2=' GROUP BY store_id,transaction_type_id';
	end if;
	
	SET @sql_text=concat('SELECT ',@GroupBy1,' FROM view_transaction t WHERE 1=1 ',@TransDate,@AddDate,@EditDate,@FromStore,@ToStore,@AddUser,@EditUser,@TransactionUser,@Transactor,@BillTransactor,@TransType,@TransReason,' ',@GroupBy2);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transaction_item;
DELIMITER //
CREATE PROCEDURE sp_report_transaction_item
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_store2_id int,
	IN in_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_add_user_detail_id int,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date_from datetime,
	IN in_edit_date_to datetime,
	IN in_transaction_id bigint,
	IN in_item_id bigint,
	IN in_transaction_user_detail_id int,
	IN in_bill_transactor_id bigint 
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @ToStore='';
	SET @Transactor='';
	SET @BillTransactor='';
	SET @TransType='';
	SET @TransReason='';
	SET @AddUser='';
	SET @AddDate='';
	SET @EditUser='';
	SET @EditDate='';
	SET @TransactionUser='';
	SET @TransId='';
	SET @ItemId='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (ti.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (ti.add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if ((in_edit_date_from is not null) and (in_edit_date_to is not null)) then 
		SET @EditDate=concat(" AND (ti.edit_date BETWEEN '",in_edit_date_from,"' AND '",in_edit_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND ti.store_id=',in_store_id);
	end if;
	if (in_store2_id!=0) then 
		SET @ToStore=concat(' AND ti.store2_id=',in_store2_id);
	end if;
	if (in_transactor_id!=0) then 
		SET @Transactor=concat(' AND ti.transactor_id=',in_transactor_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND ti.bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND ti.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_transaction_reason_id!=0) then 
		SET @TransReason=concat(' AND ti.transaction_reason_id=',in_transaction_reason_id);
	end if;
	if (in_add_user_detail_id!=0) then 
		SET @AddUser=concat(' AND ti.add_user_detail_id=',in_add_user_detail_id);
	end if;
	if (in_edit_user_detail_id!=0) then 
		SET @EditUser=concat(' AND ti.edit_user_detail_id=',in_edit_user_detail_id);
	end if;
	if (in_transaction_user_detail_id!=0) then 
		SET @TransactionUser=concat(' AND ti.transaction_user_detail_id=',in_transaction_user_detail_id);
	end if;
	if (in_transaction_id!=0) then 
		SET @TransId=concat(' AND ti.transaction_id=',in_transaction_id);
	end if;
	if (in_item_id!=0) then 
		SET @ItemId=concat(' AND ti.item_id=',in_item_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_transaction_item ti WHERE 1=1 ',
		@TransId,@ItemId,@TransDate,@AddDate,@EditDate,@FromStore,@ToStore,@AddUser,@EditUser,@TransactionUser,@Transactor,@BillTransactor,@TransType,@TransReason,
		' ORDER BY add_date DESC');
	-- SELECT @sql_text;
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transaction_item_user_earn;
DELIMITER //
CREATE PROCEDURE sp_report_transaction_item_user_earn
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_transaction_user_detail_id int
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @TransType='';
	SET @TransReason='';
	SET @TransactionUser='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (ti.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND ti.store_id=',in_store_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND ti.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_transaction_reason_id!=0) then 
		SET @TransReason=concat(' AND ti.transaction_reason_id=',in_transaction_reason_id);
	end if;
	if (in_transaction_user_detail_id!=0) then 
		SET @TransactionUser=concat(' AND ti.transaction_user_detail_id=',in_transaction_user_detail_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_transaction_item ti WHERE ti.earn_amount>0 ',
		@TransDate,@FromStore,@TransactionUser,@TransType,@TransReason,
		' ORDER BY add_date DESC');
	-- SELECT @sql_text;
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transaction_user_earn_summary;
DELIMITER //
CREATE PROCEDURE sp_report_transaction_user_earn_summary
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_transaction_user_detail_id int
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @TransType='';
	SET @TransReason='';
	SET @TransactionUser='';
	SET @GroupBy1='';
	SET @GroupBy2='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (t.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND t.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_transaction_reason_id!=0) then 
		SET @TransReason=concat(' AND t.transaction_reason_id=',in_transaction_reason_id);
	end if;
	if (in_transaction_user_detail_id!=0) then 
		SET @TransactionUser=concat(' AND t.transaction_user_detail_id=',in_transaction_user_detail_id);
	end if;
	
	SET @sql_text=concat('SELECT transaction_user_detail_id AS EarnUserId,SUM(earn_amount) AS TotalEarnAmount FROM view_transaction_item t WHERE t.earn_amount>0 ',@TransDate,@FromStore,@TransactionUser,@TransType,@TransReason,' ','GROUP BY transaction_user_detail_id');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_pay;
DELIMITER //
CREATE PROCEDURE sp_report_pay
(
	IN in_pay_date_from date,
	IN in_pay_date_to date,
	IN in_store_id int,
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_add_user_detail_id int,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date_from datetime,
	IN in_edit_date_to datetime,
	IN in_transaction_id bigint,
	IN in_pay_method_id int,
	IN in_pay_id bigint
) 
BEGIN 
	SET @PayDate='';
	SET @Store='';
	SET @BillTransactor='';
	SET @TransType='';
	SET @AddUser='';
	SET @AddDate='';
	SET @EditUser='';
	SET @EditDate='';
	SET @TransId='';
	SET @PayMethodId='';
	SET @PayId='';

	if ((in_pay_date_from is not null) and (in_pay_date_to is not null)) then 
		SET @PayDate=concat(" AND (t.pay_date BETWEEN '",in_pay_date_from,"' AND '",in_pay_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (t.add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if ((in_edit_date_from is not null) and (in_edit_date_to is not null)) then 
		SET @EditDate=concat(" AND (t.edit_date BETWEEN '",in_edit_date_from,"' AND '",in_edit_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND t.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_add_user_detail_id!=0) then 
		SET @AddUser=concat(' AND t.add_user_detail_id=',in_add_user_detail_id);
	end if;
	if (in_edit_user_detail_id!=0) then 
		SET @EditUser=concat(' AND t.edit_user_detail_id=',in_edit_user_detail_id);
	end if;
	if (in_transaction_id!=0) then 
		SET @TransId=concat(' AND t.transaction_id=',in_transaction_id);
	end if;
	if (in_pay_method_id!=0) then 
		SET @PayMethodId=concat(' AND t.pay_method_id=',in_pay_method_id);
	end if;
	if (in_pay_id!=0) then 
		SET @PayId=concat(' AND t.pay_id=',in_pay_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_pay AS t WHERE 1=1 ',
		@TransId,@PayDate,@AddDate,@EditDate,@Store,@AddUser,@EditUser,@BillTransactor,@TransType,@PayMethodId,@PayId);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_pay_summary;
DELIMITER //
CREATE PROCEDURE sp_report_pay_summary
(
	IN in_pay_date_from date,
	IN in_pay_date_to date,
	IN in_store_id int,
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_id int,
	IN in_add_user_detail_id int,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_edit_user_detail_id int,
	IN in_edit_date_from datetime,
	IN in_edit_date_to datetime,
	IN in_transaction_id bigint,
	IN in_pay_method_id int,
	IN in_pay_id bigint,
	IN in_group_by_field varchar(100) 
) 
BEGIN 
	SET @PayDate='';
	SET @Store='';
	SET @BillTransactor='';
	SET @TransType='';
	SET @AddUser='';
	SET @AddDate='';
	SET @EditUser='';
	SET @EditDate='';
	SET @TransId='';
	SET @PayMethodId='';
	SET @PayId='';
	SET @GroupBy1='';
	SET @GroupBy2='';

	if ((in_pay_date_from is not null) and (in_pay_date_to is not null)) then 
		SET @PayDate=concat(" AND (t.pay_date BETWEEN '",in_pay_date_from,"' AND '",in_pay_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (t.add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if ((in_edit_date_from is not null) and (in_edit_date_to is not null)) then 
		SET @EditDate=concat(" AND (t.edit_date BETWEEN '",in_edit_date_from,"' AND '",in_edit_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_id!=0) then 
		SET @TransType=concat(' AND t.transaction_type_id=',in_transaction_type_id);
	end if;
	if (in_add_user_detail_id!=0) then 
		SET @AddUser=concat(' AND t.add_user_detail_id=',in_add_user_detail_id);
	end if;
	if (in_edit_user_detail_id!=0) then 
		SET @EditUser=concat(' AND t.edit_user_detail_id=',in_edit_user_detail_id);
	end if;
	if (in_transaction_id!=0) then 
		SET @TransId=concat(' AND t.transaction_id=',in_transaction_id);
	end if;
	if (in_pay_method_id!=0) then 
		SET @PayMethodId=concat(' AND t.pay_method_id=',in_pay_method_id);
	end if;
	if (in_pay_id!=0) then 
		SET @PayId=concat(' AND t.pay_id=',in_pay_id);
	end if;
	
	if (in_group_by_field!='') then 
		SET @GroupBy1=concat('store_id,transaction_type_id,',in_group_by_field,' AS field_name,',
		'SUM(t.paid_amount) AS sum_paid_amount,',
		'SUM(t.points_spent_amount) AS sum_points_spent_amount');
		SET @GroupBy2=concat(' GROUP BY store_id,transaction_type_id,',in_group_by_field,' ORDER BY store_id,transaction_type_id,',in_group_by_field);
	else
		SET @GroupBy1=concat('store_id,transaction_type_id,',
		'SUM(t.paid_amount) AS sum_paid_amount,',
		'SUM(t.points_spent_amount) AS sum_points_spent_amount');
		SET @GroupBy2=' GROUP BY store_id,transaction_type_id';
	end if;
	
	SET @sql_text=concat('SELECT store_id,transaction_type_id,add_user_names,edit_user_names,bill_transactor_names,store_name,transaction_type_name,pay_method_name,',@GroupBy1,' FROM view_pay t WHERE 1=1 ',
		@TransId,@PayDate,@AddDate,@EditDate,@Store,@AddUser,@EditUser,@BillTransactor,@TransType,@PayMethodId,@PayId,
		' ',@GroupBy2);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_in;
DELIMITER //
CREATE PROCEDURE sp_report_stock_in
(
	IN in_store_id int,
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_expiry_date_from date,
	IN in_expiry_date_to date
) 
BEGIN 
	SET @Store='';
	SET @Category='';
	SET @SubCategory='';
	SET @ExpiryDate='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	if ((in_expiry_date_from is not null) and (in_expiry_date_to is not null)) then 
		SET @ExpiryDate=concat(" AND (t.item_exp_date BETWEEN '",in_expiry_date_from,"' AND '",in_expiry_date_to,"')");
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_stock_in AS t WHERE 1=1 ',
		@Store,@Category,@SubCategory,@ExpiryDate, ' ORDER BY t.description ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_in_summary;
DELIMITER //
CREATE PROCEDURE sp_report_stock_in_summary
(
	IN in_store_id int,
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_expiry_date_from date,
	IN in_expiry_date_to date
) 
BEGIN 
	SET @Store='';
	SET @Category='';
	SET @SubCategory='';
	SET @ExpiryDate='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	if ((in_expiry_date_from is not null) and (in_expiry_date_to is not null)) then 
		SET @ExpiryDate=concat(" AND (t.item_exp_date BETWEEN '",in_expiry_date_from,"' AND '",in_expiry_date_to,"')");
	end if;
	
	SET @sql_text=concat('SELECT store_id,store_name,category_id,category_name,sub_category_id,sub_category_name,
	sum(currentqty) AS sum_currentqty	FROM view_stock_in AS t WHERE 1=1 ',@Store,@Category,@SubCategory,@ExpiryDate,
	' GROUP BY store_id,category_id,sub_category_id',' ORDER BY store_name,category_name,sub_category_name ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_total;
DELIMITER //
CREATE PROCEDURE sp_report_stock_total
(
	IN in_store_id int,
	IN in_category_id int,
	IN in_sub_category_id int
) 
BEGIN 
	SET @Store='';
	SET @Category='';
	SET @SubCategory='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_stock_total AS t WHERE 1=1 ',
		@Store,@Category,@SubCategory, ' ORDER BY t.description ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_total_summary;
DELIMITER //
CREATE PROCEDURE sp_report_stock_total_summary
(
	IN in_store_id int,
	IN in_category_id int,
	IN in_sub_category_id int
) 
BEGIN 
	SET @Store='';
	SET @Category='';
	SET @SubCategory='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	
	SET @sql_text=concat('SELECT store_id,store_name,category_id,category_name,sub_category_id,sub_category_name,
	sum(currentqty) AS sum_currentqty,sum(cost_value) AS sum_cost_value,
	sum(wholesale_value) AS sum_wholesale_value,sum(retailsale_value) AS sum_retailsale_value 
	FROM view_stock_total AS t WHERE 1=1 ',@Store,@Category,@SubCategory,
	' GROUP BY store_id,category_id,sub_category_id',' ORDER BY store_name,category_name,sub_category_name ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_all;
DELIMITER //
CREATE PROCEDURE sp_report_stock_all
(
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_reorder_filter varchar(3)
) 
BEGIN 
	SET @Category='';
	SET @SubCategory='';
	SET @ReorderFilter='';

	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	if(in_reorder_filter='Yes') then 
		SET @ReorderFilter=' AND t.currentqty<=t.reorder_level';
	end if;
	if(in_reorder_filter='No') then 
		SET @ReorderFilter=' AND t.currentqty>t.reorder_level';
	end if;

	SET @sql_text=concat('SELECT * FROM view_stock_all AS t WHERE 1=1 ',
	@Category,@SubCategory,@ReorderFilter,' ORDER BY t.description ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_stock_all_summary;
DELIMITER //
CREATE PROCEDURE sp_report_stock_all_summary
(
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_reorder_filter varchar(3)
) 
BEGIN 
	SET @Category='';
	SET @SubCategory='';
	SET @ReorderFilter='';

	if (in_category_id!=0) then 
		SET @Category=concat(' AND t.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND t.sub_category_id=',in_sub_category_id);
	end if;
	if(in_reorder_filter='Yes') then 
		SET @ReorderFilter=' AND t.currentqty<=t.reorder_level';
	end if;
	if(in_reorder_filter='No') then 
		SET @ReorderFilter=' AND t.currentqty>t.reorder_level';
	end if;
	
	SET @sql_text=concat('SELECT category_id,category_name,sub_category_id,sub_category_name,
	sum(currentqty) AS sum_currentqty FROM view_stock_all AS t WHERE 1=1 ',@Category,@SubCategory,@ReorderFilter,
	' GROUP BY category_id,sub_category_id',' ORDER BY category_name,sub_category_name ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_item;
DELIMITER //
CREATE PROCEDURE sp_report_item
(
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_is_suspended varchar(3)
) 
BEGIN 
	SET @Category='';
	SET @SubCategory='';
	SET @Suspended='';

	if (in_category_id!=0) then 
		SET @Category=concat(' AND i.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND i.sub_category_id=',in_sub_category_id);
	end if;
	if (in_is_suspended!='') then 
		SET @Suspended=concat(" AND i.is_suspended='",in_is_suspended,"'");
	end if;

	SET @sql_text=concat('SELECT * FROM view_item AS i WHERE 1=1 ',
	@Category,@SubCategory,@Suspended,' ORDER BY i.description ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_item_summary;
DELIMITER //
CREATE PROCEDURE sp_report_item_summary
(
	IN in_category_id int,
	IN in_sub_category_id int,
	IN in_is_suspended varchar(3)
) 
BEGIN 
	SET @Category='';
	SET @SubCategory='';
	SET @Suspended='';

	if (in_category_id!=0) then 
		SET @Category=concat(' AND i.category_id=',in_category_id);
	end if;
	if (in_sub_category_id!=0) then 
		SET @SubCategory=concat(' AND i.sub_category_id=',in_sub_category_id);
	end if;
	if (in_is_suspended!='') then 
		SET @Suspended=concat(" AND i.is_suspended='",in_is_suspended,"'");
	end if;
	
	SET @sql_text=concat('SELECT item_id,category_id,category_name,sub_category_id,sub_category_name,
	count(item_id) AS count_items FROM view_item AS i WHERE 1=1 ',@Category,@SubCategory,@Suspended,
	' GROUP BY category_id,sub_category_id',' ORDER BY category_name,sub_category_name ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transactor;
DELIMITER //
CREATE PROCEDURE sp_report_transactor
(
	IN in_transactor_type varchar(20)
) 
BEGIN 
	SET @TransactorType='';
	if (in_transactor_type!='') then 
		SET @TransactorType=concat(" AND t.transactor_type='",in_transactor_type,"'");
	end if;

	SET @sql_text=concat('SELECT * FROM transactor AS t WHERE 1=1 ',
	@TransactorType,' ORDER BY t.transactor_names ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_location;
DELIMITER //
CREATE PROCEDURE sp_insert_location
(
	IN in_store_id int,
	IN in_location_name varchar(20)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("location","location_id",@new_id);
	INSERT INTO location
	(
		location_id,
		store_id,
		location_name
	) 
    VALUES
	(
		@new_id,
		in_store_id,
		in_location_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_location;
DELIMITER //
CREATE PROCEDURE sp_update_location
(
	IN in_location_id bigint,
	IN in_store_id int,
	IN in_location_name varchar(20)
) 
BEGIN 
	UPDATE location SET 
		store_id=in_store_id,
		location_name=in_location_name
	WHERE location_id=in_location_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_location;
DELIMITER //
CREATE PROCEDURE sp_search_location
(
	IN in_store_id int,
	IN in_search_name varchar(20),
	IN in_limit_no int 
) 
BEGIN 
	SET @StoreId='';
	SET @SearchName='';
	SET @LimitNo='';

	if (in_store_id!=0) then 
		SET @StoreId=concat(' AND store_id=',in_store_id);
	end if;
	if (in_search_name!='') then 
		SET @SearchName=concat(" AND (store_name LIKE '%",in_search_name,"%' OR location_name LIKE '%",in_search_name,"%')");
	end if;
	if (in_limit_no!=0) then 
		SET @LimitNo=concat(' LIMIT ',in_limit_no);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_location WHERE 1=1 ',
		@StoreId,@SearchName,' ORDER BY store_name,location_name ASC ',@LimitNo);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_location_by_item_id;
DELIMITER //
CREATE PROCEDURE sp_search_location_by_item_id
(
	IN in_item_id bigint
) 
BEGIN 
	SELECT * FROM view_item_location WHERE item_id=in_item_id ORDER BY store_name,location_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_item_location;
DELIMITER //
CREATE PROCEDURE sp_report_item_location
(
	IN in_store_id int,
	IN in_location_id bigint,
	IN in_item_id bigint
) 
BEGIN 
	SET @Store='';
	SET @Location='';
	SET @Item='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND store_id=',in_store_id);
	end if;
	if (in_location_id!=0) then 
		SET @Location=concat(' AND location_id=',in_location_id);
	end if;
	if (in_item_id!=0) then 
		SET @Item=concat(' AND item_id=',in_item_id);
	end if;

	SET @sql_text=concat('SELECT * FROM view_item_location WHERE 1=1 ',
	@Store,@Location,@Item,' ORDER BY store_name,location_name,description ASC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_item_location;
DELIMITER //
CREATE PROCEDURE sp_insert_item_location
(
	IN in_item_id bigint,
	IN in_location_id bigint
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("item_location","item_location_id",@new_id);
	INSERT INTO item_location
	(
		item_location_id,
		item_id,
		location_id
	) 
    VALUES
	(
		@new_id,
		in_item_id,
		in_location_id
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_item_location;
DELIMITER //
CREATE PROCEDURE sp_update_item_location
(
	IN in_item_location_id bigint,
	IN in_item_id bigint,
	IN in_location_id bigint
) 
BEGIN 
	UPDATE item_location SET 
		item_id=in_item_id,
		location_id=in_location_id 
	WHERE item_location_id=in_item_location_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_item_location;
DELIMITER //
CREATE PROCEDURE sp_search_item_location
(
	IN in_item_location_id bigint,
	IN in_store_id int,
	IN in_item_id bigint,
	IN in_location_id bigint,
	IN in_limit_no int 
) 
BEGIN 
	SET @ItemLocationId='';
	SET @StoreId='';
	SET @ItemId='';
	SET @LocationId='';
	SET @LimitNo='';

	if (in_item_location_id!=0) then 
		SET @ItemLocationId=concat(' AND item_location_id=',in_item_location_id);
	end if;
	if (in_store_id!=0) then 
		SET @StoreId=concat(' AND store_id=',in_store_id);
	end if;
	if (in_item_id!=0) then 
		SET @ItemId=concat(' AND item_id=',in_item_id);
	end if;
	if (in_location_id!=0) then 
		SET @LocationId=concat(' AND location_id=',in_location_id);
	end if;
	if (in_limit_no!=0) then 
		SET @LimitNo=concat(' LIMIT ',in_limit_no);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_item_location WHERE 1=1 ',@ItemLocationId,@StoreId,
		@ItemId,@LocationId,' ORDER BY store_name,description,location_name ASC ',@LimitNo);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_category_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_category_by_id
(
	IN in_category_id int
) 
BEGIN 
	SELECT * FROM category 
	WHERE category_id=in_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_category_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_category_by_name
(
	IN in_category_name varchar(50)
) 
BEGIN 
	SELECT * FROM category 
	WHERE category_name LIKE concat('%',in_category_name,'%') 
	ORDER BY category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_category_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_category_by_none() 
BEGIN 
	SELECT * FROM category ORDER BY category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_sub_category_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_sub_category_by_id
(
	IN in_sub_category_id int
) 
BEGIN 
	SELECT * FROM view_sub_category 
	WHERE sub_category_id=in_sub_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_sub_category_by_category_id;
DELIMITER //
CREATE PROCEDURE sp_search_sub_category_by_category_id
(
	IN in_category_id int
) 
BEGIN 
	SELECT * FROM view_sub_category 
	WHERE category_id=in_category_id 
	ORDER BY category_name,sub_category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_sub_category_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_sub_category_by_name
(
	IN in_search_name varchar(50)
) 
BEGIN 
	SELECT * FROM view_sub_category 
	WHERE sub_category_name LIKE concat('%',in_search_name,'%') OR category_name LIKE concat('%',in_search_name,'%') 
	ORDER BY category_name,sub_category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_sub_category_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_sub_category_by_none() 
BEGIN 
	SELECT * FROM view_sub_category ORDER BY category_name,sub_category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_unit_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_unit_by_id
(
	IN in_unit_id int
) 
BEGIN 
	SELECT * FROM unit 
	WHERE unit_id=in_unit_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_unit_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_unit_by_name
(
	IN in_unit_name varchar(50)
) 
BEGIN 
	SELECT * FROM unit 
	WHERE unit_name LIKE concat('%',in_unit_name,'%') 
	ORDER BY unit_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_unit_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_unit_by_none() 
BEGIN 
	SELECT * FROM unit 
	ORDER BY unit_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_method_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_pay_method_by_id
(
	IN in_pay_method_id int
) 
BEGIN 
	SELECT * FROM pay_method 
	WHERE pay_method_id=in_pay_method_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_method_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_pay_method_by_name
(
	IN in_pay_method_name varchar(50)
) 
BEGIN 
	SELECT * FROM pay_method 
	WHERE pay_method_name LIKE concat('%',in_pay_method_name,'%') 
	ORDER BY pay_method_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_method_by_equal_name;
DELIMITER //
CREATE PROCEDURE sp_search_pay_method_by_equal_name
(
	IN in_pay_method_name varchar(50)
) 
BEGIN 
	SELECT * FROM pay_method 
	WHERE pay_method_name=in_pay_method_name 
	ORDER BY pay_method_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_pay_method_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_pay_method_by_none() 
BEGIN 
	SELECT * FROM pay_method 
	ORDER BY pay_method_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_transactor_ledger;
DELIMITER //
CREATE PROCEDURE sp_insert_transactor_ledger
(
	IN in_store_id int,
	IN in_store_name varchar(20),
	IN in_transaction_id bigint,
	IN in_pay_id bigint,
	IN in_transaction_type_name varchar(50),
	IN in_description varchar(50),
	IN in_transaction_date date,
	IN in_transactor_id bigint,
	IN in_transactor_names varchar(100),
	IN in_ledger_entry_type varchar(2),
	IN in_amount_debit float,
	IN in_amount_credit float,
	IN in_bill_transactor_id bigint,
	IN in_bill_transactor_names varchar(100)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transactor_ledger","transactor_ledger_id",@new_id);
	
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);
	SET @transaction_id=null;
	if (in_transaction_id!=0) then 
		SET @transaction_id=in_transaction_id;
	end if;
	SET @transactor_id=null;
	if (in_transactor_id!=0) then 
		SET @transactor_id=in_transactor_id;
	end if;

	INSERT INTO transactor_ledger
	(
		transactor_ledger_id,
		store_id,
		store_name,
		transaction_id,
		pay_id,
		transaction_type_name,
		description,
		transaction_date,
		add_date,
		transactor_id,
		transactor_names,
		ledger_entry_type,
		amount_debit,
		amount_credit,
		bill_transactor_id,
		bill_transactor_names
	) 
    VALUES
	(
		@new_id,
		in_store_id,
		in_store_name,
		@transaction_id,
		in_pay_id,
		in_transaction_type_name,
		in_description,
		in_transaction_date,
		@cur_sys_datetime,
		@transactor_id,
		in_transactor_names,
		in_ledger_entry_type,
		in_amount_debit,
		in_amount_credit,
		in_bill_transactor_id,
		in_bill_transactor_names
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_transactor_ledger;
DELIMITER //
CREATE PROCEDURE sp_update_transactor_ledger
(
	IN in_transactor_ledger_id bigint,
	IN in_store_id int,
	IN in_store_name varchar(20),
	IN in_transaction_id bigint,
	IN in_pay_id bigint,
	IN in_transaction_type_name varchar(50),
	IN in_description varchar(50),
	IN in_transaction_date date,
	IN in_add_date datetime,
	IN in_transactor_id bigint,
	IN in_transactor_names varchar(100),
	IN in_ledger_entry_type varchar(2),
	IN in_amount_debit float,
	IN in_amount_credit float,
	IN in_bill_transactor_id bigint,
	IN in_bill_transactor_names varchar(100)
) 
BEGIN 
	SET @transaction_id=null;
	if (in_transaction_id!=0) then 
		SET @transaction_id=in_transaction_id;
	end if;
	SET @transactor_id=null;
	if (in_transactor_id!=0) then 
		SET @transactor_id=in_transactor_id;
	end if;

	UPDATE transactor_ledger SET 
		store_id=in_store_id,
		store_name=in_store_name,
		transaction_id=@transaction_id,
		pay_id=in_pay_id,
		transaction_type_name=in_transaction_type_name,
		description=in_description,
		transaction_date=in_transaction_date,
		add_date=in_add_date,
		transactor_id=@transactor_id,
		transactor_names=in_transactor_names,
		ledger_entry_type=in_ledger_entry_type,
		amount_debit=in_amount_debit,
		amount_credit=in_amount_credit,
		bill_transactor_id=in_bill_transactor_id,
		bill_transactor_names=in_bill_transactor_names 
	WHERE transactor_ledger_id=in_transactor_ledger_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_ledger_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_ledger_by_id
(
	IN in_transactor_ledger_id bigint
) 
BEGIN 
	SELECT * FROM transactor_ledger
	WHERE transactor_ledger_id=in_transactor_ledger_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_ledger_by_transid_transtype;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_ledger_by_transid_transtype
(
	IN in_transaction_id bigint,
	IN in_transaction_type_name varchar(50)
) 
BEGIN 
	SELECT * FROM transactor_ledger l1 WHERE l1.transactor_ledger_id=(
		SELECT MIN(transactor_ledger_id) FROM transactor_ledger l2 
		WHERE l2.transaction_id=in_transaction_id AND l2.transaction_type_name=in_transaction_type_name); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transactor_ledger_by_min_transid_type_desc;
DELIMITER //
CREATE PROCEDURE sp_search_transactor_ledger_by_min_transid_type_desc
(
	IN in_transaction_id bigint,
	IN in_transaction_type_name varchar(50),
	IN in_description varchar(50)
) 
BEGIN 
	SELECT * FROM transactor_ledger l1 WHERE l1.transactor_ledger_id=(
		SELECT MIN(transactor_ledger_id) FROM transactor_ledger l2 
		WHERE l2.transaction_id=in_transaction_id AND l2.transaction_type_name=in_transaction_type_name AND 
		l2.description=in_description); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transactor_ledger;
DELIMITER //
CREATE PROCEDURE sp_report_transactor_ledger
(
	IN in_store_id int,
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_add_date_from datetime,
	IN in_add_date_to datetime,
	IN in_bill_transactor_id bigint,
	IN in_transaction_type_name varchar(50)
) 
BEGIN 
	SET @Store='';
	SET @TransDate='';
	SET @AddDate='';
	SET @BillTransactor='';
	SET @TransactionTypeName='';

	if (in_store_id!=0) then 
		SET @Store=concat(' AND store_id=',in_store_id);
	end if;
	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if ((in_add_date_from is not null) and (in_add_date_to is not null)) then 
		SET @AddDate=concat(" AND (add_date BETWEEN '",in_add_date_from,"' AND '",in_add_date_to,"')");
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND bill_transactor_id=',in_bill_transactor_id);
	end if;
	if (in_transaction_type_name!='') then 
		SET @TransactionTypeName=concat(" AND transaction_type_name='",in_transaction_type_name,"'");
	end if;

	SET @sql_text=concat('SELECT * FROM transactor_ledger WHERE 1=1 ',@Store,@TransactionTypeName,@TransDate,@AddDate,@BillTransactor,' ORDER BY add_date DESC,transactor_ledger_id DESC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transactor_ledger_summary_all;
DELIMITER //
CREATE PROCEDURE sp_report_transactor_ledger_summary_all
() 
BEGIN 
	SET @sql_text="SELECT SUM(amount_debit) as sum_amount_debit,SUM(amount_credit) as sum_amount_credit FROM transactor_ledger";
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transactor_ledger_summary_all_individual;
DELIMITER //
CREATE PROCEDURE sp_report_transactor_ledger_summary_all_individual
() 
BEGIN 
	SET @sql_text=concat("SELECT bill_transactor_id,bill_transactor_names,SUM(amount_debit) as sum_amount_debit,SUM(amount_credit) as sum_amount_credit FROM transactor_ledger GROUP BY bill_transactor_id ORDER BY bill_transactor_names ASC");
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_transactor_ledger_summary_single_individual;
DELIMITER //
CREATE PROCEDURE sp_report_transactor_ledger_summary_single_individual
(
	IN in_bill_transactor_id bigint
) 
BEGIN 
	SET @sql_text=concat("SELECT SUM(amount_debit) as sum_amount_debit,
	SUM(amount_credit) as sum_amount_credit FROM transactor_ledger 
	WHERE bill_transactor_id=",in_bill_transactor_id);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_copy_transaction;
DELIMITER //
CREATE PROCEDURE sp_copy_transaction
(
	IN in_transaction_id bigint,
	IN in_hist_flag varchar(10),
	OUT out_transaction_hist_id bigint
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_hist","transaction_hist_id",@new_id);
	
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO transaction_hist(transaction_hist_id,hist_flag,hist_add_date,
	transaction_id,transaction_date,store_id,store2_id,transactor_id,transaction_type_id,
	transaction_reason_id,total_trade_discount,total_vat,add_user_detail_id,add_date,
	edit_user_detail_id,edit_date,transaction_ref,transaction_comment,sub_total,grand_total,
	cash_discount,points_awarded,card_number,total_std_vatable_amount,total_zero_vatable_amount,
	total_exempt_vatable_amount,vat_perc,amount_tendered,change_amount,is_cash_discount_vat_liable,
	total_profit_margin,transaction_user_detail_id,bill_transactor_id,scheme_transactor_id,
	princ_scheme_member,scheme_card_number,transaction_number,delivery_date,delivery_address,
	pay_terms,terms_conditions,authorised_by_user_detail_id,authorise_date,pay_due_date,expiry_date 
	) SELECT @new_id,in_hist_flag,@cur_sys_datetime,
	transaction_id,transaction_date,store_id,store2_id,transactor_id,transaction_type_id,
	transaction_reason_id,total_trade_discount,total_vat,add_user_detail_id,add_date,
	edit_user_detail_id,edit_date,transaction_ref,transaction_comment,sub_total,grand_total,
	cash_discount,points_awarded,card_number,total_std_vatable_amount,total_zero_vatable_amount,
	total_exempt_vatable_amount,vat_perc,amount_tendered,change_amount,is_cash_discount_vat_liable,
	total_profit_margin,transaction_user_detail_id,bill_transactor_id,scheme_transactor_id,
	princ_scheme_member,scheme_card_number,transaction_number,delivery_date,delivery_address,
	pay_terms,terms_conditions,authorised_by_user_detail_id,authorise_date,pay_due_date,expiry_date 
	FROM transaction WHERE transaction_id=in_transaction_id;
	SET out_transaction_hist_id=@new_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_copy_transaction_item;
DELIMITER //
CREATE PROCEDURE sp_copy_transaction_item
(
	IN in_transaction_id bigint,
	IN in_transaction_hist_id bigint,
	IN in_transaction_item_id bigint
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("transaction_item_hist","transaction_item_hist_id",@new_id);

	INSERT INTO transaction_item_hist(transaction_item_hist_id,transaction_hist_id,
	transaction_item_id,transaction_id,item_id,batchno,item_qty,unit_price,item_expiry_date,
    item_mnf_date,unit_trade_discount,unit_vat,amount,vat_rated,vat_perc,unit_price_exc_vat,
    amount_inc_vat,amount_exc_vat,unit_price_inc_vat,stock_effect,is_trade_discount_vat_liable,
	unit_cost_price,unit_profit_margin,earn_perc,earn_amount
	) SELECT @new_id,in_transaction_hist_id,
	transaction_item_id,transaction_id,item_id,batchno,item_qty,unit_price,item_expiry_date,
    item_mnf_date,unit_trade_discount,unit_vat,amount,vat_rated,vat_perc,unit_price_exc_vat,
    amount_inc_vat,amount_exc_vat,unit_price_inc_vat,stock_effect,is_trade_discount_vat_liable,
	unit_cost_price,unit_profit_margin,earn_perc,earn_amount 
	FROM transaction_item WHERE transaction_item_id=in_transaction_item_id AND transaction_id=in_transaction_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_deleted_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_deleted_by_id
(
	IN in_transaction_id bigint 
) 
BEGIN 
		SELECT * FROM transaction_hist t 
		WHERE t.transaction_id=in_transaction_id AND t.hist_flag='Delete';
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_void_transaction;
DELIMITER //
CREATE PROCEDURE sp_void_transaction
(
	IN in_transaction_id bigint,
	IN in_edit_user_detail_id int
) 
BEGIN 

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	UPDATE transaction SET 
		cash_discount=0,
		total_vat=0,
		edit_user_detail_id=in_edit_user_detail_id,
		edit_date=@cur_sys_datetime,
		sub_total=0,
		grand_total=0,
		total_trade_discount=0,
		total_std_vatable_amount=0,
		total_zero_vatable_amount=0,
		total_exempt_vatable_amount=0 
	WHERE transaction_id=in_transaction_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_void_transaction_item;
DELIMITER //
CREATE PROCEDURE sp_void_transaction_item
(
	IN in_transaction_item_id bigint
) 
BEGIN 

	UPDATE transaction_item SET 
		item_qty=0,
		unit_price=0,
		unit_trade_discount=0,
		unit_vat=0,
		amount=0,
		unit_price_inc_vat =0,
		unit_price_exc_vat =0,
		amount_inc_vat =0,
		amount_exc_vat =0  
	WHERE transaction_item_id=in_transaction_item_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_item_hist_by_ids;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_item_hist_by_ids
(
	IN in_transaction_id bigint,
	IN in_transaction_hist_id bigint
) 
BEGIN 
		SELECT * FROM transaction_item_hist tih 
		WHERE tih.transaction_hist_id=in_transaction_hist_id AND tih.transaction_id=in_transaction_id ORDER BY tih.transaction_item_id ASC;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_by_add_user_detail_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_by_add_user_detail_id
(
	IN in_add_user_detail_id int,
	IN in_transaction_type_id int,
	IN in_store_id int
) 
BEGIN 
		SELECT * FROM transaction t 
		WHERE t.store_id=in_store_id AND t.transaction_type_id=in_transaction_type_id AND 
		t.add_user_detail_id=in_add_user_detail_id 
		ORDER BY t.transaction_id DESC LIMIT 10;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_copy_pay;
DELIMITER //
CREATE PROCEDURE sp_copy_pay
(
	IN in_pay_id bigint,
	IN in_hist_flag varchar(10),
	OUT out_pay_hist_id bigint
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("pay_hist","pay_hist_id",@new_id);
	
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO pay_hist(pay_hist_id,hist_flag,hist_add_date,
	pay_id,transaction_id,pay_date,paid_amount,pay_method_id,add_user_detail_id,
	edit_user_detail_id,add_date,edit_date,points_spent,points_spent_amount,delete_pay_id,
	pay_ref_no,pay_category,bill_transactor_id,transaction_type_id,transaction_reason_id,store_id
	) SELECT @new_id,in_hist_flag,@cur_sys_datetime,
	pay_id,transaction_id,pay_date,paid_amount,pay_method_id,add_user_detail_id,
	edit_user_detail_id,add_date,edit_date,points_spent,points_spent_amount,delete_pay_id,
	pay_ref_no,pay_category,bill_transactor_id,transaction_type_id,transaction_reason_id,store_id 
	FROM pay WHERE pay_id=in_pay_id;
	SET out_pay_hist_id=@new_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_login_session;
DELIMITER //
CREATE PROCEDURE sp_insert_login_session
(
	IN in_user_detail_id int,
	IN in_store_id int,
	IN in_session_id varchar(100),
	IN in_remote_ip varchar(50),
	IN in_remote_host varchar(50),
	IN in_remote_user varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("login_session","login_session_id",@new_id);

	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);

	INSERT INTO login_session
	(
		login_session_id,
		user_detail_id,
		store_id,
		session_id,
		add_date,
		remote_ip,
		remote_host,
		remote_user
	) 
    VALUES
	(
		@new_id,
		in_user_detail_id,
		in_store_id,
		in_session_id,
		@cur_sys_datetime,
		in_remote_ip,
		in_remote_host,
		in_remote_user
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_login_session_by_user;
DELIMITER //
CREATE PROCEDURE sp_delete_login_session_by_user
(
	IN in_user_detail_id int
) 
BEGIN 
	DELETE FROM login_session WHERE user_detail_id=in_user_detail_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_login_session_by_session;
DELIMITER //
CREATE PROCEDURE sp_delete_login_session_by_session
(
	IN in_session_id varchar(100)
) 
BEGIN 
	DELETE FROM login_session WHERE session_id=in_session_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_login_session;
DELIMITER //
CREATE PROCEDURE sp_search_login_session() 
BEGIN 
	SELECT * FROM login_session ORDER BY user_detail_id,store_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_login_session_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_login_session_by_id
(
	IN in_login_session_id int
) 
BEGIN 
	SELECT * FROM login_session WHERE login_session_id=in_login_session_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_login_session_by_user;
DELIMITER //
CREATE PROCEDURE sp_search_login_session_by_user
(
	IN in_user_detail_id int
) 
BEGIN 
	SELECT * FROM login_session WHERE user_detail_id=in_user_detail_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_login_session_by_session;
DELIMITER //
CREATE PROCEDURE sp_search_login_session_by_session
(
	IN in_session_id varchar(100)
) 
BEGIN 
	SELECT * FROM login_session WHERE session_id=in_session_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_delete_login_session_unlogged_out;
DELIMITER //
CREATE PROCEDURE sp_delete_login_session_unlogged_out
() 
BEGIN 
	SET @cur_sys_datetime=null;
	CALL sp_get_current_system_datetime(@cur_sys_datetime);
	DELETE FROM login_session where timediff(@cur_sys_datetime,add_date)>'12:00:00';
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_user_category;
DELIMITER //
CREATE PROCEDURE sp_insert_user_category
(
	IN in_user_category_name varchar(50)
) 
BEGIN 
	SET @new_id=0;
	CALL sp_get_new_id("user_category","user_category_id",@new_id);
	INSERT INTO user_category
	(
		user_category_id,
		user_category_name
	) 
    VALUES
	(
		@new_id,
		in_user_category_name
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_user_category;
DELIMITER //
CREATE PROCEDURE sp_update_user_category
(
	IN in_user_category_id int,
	IN in_user_category_name varchar(50)
) 
BEGIN 
	UPDATE user_category SET 
		user_category_name=in_user_category_name
	WHERE user_category_id=in_user_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_category_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_user_category_by_id
(
	IN in_user_category_id int
) 
BEGIN 
	SELECT * FROM user_category 
	WHERE user_category_id=in_user_category_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_category_by_name;
DELIMITER //
CREATE PROCEDURE sp_search_user_category_by_name
(
	IN in_search_name varchar(50)
) 
BEGIN 
	SELECT * FROM user_category 
	WHERE user_category_name LIKE concat('%',in_search_name,'%')  
	ORDER BY user_category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_category_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_user_category_by_none() 
BEGIN 
	SELECT * FROM user_category ORDER BY user_category_name ASC; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_insert_user_item_earn;
DELIMITER //
CREATE PROCEDURE sp_insert_user_item_earn
(
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_user_category_id int,
	IN in_item_category_id int,
	IN in_item_sub_category_id int,
	IN in_earn_perc float
) 
BEGIN 
	SET @new_id=0;
	SET @item_sub_category_id=NULL;
	if (in_item_sub_category_id!=0) then
		set @item_sub_category_id=in_item_sub_category_id;
	end if;
	CALL sp_get_new_id("user_item_earn","user_item_earn_id",@new_id);

	INSERT INTO user_item_earn
	(
		user_item_earn_id,
		transaction_type_id,
		transaction_reason_id,
		user_category_id,
		item_category_id,
		item_sub_category_id,
		earn_perc
	) 
    VALUES
	(
		@new_id,
		in_transaction_type_id,
		in_transaction_reason_id,
		in_user_category_id,
		in_item_category_id,
		in_item_sub_category_id,
		in_earn_perc
	); 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_update_user_item_earn;
DELIMITER //
CREATE PROCEDURE sp_update_user_item_earn
(
	IN in_user_item_earn_id bigint,
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_user_category_id int,
	IN in_item_category_id int,
	IN in_item_sub_category_id int,
	IN in_earn_perc float
) 
BEGIN 
	SET @item_sub_category_id=NULL;
	if (in_item_sub_category_id!=0) then
		set @item_sub_category_id=in_item_sub_category_id;
	end if;

	UPDATE user_item_earn SET 
		transaction_type_id=in_transaction_type_id,
		transaction_reason_id=in_transaction_reason_id,
		user_category_id=in_user_category_id,
		item_category_id=in_item_category_id,
		item_sub_category_id=in_item_sub_category_id,
		earn_perc=in_earn_perc 
	WHERE user_item_earn_id=in_user_item_earn_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_item_earn_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_user_item_earn_by_id
(
	IN in_user_item_earn_id bigint 
) 
BEGIN 
		SELECT * FROM user_item_earn 
		WHERE user_item_earn_id=in_user_item_earn_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_item_earn_by_user_cat;
DELIMITER //
CREATE PROCEDURE sp_search_user_item_earn_by_user_cat
(
	IN in_user_category_id int 
) 
BEGIN 
		SELECT * FROM user_item_earn 
		WHERE user_category_id=in_user_category_id 
		ORDER BY transaction_type_id,user_category_id,item_category_id,item_sub_category_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_item_earn_by_none;
DELIMITER //
CREATE PROCEDURE sp_search_user_item_earn_by_none() 
BEGIN 
		SELECT * FROM user_item_earn 
		ORDER BY transaction_type_id,user_category_id,item_category_id,item_sub_category_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_user_item_earn_by_ttype_treas_icat_isubcat_ucat;
DELIMITER //
CREATE PROCEDURE sp_search_user_item_earn_by_ttype_treas_icat_isubcat_ucat
(
	IN in_transaction_type_id int,
	IN in_transaction_reason_id int,
	IN in_item_category_id int,
	IN in_item_sub_category_id int,
	IN in_user_category_id int
) 
BEGIN 
	SELECT * FROM user_item_earn WHERE 
	transaction_type_id=in_transaction_type_id AND transaction_reason_id=in_transaction_reason_id AND 
	item_category_id=in_item_category_id AND item_sub_category_id=in_item_sub_category_id AND 
	user_category_id=in_user_category_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_bill;
DELIMITER //
CREATE PROCEDURE sp_report_bill
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_bill_transactor_id bigint 
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @BillTransactor='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (t.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	
	SET @sql_text=concat('SELECT * FROM view_transaction t WHERE transaction_type_id=2 ',@TransDate,@FromStore,@BillTransactor,' ORDER BY t.transaction_id DESC');
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_report_bill_summary;
DELIMITER //
CREATE PROCEDURE sp_report_bill_summary
(
	IN in_transaction_date_from date,
	IN in_transaction_date_to date,
	IN in_store_id int,
	IN in_bill_transactor_id bigint 
) 
BEGIN 
	SET @TransDate='';
	SET @FromStore='';
	SET @BillTransactor='';

	if ((in_transaction_date_from is not null) and (in_transaction_date_to is not null)) then 
		SET @TransDate=concat(" AND (t.transaction_date BETWEEN '",in_transaction_date_from,"' AND '",in_transaction_date_to,"')");
	end if;
	if (in_store_id!=0) then 
		SET @FromStore=concat(' AND t.store_id=',in_store_id);
	end if;
	if (in_bill_transactor_id!=0) then 
		SET @BillTransactor=concat(' AND t.bill_transactor_id=',in_bill_transactor_id);
	end if;
	
	SET @sql_text=concat('SELECT sum(grand_total) as sum_grand_total FROM view_transaction t WHERE transaction_type_id=2 ',@TransDate,@FromStore,@BillTransactor);
	PREPARE stmt FROM @sql_text;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
END//
DELIMITER ;


DROP PROCEDURE IF EXISTS sp_report_bill_items_summary;
DELIMITER //
CREATE PROCEDURE sp_report_bill_items_summary
(
	IN in_transaction_id bigint 
) 
BEGIN 
	SELECT category_id,category_name,sum(amount_exc_vat) as sum_amount_exc_vat,sum(amount_inc_vat) as sum_amount_inc_vat 
	FROM view_transaction_item 
	WHERE transaction_id=in_transaction_id 
	GROUP BY category_id;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_type_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_type_by_id
(
	IN in_transaction_type_id int
) 
BEGIN 
	SELECT * FROM transaction_type 
	WHERE transaction_type_id=in_transaction_type_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_reason_by_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_reason_by_id
(
	IN in_transaction_reason_id int
) 
BEGIN 
	SELECT * FROM transaction_reason 
	WHERE transaction_reason_id=in_transaction_reason_id; 
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_search_transaction_reason_by_transtype_id;
DELIMITER //
CREATE PROCEDURE sp_search_transaction_reason_by_transtype_id
(
	IN in_transaction_type_id int
) 
BEGIN 
	SELECT * FROM transaction_reason 
	WHERE transaction_type_id=in_transaction_type_id; 
END//
DELIMITER ;


