package com.fhf.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class Country {
    private String code;
    private String name;
    private Float surfaceArea;
    private String region;

    private List<City> cities;
}
