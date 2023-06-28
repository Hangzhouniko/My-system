package com.cqq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqq.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
