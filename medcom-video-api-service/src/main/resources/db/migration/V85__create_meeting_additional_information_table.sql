CREATE TABLE `meeting_additional_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `meeting_id` bigint(20) NOT NULL,
  `info_key` varchar(100),
  `info_value` varchar(100),
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`meeting_id`) REFERENCES `meetings`(`id`)
)