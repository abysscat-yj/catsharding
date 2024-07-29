package com.abysscat.catsharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sharding result.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 0:23
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShardingResult {

	private String targetDataSourceName;

	private String targetSqlStatement;

	private Object[] parameters;

}
