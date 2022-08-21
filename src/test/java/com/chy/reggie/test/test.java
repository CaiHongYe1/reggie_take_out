package com.chy.reggie.test;

import com.chy.reggie.dto.DishDto;
import com.chy.reggie.mapper.DishMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class test {

    @Autowired
    private DishMapper dishMapper;

    @Test
    public void test01(){
        List<DishDto> getpage = dishMapper.getpage(1, 10, null);
        for (DishDto dishDto : getpage) {
            System.out.println("id:"+dishDto.getId()+"name:"+dishDto.getName()+dishDto.toString());
        }
    }
}
