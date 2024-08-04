package com.abysscat.catsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * order entity.
 *
 * @Author: abysscat-yj
 * @Create: 2024/8/5 1:43
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {

	private int id;

	private int uid;

	private double price;

}
