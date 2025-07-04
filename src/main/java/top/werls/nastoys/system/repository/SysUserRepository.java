package top.werls.nastoys.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.werls.nastoys.system.entity.SysUser;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
  SysUser findByUsername(String username);
}
