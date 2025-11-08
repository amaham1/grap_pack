package com.example.cms.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 설정
 */
@Configuration
@MapperScan(basePackages = {
    "com.example.cms.auth.mapper",
    "com.example.cms.admin.content.mapper",
    "com.example.cms.admin.image.mapper",
    "com.example.cms.user.content.mapper"
})
public class MyBatisConfig {

    /**
     * SqlSessionFactory 빈 등록
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // Mapper XML 위치 설정
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/**/*.xml"));

        // Type Alias는 application.yml에서 설정
        // (중복 설정 시 @Alias 어노테이션과 충돌 발생 가능)

        return sessionFactory.getObject();
    }
}
