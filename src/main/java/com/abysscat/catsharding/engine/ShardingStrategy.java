package com.abysscat.catsharding.engine;

import java.util.List;
import java.util.Map;

/**
 * sharding strategy interface.
 *
 * @Author: abysscat-yj
 * @Create: 2024/8/1 1:31
 */
public interface ShardingStrategy {

	List<String> getShardingColumns();

	String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
