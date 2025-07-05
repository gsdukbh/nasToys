package top.werls.nastoys.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.werls.nastoys.system.entity.SysUser;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
  SysUser findByUsername(String username);

  @Transactional
  @Modifying
  @Query("update SysUser s set s.password = ?1 where s.uid = ?2")
  int updatePasswordByUid(String password, Long uid);
}
