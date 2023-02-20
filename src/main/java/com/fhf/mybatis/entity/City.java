package com.fhf.mybatis.entity;
import lombok.Data;

@Data
public class City {
    Integer id;
    String name;
    String countryCode;
    String district;
    Integer population;

}
