package com.cqq.reggie.dto;

import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;



}
