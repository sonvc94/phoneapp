package com.geardao.phoneapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geardao.phoneapp.service.PhoneService;
import com.geardao.phoneapp.web.rest.errors.BadRequestAlertException;
import com.geardao.phoneapp.web.rest.util.HeaderUtil;
import com.geardao.phoneapp.web.rest.util.PaginationUtil;
import com.geardao.phoneapp.service.dto.PhoneDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Phone.
 */
@RestController
@RequestMapping("/api")
public class PhoneResource {

    private final Logger log = LoggerFactory.getLogger(PhoneResource.class);

    private static final String ENTITY_NAME = "phone";

    private final PhoneService phoneService;

    public PhoneResource(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    /**
     * POST  /phones : Create a new phone.
     *
     * @param phoneDTO the phoneDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phoneDTO, or with status 400 (Bad Request) if the phone has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/phones")
    @Timed
    public ResponseEntity<PhoneDTO> createPhone(@RequestBody PhoneDTO phoneDTO) throws URISyntaxException {
        log.debug("REST request to save Phone : {}", phoneDTO);
        if (phoneDTO.getId() != null) {
            throw new BadRequestAlertException("A new phone cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PhoneDTO result = phoneService.save(phoneDTO);
        return ResponseEntity.created(new URI("/api/phones/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phones : Updates an existing phone.
     *
     * @param phoneDTO the phoneDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phoneDTO,
     * or with status 400 (Bad Request) if the phoneDTO is not valid,
     * or with status 500 (Internal Server Error) if the phoneDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/phones")
    @Timed
    public ResponseEntity<PhoneDTO> updatePhone(@RequestBody PhoneDTO phoneDTO) throws URISyntaxException {
        log.debug("REST request to update Phone : {}", phoneDTO);
        if (phoneDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PhoneDTO result = phoneService.save(phoneDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, phoneDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phones : get all the phones.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of phones in body
     */
    @GetMapping("/phones")
    @Timed
    public ResponseEntity<List<PhoneDTO>> getAllPhones(Pageable pageable) {
        log.debug("REST request to get a page of Phones");
        Page<PhoneDTO> page = phoneService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/phones");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /phones/:id : get the "id" phone.
     *
     * @param id the id of the phoneDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phoneDTO, or with status 404 (Not Found)
     */
    @GetMapping("/phones/{id}")
    @Timed
    public ResponseEntity<PhoneDTO> getPhone(@PathVariable String id) {
        log.debug("REST request to get Phone : {}", id);
        Optional<PhoneDTO> phoneDTO = phoneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(phoneDTO);
    }

    /**
     * DELETE  /phones/:id : delete the "id" phone.
     *
     * @param id the id of the phoneDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/phones/{id}")
    @Timed
    public ResponseEntity<Void> deletePhone(@PathVariable String id) {
        log.debug("REST request to delete Phone : {}", id);
        phoneService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/phones?query=:query : search for the phone corresponding
     * to the query.
     *
     * @param query the query of the phone search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/phones")
    @Timed
    public ResponseEntity<List<PhoneDTO>> searchPhones(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Phones for query {}", query);
        Page<PhoneDTO> page = phoneService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/phones");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
