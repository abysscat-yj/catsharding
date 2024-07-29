package com.abysscat.catsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * user entity.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/29 23:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	private int id;

	private String name;

	private int age;

}
