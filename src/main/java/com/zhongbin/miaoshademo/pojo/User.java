package com.zhongbin.miaoshademo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author dabin
 * @since 2021-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，手机号码
     */
    private Long id;

    private String nickname;

    /**
     * MD5(MD5(pwd + salt) + salt)
     */
    private String password;

    private String salt;

    /**
     * 头像
     */
    private String head;

    private Date registerDate;

    private Date lastLoginTime;

    private Integer loginCount;


}
