package com.yishi.usercentor.service;

import com.yishi.usercentor.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 19720
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-04-08 22:29:49
*/
public interface UserService extends IService<User> {

    /**
     * 用户注释
     *
     * @param userAcount
     * @param userPassword
     * @param checkPassword
     * @return 用户id
     */
    long userRegister(String userAcount,String userPassword,String checkPassword);

    /**
     * 用户登录
     *
     * @param userAcount 账户
     * @param userPassword 密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAcount, String userPassword, HttpServletRequest request);
}
