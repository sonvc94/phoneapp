package com.geardao.phoneapp.service;

import com.geardao.phoneapp.service.dto.BrandDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Brand.
 */
public interface BrandService {

    /**
     * Save a brand.
     *
     * @param brandDTO the entity to save
     * @return the persisted entity
     */
    BrandDTO save(BrandDTO brandDTO);

    /**
     * Get all the brands.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<BrandDTO> findAll(Pageable pageable);


    /**
     * Get the "id" brand.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<BrandDTO> findOne(String id);

    /**
     * Delete the "id" brand.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the brand corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<BrandDTO> search(String query, Pageable pageable);
}
