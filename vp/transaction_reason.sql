-- phpMyAdmin SQL Dump
-- version 3.2.0.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 14, 2016 at 10:32 AM
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
-- Table structure for table `transaction_reason`
--

DROP TABLE IF EXISTS `transaction_reason`;
CREATE TABLE IF NOT EXISTS `transaction_reason` (
  `transaction_reason_id` int(11) NOT NULL,
  `transaction_reason_name` varchar(50) NOT NULL,
  `transaction_type_id` int(11) NOT NULL,
  PRIMARY KEY (`transaction_reason_id`),
  UNIQUE KEY `transaction_reason_name` (`transaction_reason_name`),
  KEY `TransType_to_TransReas_on_TransReasId` (`transaction_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaction_reason`
--

INSERT INTO `transaction_reason` (`transaction_reason_id`, `transaction_reason_name`, `transaction_type_id`) VALUES
(1, 'PURCHASE INVOICE', 1),
(2, 'RETAIL SALE INVOICE', 2),
(3, 'EXPIRED', 3),
(4, 'LOST', 3),
(5, 'DAMAGE', 3),
(6, 'ISSUE TRANSFER', 4),
(7, 'RETURN TRANSFER', 4),
(8, 'ITEM', 5),
(9, 'UNPACK', 7),
(10, 'WHOLE SALE INVOICE', 2),
(11, 'COST-PRICE SALE INVOICE', 2),
(12, 'PURCHASE ORDER', 8),
(13, 'GOODS RECEIVED', 9),
(14, 'RETAIL SALE QUOTATION', 10),
(15, 'WHOLE SALE QUOTATION', 10),
(16, 'SALE ORDER', 11),
(17, 'EXEMPT SALE INVOICE', 2),
(18, 'GOODS DELIVERY', 12),
(19, 'ISSUE TRANSFER REQUEST', 13),
(20, 'RETURN TRANSFER REQUEST', 13);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transaction_reason`
--
ALTER TABLE `transaction_reason`
  ADD CONSTRAINT `TransType_to_TransReas_on_TransReasId` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`transaction_type_id`);

SET FOREIGN_KEY_CHECKS=1;
