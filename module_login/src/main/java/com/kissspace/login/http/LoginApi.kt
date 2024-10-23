package com.kissspace.login.http

object LoginApi {

    //友盟一键登录
    const val API_QUICK_LOGIN = "/hamster-user/user/register"

    //完善用户信息
    const val API_EDIT_USER_INFO = "/hamster-user/user/updateUser"

    //手机验证码登录
    const val API_SMS_CODE_LOGIN = "/hamster-user/user/login"

    //友盟手机获取用户列表
    const val API_USER_LIST_UMENG = "/hamster-user/user/queryUserListByUMeng"

    //获取启动页广告
    const val API_GET_AD = "/hamster-user/splashAd/queryOne"

    const val API_PASSWORD_LOGIN = "/hamster-user/user/passwordLogin"

    //重置密码
    const val API_RESET_LOGIN_PASSWORD = "/hamster-user/user/resetUserLoginPassword"

    //查询手机号是否存在
    const val API_QUERY_PHONE_EXIST = "/hamster-user/user/queryPhoneExist"
}