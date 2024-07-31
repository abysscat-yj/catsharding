package com.abysscat.catsharding.config;

import com.abysscat.catsharding.datasource.ShardingDataSource;
import com.abysscat.catsharding.engine.ShardingEngine;
import com.abysscat.catsharding.engine.StandardShardingEngine;
import com.abysscat.catsharding.mybatis.SqlStatementInterceptor;
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

	@Bean
	public SqlStatementInterceptor sqlStatementInterceptor() {
		return new SqlStatementInterceptor();
	}

	@Bean
	public ShardingEngine shardingEngine(ShardingProperties properties) {
		return new StandardShardingEngine(properties);
	}

}
