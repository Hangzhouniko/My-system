package com.cqq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.dto.SetmealDto;
import com.cqq.reggie.pojo.Category;
import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.pojo.SetmealDish;
import com.cqq.reggie.service.CategoryService;
import com.cqq.reggie.service.SetmealDishService;
import com.cqq.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("setmeal")
@Slf4j
@Transactional
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> getPage(int page,int pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        if(name!=null&&name!=""){
            queryWrapper.like(Setmeal::getName,name);
        }

        setmealService.page(pageInfo,queryWrapper);

        Page<SetmealDto> dtoPage=new Page<>();

        BeanUtils.copyProperties(pageInfo, dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> dtoList=new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());

            dtoList.add(setmealDto);
        }
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }


    /**
     * 新增套餐
     * @param setMealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setMealDto){
        log.info("!!@@@@@@@@@@@@@参数setMealDto：{}",setMealDto);

        setmealService.saveWithDish(setMealDto);

        return Result.success("套餐新增成功！");
    }


    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> saveStatus(@PathVariable("status") Integer status,long[] ids){
            for (long id : ids) {
                Setmeal setmeal = setmealService.getById(id);
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        return Result.success("修改成功！");
    }


    /**
     * 返回单个套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> getOne(@PathVariable Long id){
        SetmealDto setmealDto=new SetmealDto();

        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);


        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(dishes);

        Category category = categoryService.getById(setmeal.getCategoryId());

        setmealDto.setCategoryName(category.getName());

        return Result.success(setmealDto);

    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return Result.success("修改成功！");
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteById(long[] ids){
        log.info("========>ids:{}",ids);
        setmealService.deleteWithDish(ids);

        return Result.success("删除成功！");
    }


    @GetMapping("/list")
    public Result<List<SetmealDto>> getSetmealList(Long categoryId,Integer status){
        log.info("categoryId:{},,status:{}",categoryId,status);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        List<SetmealDto> dtoList=new ArrayList<>();

        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,setmeal.getId());
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper1);
            setmealDto.setSetmealDishes(setmealDishes);
            dtoList.add(setmealDto);
        }

        return Result.success(dtoList);




    }

}
