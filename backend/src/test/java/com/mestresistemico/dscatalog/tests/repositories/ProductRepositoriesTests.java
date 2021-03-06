package com.mestresistemico.dscatalog.tests.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.mestresistemico.dscatalog.entities.Category;
import com.mestresistemico.dscatalog.entities.Product;
import com.mestresistemico.dscatalog.repositories.CategoryRepository;
import com.mestresistemico.dscatalog.repositories.ProductRepository;
import com.mestresistemico.dscatalog.tests.factory.ProductFactory;


@DataJpaTest
public class ProductRepositoriesTests {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private long countPcGamerProducts;
	private long countLivrosProducts;
	private long countEletronicosProducts;
	private PageRequest pageRequest;
	
	@BeforeEach
	void setup() throws Exception{
		existingId = 1L;
		nonExistingId = 1000000000000L;
		countTotalProducts = 25L;
		countPcGamerProducts = 21L;
		countLivrosProducts = 1L;
		countEletronicosProducts = 2L;
		pageRequest = PageRequest.of(0, 10);
	}
	
	@Test
	public void findShouldReturnAllProductsWhenCategoryNotInformed() {
		List<Category> categories = null;
		Page<Product> result = repository.find(categories, "", pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnNoProductsWhenCategoryInexistant() {
//		List<Category> categories = Arrays.asList(categoryRepository.getOne(nonExistingId));
		List<Category> categories = new ArrayList<>();
		categories.add(new Category(nonExistingId, null));
		Page<Product> result = repository.find(categories, "", pageRequest);
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findShouldReturnOnlySelectedProductWhenCategoryInformed() {
//		List<Category> categories = Arrays.asList(categoryRepository.getOne(existingId));
		List<Category> categories = new ArrayList<>();
		categories.add(new Category(existingId, null));
		Page<Product> result = repository.find(categories, "", pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countLivrosProducts, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnProductsWhenMoreThanOneCategory() {
		List<Category> categories = Arrays.asList(categoryRepository.getOne(existingId),
				categoryRepository.getOne(existingId+1));
		Page<Product> result = repository.find(categories, "", pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countLivrosProducts + countEletronicosProducts, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnAllProductsWhenNameIsEmpty() {
		String name = ""; 
		Page<Product> result = repository.find(null, name, pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnProductsWhenNameExistsIgnoringCase() {
		String name = "pc gAMeR"; 
		Page<Product> result = repository.find(null, name, pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countPcGamerProducts, result.getTotalElements());
	}
	
	@Test
	public void findShouldReturnProductsWhenNameExists() {
		String name = "PC Gamer"; 
		Page<Product> result = repository.find(null, name, pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countPcGamerProducts, result.getTotalElements());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = ProductFactory.createProduct();
		product.setId(null);
		product = repository.save(product);
		Optional<Product> result = repository.findById(product.getId());
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts+1, product.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), product);
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}
}
