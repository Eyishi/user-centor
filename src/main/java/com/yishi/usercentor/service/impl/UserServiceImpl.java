package com.yishi.usercentor.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yishi.usercentor.service.UserService;
import com.yishi.usercentor.model.domain.User;
import com.yishi.usercentor.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author 19720
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-04-08 22:29:49
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private final String SALT = "yishi";

    /**
     *  用户登录态键
     */
    private static final String USER_LOGIN_STATE = "userLoginState";
    @Override
    public long userRegister(String userAcount, String userPassword, String checkPassword) {
        //1. 校验
        if(StringUtils.isAnyBlank(userAcount,userPassword,checkPassword)){//判断这几位是不是null
            return -1;
        }
        if(userAcount.length() < 4){
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if(matcher.find()){
            return -1;
        }
        //密码和校验码相同
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        //账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAcount",userAcount);//查询userAcount=userAcount的条件
        long count = userMapper.selectCount(userQueryWrapper);
        if(count< 0){
            return -1;
        }

        //2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3. 插入数据
        User user = new User();
        user.setUserAcount(userAcount);
        user.setUserPassword(userPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAcount, String userPassword, HttpServletRequest request) {
        //1. 校验
        if(StringUtils.isAnyBlank(userAcount,userPassword)){//判断这几位是不是null
            return null;
        }
        if(userAcount.length() < 4){
            return null;
        }
        if (userPassword.length() < 8 ){
            return null;
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if(matcher.find()){
            return null;
        }
        //2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("UserAcount",userAcount);
        userQueryWrapper.eq("userPassword",userPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if (user == null){
            log.info("user login failed, userAcount cannot match userPassword");
            return null;
        }
        //long count = userMapper.selectCount(userQueryWrapper);
        // 3.用户脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAcount(user.getUserAcount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        // 4.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
}




