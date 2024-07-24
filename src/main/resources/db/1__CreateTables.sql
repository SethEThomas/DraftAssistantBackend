CREATE TABLE IF NOT EXISTS `TEAM` (
  `ID` int NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Abbreviation` varchar(45) DEFAULT NULL,
  `ByeWeek` int DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `POSITION` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name_UNIQUE` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `PLAYER` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Position` int DEFAULT NULL,
  `NormalizedName` varchar(300) NULL,
  `FirstName` varchar(45) DEFAULT NULL,
  `LastName` varchar(45) DEFAULT NULL,
  `Team` int DEFAULT NULL,
  `Age` int DEFAULT NULL,
  `PositionalDepth` int DEFAULT NULL,
  `Notes` varchar(1000) DEFAULT NULL,
  `IsSleeper` tinyint DEFAULT '0',
  `ECR` float DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Unique_Player_Constraint` (`Position`, `FirstName`, `LastName`, `Team`),
  KEY `FK_Player_Team_ID_idx` (`Team`),
  CONSTRAINT `FK_Player_Position_ID` FOREIGN KEY (`Position`) REFERENCES `POSITION` (`ID`),
  CONSTRAINT `FK_Player_Team_ID` FOREIGN KEY (`Team`) REFERENCES `TEAM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `TIER` (
  `PlayerId` int NOT NULL,
  `Position` int NOT NULL,
  `Tier` int DEFAULT NULL,
  PRIMARY KEY (`PlayerId`,`Position`),
  KEY `TIER_POSITION_idx` (`Position`),
  CONSTRAINT `TIER_PLAYER` FOREIGN KEY (`PlayerId`) REFERENCES `PLAYER` (`ID`),
  CONSTRAINT `TIER_POSITION` FOREIGN KEY (`Position`) REFERENCES `POSITION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ADP` (
  `PlayerId` int NOT NULL,
  `Sleeper` float DEFAULT NULL,
  `ESPN` float DEFAULT NULL,
  `Yahoo` float DEFAULT NULL,
  `FantasySports` float DEFAULT NULL,
  `Average` float DEFAULT NULL,
  PRIMARY KEY (`PlayerId`),
  CONSTRAINT `ADP_Player_ID` FOREIGN KEY (`PlayerId`) REFERENCES `PLAYER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `STRENGTH_OF_SCHEDULE` (
  `Team` int NOT NULL,
  `Position` int NOT NULL,
  `StrengthOfSchedule` int DEFAULT NULL,
  PRIMARY KEY (`Team`,`Position`),
  KEY `STRENGTH_OF_SCHEDULE_POSITION_idx` (`Position`),
  CONSTRAINT `STRENGTH_OF_SCHEDULE_POSITION` FOREIGN KEY (`Position`) REFERENCES `POSITION` (`ID`),
  CONSTRAINT `STRENGTH_OF_SCHEDULE_TEAM` FOREIGN KEY (`Team`) REFERENCES `TEAM` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
