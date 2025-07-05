package top.werls.nastoys.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.werls.nastoys.entity.WolLog;

@Repository
public interface WolLogRepository extends JpaRepository<WolLog, Long> {

  /**
   * 根据设备ID查询WOL日志
   *
   * @param mac 设备ID
   * @return WOL日志
   */
  List<WolLog> findByMac(String  mac);

  /**
   * 根据设备ID和状态查询WOL日志
   *
   * @param mac    设备ID
   * @param status 状态
   * @return WOL日志
   */
  List<WolLog> findByMacAndStatus(String  mac, Boolean status);

  @Query("select  w from WolLog w where w.mac = ?1 and w.status = ?2 order by w.createTime desc")
  List<WolLog> queryByMac(String mac, Boolean status);

  @Transactional
  @Modifying
  @Query("update WolLog w set w.completedTime = ?1, w.status = ?2 where w.id = ?3")
  int updateCompletedTimeAndStatusById(Date completedTime, Boolean status, Long id);
}
