package com.cqq.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqq.reggie.common.BaseContext;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.pojo.ShoppingCart;
import com.cqq.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加到购物车的dishDto:{}", shoppingCart);
        Long userId = BaseContext.threadLocal.get();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart1 == null) {
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }else {
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartService.updateById(shoppingCart1);
        }


        return Result.success("添加成功");
    }


    /**
     * 获取用户购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        Long userId = BaseContext.threadLocal.get();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }


    /**
     * 减少餐品
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
//        AtomicInteger amount=new AtomicInteger();

        log.info("需要删除的购物车餐品信息：{}", shoppingCart);
        Long userId = BaseContext.threadLocal.get();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());

        shoppingCartService.remove(queryWrapper);

        return Result.success("删除成功！");

    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        Long userId = BaseContext.threadLocal.get();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(queryWrapper);

        return Result.success("清空购物车成功！");
    }

}
