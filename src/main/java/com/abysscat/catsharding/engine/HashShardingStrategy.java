package com.abysscat.catsharding.engine;

import groovy.lang.Closure;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * hash sharding strategy.
 *
 * @Author: abysscat-yj
 * @Create: 2024/8/1 1:32
 */
@Data
public class HashShardingStrategy implements ShardingStrategy {

	public HashShardingStrategy(Properties properties){
		this.shardingColumn = properties.getProperty("shardingColumn");
		this.algorithmExpression = properties.getProperty("algorithmExpression");
	}

	private String shardingColumn;
	private String algorithmExpression;

	@Override
	public List<String> getShardingColumns() {
		return Collections.singletonList(shardingColumn);
	}

	@Override
	public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
		// 计算算法表达式，得出分片结果
		InlineExpressionParser parser = new InlineExpressionParser(InlineExpressionParser.handlePlaceHolder(algorithmExpression));
		Closure<?> closure = parser.evaluateClosure();
		closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
		return closure.call().toString();
	}
}
