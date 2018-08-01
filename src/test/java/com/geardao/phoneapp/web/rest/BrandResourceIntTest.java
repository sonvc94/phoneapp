package com.geardao.phoneapp.web.rest;

import com.geardao.phoneapp.PhoneappApp;

import com.geardao.phoneapp.domain.Brand;
import com.geardao.phoneapp.repository.BrandRepository;
import com.geardao.phoneapp.repository.search.BrandSearchRepository;
import com.geardao.phoneapp.service.BrandService;
import com.geardao.phoneapp.service.dto.BrandDTO;
import com.geardao.phoneapp.service.mapper.BrandMapper;
import com.geardao.phoneapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;


import static com.geardao.phoneapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BrandResource REST controller.
 *
 * @see BrandResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PhoneappApp.class)
public class BrandResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    @Autowired
    private BrandRepository brandRepository;


    @Autowired
    private BrandMapper brandMapper;
    

    @Autowired
    private BrandService brandService;

    /**
     * This repository is mocked in the com.geardao.phoneapp.repository.search test package.
     *
     * @see com.geardao.phoneapp.repository.search.BrandSearchRepositoryMockConfiguration
     */
    @Autowired
    private BrandSearchRepository mockBrandSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restBrandMockMvc;

    private Brand brand;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BrandResource brandResource = new BrandResource(brandService);
        this.restBrandMockMvc = MockMvcBuilders.standaloneSetup(brandResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createEntity() {
        Brand brand = new Brand()
            .name(DEFAULT_NAME)
            .country(DEFAULT_COUNTRY);
        return brand;
    }

    @Before
    public void initTest() {
        brandRepository.deleteAll();
        brand = createEntity();
    }

    @Test
    public void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBrand.getCountry()).isEqualTo(DEFAULT_COUNTRY);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).save(testBrand);
    }

    @Test
    public void createBrandWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand with an existing ID
        brand.setId("existing_id");
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(0)).save(brand);
    }

    @Test
    public void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.save(brand);

        // Get all the brandList
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }
    

    @Test
    public void getBrand() throws Exception {
        // Initialize the database
        brandRepository.save(brand);

        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()));
    }
    @Test
    public void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBrand() throws Exception {
        // Initialize the database
        brandRepository.save(brand);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).get();
        updatedBrand
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY);
        BrandDTO brandDTO = brandMapper.toDto(updatedBrand);

        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBrand.getCountry()).isEqualTo(UPDATED_COUNTRY);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).save(testBrand);
    }

    @Test
    public void updateNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(0)).save(brand);
    }

    @Test
    public void deleteBrand() throws Exception {
        // Initialize the database
        brandRepository.save(brand);

        int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Get the brand
        restBrandMockMvc.perform(delete("/api/brands/{id}", brand.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).deleteById(brand.getId());
    }

    @Test
    public void searchBrand() throws Exception {
        // Initialize the database
        brandRepository.save(brand);
        when(mockBrandSearchRepository.search(queryStringQuery("id:" + brand.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(brand), PageRequest.of(0, 1), 1));
        // Search the brand
        restBrandMockMvc.perform(get("/api/_search/brands?query=id:" + brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = new Brand();
        brand1.setId("id1");
        Brand brand2 = new Brand();
        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);
        brand2.setId("id2");
        assertThat(brand1).isNotEqualTo(brand2);
        brand1.setId(null);
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BrandDTO.class);
        BrandDTO brandDTO1 = new BrandDTO();
        brandDTO1.setId("id1");
        BrandDTO brandDTO2 = new BrandDTO();
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
        brandDTO2.setId(brandDTO1.getId());
        assertThat(brandDTO1).isEqualTo(brandDTO2);
        brandDTO2.setId("id2");
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
        brandDTO1.setId(null);
        assertThat(brandDTO1).isNotEqualTo(brandDTO2);
    }
}
