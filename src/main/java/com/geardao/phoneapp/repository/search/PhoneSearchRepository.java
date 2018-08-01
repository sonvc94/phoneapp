package com.geardao.phoneapp.repository.search;

import com.geardao.phoneapp.domain.Phone;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Phone entity.
 */
public interface PhoneSearchRepository extends ElasticsearchRepository<Phone, String> {
}
