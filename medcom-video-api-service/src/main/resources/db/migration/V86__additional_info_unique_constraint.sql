ALTER TABLE `meeting_additional_info`
ADD CONSTRAINT `uc_info_key` UNIQUE (`meeting_id`, `info_key`);