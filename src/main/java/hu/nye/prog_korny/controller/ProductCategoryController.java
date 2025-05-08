package hu.nye.prog_korny.controller;

import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.service.ProductCategoryService;
import hu.nye.prog_korny.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class ProductCategoryController {

    private final ProductCategoryService service;
    private final ProductService productService;

    public ProductCategoryController(ProductCategoryService service, ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @GetMapping
    @ResponseBody
    public List<ProductCategory> getAllCategories() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ProductCategory> getCategory(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ProductCategory createCategory(@RequestBody ProductCategory category) {
        return service.save(category);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        ProductCategory category = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        List<Product> products = productService.getProductsByCategory(category);
        for (Product product : products) {
            product.setCategory(null);
        }
        productService.saveAll(products);

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view")
    public String viewCategories(Model model){
        List<ProductCategory> categories = service.findAll();
        model.addAttribute("categories", categories);
        return "category-list";
    }

    @GetMapping("/create")
    public String showCreateCategory(Model model){
        model.addAttribute("category",new ProductCategory());
        model.addAttribute("formAction","/categories/create");
        model.addAttribute("formTitle","Kategória létrehozása");
        return "category-form";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute ProductCategory category,Model model){
        service.save(category);
        return "redirect:/categories/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        ProductCategory category = service.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"There is not category with this ID") );
        model.addAttribute("category",category);
        model.addAttribute("formAction","/categories/edit/"+id);
        model.addAttribute("formTitle","Kategória módosítása");
        return "category-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute ProductCategory category){
        category.setId(id);
        service.save(category);
        return "redirect:/categories/view";
    }
}
