package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
