package iuh.fit.edu.persistenceworker.repository;

import iuh.fit.edu.persistenceworker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
