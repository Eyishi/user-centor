package com.yishi.usercentor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yishi.usercentor.common.ErrorCode;
import com.yishi.usercentor.exception.BusinessException;
import com.yishi.usercentor.service.UserService;
import com.yishi.usercentor.model.domain.User;
import com.yishi.usercentor.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yishi.usercentor.contant.UserConstant.USER_LOGIN_STATE;

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


    @Override
    public long userRegister(String userAcount, String userPassword,
                             String checkPassword,String planetCode) {
        //1. 校验
        if(StringUtils.isAnyBlank(userAcount,userPassword,checkPassword,planetCode)){//判断这几位是不是null
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        if(userAcount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度<4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码<8 ");
        }
        if (planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号>5");
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*" +
                "（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"包含特殊字符");
        }
        //密码和校验码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        //账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAcount",userAcount);//查询userAcount=userAcount的条件
        long count = userMapper.selectCount(userQueryWrapper);
        if(count != 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }
        //星球编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planetCode",planetCode);//查询userAcount=userAcount的条件
        count = userMapper.selectCount(userQueryWrapper);
        if(count != 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }

        //2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3. 插入数据
        User user = new User();
        user.setUserAcount(userAcount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数有null");
        }
        if(userAcount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户<4");
        }
        if (userPassword.length() < 8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码<8");
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"包含特殊字符");
        }
        //2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("UserAcount",userAcount);
        userQueryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if (user == null){
            log.info("user login failed, userAcount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        //long count = userMapper.selectCount(userQueryWrapper);
        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return User
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAcount(originUser.getUserAcount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //注销
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




