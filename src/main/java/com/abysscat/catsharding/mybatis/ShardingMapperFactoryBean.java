package com.abysscat.catsharding.mybatis;

import com.abysscat.catsharding.demo.model.User;
import com.abysscat.catsharding.engine.ShardingContext;
import com.abysscat.catsharding.engine.ShardingResult;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * sharding mapper factory bean.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/30 1:13
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

	public ShardingMapperFactoryBean() {
	}

	public ShardingMapperFactoryBean(Class<T> mapperInterface) {
		super(mapperInterface);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		Object proxy = super.getObject();
		SqlSession session = getSqlSession();
		Configuration configuration = session.getConfiguration();
		Class<?> clazz = getMapperInterface();
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {
			BoundSql boundSql = getBoundSql(method, args, clazz, configuration);
			Object[] params = getParams(args, boundSql);

			if (params.length == 0) {
				return method.invoke(proxy, args);
			}
			ShardingResult result = new ShardingResult();
			result.setParameters(params);
			result.setTargetSqlStatement(boundSql.getSql());

			if (params[0] instanceof User user) {
				if (user.getId() % 2 == 0) {
					result.setTargetDataSourceName("ds0");
				} else {
					result.setTargetDataSourceName("ds1");
				}
			} else if (params[0] instanceof Integer id) {
				if (id % 2 == 0) {
					result.setTargetDataSourceName("ds0");
				} else {
					result.setTargetDataSourceName("ds1");
				}
			}
			ShardingContext.set(result);
			return method.invoke(proxy, args);
		});
	}

	private static Object[] getParams(Object[] args, BoundSql boundSql) throws IllegalAccessException {
		Object[] params = args;
		if(args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
			Object arg = args[0];
			List<String> cols = boundSql.getParameterMappings()
					.stream().map(ParameterMapping::getProperty).toList();
			Object[] values = new Object[cols.size()];
			for (int i = 0; i < cols.size(); i++) {
				Field field = ReflectionUtils.findField(arg.getClass(), cols.get(i));
				if(field == null) throw new IllegalArgumentException("can not find field " + cols.get(i));
				field.setAccessible(true);
				values[i] = field.get(arg);
			}
			params = values;
		}
		return params;
	}

	private static BoundSql getBoundSql(Method method, Object[] args, Class<?> clazz, Configuration configuration) {
		String mapperId = clazz.getName() + "." + method.getName();
		MappedStatement statement = configuration.getMappedStatement(mapperId);
		return statement.getBoundSql(args);
	}

}
