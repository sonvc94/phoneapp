package com.geardao.phoneapp.service.impl;

import com.geardao.phoneapp.service.PhoneService;
import com.geardao.phoneapp.domain.Phone;
import com.geardao.phoneapp.repository.PhoneRepository;
import com.geardao.phoneapp.repository.search.PhoneSearchRepository;
import com.geardao.phoneapp.service.dto.PhoneDTO;
import com.geardao.phoneapp.service.mapper.PhoneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Phone.
 */
@Service
public class PhoneServiceImpl implements PhoneService {

    private final Logger log = LoggerFactory.getLogger(PhoneServiceImpl.class);

    private final PhoneRepository phoneRepository;

    private final PhoneMapper phoneMapper;

    private final PhoneSearchRepository phoneSearchRepository;

    public PhoneServiceImpl(PhoneRepository phoneRepository, PhoneMapper phoneMapper, PhoneSearchRepository phoneSearchRepository) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
        this.phoneSearchRepository = phoneSearchRepository;
    }

    /**
     * Save a phone.
     *
     * @param phoneDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PhoneDTO save(PhoneDTO phoneDTO) {
        log.debug("Request to save Phone : {}", phoneDTO);
        Phone phone = phoneMapper.toEntity(phoneDTO);
        phone = phoneRepository.save(phone);
        PhoneDTO result = phoneMapper.toDto(phone);
        phoneSearchRepository.save(phone);
        return result;
    }

    /**
     * Get all the phones.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<PhoneDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Phones");
        return phoneRepository.findAll(pageable)
            .map(phoneMapper::toDto);
    }


    /**
     * Get one phone by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<PhoneDTO> findOne(String id) {
        log.debug("Request to get Phone : {}", id);
        return phoneRepository.findById(id)
            .map(phoneMapper::toDto);
    }

    /**
     * Delete the phone by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Phone : {}", id);
        phoneRepository.deleteById(id);
        phoneSearchRepository.deleteById(id);
    }

    /**
     * Search for the phone corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<PhoneDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Phones for query {}", query);
        return phoneSearchRepository.search(queryStringQuery(query), pageable)
            .map(phoneMapper::toDto);
    }
}
