package com.cqq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.reggie.dto.DishDto;
import com.cqq.reggie.mapper.DishMapper;
import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.DishFlavor;
import com.cqq.reggie.service.DishFlavorService;
import com.cqq.reggie.service.DishService;
import com.sun.deploy.uitoolkit.ui.DialogHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>implements DishService {

    @Autowired
    public DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavors(DishDto dishDto) {

        this.save(dishDto);
        //获取菜品ID
        Long dishId = dishDto.getId();


        //设置菜品ID再添加口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dishFlavorService.save(flavor);
        }

    }

    @Override
    public void updateWithFlavors(DishDto dishDto) {
        this.updateById(dishDto);
        Long dishId = dishDto.getId();

        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);

        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dishFlavorService.save(flavor);
        }



    }
}
