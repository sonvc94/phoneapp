package com.geardao.phoneapp.service.mapper;

import com.geardao.phoneapp.domain.*;
import com.geardao.phoneapp.service.dto.BrandDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Brand and its DTO BrandDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BrandMapper extends EntityMapper<BrandDTO, Brand> {


}
