package hu.nye.prog_korny.controller;

import hu.nye.prog_korny.domain.Product;
import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.service.ProductCategoryService;
import hu.nye.prog_korny.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductCategoryControllerTest {
    @InjectMocks
    private ProductCategoryController controller;

    @Mock
    private ProductCategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<ProductCategory> categories = List.of(new ProductCategory());
        when(categoryService.findAll()).thenReturn(categories);

        List<ProductCategory> result = controller.getAllCategories();

        assertEquals(1, result.size());
    }

    @Test
    void testGetCategoryFound() {
        ProductCategory category = new ProductCategory();
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<ProductCategory> response = controller.getCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testGetCategoryNotFound() {
        when(categoryService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ProductCategory> response = controller.getCategory(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateCategory() {
        ProductCategory category = new ProductCategory();
        when(categoryService.save(category)).thenReturn(category);

        ProductCategory saved = controller.createCategory(category);

        assertEquals(category, saved);
    }

    @Test
    void testDeleteCategory() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(productService.getProductsByCategory(category)).thenReturn(List.of(new Product()));

        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).deleteById(1L);
    }

    @Test
    void testViewCategories() {
        when(categoryService.findAll()).thenReturn(List.of(new ProductCategory()));

        String view = controller.viewCategories(model);

        assertEquals("category-list", view);
        verify(model).addAttribute(eq("categories"), any());
    }

    @Test
    void testShowCreateCategory() {
        String view = controller.showCreateCategory(model);

        assertEquals("category-form", view);
        verify(model).addAttribute(eq("category"), any(ProductCategory.class));
    }

    @Test
    void testUpdateCategoryValid() {
        ProductCategory category = new ProductCategory();
        category.setName("Test");

        String view = controller.updateCategory(1L, category, model);

        assertEquals("redirect:/categories/view", view);
        verify(categoryService).save(category);
    }
}
