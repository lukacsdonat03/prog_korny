package hu.nye.prog_korny.service;


import hu.nye.prog_korny.domain.ProductCategory;
import hu.nye.prog_korny.repository.ProductCategoryRepository;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class ProductCategoryServiceTest {


    ProductCategoryRepository repository = mock(ProductCategoryRepository.class);
    ProductCategoryService service = new ProductCategoryService(repository);

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(new ProductCategory(), new ProductCategory()));
        List<ProductCategory> result = service.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        ProductCategory c = new ProductCategory();
        c.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        Optional<ProductCategory> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testSave() {
        ProductCategory c = new ProductCategory();
        c.setName("Test");
        when(repository.save(c)).thenReturn(c);
        ProductCategory saved = service.save(c);
        assertEquals("Test", saved.getName());
    }

    @Test
    void testDelete() {
        service.deleteById(3L);
        verify(repository).deleteById(3L);
    }

}
