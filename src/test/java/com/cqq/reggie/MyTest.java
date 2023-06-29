package com.cqq.reggie;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.dto.DishDto;
import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.service.DishService;
import com.cqq.reggie.service.SetmealService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
//@RunWith(SpringRunner.class)
public class MyTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${reggie.path}")
    private String basePath;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Test
    public void testUpload(){
        String name ="a2e20130929094338_88710.jpg";
        int i = name.lastIndexOf(".");

        String substring = name.substring(i);
        System.out.println(substring);

        System.out.println(substring.endsWith(".jpg"));
    }


    @Test
    public void testC(){
//        List<Dish> list = dishService.list();
//
//        list.forEach(System.out::println);
        Dish dish = dishService.getById(1397849739276890114l);
        System.out.println(dish);

        DishDto dishDto = new DishDto();
//        dishDto=(DishDto)dish;
//        BeanUtils.copyProperties(dish, dishDto,"{categoryName},{copies}");
        BeanUtils.copyProperties(dish, dishDto);
        System.out.println("dishDto==========>"+dishDto);

     }

     @Test
    public void testD(){
         Page<Dish> dishPage=new Page<>(1,5);
         List<DishDto> dtoList=new ArrayList<>();


         Page<Dish> page = dishService.page(dishPage, null);

         List<Dish> records = page.getRecords();

         for (Dish record : records) {
             DishDto dishDto=new DishDto();
             BeanUtils.copyProperties(record, dishDto);
             dtoList.add(dishDto);
         }

         for (DishDto dto : dtoList) {
             String name = dto.getName();
             System.out.println(name);
         }

     }

    @Test
    public void testCopy() throws IOException {
        List<Dish> list = dishService.list();


        String sourceFile="C:\\Users\\Nikoo\\Desktop\\LinuxLocal\\1.jpg";
        String desPath="C:\\Users\\Nikoo\\Desktop\\LinuxLocal\\";

        String desFile=null;


        for (Dish dish : list) {
            String image = dish.getImage();
            desFile=desPath+image;
            copyFile(sourceFile, desFile);
        }


    } @Test
    public void testCopySetMeal() throws IOException {
        List<Setmeal> list = setmealService.list();


        String sourceFile="C:\\Users\\Nikoo\\Desktop\\LinuxLocal\\1.jpg";
        String desPath="C:\\Users\\Nikoo\\Desktop\\LinuxLocal\\";

        String desFile=null;


        for (Setmeal setmeal : list) {
            String image = setmeal.getImage();
            desFile=desPath+image;
            copyFile(sourceFile, desFile);
        }


    }

    private void copyFile(String sourceFile, String desFile) throws IOException {
        FileInputStream fileInputStream =new FileInputStream(sourceFile);
        byte[] bytes=new byte[1024];
        int len =0;
        FileOutputStream fileOutputStream=new FileOutputStream(desFile);




        while ((len=fileInputStream.read(bytes))!=-1){
            fileOutputStream.write(bytes,0,len);
            fileOutputStream.flush();

        }

        fileInputStream.close();
        fileOutputStream.close();
    }

    @Test
    public void testRedis(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("city", "hangzhou");
        Integer append = redisTemplate.opsForValue().append("city", "222");

//        redisTemplate.delete("city");
        System.out.println(append);
        System.out.println(redisTemplate.opsForValue().get("city"));
//        Object city = redisTemplate.opsForValue().get("city");
//        System.out.println(city);

    }

    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();
//        hashOperations.put("hashName", "hashKeyName", "hashValue");
        hashOperations.put("hashName", "hashKeyName2", "hashValue2");
        hashOperations.put("hashName", "hashKeyName3", "hashValue3");

//        Object o = hashOperations.get("hashName", "hashKeyName");
//        System.out.println(o);
        Set keys = hashOperations.keys("hashName");
        for (Object key : keys) {
            System.out.println(key);
        }
        List values = hashOperations.values("hashName");
        for (Object value : values) {
            System.out.println(value);
        }

    }

    @Test
    public void testKeys(){
        Set keys = redisTemplate.keys("*");
        for (Object key : keys) {
            System.out.println(key);
        }


    }
}
