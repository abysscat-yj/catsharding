package com.abysscat.catsharding.engine;

import com.abysscat.catsharding.config.ShardingProperties;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * default sharding engine.
 *
 * @Author: abysscat-yj
 * @Create: 2024/8/1 1:30
 */
public class StandardShardingEngine implements ShardingEngine {

	private final MultiValueMap<String,String> actualDatabaseNames = new LinkedMultiValueMap<>();
	private final MultiValueMap<String,String> actualTableNames    = new LinkedMultiValueMap<>();
	private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
	private final Map<String, ShardingStrategy> tableStrategies    = new HashMap<>();

	public StandardShardingEngine(ShardingProperties properties) {
		properties.getTables().forEach((table, tableProperties) -> {
			tableProperties.getActualDataNodes().forEach(actualDataNode -> {
				String[] split = actualDataNode.split("\\.");
				String databaseName = split[0], tableName = split[1];
				actualDatabaseNames.add(databaseName, tableName);
				actualTableNames.add(tableName, databaseName);
			});
			// 不同库表可以配置不同的分片策略
			databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
			tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
		});
	}

	@Override
	public ShardingResult sharding(String sql, Object[] args) {
		// 通过druid工具类解析SQL
		SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);

		String table = null;
		Map<String, Object> shardingColumsMap = null;

		if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
			// insert
			table = sqlInsertStatement.getTableName().getSimpleName();
			shardingColumsMap = getShardingColumsMap(sqlInsertStatement, args);
		} else {
			// select/update/delete
			MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
			visitor.setParameters(List.of(args));
			sqlStatement.accept(visitor);

			LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
			if(sqlNames.size() > 1) {
				throw new RuntimeException("not support multi table sharding");
			}

			table = sqlNames.iterator().next().getSimpleName();
			System.out.println(" visitor.getOriginalTables = " + table);
			shardingColumsMap = visitor.getConditions().stream()
					.collect(Collectors.toMap(k -> k.getColumn().getName(), v -> v.getValues().get(0)));
			System.out.println(" visitor.getConditions = " + shardingColumsMap);
		}

		ShardingStrategy databaseStrategy = databaseStrategies.get(table);
		String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumsMap);
		ShardingStrategy tableStrategy = tableStrategies.get(table);
		String targetTable = tableStrategy.doSharding(actualTableNames.get(table), table, shardingColumsMap);
		System.out.println(" ===>>> ");
		System.out.println(" ===>>> target db.table = " + targetDatabase + "." + targetTable);
		System.out.println(" ===>>> ");

		// todo 此处直接替换不严谨
		String realSql = sql.replace(table, targetTable);

		return new ShardingResult(targetDatabase, realSql, args);
	}


	private static Map<String, Object> getShardingColumsMap(SQLInsertStatement sqlInsertStatement, Object[] args) {
		Map<String, Object> shardingColumsMap = new HashMap<>();
		List<SQLExpr> columns = sqlInsertStatement.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			SQLExpr column = columns.get(i);
			SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
			String columnName = columnExpr.getSimpleName();
			shardingColumsMap.put(columnName, args[i]);
		}
		return shardingColumsMap;
	}
}
