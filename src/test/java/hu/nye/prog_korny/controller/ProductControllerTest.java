package hu.nye.prog_korny.controller;

import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.service.ProductCategoryService;
import hu.nye.prog_korny.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductControllerTest {

    @InjectMocks
    private ProductController controller;

    @Mock
    private ProductService productService;

    @Mock
    private ProductCategoryService productCategoryService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(new Product(), new Product());
        when(productService.getAllProducts()).thenReturn(products);

        List<Product> result = controller.getAllProducts();

        assertEquals(2, result.size());
        verify(productService).getAllProducts();
    }

    @Test
    void testGetProductByIdFound() {
        Product product = new Product();
        when(productService.getProductByid(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = controller.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productService.getProductByid(1L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = controller.getProductById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateOrUpdateProductSuccess() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        Product product = new Product();
        product.setCategory(category);

        when(productCategoryService.findById(1L)).thenReturn(Optional.of(category));
        when(productService.saveProduct(any())).thenReturn(product);

        ResponseEntity<Product> response = controller.createOrUpdateProduct(product);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void testCreateOrUpdateProductMissingCategory() {
        Product product = new Product(); // category is null
        ResponseEntity<Product> response = controller.createOrUpdateProduct(product);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testViewProducts() {
        when(productService.getAllProducts()).thenReturn(List.of(new Product()));

        String view = controller.viewProducts(model);

        assertEquals("product-list", view);
        verify(model).addAttribute(eq("products"), any());
    }

    @Test
    void testShowCreateProductForm() {
        when(productCategoryService.findAll()).thenReturn(List.of(new ProductCategory()));

        String view = controller.showCreateProductForm(model);

        assertEquals("product-form", view);
        verify(model).addAttribute(eq("product"), any(Product.class));
        verify(model).addAttribute(eq("categories"), any());
    }

    @Test
    void testDeleteProduct() {
        String redirect = controller.deleteProduct(5L);
        assertEquals("redirect:/products/view", redirect);
        verify(productService).deleteProduct(5L);
    }

}
