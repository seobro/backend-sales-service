package com.dev.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backend.model.SalesOrder;

@RepositoryRestResource(collectionResourceRel = "salesorder", path = "salesorder")
public interface SalesOrderRepository extends CrudRepository<SalesOrder, Long>{

	SalesOrder findByOrderNumber(@Param("number") String number);
	
	@Transactional
	Long deleteByOrderNumber(@Param("number") String number);
}
