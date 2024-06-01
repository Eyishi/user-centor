package com.yishi.usercentor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yishi.usercentor.common.BaseResponse;
import com.yishi.usercentor.common.ErrorCode;
import com.yishi.usercentor.common.ResultUtils;
import com.yishi.usercentor.exception.BusinessException;
import com.yishi.usercentor.model.domain.User;
import com.yishi.usercentor.model.domain.request.UserLoginRequest;
import com.yishi.usercentor.model.domain.request.UserRegisterRequest;
import com.yishi.usercentor.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yishi.usercentor.contant.UserConstant.ADMIN_ROLE;
import static com.yishi.usercentor.contant.UserConstant.USER_LOGIN_STATE;

/***
 * 用户接口
 * @author yishi
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("用户注册");
        String userAcount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAcount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        long data = userService.userRegister(userAcount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(data);
    }
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){
        log.info("用户登录");
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAcount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAcount,userPassword) ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        User data = userService.userLogin(userAcount, userPassword, request);
         return ResultUtils.success(data);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        Integer id = userService.userLogout(request);
        return  ResultUtils.success(id);
    }
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        Long id = currentUser.getId();
        //todo 后面校验用户是否合法
        User user = userService.getById(id);

        user = userService.getSafetyUser(user);
        return ResultUtils.success(user);
    }
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        //鉴权，仅管理员查询
        if (!isAdmin(request)){
           throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("userName",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user ->
        {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id,HttpServletRequest request){
        //鉴权，仅管理员查询
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 判断是否是管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //鉴权，仅管理员查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
