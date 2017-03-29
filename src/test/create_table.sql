CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL,
  `username` varchar(32) DEFAULT NULL,
  `login_account` varchar(32) DEFAULT NULL,
  `login_pwd` varchar(64) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;