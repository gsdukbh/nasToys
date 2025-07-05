package top.werls.nastoys.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
import top.werls.nastoys.entity.WolLog;
import top.werls.nastoys.service.MachineService;
import top.werls.nastoys.service.WolService;

/**
* nas接口
* 
* @author JiaWei Lee
* @since on   03 7月 2025 
* @version 1
*/
@RestController
public class WakeOnLanController {

private final WolService wolService;

private  final  MachineService machineService;

  public WakeOnLanController(WolService wolService, MachineService machineService) {
    this.wolService = wolService;
    this.machineService = machineService;
  }


  // 这里可以添加处理 WOL 请求的端点
  // 例如，接收 MAC 地址和广播地址，然后调用 wolService.sendMagicPacket(macAddress, broadcastAddress);
  /**
   * 唤醒设备
   * @param mac 设备的 MAC 地址，例如 "2c:f0:5d:36:2a:26	"
   * @param broadcastIp 设备所在网络的广播地址，例如 "
   * @return 唤醒结果
   */
  @Operation(summary = "唤醒设备", description = "发送 WOL 魔法数据包以唤醒指定设备")
  @PostMapping("/api/wol")
  public ResultData<String> wakeUp(@RequestParam String mac, @RequestParam String broadcastIp) {
    try {
      wolService.sendMagicPacket(mac, broadcastIp);
      return ResultData.success("唤醒指令已发送至 " + mac);
    } catch (Exception e) {
      return ResultData.fail("错误: " + e.getMessage());
    }
  }

  /**
   * 根据机器 ID 唤醒设备
   * @param id 机器 ID
   * @return 唤醒结果
   */
  @Operation(summary = "根据机器 ID 唤醒设备", description = "发送 WOL 魔法数据包以唤醒指定 ID 的设备")
  @GetMapping( "/api/wakeUpById/{id}")
  public ResultData<String > wakeUpById(@PathVariable Long id) {
    try {
      wolService.wolMacById(id);
      return ResultData.success("唤醒指令已发送至 ID: " + id);
    } catch (Exception e) {
      return ResultData.fail("错误: " + e.getMessage());
    }
  }

  /**
   * 获取 当前设备是否要关机。
   * @param mac 设备的 MAC 地址，例如 "2c:f0:5d:36:2a:26"
   * @return 返回设备的
   */
  @GetMapping("/api/wolLog")
  public ResultData<WolLog>  getWolLog(@RequestParam String mac) {
    WolLog wolLog = machineService.getWol(mac);
    if (wolLog == null) {
      return ResultData.fail("未找到对应");
    }
    return ResultData.success(wolLog);
  }

  @PostMapping("/api/upDataWolLog")
  public ResultData<String > upDataWolLog(@RequestBody WolLog log) {
    machineService.updateWolLog(log);
    return ResultData.success("WOL日志已更新: ");
  }


  @PostMapping("/shoutdown/{name}")
  public ResultData<String > shoutDownMachine(@PathVariable String  name) {
    // 这里可以添加关机逻辑
    // 例如，调用关机命令或发送关机请求
    var msg=  machineService.shoutDownMachine(name);
    return ResultData.success(msg);
  }

}
