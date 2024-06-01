package com.yishi.usercentor.service;
import java.util.Date;

import com.yishi.usercentor.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/*
* 用户服务测试
* @author yishi
* */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();

        user.setUserName("周机菌");
        user.setUserAcount("1111110");
        user.setAvatarUrl("https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BB1lgqJ4.img?w=320&h=184&m=6");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("456");


        boolean res = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(res);//断言，就是判断期望的值跟实际的值是否相等
    }

    @Test
    void userRegister() {
        String userAcount = "yishi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "";
        long result;
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

        userAcount="yi";
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

        userAcount="yishi";
        userPassword="123456";
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

        userAcount="yi shi";
        userPassword="12345678";
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);


        userPassword="123456789";
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

        userAcount="1111110";

        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

        userAcount="jijun";
        userPassword="123456789";
        checkPassword = "123456789";
        result = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        Assertions.assertTrue(result>0);
    }
}