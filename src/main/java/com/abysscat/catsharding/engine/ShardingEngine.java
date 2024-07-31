package com.abysscat.catsharding.engine;

/**
 * sharding engine.
 *
 * @Author: abysscat-yj
 * @Create: 2024/8/1 1:29
 */
public interface ShardingEngine {

	ShardingResult sharding(String sql, Object[] args);

}
