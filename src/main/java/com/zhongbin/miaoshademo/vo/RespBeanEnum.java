package com.zhongbin.miaoshademo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    //登陆
    LOGIN_ERROR(500210, "用户名或密码错误"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    MOBILE_NOT_EXISTS(500213, "手机号码不存在"),
    BIND_ERROR(500212, "参数校验异常"),
    SESSION_ERROR(500215, "用户未登陆或用户不存在"),
    //秒杀
    ENPTY_STOCK(500500, "库存不足"),
    REPEATE_ERROR(500501, "重复抢购"),
    REQUEST_ILLEGAL(500502, "请求非法"),
    ACCESS_LIMITED_REACHED(500503, "访问过于频繁"),

    ORDER_NOT_EXIST(500801, "订单不存在"),

    ERROR_CAPTCHA(500888, "验证码错误，请重新输入"),

    FAILED_TO_UPDATE_PASWORD(500222, "修改密码失败");


    private final Integer code;
    private final String message;
}
