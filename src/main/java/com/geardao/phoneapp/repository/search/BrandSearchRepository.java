package com.geardao.phoneapp.repository.search;

import com.geardao.phoneapp.domain.Brand;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Brand entity.
 */
public interface BrandSearchRepository extends ElasticsearchRepository<Brand, String> {
}
