package com.cqq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqq.reggie.dto.SetmealDto;
import com.cqq.reggie.pojo.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(long[] ids);
}
