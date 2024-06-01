package com.yishi.usercentor.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/***
 * 用户注册的请求体
 *
 * @author yishi
 */
@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = -5643944241386886699L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
