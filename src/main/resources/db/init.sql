CREATE TABLE IF NOT EXISTS `TEAM` (
  `ID` int NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Abbreviation` varchar(45) DEFAULT NULL,
  `StrengthOfSchedule` int DEFAULT NULL,
  `StrengthOfSchedulePlayoffs` int DEFAULT NULL,
  `ByeWeek` int DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS  `PLAYER` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(45) DEFAULT NULL,
  `LastName` varchar(45) DEFAULT NULL,
  `Team` int DEFAULT NULL,
  `Age` int DEFAULT NULL,
  `Positional Depth` int DEFAULT NULL,
  `Notes` varchar(1000) DEFAULT NULL,
  `IsSleeper` tinyint DEFAULT '0',
  `ECR` float DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Player_Team_ID_idx` (`Team`),
  CONSTRAINT `FK_Player_Team_ID` FOREIGN KEY (`Team`) REFERENCES `TEAM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS  `ADP` (
  `PlayerId` int NOT NULL,
  `ESPN` float DEFAULT NULL,
  `Yahoo` float DEFAULT NULL,
  `FantasySports` float DEFAULT NULL,
  `Average` float DEFAULT NULL,
  PRIMARY KEY (`PlayerId`),
  CONSTRAINT `ADP_Player_ID` FOREIGN KEY (`PlayerId`) REFERENCES `PLAYER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS  `TIER_TYPE` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name_UNIQUE` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS  `TIER` (
  `PlayerId` int NOT NULL,
  `TierType` int NOT NULL,
  `Tier` int DEFAULT NULL,
  PRIMARY KEY (`PlayerId`,`TierType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO `DraftAssistant`.`TIER_TYPE`(`ID`,`Name`) VALUES ("1","Overall");
INSERT IGNORE INTO `DraftAssistant`.`TIER_TYPE`(`ID`,`Name`) VALUES ("2","QB");
INSERT IGNORE INTO `DraftAssistant`.`TIER_TYPE`(`ID`,`Name`) VALUES ("3","WR");
INSERT IGNORE INTO `DraftAssistant`.`TIER_TYPE`(`ID`,`Name`) VALUES ("4","TE");
INSERT IGNORE INTO `DraftAssistant`.`TIER_TYPE`(`ID`,`Name`) VALUES ("5","RB");