package com.abysscat.catsharding.engine;

/**
 * sharding context.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 0:22
 */
public class ShardingContext {

	private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

	public static ShardingResult get() {
		return LOCAL.get();
	}
	public static void set(ShardingResult shardingResult) {
		LOCAL.set(shardingResult);
	}

}
