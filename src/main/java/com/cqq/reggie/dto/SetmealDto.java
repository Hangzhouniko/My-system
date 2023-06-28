package com.cqq.reggie.dto;


import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;

 }
