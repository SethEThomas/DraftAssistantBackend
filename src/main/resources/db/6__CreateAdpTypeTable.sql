CREATE TABLE `DraftAssistant`.`ADP_TYPE` (
  `ID` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  PRIMARY KEY (`ID`));

  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("1","Standard");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("2","Half PPR");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("3","PPR");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("4","2QB");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("5","Dynasty Standard");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("6","Dynasty Half PPR");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("7","Dynasty PPR");
  INSERT IGNORE INTO `DraftAssistant`.`ADP_TYPE`(`ID`,`Name`) VALUES ("8","Dynasty 2QB");