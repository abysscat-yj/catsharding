package com.abysscat.catsharding.datasource;

import com.abysscat.catsharding.config.ShardingProperties;
import com.abysscat.catsharding.engine.ShardingContext;
import com.abysscat.catsharding.engine.ShardingResult;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sharding datasource.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 0:09
 */
public class ShardingDataSource extends AbstractRoutingDataSource {

	public ShardingDataSource(ShardingProperties properties) {
		Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
		properties.getDatasources().forEach((k, v) -> {
			try {
				dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		setTargetDataSources(dataSourceMap);
		// 设置默认数据源
		setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
	}


	@Override
	protected Object determineCurrentLookupKey() {
		ShardingResult shardingResult = ShardingContext.get();
		Object key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
		System.out.println("===> determineCurrentLookupKey: " + key);
		return key;
	}
}
