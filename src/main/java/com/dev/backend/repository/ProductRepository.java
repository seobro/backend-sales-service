package com.dev.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backend.model.Product;

@RepositoryRestResource(collectionResourceRel = "product", path = "product")
public interface ProductRepository extends CrudRepository<Product, Long>{
	
	Product findByCode(@Param("code") String code);
	
	@Transactional
	Long deleteByCode(@Param("code") String code);
}
