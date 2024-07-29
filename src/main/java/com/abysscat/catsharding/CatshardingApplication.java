package com.abysscat.catsharding;

import com.abysscat.catsharding.demo.mapper.UserMapper;
import com.abysscat.catsharding.demo.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@MapperScan(value = "com.abysscat.catsharding.demo.mapper")
public class CatshardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatshardingApplication.class, args);
	}

	@Autowired
	UserMapper userMapper;

	@Bean
	ApplicationRunner applicationRunner() {
		return x -> {
			System.out.println("=================================================");
			System.out.println("===== Run catsharding test for mybatis CRUD =====");
			System.out.println("=================================================");

			for (int id = 1; id <= 1; id++) {
				test(id);
			}

			System.out.println("=================================================");
			System.out.println("=====        Run all test completely.       =====");
			System.out.println("=================================================");
		};
	}

	private void test(int id) {

		System.out.println(" ===> \n\n");
		System.out.println(" ===> test for id = " + id);

		System.out.println(" ===> 0. test delete ...");
		int deleted = userMapper.delete(id);
		System.out.println(" ===> deleted = " + deleted);

		System.out.println(" ===> 1. test insert ...");
		int inserted = userMapper.insert(new User(id, "abysscat", 20));
		System.out.println(" ===> inserted = " + inserted);

		System.out.println(" ===> 2. test find ...");
		User user = userMapper.findById(id);
		System.out.println(" ===> find = " + user);

		System.out.println(" ===> 3. test update ...");
		user.setName("aaa");
		int updated = userMapper.update(user);
		System.out.println(" ===> updated = " + updated);

		System.out.println(" ===> 4. test new find ...");
		User user2 = userMapper.findById(id);
		System.out.println(" ===> find = " + user2);


		System.out.println(" ===> complete for id = " + id);
		System.out.println(" ===> \n\n");

	}

}
