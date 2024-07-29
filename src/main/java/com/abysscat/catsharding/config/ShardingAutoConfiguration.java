package com.abysscat.catsharding.config;

import com.abysscat.catsharding.datasource.ShardingDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sharding auto configuration.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 0:27
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

	@Bean
	public ShardingDataSource shardingDataSource(ShardingProperties properties) {
		return new ShardingDataSource(properties);
	}

}
