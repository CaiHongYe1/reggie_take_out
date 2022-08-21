package com.chy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Category;

public interface CategoryService extends IService<Category> {
    R<String> remove(Long id);
}
