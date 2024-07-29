package com.abysscat.catsharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;

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
		StatementHandler handler = (StatementHandler) invocation.getTarget();
		BoundSql boundSql = handler.getBoundSql();
		System.out.println(" ===> sql statement: " + boundSql.getSql());
		Object parameterObject = boundSql.getParameterObject();
		System.out.println(" ===> sql parameters: " + parameterObject);
		return invocation.proceed();
	}
}
