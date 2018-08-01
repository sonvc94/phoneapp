package com.geardao.phoneapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of PhoneSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PhoneSearchRepositoryMockConfiguration {

    @MockBean
    private PhoneSearchRepository mockPhoneSearchRepository;

}
