package com.cqq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqq.reggie.dto.DishDto;
import com.cqq.reggie.pojo.Dish;

public interface DishService extends IService<Dish> {

     void saveWithFlavors(DishDto dishDto);

     void updateWithFlavors(DishDto dishDto);


}
