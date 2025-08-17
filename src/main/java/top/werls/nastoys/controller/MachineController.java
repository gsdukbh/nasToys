package top.werls.nastoys.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.PublicKey;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.werls.nastoys.common.ResultData;
import top.werls.nastoys.entity.Machine;
import top.werls.nastoys.repository.MachineRepository;
import top.werls.nastoys.service.MachineService;

/**
 * 设备
 *
 * @author JiaWei Lee
 * @since on 05 7月 2025
 * @version 1
 */
@RestController
@RequestMapping("/api/machine")
public class MachineController {

  private final MachineRepository machineRepository;



  public MachineController(MachineRepository machineRepository) {
    this.machineRepository = machineRepository;

  }

  // 这里可以添加处理机器相关请求的端点
  // 例如，获取所有机器信息、添加新机器等
  @GetMapping("/machines")
  public ResultData<List<Machine>> getAllMachines() {
    List<Machine> machines = machineRepository.findAll();
    return ResultData.success(machines);
  }

  // 添加设备
  @PostMapping("/addMachine")
  public ResultData<String> addMachine(@Valid @RequestBody Machine machine) {
    machine.setMac(machine.getMac().toLowerCase());
    return ResultData.success("设备已添加: " + machine.getName());
  }

  @PostMapping("/registerMachine")
  public ResultData<Machine> registerMachine(@Valid @RequestBody Machine machine) {
    String mac = machine.getMac().toLowerCase();
    machine.setId(null); // 确保是新设备
    machine.setMac(mac);
    Machine savedMachine = machineRepository.save(machine);
    return ResultData.success(savedMachine);
  }

  @Operation (summary = "更新机器信息", description = "更新指定机器的信息")
  @PostMapping("/updateMachine")
  public ResultData<Machine>  updateMachine( @Valid @RequestBody Machine machine) {
    String mac = machine.getMac().toLowerCase();
    machine.setMac(mac);
    Machine updatedMachine = machineRepository.save(machine);
    return ResultData.success(updatedMachine);
  }



}
