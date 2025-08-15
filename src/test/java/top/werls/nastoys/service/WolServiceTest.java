package top.werls.nastoys.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
* 
* 
* @author JiaWei Lee
* @since on   07 7月 2025 
* @version 
*/
@SpringBootTest
class WolServiceTest {

  @Resource
  WolService wolService ;

  @Test
  void sendMagicPacket() {
    wolService.sendMagicPacket("2c:f0:5d:36:2a:26", "192.168.1.255");
    }
}