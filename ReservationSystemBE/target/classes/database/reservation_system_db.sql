-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema reservation_system_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reservation_system_db` DEFAULT CHARACTER SET utf8 ;
USE `reservation_system_db` ;

-- -----------------------------------------------------
-- Table `reservation_system_db`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reservation_system_db`.`users` (
                                              `user_id` INT NOT NULL AUTO_INCREMENT,
                                              `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(255) NOT NULL,
    `role` ENUM("USER", "ADMIN") NOT NULL DEFAULT 'USER',
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
    UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reservation_system_db`.`resources`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reservation_system_db`.`resources` (
                                                  `resource_id` INT NOT NULL AUTO_INCREMENT,
                                                  `resource_name` VARCHAR(50) NOT NULL,
    `resource_type` ENUM("DESK", "SMALL_CONFERENCE", "MED_CONFERENCE", "BIG_CONFERENCE") NOT NULL,
    `time_from` TIME NOT NULL,
    `time_to` TIME NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`resource_id`))
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `reservation_system_db`.`reservations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reservation_system_db`.`reservations` (
                                                     `reservation_id` INT NOT NULL AUTO_INCREMENT,
                                                     `user_id` INT NOT NULL,
                                                     `resource_id` INT NOT NULL,
                                                     `date` DATE NOT NULL,
                                                     `start_time` TIME NOT NULL,
                                                     `end_time` TIME NOT NULL,
                                                     `status` ENUM("ACTIVE", "CANCELED") NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    PRIMARY KEY (`reservation_id`),
    UNIQUE INDEX `reservation_id_UNIQUE` (`reservation_id` ASC) VISIBLE,
    INDEX `fk_reservations_users1_idx` (`user_id` ASC) VISIBLE,
    INDEX `fk_reservations_resources1_idx` (`resource_id` ASC) VISIBLE,
    CONSTRAINT `fk_reservations_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reservation_system_db`.`users` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_reservations_resources1`
    FOREIGN KEY (`resource_id`)
    REFERENCES `reservation_system_db`.`resources` (`resource_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reservation_system_db`.`reservation_repetition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reservation_system_db`.`reservation_repetition` (
                                                               `reservation_id` INT NOT NULL,
                                                               `repetition_type` ENUM("DAILY", "WEEKLY", "MONTHLY", "YEARLY") NOT NULL,
    `repetition_end_date` DATE NOT NULL,
    INDEX `fk_reservation_repetition_reservations1_idx` (`reservation_id` ASC) VISIBLE,
    PRIMARY KEY (`reservation_id`),
    UNIQUE INDEX `reservations_reservation_id_UNIQUE` (`reservation_id` ASC) VISIBLE,
    CONSTRAINT `fk_reservation_repetition_reservations1`
    FOREIGN KEY (`reservation_id`)
    REFERENCES `reservation_system_db`.`reservations` (`reservation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
