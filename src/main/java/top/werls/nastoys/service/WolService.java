package top.werls.nastoys.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.werls.nastoys.entity.Machine;
import top.werls.nastoys.repository.MachineRepository;

/**
 * wol 服务 发送wol 数据包
 *
 * @author JiaWei Lee
 * @since on 03 7月 2025
 * @version 1
 */
@Service
@Slf4j
public class WolService {

  private static final int PORT = 9; // WOL 标准端口是 9 或 7

  private final MachineRepository machineRepository;

  public WolService(MachineRepository machineRepository) {
    this.machineRepository = machineRepository;
  }


  /**
   * 根据机器 ID 唤醒机器
   * @param id 机器 ID
   */
  public  void wolMacById(Long id) {
    var machine = machineRepository.findById(id).orElseThrow(() -> new RuntimeException("机器不存在"));
    if (machine.getMac() == null || machine.getMac().isEmpty()) {
      throw new RuntimeException("机器 MAC 地址不能为空");
    }
    if (machine.getBroadcastIp() == null || machine.getBroadcastIp().isEmpty()) {
      throw new RuntimeException("机器广播地址不能为空");
    }
    sendMagicPacket(machine.getMac(), machine.getBroadcastIp());
  }



  /**
   * 发送 WOL 魔法数据包
   * @param macAddress 目标机器的 MAC 地址，例如 "00:11:22:AA:BB:CC"
   * @param broadcastAddress 目标机器所在网络的广播地址，例如 "192.168.1.255"
   */
  public void sendMagicPacket(String macAddress, String broadcastAddress) {
    try {
      byte[] macBytes = getMacBytes(macAddress);
      byte[] bytes = new byte[6 + 16 * macBytes.length];

      // 1. 前 6 个字节填充为 0xFF
      for (int i = 0; i < 6; i++) {
        bytes[i] = (byte) 0xff;
      }

      // 2. 后面填充 16 次 MAC 地址
      for (int i = 6; i < bytes.length; i += macBytes.length) {
        System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
      }

      // 3. 创建 UDP 包并发送
      InetAddress address = InetAddress.getByName(broadcastAddress);
      DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);

      // 使用 try-with-resources 确保 socket被关闭
      try (DatagramSocket socket = new DatagramSocket()) {
        socket.setBroadcast(true); // 必须允许发送广播
        socket.send(packet);
      }

      log.info("成功向 MAC 地址 {} 发送唤醒包到 {}", macAddress, broadcastAddress);

    } catch (Exception e) {
      log.error("发送唤醒包失败: ", e);
      throw new RuntimeException("发送唤醒包失败", e);
    }
  }
  private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
    byte[] bytes = new byte[6];
    String[] hex = macStr.split("([:\\-])");
    if (hex.length != 6) {
      throw new IllegalArgumentException("无效的 MAC 地址格式.");
    }
    try {
      for (int i = 0; i < 6; i++) {
        bytes[i] = (byte) Integer.parseInt(hex[i], 16);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无效的十六进制字符在 MAC 地址中.");
    }
    return bytes;
  }

}
