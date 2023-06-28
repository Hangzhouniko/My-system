package com.cqq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.reggie.dto.SetmealDto;
import com.cqq.reggie.mapper.SetmealMapper;
import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.pojo.SetmealDish;
import com.cqq.reggie.service.SetmealDishService;
import com.cqq.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDishService.save(setmealDish);
        }


    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealId);
            setmealDishService.save(setmealDish);
        }

    }

    @Override
    public void deleteWithDish(long[] ids) {
        for (long id : ids) {

            this.removeById(id);

            LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(queryWrapper);
        }

    }
}
