package com.fhf.mybatis.mapper;


import com.fhf.mybatis.entity.City;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public interface CityMapper {

    /**
     * 添加用户信息
     * */
    public int insertCity(City city);
    /**
     * 获取自增主键
     * */
    public int insertWithPrimaryKey(City city);

    /**
     * 通过ID查询
     * */
    public City getCityByID(@Param("id") Integer id);

    /**
     * 通过CountryCode+District查询
     * */
    public List<City> getCityByDistrict(@Param("countryCode") String countryCode, @Param("district") String district);

    /**
     * 范围查询
     * */
    public List<City> getCites(@Param("ids") List<Integer> ids);

    /**
     * 更新用户
     * */
    public int updateCityById(City city);


    /**
     * 动态SQL
     * */
    public List<City> queryByObject(City city);

}
