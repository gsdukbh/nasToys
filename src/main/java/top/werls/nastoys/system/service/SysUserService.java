package top.werls.nastoys.system.service;


import top.werls.nastoys.system.dto.param.LoginParam;
import top.werls.nastoys.system.dto.vo.LoginVo;
import top.werls.nastoys.system.entity.SysUser;

public interface SysUserService {


    /**
     *
     * 登录
     * @param param
     * @return
     */
    LoginVo login(LoginParam param);

    /**
     * 修改密码
     * @param user 用户信息
     * @return 修改结果
     */
    String changePassword(SysUser user);

    SysUser findByUsername(String username);


}
