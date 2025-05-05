package hu.nye.prog_korny.service;

import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository repository;

    public ProductCategoryService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    public Optional<ProductCategory> findById(Long id) {
        return repository.findById(id);
    }

    public ProductCategory save(ProductCategory category) {
        return repository.save(category);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
