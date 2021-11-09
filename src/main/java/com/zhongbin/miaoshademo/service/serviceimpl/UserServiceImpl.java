package com.zhongbin.miaoshademo.service.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongbin.miaoshademo.exception.GlobalException;
import com.zhongbin.miaoshademo.mapper.UserMapper;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IUserService;
import com.zhongbin.miaoshademo.utils.MD5Util;
import com.zhongbin.miaoshademo.utils.ValidatorUtil;
import com.zhongbin.miaoshademo.vo.LoginVo;
import com.zhongbin.miaoshademo.vo.RespBean;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dabin
 * @since 2021-11-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password))
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        User user = userMapper.selectById(mobile);
        if(null == user)
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        if(!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success();
    }
}
