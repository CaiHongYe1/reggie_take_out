package com.chy.reggie.dto;


import com.chy.reggie.javabean.Setmeal;
import com.chy.reggie.javabean.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
