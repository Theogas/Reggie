package com.cat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cat.common.R;
import com.cat.dto.UserDto;
import com.cat.pojo.User;
import com.cat.service.UserService;
import com.cat.utils.SMSUtils;
import com.cat.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService service;

    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user){
        //获取手机号
        String phone= user.getPhone();

        if(StringUtils.isNotBlank(phone)){
            //生成验证码
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为{}",s);

            //发送短信
            SMSUtils.sendMessage("外卖","",phone,s);

            //保存验证码
            session.setAttribute(phone,s);

            return R.success("发送成功");
        }
        return  R.error("发送失败");
    }


    /**
     * 登录
     * @param userDto
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpSession session){
        //获取数据
        String phone = userDto.getPhone();
        String code = userDto.getCode();

        //获取之前保存的验证码
        String attribute = (String) session.getAttribute(phone);

        //验证验证码
        if(attribute.equals(code)){
            //查询数据库，判定用户是否存在
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone,phone);
            User user = service.getOne(qw);

            //不存在
            if(user==null){
               user=new User();
               user.setPhone(phone);
               service.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);

        }

        return R.error("登录失败");
    }

}
