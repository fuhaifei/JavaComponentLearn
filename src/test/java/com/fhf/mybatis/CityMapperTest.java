package com.fhf.mybatis;

import com.fhf.mybatis.entity.City;
import com.fhf.mybatis.mapper.CityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CityMapperTest {
     static CityMapper cityMapper;
    @BeforeClass
    public static void init() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        //创建SqlSessionFactoryBuilder对象
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new
                SqlSessionFactoryBuilder();
        //通过核心配置文件所对应的字节输入流创建工厂类SqlSessionFactory，生产SqlSession对象
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        //创建SqlSession对象，此时通过SqlSession对象所操作的sql都必须手动提交或回滚事务
        //SqlSession sqlSession = sqlSessionFactory.openSession();
        //创建SqlSession对象，此时通过SqlSession对象所操作的sql都会自动提交
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        //通过代理模式创建UserMapper接口的代理实现类对象
       cityMapper = sqlSession.getMapper(CityMapper.class);
    }

    @Test
    public void TestInsert(){
        City city = new City();
        city.setName("yiyang");
        city.setCountryCode("CHN");
        city.setDistrict("Henan");
        city.setPopulation(185000);
        cityMapper.insertCity(city);
        cityMapper.insertWithPrimaryKey(city);
        System.out.println(city.getId());
    }

    @Test
    public void TestSelect(){
        System.out.println(cityMapper.getCityByID(10));
        List<City> cityByDistrict = cityMapper.getCityByDistrict("CHN", "HeNan");
        for(City city:cityByDistrict){
            System.out.println(city.getCountryCode());
        }
        List<Integer> ids = new ArrayList<>();
        for(int i = 1;i < 10;i++){
            ids.add(i);
        }
        List<City> cites = cityMapper.getCites(ids);
        System.out.println("Get By Id:");
        for(City city:cites){
            System.out.println(city);
        }
    }

    @Test
    public void TestUpdate(){
        City city = new City();
        city.setId(4084);
        city.setName("yibin");
        city.setCountryCode("CHN");
        city.setDistrict("HeNan");
        city.setPopulation(10);
        int i = cityMapper.updateCityById(city);
        System.out.println("影响行数为："+i);
    }


    @Test
    public void queryByObject(){
        City city = new City();

        city.setName("zhengzhou");
        List<City> cities = cityMapper.queryByObject(city);
        for(City res:cities){
            System.out.println(res);
        }
        city.setName(null);
        city.setDistrict("henan");
        cities = cityMapper.queryByObject(city);
        for(City res:cities){
            System.out.println(res);
        }
    }
}
