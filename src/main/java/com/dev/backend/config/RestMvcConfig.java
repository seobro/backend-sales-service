package com.dev.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.dev.backend.model.Customer;
import com.dev.backend.model.Product;
import com.dev.backend.model.SalesOrder;

@Configuration
public class RestMvcConfig extends RepositoryRestMvcConfiguration {

    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    	// add ids to the entities representation
        config.exposeIdsFor(Customer.class);
        config.exposeIdsFor(Product.class);
        config.exposeIdsFor(SalesOrder.class);
    }
}
