package top.werls.nastoys.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.werls.nastoys.entity.Machine;
import top.werls.nastoys.entity.WolLog;
import top.werls.nastoys.repository.MachineRepository;
import top.werls.nastoys.repository.WolLogRepository;

/**
 * 设备服务
 *
 * @author JiaWei Lee
 * @since on 05 7月 2025
 * @version 1
 */
@Service
@Transactional
public class MachineService {

  private final MachineRepository machineRepository;

  private final WolLogRepository wolLogRepository;
  public MachineService(MachineRepository machineRepository, WolLogRepository wolLogRepository) {
    this.machineRepository = machineRepository;
    this.wolLogRepository = wolLogRepository;
  }

  /**
   * 保存机器信息
   * @param v 机器信息
   */
  public void saveMachine(Machine v) {
    machineRepository.save(v);
  }

  public String  shoutDownMachine(String name) {
    // 这里可以添加关机逻辑
    // 例如，调用关机命令或发送关机请求
    var machine = machineRepository.findByName(name);
    if (machine == null) {
      return "设备不存在: " + name;
    }
    WolLog ne = new WolLog();
    ne.setName(machine.getName());
    ne.setMac(machine.getMac());
    ne.setBroadcastIp( machine.getBroadcastIp());
    ne.setStatus(false);
    ne.setDelayTime(10);
    wolLogRepository.save(ne);

    return "设备已关机: " ;
  }

  public WolLog getWol(String mac){
    var tem  = wolLogRepository.queryByMac(mac.toLowerCase(), false);
    if (tem.isEmpty()) {
      return null;
    }
    return  tem.getFirst();
  }

  public void updateWolLog(WolLog wolLog) {
    wolLogRepository.updateCompletedTimeAndStatusById(wolLog.getCompletedTime(), wolLog.getStatus(), wolLog.getId());
  }

}
