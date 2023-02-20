package com.fhf.mybatis.mapper;

import com.fhf.mybatis.entity.Country;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CountryMapper {

    public List<Country> getCountryWithCity(@Param("name") String name);
}
