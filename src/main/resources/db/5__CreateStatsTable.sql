CREATE TABLE `DraftAssistant`.`PROJECTED_STATS` (
  `PlayerId` INT NOT NULL,
  `RushingYards` FLOAT NULL,
  `RushingTds` FLOAT NULL,
  `RushingFirstDowns` FLOAT NULL,
  `RushingAttempts` FLOAT NULL,
  `Rushing2Pt` FLOAT NULL,
  `ReceivingYards` FLOAT NULL,
  `ReceivingTds` FLOAT NULL,
  `ReceivingFirstDowns` FLOAT NULL,
  `Receptions40Plus` FLOAT NULL,
  `Receptions` FLOAT NULL,
  `Receiving2Pt` FLOAT NULL,
  `Fumbles` FLOAT NULL,
  `PassingYards` FLOAT NULL,
  `PassingTds` FLOAT NULL,
  `PassingInterceptions` FLOAT NULL,
  `PassingFirstDowns` FLOAT NULL,
  `PassCompletions` FLOAT NULL,
  `PassAttempts` FLOAT NULL,
  `Passing2Pt` FLOAT NULL,
  `CompletionPercentage` FLOAT NULL,
  INDEX `PROJECTED_STATS_PLAYER_ID_idx` (`PlayerId` ASC) VISIBLE,
  CONSTRAINT `PROJECTED_STATS_PLAYER_ID`
    FOREIGN KEY (`PlayerId`)
    REFERENCES `DraftAssistant`.`PLAYER` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);