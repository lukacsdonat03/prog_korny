package hu.nye.prog_korny.repository;


import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
}
