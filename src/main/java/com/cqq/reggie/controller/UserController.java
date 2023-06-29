package com.cqq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.pojo.User;
import com.cqq.reggie.service.UserService;
import com.cqq.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        log.info("########phone:{}", user);

        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            code="1234";//为了测试方便验证码改成1234
            log.info("短信验证码:{}", code);
            //发送短信
//            SMSUtils.sendMessage("瑞吉外卖", "", phone,code);



            //验证码缓存到Redis中
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            //保存验证码到Session
//            httpSession.setAttribute(phone, code);
//            log.info("phone:{},code:{}", phone, code);

            return Result.success("手机短信发送成功!");
        }
        return Result.error("短信发送失败！");
    }


    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession httpSession) {
        log.info("map:{}", map);

        String phone = map.get("phone").toString();
        log.info("phone:{}", phone);

        String code = map.get("code").toString();
        log.info("code:{}", code);


//        Object codeInSession = httpSession.getAttribute(phone);
//        log.info("codeInSession:{}", codeInSession);

        String codeInRedis = (String) redisTemplate.opsForValue().get(phone);


        if (codeInRedis != null && codeInRedis.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user ==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            httpSession.setAttribute("user", user.getId());

            //如果用户登录成功，删除Redis中的验证码
            redisTemplate.delete(phone);

            return Result.success(user);
        }
        return Result.error("登录失败！");

    }


    @PostMapping("/loginout")
    public Result<String> logout(HttpSession httpSession){
        log.info("user:{}",httpSession.getAttribute("user"));

        httpSession.setAttribute("user", null);

        log.info("user:{}",httpSession.getAttribute("user"));
        return Result.success("退出成功！");

    }
}
