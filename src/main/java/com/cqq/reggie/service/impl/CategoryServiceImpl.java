package com.cqq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.reggie.common.CustomException;
import com.cqq.reggie.mapper.CategoryMapper;
import com.cqq.reggie.pojo.Category;
import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.service.CategoryService;
import com.cqq.reggie.service.DishService;
import com.cqq.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {

        //查询dish中是否关联
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(queryWrapper);
        if(count1>0){
            throw new CustomException("已关联菜品！无法删除！");
        }

        //查询Setmeal中是否关联
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2>0){
            throw new CustomException("已关联套餐！无法删除！");

        }

        //正常删除
        super.removeById(id);

    }
}
