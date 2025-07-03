package top.werls.nastoys.system.service;


import top.werls.nastoys.system.param.LoginParam;
import top.werls.nastoys.system.vo.LoginVo;

public interface SysUserService {


    /**
     *
     * 登录
     * @param param
     * @return
     */
    LoginVo login(LoginParam param);

}
