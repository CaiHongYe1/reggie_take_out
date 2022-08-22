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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request){
//        获取手机号
        String phone = user.getPhone();
        System.out.println(phone);
        if(StringUtils.isNotEmpty(phone)){
//        通过工具类生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//        通过阿里云短信服务发送短信
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
//        保存验证码到Session中,以手机号为键  以code为值
//            request.getSession().setAttribute(phone,code);

//          将生成的验证码缓存到Redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set("phone:"+phone,code,5, TimeUnit.MINUTES);
            System.out.println("验证码："+code);
        }

        return R.success("发送验证码成功");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        log.info("手机号:{}", phone);
        log.info("验证码:{}", code);
        //从redis缓存中取出手机号对应的验证码
        String rediscode = redisTemplate.opsForValue().get("phone:" + phone).toString();

        if(rediscode != null && rediscode.equals(code)){
//            如果对比成功，说明登录成功
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("phone", phone);
            User result = userService.getOne(userQueryWrapper);
            //如果该手机号是新用户就先注册
            if(result == null){
                result = new User();
                result.setStatus(1);
                result.setPhone(phone);
                userService.save(result);
            }
            //如果user不为空 说明是老用户
            request.getSession().setAttribute("user",result.getId());
//            如果用户登录成功，则我们可以删除redis中缓存的验证码
            redisTemplate.delete("phone:" + phone);
            return R.success(result);
        }
        return R.error("登录失败");
    }
}
