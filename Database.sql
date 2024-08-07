-- MySQL dump 10.13  Distrib 8.0.36, for macos14 (arm64)
--
-- Host: localhost    Database: db_gruppi_lavoro
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `Entrant`
--

DROP TABLE IF EXISTS `Entrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Entrant` (
  `GroupId` int NOT NULL,
  `UserId` int NOT NULL,
  PRIMARY KEY (`GroupId`,`UserId`),
  KEY `UserId` (`UserId`),
  CONSTRAINT `entrant_ibfk_1` FOREIGN KEY (`GroupId`) REFERENCES `Group` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `entrant_ibfk_2` FOREIGN KEY (`UserId`) REFERENCES `User` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Entrant`
--

LOCK TABLES `Entrant` WRITE;
/*!40000 ALTER TABLE `Entrant` DISABLE KEYS */;
INSERT INTO `Entrant` VALUES (1,1),(4,1),(6,1),(19,1),(22,1),(23,1),(24,1),(28,1),(1,2),(17,3),(18,3),(19,3),(21,3),(25,3),(2,4),(3,4),(6,4),(17,4),(18,4),(21,4),(22,4),(23,4),(24,4),(26,4),(28,4),(2,5),(17,5),(18,5),(19,5),(21,5),(24,5),(25,5),(26,5),(27,5),(2,6),(3,6),(5,6),(17,6),(18,6),(21,6),(22,6),(24,6),(26,6),(27,6),(3,7),(17,7),(18,7),(22,7),(23,7),(26,7),(2,8),(4,8),(18,8),(19,8),(21,8),(22,8),(24,8),(26,8),(28,8),(19,9),(22,9),(23,9),(26,9),(2,10),(19,10),(21,10),(22,10),(24,10),(26,10),(28,10),(2,11),(28,11);
/*!40000 ALTER TABLE `Entrant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Group`
--

DROP TABLE IF EXISTS `Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Group` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `CreatorId` int NOT NULL,
  `Title` varchar(100) NOT NULL,
  `CreationDate` date NOT NULL DEFAULT (curdate()),
  `Duration` int NOT NULL,
  `MinEntrants` int NOT NULL,
  `MaxEntrants` int NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `CreatorId` (`CreatorId`),
  CONSTRAINT `group_ibfk_1` FOREIGN KEY (`CreatorId`) REFERENCES `User` (`Id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Group`
--

LOCK TABLES `Group` WRITE;
/*!40000 ALTER TABLE `Group` DISABLE KEYS */;
INSERT INTO `Group` VALUES (1,2,'GruppoVacanze','2014-07-24',32,2,8),(2,1,'Hollywood Party','2024-07-26',25,2,6),(3,1,'Festival di Cannes 2024','2024-08-02',65,1,10),(4,2,'Oscar 2024','2024-08-02',25,2,33),(5,2,'Mostra internazionale Venezia','2024-08-02',75,1,42),(6,2,'Sundance Film Festival','2024-08-02',22,2,7),(17,8,'titolo','2024-08-04',1,1,10),(18,1,'Commemorazione per Troisi','2024-08-04',7,1,17),(19,2,'Ricordo oscar 1993','2024-08-04',100,3,18),(21,1,'Vacanza a Capri','2024-08-04',7,3,6),(22,5,'Compleanno Benigni','2024-08-04',50,4,10),(23,5,'Vacanza in toscana-estate 2024','2024-08-04',8,1,15),(24,7,'Compleanno Emma Stone','2024-08-04',7,3,8),(25,7,'Met Gala 2025 ','2024-08-04',360,2,7),(26,11,'Festa da Sophia Loren ','2024-08-04',5,3,11),(27,11,'Celebrazione per Benigni - Oscar 1999','2024-08-04',31,1,5),(28,7,'Festa a sorpresa per Robert De Niro','2024-08-04',7,5,7);
/*!40000 ALTER TABLE `Group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Surname` varchar(100) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (1,'Jack Nicholson','1234','lollo@gmail.com','Jack','Nicholson'),(2,'Max_Troisi','immax','massimo@gmail.com','Massimo','Troisi'),(3,'RobDeNiro','12','rob@gmal.com','Robert','De Niro'),(4,'LeoDiCaprio','leo','leo@gmail.com','Leonardo','Di Caprio'),(5,'Roberto Benigni','imnotvip','fede@gmail.com','Roberto','Benigni'),(6,'Monica Bellucci','password','monica@gmail.com','Monica','Belllucci'),(7,'Emma__stone','emmas','emmastone@gmail.com','Emma','Stone'),(8,'Paul Newman','paul1','paulnewman@gmail.com','Paul','Newman'),(9,'Al Pacino','pacino','pacino@outlook.com','Alfredo James','Pacino'),(10,'Clint','clint','ClintEast@gmail.com','Clint','Eastwood'),(11,'SophiaLoren','sophia','Sofia@gmail.com','Sophia','Loren'),(12,'Paola Cortellesi','paola','Paola@hotmail.com','Paola','Cortellesi'),(13,'Matteo Garrone','matteo','matteo@gmail.com','Matteo','Garrone');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-04 14:58:04
