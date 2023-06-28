package com.cqq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cqq.reggie.common.BaseContext;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.pojo.AddressBook;
import com.cqq.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取地址列表
     * @param httpSession
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> getList(HttpSession httpSession){
        long userId = (long) httpSession.getAttribute("user");

        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);

        return Result.success(addressBookList);
    }


    /**
     * 新增地址信息
     * @param addressBook
     * @param httpSession
     * @return
     */
    @PostMapping
    public Result<String> addAddress(@RequestBody AddressBook addressBook
                            ,HttpSession httpSession){
        log.info("addressBook:{}",addressBook);
        long userId = (long)httpSession.getAttribute("user");

        addressBook.setUserId(userId);

        addressBookService.save(addressBook);
        return Result.success("地址添加成功！");
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @param httpSession
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook,
                                     HttpSession httpSession){
        log.info("addressBook:{}",addressBook);
        long userId = (long) httpSession.getAttribute("user");
        Long id = BaseContext.threadLocal.get();
        log.info("id:{}，，user:{}",id,userId);

        LambdaUpdateWrapper<AddressBook> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,userId);
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);

        return Result.success("设置成功！");
    }


    /**
     * 跳转修改地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> toUpdateAddr(@PathVariable("id")long id){
        log.info("需要修改的地址id:{}",id);
        AddressBook addressBook = addressBookService.getById(id);

        return Result.success(addressBook);
    }


    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> updateAddr(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}",addressBook);

        addressBookService.updateById(addressBook);

        return Result.success("修改成功！");
    }


    /**
     * 删除地址信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteAddr(Long ids){
        log.info("需要删除的地址ids：{}",ids);

        addressBookService.removeById(ids);


        return Result.success("删除成功！");
    }


    @GetMapping("/default")
    public Result<AddressBook> defaultAddr(){
        Long userId = BaseContext.threadLocal.get();

        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);


        return Result.success(addressBook);
    }


}
