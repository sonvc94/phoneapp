package com.geardao.phoneapp.service.mapper;

import com.geardao.phoneapp.domain.*;
import com.geardao.phoneapp.service.dto.PhoneDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Phone and its DTO PhoneDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PhoneMapper extends EntityMapper<PhoneDTO, Phone> {


}
