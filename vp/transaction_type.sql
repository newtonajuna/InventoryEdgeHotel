-- phpMyAdmin SQL Dump
-- version 3.2.0.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 14, 2016 at 10:31 AM
-- Server version: 5.1.36
-- PHP Version: 5.3.0

SET FOREIGN_KEY_CHECKS=0;

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `inventoryedge`
--

-- --------------------------------------------------------

--
-- Table structure for table `transaction_type`
--

DROP TABLE IF EXISTS `transaction_type`;
CREATE TABLE IF NOT EXISTS `transaction_type` (
  `transaction_type_id` int(11) NOT NULL,
  `transaction_type_name` varchar(50) NOT NULL,
  `transactor_label` varchar(50) NOT NULL DEFAULT ' ',
  `transaction_number_label` varchar(50) NOT NULL DEFAULT ' ',
  `transaction_output_label` varchar(50) NOT NULL DEFAULT ' ',
  `bill_transactor_label` varchar(50) NOT NULL DEFAULT ' ',
  `transaction_ref_label` varchar(50) NOT NULL DEFAULT ' ',
  `transaction_date_label` varchar(50) NOT NULL DEFAULT ' ',
  `transaction_user_label` varchar(50) NOT NULL DEFAULT ' ',
  `is_transactor_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_transaction_user_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_transaction_ref_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_authorise_user_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_authorise_date_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_delivery_address_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_delivery_date_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_pay_due_date_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  `is_expiry_date_mandatory` varchar(3) NOT NULL DEFAULT 'No',
  PRIMARY KEY (`transaction_type_id`),
  UNIQUE KEY `transaction_type_name` (`transaction_type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaction_type`
--

INSERT INTO `transaction_type` (`transaction_type_id`, `transaction_type_name`, `transactor_label`, `transaction_number_label`, `transaction_output_label`, `bill_transactor_label`, `transaction_ref_label`, `transaction_date_label`, `transaction_user_label`, `is_transactor_mandatory`, `is_transaction_user_mandatory`, `is_transaction_ref_mandatory`, `is_authorise_user_mandatory`, `is_authorise_date_mandatory`, `is_delivery_address_mandatory`, `is_delivery_date_mandatory`, `is_pay_due_date_mandatory`, `is_expiry_date_mandatory`) VALUES
(1, 'PURCHASE INVOICE', 'Supplier', 'Purchase Invoice No', 'PURCHASE INVOICE', 'Bill Supplier', 'Purchase Order Ref', 'Invoice Date', 'Received By', 'Yes', 'No', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'),
(2, 'SALE INVOICE', 'Customer', 'Sales Invoice No', 'SALES INVOICE', 'Bill Customer', 'Purchase Order Ref', 'Invoice Date', 'Served By', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(3, 'DISPOSE', '', 'Dispose Stock No', 'DISPOSE STOCK', '', '', 'Dispose Date', 'Disposed By', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(4, 'TRANSFER', '', 'Stock Transfer No', 'STOCK TRANSFER', '', 'Stock Transfer Request Ref', 'Transfer Date', 'Transfered By', '', 'No', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'),
(5, 'ITEM', '', '', '', '', '', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'),
(6, 'PAYMENT', '', '', '', '', '', 'Pay Date', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'),
(7, 'UNPACK', '', 'Unpack No', '', '', '', 'Unpack Date', 'Unpacked By', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'),
(8, 'PURCHASE ORDER', 'Supplier', 'Purchase Order No', 'PURCHASE ORDER', 'Bill Supplier', 'Transaction Ref', 'Purchase Order Date', 'Ordered By', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(9, 'GOODS RECEIVED', 'Supplier', 'Goods Receive No', 'GOODS RECEIVED NOTE', 'Bill Supplier', 'Purchase Order Ref', 'Receive Date', 'Received By', 'Yes', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(10, 'SALE QUOTATION', 'Customer', 'Sales Quote No', 'SALES QUOTATION', 'Bill Customer', 'Transaction Ref', 'Quote Date', 'Quoted By', 'Yes', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(11, 'SALE ORDER', 'Customer', 'Sales Order No', 'SALES ORDER', 'Bill Customer', 'Purchase Order Ref', 'Order Date', 'Received By', 'Yes', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No', 'No'),
(12, 'GOODS DELIVERY', 'Customer', 'Goods Delivery No', 'GOODS DELIVERY NOTE', 'Bill Customer', 'Sales Invoice Ref', 'Delivery Date', 'Delivered By', 'Yes', 'Yes', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'),
(13, 'TRANSFER REQUEST', '', 'Stock Transfer Request No', 'STOCK TRANSFER REQUEST', '', 'Request Ref', 'Request Date', '', '', 'No', 'No', 'No', 'No', 'No', 'No', 'No', 'No');

SET FOREIGN_KEY_CHECKS=1;
