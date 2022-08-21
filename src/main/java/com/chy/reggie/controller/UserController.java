package com.chy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.User;
import com.chy.reggie.service.UserService;
import com.chy.reggie.utils.SMSUtils;
import com.chy.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request){
//        获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
//        通过工具类生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//        通过阿里云短信服务发送短信
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
//        保存验证码到Session中,以手机号为键  以code为值
            request.getSession().setAttribute(phone,code);
        }

        return R.success("发送验证码成功");
    }

    /**
     * 移动端用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user,HttpServletRequest request){
        log.info("手机号:{}",user.getPhone());

        if(StringUtils.isNotEmpty(user.getPhone())){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("phone", user.getPhone());
            User result = userService.getOne(userQueryWrapper);
            //如果该手机号是新用户就先注册
            if(result == null){
                result = new User();
                result.setStatus(1);
                result.setPhone(user.getPhone());
                userService.save(result);
            }
            //如果user不为空 说明是老用户
            request.getSession().setAttribute("user",result.getId());
            return R.success(result);
        }
        return R.error("登录失败");
    }
}
