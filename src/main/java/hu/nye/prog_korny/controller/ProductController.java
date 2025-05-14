package hu.nye.prog_korny.controller;

import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.service.ProductCategoryService;
import hu.nye.prog_korny.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductController(ProductService productService, ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Product> createOrUpdateProduct(@RequestBody Product product) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        return productCategoryService.findById(product.getCategory().getId())
                .map(category -> {
                    product.setCategory(category);
                    Product savedProduct = productService.saveProduct(product);
                    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping
    @ResponseBody
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductByid(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/view";
    }


    @GetMapping("/view")
    public String viewProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productCategoryService.findAll());
        model.addAttribute("formAction", "/products/create");
        model.addAttribute("formTitle","Termék létrehozása");
        return "product-form";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product, Model model) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            model.addAttribute("error", "Termék kategória nem lehet üres!");
            return "product-form";
        }

        if (product.getStock() == null || product.getStock() < 0) {
            model.addAttribute("error", "A készlet nem lehet negatív.");
            return "product-form";
        }

        if(product.getName() == null || product.getName().trim().isEmpty()){
            model.addAttribute("error", "A termék neve nem lehet üress!");
            return "product-form";
        }


        productCategoryService.findById(product.getCategory().getId())
                .ifPresent(category -> {
                    product.setCategory(category);
                    productService.saveProduct(product);
                });

        return "redirect:/products/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductByid(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("product", product);
        model.addAttribute("categories", productCategoryService.findAll());
        model.addAttribute("formAction", "/products/edit/" + id);
        model.addAttribute("formTitle","Termék módosítása");
        return "product-form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, Model model) {
        product.setId(id);

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            model.addAttribute("error", "Termék kategória nem lehet üres!");
            return "product-form";
        }

        if (product.getStock() == null || product.getStock() < 0) {
            model.addAttribute("error", "A készlet nem lehet negatív.");
            return "product-form";
        }

        if(product.getName() == null || product.getName().trim().isEmpty()){
            model.addAttribute("error", "A termék neve nem lehet üress!");
            return "product-form";
        }

        productService.saveProduct(product);
        return "redirect:/products/view";
    }

}
