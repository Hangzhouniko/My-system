package com.cqq.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.dto.DishDto;
import com.cqq.reggie.pojo.Category;
import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.DishFlavor;
import com.cqq.reggie.service.CategoryService;
import com.cqq.reggie.service.DishFlavorService;
import com.cqq.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 菜品
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 菜品分页信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> getPage(int page, int pageSize, String name) {
        log.info("！！！参数page:{},pageSize:{},name:{}", page, pageSize, name);

        String key ="dishPage_"+page+"_"+pageSize+"_"+name;
        Page<DishDto> dishPageRedis = (Page<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishPageRedis!=null){
            return Result.success(dishPageRedis);
        }


        Page<Dish> pageInfo = new Page<>(page, pageSize);

        //查询原始dish数据
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        queryWrapper.orderByAsc(Dish::getSort);
        dishService.page(pageInfo, queryWrapper);


        //转换为dishDto数据
        Page<DishDto> dtoPage = new Page<>();

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<DishDto> dtoList = new ArrayList<>();
        List<Dish> records = pageInfo.getRecords();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(record, dishDto);
            Long categoryId = record.getCategoryId();

            if (categoryId != null) {
                String categoryName = categoryService.getById(categoryId).getName();
                dishDto.setCategoryName(categoryName);
            }

            dtoList.add(dishDto);
        }
        dtoPage.setRecords(dtoList);

        redisTemplate.opsForValue().set(key, dtoPage,1,TimeUnit.HOURS);


        return Result.success(dtoPage);
    }


    /**
     * 菜品信息
     */
    @GetMapping("/{id}")
    public Result<DishDto> getOne(@PathVariable("id") Long id) {
        log.info("!!!查询菜品的id：{}", id);
        Dish dish = dishService.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);


        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);

        if (!dishFlavors.isEmpty()) {
            dishDto.setFlavors(dishFlavors);
        }

        Long categoryId = dish.getCategoryId();
        if (categoryId != null) {
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        return Result.success(dishDto);

    }


    /**
     * 按条件查询对应菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> getCategory(Dish dish) {
        List<DishDto> dtoList =null;

        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        log.info("key:{}",key);

        dtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);

        if(dtoList!=null){
            return Result.success(dtoList);
        }


        dtoList =new ArrayList<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.eq(dish.getCategoryId() != null,
                Dish::getCategoryId,
                dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus, 1);

        queryWrapper.like(dish.getName() != null && dish.getName() != "",
                Dish::getName,
                dish.getName());

        queryWrapper.orderByDesc(Dish::getSort);
        List<Dish> list = dishService.list(queryWrapper);


        for (Dish dish1 : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavors);
            dtoList.add(dishDto);
        }

        redisTemplate.opsForValue().set(key, dtoList,60, TimeUnit.MINUTES);


        return Result.success(dtoList);
    }


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("!!!!dishDto参数获取：{}", dishDto.toString());
        dishService.saveWithFlavors(dishDto);

        String key ="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return Result.success("添加成功！");
    }


    /**
     * 更新菜品和口味信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> updateOne(@RequestBody DishDto dishDto) {
        log.info("菜品信息修改为：{}", dishDto.toString());
        dishService.updateWithFlavors(dishDto);

//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        String key ="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);


        return Result.success("修改成功！");
    }


    /**
     * 修改菜品状态：启售、停售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable("status") Integer status, Long[] ids) {
        log.info("@@@@!!!====>status:{}.......ids:{}", status, ids);

        for (Long id : ids) {
            Dish dish = dishService.getById(id);

            dish.setStatus(status);

            dishService.updateById(dish);
        }

        return Result.success("停售成功！");

    }


    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteDish(Long[] ids) {
        log.info("!!@@@@@=====> ids:{}", ids.toString());

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        for (Long id : ids) {
            queryWrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(queryWrapper);

            dishService.removeById(id);
        }
        return Result.success("删除菜品成功!");
    }

}
