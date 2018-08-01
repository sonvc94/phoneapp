package com.geardao.phoneapp.service;

import com.geardao.phoneapp.service.dto.PhoneDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Phone.
 */
public interface PhoneService {

    /**
     * Save a phone.
     *
     * @param phoneDTO the entity to save
     * @return the persisted entity
     */
    PhoneDTO save(PhoneDTO phoneDTO);

    /**
     * Get all the phones.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PhoneDTO> findAll(Pageable pageable);


    /**
     * Get the "id" phone.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<PhoneDTO> findOne(String id);

    /**
     * Delete the "id" phone.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the phone corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PhoneDTO> search(String query, Pageable pageable);
}
