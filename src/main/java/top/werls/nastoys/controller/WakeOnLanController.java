package top.werls.nastoys.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
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

  public WakeOnLanController(WolService wolService) {
    this.wolService = wolService;
  }

  // 这里可以添加处理 WOL 请求的端点
  // 例如，接收 MAC 地址和广播地址，然后调用 wolService.sendMagicPacket(macAddress, broadcastAddress);
  @PostMapping("/api/wol")
  public ResultData<String> wakeUp(@RequestParam String mac, @RequestParam String broadcastIp) {
    try {
      wolService.sendMagicPacket(mac, broadcastIp);
      return ResultData.success("唤醒指令已发送至 " + mac);
    } catch (Exception e) {
      return ResultData.fail("错误: " + e.getMessage());
    }
  }
}
