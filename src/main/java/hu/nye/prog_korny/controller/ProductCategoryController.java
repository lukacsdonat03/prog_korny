package hu.nye.prog_korny.controller;

import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.service.ProductCategoryService;
import hu.nye.prog_korny.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class ProductCategoryController {

    private final ProductCategoryService service;
    private final ProductService productService;

    public ProductCategoryController(ProductCategoryService service, ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategory(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductCategory createCategory(@RequestBody ProductCategory category) {
        return service.save(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        ProductCategory category = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // Az összes kapcsolódó termék category-ját nullra állítjuk
        List<Product> products = productService.getProductsByCategory(category);
        for (Product product : products) {
            product.setCategory(null);
        }
        productService.saveAll(products); // mentsük vissza a módosított termékeket

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }


}
