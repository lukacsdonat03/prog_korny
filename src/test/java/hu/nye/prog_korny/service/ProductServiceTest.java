package hu.nye.prog_korny.service;

import hu.nye.prog_korny.domain.Product;

import java.util.List;
import java.util.Optional;

import hu.nye.prog_korny.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    ProductRepository productRepository = mock(ProductRepository.class);
    ProductService productService = new ProductService(productRepository);

    @Test
    void testSaveProduct() {
        Product product = new Product();
        product.setName("Test");
        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.saveProduct(product);
        assertEquals("Test", saved.getName());
        verify(productRepository).save(product);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        List<Product> result = productService.getAllProducts();
        assertEquals(2, result.size());
    }

    @Test
    void testGetProductById() {
        Product p = new Product();
        p.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> result = productService.getProductByid(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(5L);
        verify(productRepository).deleteById(5L);
    }

}
