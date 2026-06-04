DELETE FROM `role_permission` WHERE `permission_id` = 55;
DELETE FROM `permission` WHERE `id` = 55 AND `value` = 'sensitive_word:import';
