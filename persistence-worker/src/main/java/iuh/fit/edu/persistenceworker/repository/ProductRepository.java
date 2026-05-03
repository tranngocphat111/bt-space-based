package iuh.fit.edu.persistenceworker.repository;

import iuh.fit.edu.persistenceworker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
