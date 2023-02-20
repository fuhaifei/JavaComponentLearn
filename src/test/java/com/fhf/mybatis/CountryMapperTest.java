package com.fhf.mybatis;

import com.fhf.mybatis.entity.City;
import com.fhf.mybatis.entity.Country;
import com.fhf.mybatis.mapper.CityMapper;
import com.fhf.mybatis.mapper.CountryMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.checkerframework.checker.units.qual.C;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CountryMapperTest {

    static CountryMapper countryMapper;
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
        countryMapper = sqlSession.getMapper(CountryMapper.class);
    }

    @Test
    public void TestJoin(){
        List<Country> china = countryMapper.getCountryWithCity("china");
        System.out.println(china.size());
        for(Country c:china){
            System.out.println(c);
        }
    }


}
