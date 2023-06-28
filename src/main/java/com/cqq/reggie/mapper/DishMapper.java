package com.cqq.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqq.reggie.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
