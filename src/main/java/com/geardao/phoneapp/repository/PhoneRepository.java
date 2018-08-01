package com.geardao.phoneapp.repository;

import com.geardao.phoneapp.domain.Phone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Phone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PhoneRepository extends MongoRepository<Phone, String> {

}
