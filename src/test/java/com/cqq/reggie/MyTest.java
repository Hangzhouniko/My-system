package com.cqq.reggie;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.dto.DishDto;
import com.cqq.reggie.pojo.Dish;
import com.cqq.reggie.pojo.Setmeal;
import com.cqq.reggie.service.DishService;
import com.cqq.reggie.service.SetmealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MyTest {
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


}
