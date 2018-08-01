package com.geardao.phoneapp.repository;

import com.geardao.phoneapp.domain.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Brand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {

}
