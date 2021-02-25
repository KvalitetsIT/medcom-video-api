-- Include all groups* tables in Flyway.
CREATE TABLE IF NOT EXISTS `groups` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  `group_type` int(1) NOT NULL DEFAULT '1',
  `parent_id` bigint(20) DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`group_id`),
  KEY `idx_groupid_deleted` (`group_id`,`deleted_time`),
  KEY `idx_deleted_time` (`deleted_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
;

CREATE TABLE IF NOT EXISTS `groups_authorization` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(200) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `role` varchar(200) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime DEFAULT NULL,
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` timestamp NULL DEFAULT NULL,
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=latin1
;

CREATE TABLE IF NOT EXISTS `groups_bookingdefaults` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL,
  `jsondata` longtext,
  `defaults_type` varchar(200) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime DEFAULT NULL,
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1
;

CREATE TABLE IF NOT EXISTS `groups_domains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL,
  `domain` varchar(250) NOT NULL,
  `inherit` bit(1) NOT NULL DEFAULT b'1',
  `is_history_domain` bit(1) NOT NULL DEFAULT b'0',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime DEFAULT NULL,
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `domain` (`domain`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1
;

CREATE TABLE IF NOT EXISTS `groups_settings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `setting_name` varchar(250) NOT NULL,
  `setting_value` longtext NOT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=latin1
;

CREATE TABLE IF NOT EXISTS `groups_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `name` varchar(250) NOT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `inherit` bit(1) NOT NULL DEFAULT b'1',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1
;