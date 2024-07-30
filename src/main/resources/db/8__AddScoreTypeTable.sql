CREATE TABLE `DraftAssistant`.`SCORE_TYPE` (
  `ID` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `DisplayName` VARCHAR(100) NULL,
  `PointValue` FLOAT NULL,
  PRIMARY KEY (`ID`));

INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("1","CompletionPercentage", "Completion %", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("2","Passing2Pt", "Passing 2pt conversion", 2);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("3","PassAttempt", "Pass attempt", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("4","PassCompletion", "Pass Completion", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("5","PassingFirstDown", "Passing First Down", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("6","PassingInterception", "Passing Interception", -2);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("7","PassingTd", "Passing TD", 6);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("8","PassingYard", "Passing Yard", 0.04);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("9","Fumble", "Fumble", -2);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("10","Receiving2Pt", "Receiving 2pt conversion", 2);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("11","Reception", "Reception", 1);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("12","Reception40Plus", "40+ yard reception", 1);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("13","ReceptionFirstDown", "Reception 1st down", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("14","ReceivingTd", "Receiving TD", 6);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("15","ReceivingYard", "Receiving Yard", 0.1);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("16","Rushing2Pt", "Rushing 2pt conversion", 2);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("17","RushingAttempt", "Rushing attempt", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("18","RushingFirstDown", "Rushing first down", 0);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("19","RushingTd", "Rushing TD", 6);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("20","RushingYard", "Rushing yard", 0.1);
INSERT IGNORE INTO `DraftAssistant`.`SCORE_TYPE`(`ID`,`Name`,`DisplayName`, `PointValue`) VALUES ("21","TeReceptionBonus", "Tight End Reception Bonus", 0.5);

  ALTER TABLE `DraftAssistant`.`PROJECTED_STATS`
  DROP COLUMN `CompletionPercentage`,
  DROP COLUMN `Passing2Pt`,
  DROP COLUMN `PassAttempts`,
  DROP COLUMN `PassCompletions`,
  DROP COLUMN `PassingFirstDowns`,
  DROP COLUMN `PassingInterceptions`,
  DROP COLUMN `PassingTds`,
  DROP COLUMN `PassingYards`,
  DROP COLUMN `Fumbles`,
  DROP COLUMN `Receiving2Pt`,
  DROP COLUMN `Receptions`,
  DROP COLUMN `Receptions40Plus`,
  DROP COLUMN `ReceivingFirstDowns`,
  DROP COLUMN `ReceivingTds`,
  DROP COLUMN `ReceivingYards`,
  DROP COLUMN `Rushing2Pt`,
  DROP COLUMN `RushingAttempts`,
  DROP COLUMN `RushingFirstDowns`,
  DROP COLUMN `RushingTds`,
  DROP COLUMN `RushingYards`,
  ADD COLUMN `ScoreType` INT NOT NULL AFTER `PlayerId`,
  ADD COLUMN `ProjectedAmount` FLOAT NULL AFTER `ScoreType`,
  ADD PRIMARY KEY (`PlayerId`, `ScoreType`);
  ;