package top.werls.nastoys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.werls.nastoys.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    Machine findByName(String name);
    Machine findByMac(String mac);
}
