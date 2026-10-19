-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: kahinbhi_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `address_id` bigint NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(180) NOT NULL,
  `address_line2` varchar(180) DEFAULT NULL,
  `landmark` varchar(120) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `postal_code` varchar(15) NOT NULL,
  `country` varchar(100) NOT NULL DEFAULT 'India',
  `latitude` decimal(10,7) DEFAULT NULL,
  `longitude` decimal(10,7) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`address_id`),
  KEY `idx_addresses_city_postal` (`city`,`postal_code`),
  CONSTRAINT `chk_address_coordinate_pair` CHECK ((((`latitude` is null) and (`longitude` is null)) or ((`latitude` is not null) and (`longitude` is not null)))),
  CONSTRAINT `chk_address_latitude` CHECK (((`latitude` is null) or (`latitude` between -(90) and 90))),
  CONSTRAINT `chk_address_longitude` CHECK (((`longitude` is null) or (`longitude` between -(180) and 180)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'House 10, Sector 14',NULL,NULL,'Faridabad','Haryana','121007','India',NULL,NULL,'2026-10-15 00:15:21.314'),(2,'House 25, Sector 18',NULL,NULL,'Gurugram','Haryana','122015','India',NULL,NULL,'2026-10-15 00:15:21.380'),(3,'House 10, Sector 14',NULL,NULL,'Faridabad','Haryana','121007','India',NULL,NULL,'2026-10-15 00:18:06.250'),(4,'House 25, Sector 18',NULL,NULL,'Gurugram','Haryana','122015','India',NULL,NULL,'2026-10-15 00:18:06.290');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agent_location_history`
--

DROP TABLE IF EXISTS `agent_location_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agent_location_history` (
  `location_id` bigint NOT NULL AUTO_INCREMENT,
  `agent_id` bigint NOT NULL,
  `latitude` decimal(10,7) NOT NULL,
  `longitude` decimal(10,7) NOT NULL,
  `accuracy_m` decimal(8,2) DEFAULT NULL,
  `recorded_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`location_id`),
  KEY `idx_agent_location_time` (`agent_id`,`recorded_at`),
  CONSTRAINT `fk_agent_locations_agent` FOREIGN KEY (`agent_id`) REFERENCES `delivery_agents` (`agent_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_agent_location_accuracy` CHECK (((`accuracy_m` is null) or (`accuracy_m` >= 0))),
  CONSTRAINT `chk_agent_location_latitude` CHECK ((`latitude` between -(90) and 90)),
  CONSTRAINT `chk_agent_location_longitude` CHECK ((`longitude` between -(180) and 180))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agent_location_history`
--

LOCK TABLES `agent_location_history` WRITE;
/*!40000 ALTER TABLE `agent_location_history` DISABLE KEYS */;
INSERT INTO `agent_location_history` VALUES (1,1,28.4089000,77.3178000,12.50,'2026-10-19 15:20:46.513'),(2,1,28.4095000,77.3186000,10.00,'2026-10-19 15:10:46.513'),(3,1,28.4102000,77.3194000,8.50,'2026-10-19 15:00:46.513'),(4,1,28.4110000,77.3201000,9.00,'2026-10-19 14:50:46.513'),(5,1,28.4118000,77.3210000,7.50,'2026-10-19 14:40:46.513'),(8,1,28.4089000,77.3178000,12.50,'2026-10-19 15:20:50.261'),(9,1,28.4095000,77.3186000,10.00,'2026-10-19 15:10:50.261'),(10,1,28.4102000,77.3194000,8.50,'2026-10-19 15:00:50.261'),(11,1,28.4110000,77.3201000,9.00,'2026-10-19 14:50:50.261'),(12,1,28.4118000,77.3210000,7.50,'2026-10-19 14:40:50.261'),(15,1,28.4089000,77.3178000,12.50,'2026-10-19 15:21:12.427'),(16,1,28.4095000,77.3186000,10.00,'2026-10-19 15:11:12.427'),(17,1,28.4102000,77.3194000,8.50,'2026-10-19 15:01:12.427'),(18,1,28.4110000,77.3201000,9.00,'2026-10-19 14:51:12.427'),(19,1,28.4118000,77.3210000,7.50,'2026-10-19 14:41:12.427');
/*!40000 ALTER TABLE `agent_location_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment_statuses`
--

DROP TABLE IF EXISTS `assignment_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment_statuses` (
  `assignment_status_id` int NOT NULL AUTO_INCREMENT,
  `status_name` varchar(40) NOT NULL,
  PRIMARY KEY (`assignment_status_id`),
  UNIQUE KEY `status_name` (`status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_statuses`
--

LOCK TABLES `assignment_statuses` WRITE;
/*!40000 ALTER TABLE `assignment_statuses` DISABLE KEYS */;
INSERT INTO `assignment_statuses` VALUES (2,'ACCEPTED'),(1,'ASSIGNED'),(6,'CANCELLED'),(4,'COMPLETED'),(3,'IN_PROGRESS'),(5,'REJECTED');
/*!40000 ALTER TABLE `assignment_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_addresses`
--

DROP TABLE IF EXISTS `customer_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_addresses` (
  `customer_id` bigint NOT NULL,
  `address_id` bigint NOT NULL,
  `address_label` varchar(40) NOT NULL DEFAULT 'Other',
  `is_default` tinyint(1) NOT NULL DEFAULT '0',
  `saved_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`customer_id`,`address_id`),
  KEY `idx_customer_addresses_address` (`address_id`),
  CONSTRAINT `fk_customer_addresses_address` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_customer_addresses_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_addresses`
--

LOCK TABLES `customer_addresses` WRITE;
/*!40000 ALTER TABLE `customer_addresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `registered_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `fk_customers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,1,'9876543210','2026-10-15 00:05:29.661');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_agents`
--

DROP TABLE IF EXISTS `delivery_agents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_agents` (
  `agent_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `availability_status` enum('AVAILABLE','BUSY','OFF_DUTY','INACTIVE') NOT NULL DEFAULT 'AVAILABLE',
  `joined_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`agent_id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_agents_availability` (`availability_status`),
  CONSTRAINT `fk_agents_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_agents`
--

LOCK TABLES `delivery_agents` WRITE;
/*!40000 ALTER TABLE `delivery_agents` DISABLE KEYS */;
INSERT INTO `delivery_agents` VALUES (1,2,'9876543210','AVAILABLE','2026-10-16 17:03:45.410');
/*!40000 ALTER TABLE `delivery_agents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_assignments`
--

DROP TABLE IF EXISTS `delivery_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_assignments` (
  `assignment_id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` bigint NOT NULL,
  `agent_id` bigint NOT NULL,
  `assignment_status_id` int NOT NULL,
  `assigned_by_user_id` bigint NOT NULL,
  `assigned_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `accepted_at` datetime(3) DEFAULT NULL,
  `completed_at` datetime(3) DEFAULT NULL,
  `ended_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`assignment_id`),
  KEY `fk_assignments_status` (`assignment_status_id`),
  KEY `fk_assignments_assigned_by` (`assigned_by_user_id`),
  KEY `idx_assignments_shipment_time` (`shipment_id`,`assigned_at`),
  KEY `idx_assignments_agent_status` (`agent_id`,`assignment_status_id`),
  CONSTRAINT `fk_assignments_agent` FOREIGN KEY (`agent_id`) REFERENCES `delivery_agents` (`agent_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_assignments_assigned_by` FOREIGN KEY (`assigned_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_assignments_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_assignments_status` FOREIGN KEY (`assignment_status_id`) REFERENCES `assignment_statuses` (`assignment_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_assignments`
--

LOCK TABLES `delivery_assignments` WRITE;
/*!40000 ALTER TABLE `delivery_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `delivery_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(30) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'CUSTOMER'),(3,'DELIVERY_AGENT');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_types`
--

DROP TABLE IF EXISTS `service_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_types` (
  `service_type_id` int NOT NULL AUTO_INCREMENT,
  `service_name` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`service_type_id`),
  UNIQUE KEY `service_name` (`service_name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_types`
--

LOCK TABLES `service_types` WRITE;
/*!40000 ALTER TABLE `service_types` DISABLE KEYS */;
INSERT INTO `service_types` VALUES (1,'STANDARD','Standard delivery service',1),(2,'EXPRESS','Express delivery service',1),(3,'SAME_DAY','Same-day delivery service',1);
/*!40000 ALTER TABLE `service_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_statuses`
--

DROP TABLE IF EXISTS `shipment_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_statuses` (
  `status_id` int NOT NULL AUTO_INCREMENT,
  `status_name` varchar(40) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`status_id`),
  UNIQUE KEY `status_name` (`status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_statuses`
--

LOCK TABLES `shipment_statuses` WRITE;
/*!40000 ALTER TABLE `shipment_statuses` DISABLE KEYS */;
INSERT INTO `shipment_statuses` VALUES (1,'BOOKED','Shipment booking created'),(2,'ASSIGNED','Delivery agent assigned'),(3,'PICKED_UP','Parcel picked up'),(4,'IN_TRANSIT','Parcel is in transit'),(5,'OUT_FOR_DELIVERY','Parcel is out for delivery'),(6,'DELIVERED','Parcel delivered successfully'),(7,'DELIVERY_FAILED','Delivery attempt failed'),(8,'CANCELLED','Shipment cancelled');
/*!40000 ALTER TABLE `shipment_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_tracking_events`
--

DROP TABLE IF EXISTS `shipment_tracking_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_tracking_events` (
  `event_id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` bigint NOT NULL,
  `status_id` int NOT NULL,
  `latitude` decimal(10,7) DEFAULT NULL,
  `longitude` decimal(10,7) DEFAULT NULL,
  `location_description` varchar(255) DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `recorded_by_user_id` bigint DEFAULT NULL,
  `event_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`event_id`),
  KEY `fk_tracking_user` (`recorded_by_user_id`),
  KEY `idx_tracking_shipment_time` (`shipment_id`,`event_time`,`event_id`),
  KEY `idx_tracking_status` (`status_id`),
  CONSTRAINT `fk_tracking_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_tracking_status` FOREIGN KEY (`status_id`) REFERENCES `shipment_statuses` (`status_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_tracking_user` FOREIGN KEY (`recorded_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_tracking_coordinate_pair` CHECK ((((`latitude` is null) and (`longitude` is null)) or ((`latitude` is not null) and (`longitude` is not null)))),
  CONSTRAINT `chk_tracking_latitude` CHECK (((`latitude` is null) or (`latitude` between -(90) and 90))),
  CONSTRAINT `chk_tracking_longitude` CHECK (((`longitude` is null) or (`longitude` between -(180) and 180)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_tracking_events`
--

LOCK TABLES `shipment_tracking_events` WRITE;
/*!40000 ALTER TABLE `shipment_tracking_events` DISABLE KEYS */;
INSERT INTO `shipment_tracking_events` VALUES (1,1,1,NULL,NULL,'Shipment booking created','Shipment booked successfully.',1,'2026-10-15 00:15:21.391'),(2,2,1,NULL,NULL,'Shipment booking created','Shipment booked successfully.',1,'2026-10-15 00:18:06.299'),(3,2,5,NULL,NULL,NULL,NULL,NULL,'2026-10-16 16:16:07.952');
/*!40000 ALTER TABLE `shipment_tracking_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `shipment_id` bigint NOT NULL AUTO_INCREMENT,
  `tracking_number` varchar(40) NOT NULL,
  `customer_id` bigint NOT NULL,
  `sender_name` varchar(120) NOT NULL,
  `sender_phone` varchar(20) DEFAULT NULL,
  `receiver_name` varchar(120) NOT NULL,
  `receiver_phone` varchar(20) DEFAULT NULL,
  `pickup_address_id` bigint NOT NULL,
  `delivery_address_id` bigint NOT NULL,
  `service_type_id` int NOT NULL,
  `package_description` varchar(500) DEFAULT NULL,
  `weight_kg` decimal(8,3) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `expected_delivery_at` datetime(3) DEFAULT NULL,
  `delivered_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`shipment_id`),
  UNIQUE KEY `tracking_number` (`tracking_number`),
  KEY `idx_shipments_customer_date` (`customer_id`,`created_at`),
  KEY `idx_shipments_pickup_address` (`pickup_address_id`),
  KEY `idx_shipments_delivery_address` (`delivery_address_id`),
  KEY `idx_shipments_service_type` (`service_type_id`),
  CONSTRAINT `fk_shipments_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_shipments_delivery_address` FOREIGN KEY (`delivery_address_id`) REFERENCES `addresses` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_shipments_pickup_address` FOREIGN KEY (`pickup_address_id`) REFERENCES `addresses` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_shipments_service_type` FOREIGN KEY (`service_type_id`) REFERENCES `service_types` (`service_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_shipment_weight` CHECK (((`weight_kg` is null) or (`weight_kg` > 0)))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
INSERT INTO `shipments` VALUES (1,'KB493D431DE2F8',1,'Test Sender','9876543210','Test Receiver','9876543211',1,2,1,'Test parcel',2.500,'2026-10-15 00:15:21.384','2026-10-15 00:15:21.384',NULL,NULL),(2,'KBD619066908DA',1,'Test Sender','9876543210','Test Receiver','9876543211',3,4,1,'Test parcel',2.500,'2026-10-15 00:18:06.295','2026-10-16 16:16:07.000',NULL,NULL);
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `full_name` varchar(120) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `account_status` enum('ACTIVE','INACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_role_status` (`role_id`,`account_status`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,2,'Test Customer','customer.test.1792002928640@example.com','$2a$12$AQqWZLLPRDF4jM/sxr37reHYgELQGrXnLSVM8d22tyoGDAb9JWmze','ACTIVE','2026-10-15 00:05:29.656','2026-10-15 00:05:29.656'),(2,3,'Test Delivery Agent','agent.test@kahinbhi.com','$2a$10$REPLACE_WITH_A_REAL_BCRYPT_HASH','ACTIVE','2026-10-16 17:03:45.407','2026-10-16 17:03:45.407');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-19 22:16:43
