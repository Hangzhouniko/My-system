package com.cqq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.pojo.Category;
import com.cqq.reggie.service.CategoryService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类分页信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> getPage(Integer page, Integer pageSize) {

        log.info("page:{},pageSize:{}", page, pageSize);

        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> insertCategory(@RequestBody Category category) {
//        Category category1 = new Category();

        log.info("!!!!!!!!!!name:{},type:{},sort:{}", category.getName(), category.getType(), category.getSort());
//        if (name==null||type==0||sort==0){
//            return Result.error("添加失败");
//        }
//        category1.setName(category.getName());
//        category1.setSort(category.getSort());
//        category1.setType(category.getType());

        categoryService.save(category);

        return Result.success("添加成功！");

    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> deleteCategory(long id) {
        log.info("@!@@!!!!获得需要删除的菜品id：{}", id);
        categoryService.remove(id);

        return Result.success("删除成功！");
    }


    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        log.info("！！！！需要修改的分类信息:{}", category.toString());
//        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.eq(StringUtils.isNotBlank(category.getName()),
//                Category::getName,category.getName());
        categoryService.updateById(category);

        return Result.success("修改成功！");
    }


    /**
     * 根据条件查询分类数据
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getOne(Integer type) {
        log.info("查询分类的列表：{}", type);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        if (type!=null) {
            queryWrapper.eq(Category::getType, type);
        }
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return Result.success(list);
    }


}
