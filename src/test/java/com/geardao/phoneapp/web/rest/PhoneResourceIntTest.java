package com.geardao.phoneapp.web.rest;

import com.geardao.phoneapp.PhoneappApp;

import com.geardao.phoneapp.domain.Phone;
import com.geardao.phoneapp.repository.PhoneRepository;
import com.geardao.phoneapp.repository.search.PhoneSearchRepository;
import com.geardao.phoneapp.service.PhoneService;
import com.geardao.phoneapp.service.dto.PhoneDTO;
import com.geardao.phoneapp.service.mapper.PhoneMapper;
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

import java.math.BigDecimal;
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
 * Test class for the PhoneResource REST controller.
 *
 * @see PhoneResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PhoneappApp.class)
public class PhoneResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    @Autowired
    private PhoneRepository phoneRepository;


    @Autowired
    private PhoneMapper phoneMapper;
    

    @Autowired
    private PhoneService phoneService;

    /**
     * This repository is mocked in the com.geardao.phoneapp.repository.search test package.
     *
     * @see com.geardao.phoneapp.repository.search.PhoneSearchRepositoryMockConfiguration
     */
    @Autowired
    private PhoneSearchRepository mockPhoneSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restPhoneMockMvc;

    private Phone phone;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PhoneResource phoneResource = new PhoneResource(phoneService);
        this.restPhoneMockMvc = MockMvcBuilders.standaloneSetup(phoneResource)
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
    public static Phone createEntity() {
        Phone phone = new Phone()
            .name(DEFAULT_NAME)
            .brand(DEFAULT_BRAND)
            .price(DEFAULT_PRICE);
        return phone;
    }

    @Before
    public void initTest() {
        phoneRepository.deleteAll();
        phone = createEntity();
    }

    @Test
    public void createPhone() throws Exception {
        int databaseSizeBeforeCreate = phoneRepository.findAll().size();

        // Create the Phone
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);
        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isCreated());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeCreate + 1);
        Phone testPhone = phoneList.get(phoneList.size() - 1);
        assertThat(testPhone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPhone.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testPhone.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the Phone in Elasticsearch
        verify(mockPhoneSearchRepository, times(1)).save(testPhone);
    }

    @Test
    public void createPhoneWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = phoneRepository.findAll().size();

        // Create the Phone with an existing ID
        phone.setId("existing_id");
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeCreate);

        // Validate the Phone in Elasticsearch
        verify(mockPhoneSearchRepository, times(0)).save(phone);
    }

    @Test
    public void getAllPhones() throws Exception {
        // Initialize the database
        phoneRepository.save(phone);

        // Get all the phoneList
        restPhoneMockMvc.perform(get("/api/phones?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }
    

    @Test
    public void getPhone() throws Exception {
        // Initialize the database
        phoneRepository.save(phone);

        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", phone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(phone.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }
    @Test
    public void getNonExistingPhone() throws Exception {
        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updatePhone() throws Exception {
        // Initialize the database
        phoneRepository.save(phone);

        int databaseSizeBeforeUpdate = phoneRepository.findAll().size();

        // Update the phone
        Phone updatedPhone = phoneRepository.findById(phone.getId()).get();
        updatedPhone
            .name(UPDATED_NAME)
            .brand(UPDATED_BRAND)
            .price(UPDATED_PRICE);
        PhoneDTO phoneDTO = phoneMapper.toDto(updatedPhone);

        restPhoneMockMvc.perform(put("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isOk());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeUpdate);
        Phone testPhone = phoneList.get(phoneList.size() - 1);
        assertThat(testPhone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPhone.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testPhone.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the Phone in Elasticsearch
        verify(mockPhoneSearchRepository, times(1)).save(testPhone);
    }

    @Test
    public void updateNonExistingPhone() throws Exception {
        int databaseSizeBeforeUpdate = phoneRepository.findAll().size();

        // Create the Phone
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPhoneMockMvc.perform(put("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Phone in Elasticsearch
        verify(mockPhoneSearchRepository, times(0)).save(phone);
    }

    @Test
    public void deletePhone() throws Exception {
        // Initialize the database
        phoneRepository.save(phone);

        int databaseSizeBeforeDelete = phoneRepository.findAll().size();

        // Get the phone
        restPhoneMockMvc.perform(delete("/api/phones/{id}", phone.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Phone in Elasticsearch
        verify(mockPhoneSearchRepository, times(1)).deleteById(phone.getId());
    }

    @Test
    public void searchPhone() throws Exception {
        // Initialize the database
        phoneRepository.save(phone);
        when(mockPhoneSearchRepository.search(queryStringQuery("id:" + phone.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(phone), PageRequest.of(0, 1), 1));
        // Search the phone
        restPhoneMockMvc.perform(get("/api/_search/phones?query=id:" + phone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Phone.class);
        Phone phone1 = new Phone();
        phone1.setId("id1");
        Phone phone2 = new Phone();
        phone2.setId(phone1.getId());
        assertThat(phone1).isEqualTo(phone2);
        phone2.setId("id2");
        assertThat(phone1).isNotEqualTo(phone2);
        phone1.setId(null);
        assertThat(phone1).isNotEqualTo(phone2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhoneDTO.class);
        PhoneDTO phoneDTO1 = new PhoneDTO();
        phoneDTO1.setId("id1");
        PhoneDTO phoneDTO2 = new PhoneDTO();
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
        phoneDTO2.setId(phoneDTO1.getId());
        assertThat(phoneDTO1).isEqualTo(phoneDTO2);
        phoneDTO2.setId("id2");
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
        phoneDTO1.setId(null);
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
    }
}
