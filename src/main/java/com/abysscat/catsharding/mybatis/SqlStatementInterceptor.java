package com.abysscat.catsharding.mybatis;

import com.abysscat.catsharding.engine.ShardingContext;
import com.abysscat.catsharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * mybatis sql interceptor.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 0:54
 */
@Intercepts(
		@org.apache.ibatis.plugin.Signature(
				type = StatementHandler.class,
				method = "prepare",
				args = {java.sql.Connection.class, Integer.class}
		)
)
public class SqlStatementInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		ShardingResult result = ShardingContext.get();
		if(result == null) return invocation.proceed();

		StatementHandler handler = (StatementHandler) invocation.getTarget();
		BoundSql boundSql = handler.getBoundSql();

		// 打印SQL语句
		System.out.println(" ===> SqlStatementInterceptor: " + boundSql.getSql());
		Object parameterObject = boundSql.getParameterObject();
		System.out.println(" ===> SqlStatementInterceptor: " + parameterObject);
		String targetSql = result.getTargetSqlStatement();

		if(targetSql.equalsIgnoreCase(boundSql.getSql())) {
			return invocation.proceed();
		}

		// 替换SQL语句
		replaceSql(boundSql, targetSql);
		return invocation.proceed();
	}

	private static void replaceSql(BoundSql boundSql, String targetSqlStatement) throws NoSuchFieldException {
		Field field = boundSql.getClass().getDeclaredField("sql");
		Unsafe unsafe = UnsafeUtils.getUnsafe();
		// 通过指针来修改final的sql字段
		long fieldOffset = unsafe.objectFieldOffset(field);
		unsafe.putObject(boundSql, fieldOffset, targetSqlStatement);
	}
}
