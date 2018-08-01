package com.geardao.phoneapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geardao.phoneapp.service.BrandService;
import com.geardao.phoneapp.web.rest.errors.BadRequestAlertException;
import com.geardao.phoneapp.web.rest.util.HeaderUtil;
import com.geardao.phoneapp.web.rest.util.PaginationUtil;
import com.geardao.phoneapp.service.dto.BrandDTO;
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
 * REST controller for managing Brand.
 */
@RestController
@RequestMapping("/api")
public class BrandResource {

    private final Logger log = LoggerFactory.getLogger(BrandResource.class);

    private static final String ENTITY_NAME = "brand";

    private final BrandService brandService;

    public BrandResource(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * POST  /brands : Create a new brand.
     *
     * @param brandDTO the brandDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new brandDTO, or with status 400 (Bad Request) if the brand has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/brands")
    @Timed
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) throws URISyntaxException {
        log.debug("REST request to save Brand : {}", brandDTO);
        if (brandDTO.getId() != null) {
            throw new BadRequestAlertException("A new brand cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BrandDTO result = brandService.save(brandDTO);
        return ResponseEntity.created(new URI("/api/brands/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /brands : Updates an existing brand.
     *
     * @param brandDTO the brandDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated brandDTO,
     * or with status 400 (Bad Request) if the brandDTO is not valid,
     * or with status 500 (Internal Server Error) if the brandDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/brands")
    @Timed
    public ResponseEntity<BrandDTO> updateBrand(@RequestBody BrandDTO brandDTO) throws URISyntaxException {
        log.debug("REST request to update Brand : {}", brandDTO);
        if (brandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BrandDTO result = brandService.save(brandDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, brandDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /brands : get all the brands.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of brands in body
     */
    @GetMapping("/brands")
    @Timed
    public ResponseEntity<List<BrandDTO>> getAllBrands(Pageable pageable) {
        log.debug("REST request to get a page of Brands");
        Page<BrandDTO> page = brandService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/brands");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /brands/:id : get the "id" brand.
     *
     * @param id the id of the brandDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the brandDTO, or with status 404 (Not Found)
     */
    @GetMapping("/brands/{id}")
    @Timed
    public ResponseEntity<BrandDTO> getBrand(@PathVariable String id) {
        log.debug("REST request to get Brand : {}", id);
        Optional<BrandDTO> brandDTO = brandService.findOne(id);
        return ResponseUtil.wrapOrNotFound(brandDTO);
    }

    /**
     * DELETE  /brands/:id : delete the "id" brand.
     *
     * @param id the id of the brandDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/brands/{id}")
    @Timed
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        log.debug("REST request to delete Brand : {}", id);
        brandService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/brands?query=:query : search for the brand corresponding
     * to the query.
     *
     * @param query the query of the brand search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/brands")
    @Timed
    public ResponseEntity<List<BrandDTO>> searchBrands(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Brands for query {}", query);
        Page<BrandDTO> page = brandService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/brands");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
