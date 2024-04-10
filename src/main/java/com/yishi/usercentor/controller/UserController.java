package com.yishi.usercentor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yishi.usercentor.model.domain.User;
import com.yishi.usercentor.model.domain.request.UserLoginRequest;
import com.yishi.usercentor.model.domain.request.UserRegisterRequest;
import com.yishi.usercentor.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/***
 * 用户接口
 * @author yishi
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            return null;
        }
        String userAcount = userRegisterRequest.getUserAcount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAcount,userPassword,checkPassword)){
            return null;
        }
        long id = userService.userRegister(userAcount, userPassword, checkPassword);
        return id;
    }
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){
        if (userLoginRequest == null){
            return null;
        }
        String userAcount = userLoginRequest.getUserAcount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAcount,userPassword) ){
            return null;
        }
        User user = userService.userLogin(userAcount, userPassword, request);
        return user;
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username,HttpServletRequest request){
        //鉴权，仅管理员查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("userName",username);
        }
        return userService.list(queryWrapper);
    }

    @PostMapping("/delete")
    public Boolean deleteUsers(@RequestBody long id){
        if (id < 0){
            return false;
        }
        return userService.removeById(id);
    }
}
