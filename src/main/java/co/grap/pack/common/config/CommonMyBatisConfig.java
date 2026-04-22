package co.grap.pack.common.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 공통 모듈 MyBatis 설정
 */
@org.springframework.context.annotation.Configuration
@MapperScan(
        basePackages = "co.grap.pack.common.visitor.mapper",
        sqlSessionFactoryRef = "commonSqlSessionFactory"
)
public class CommonMyBatisConfig {

    /**
     * 공통 모듈용 SqlSessionFactory 빈 등록
     */
    @Bean(name = "commonSqlSessionFactory")
    public SqlSessionFactory commonSqlSessionFactory(
            @Qualifier("grapDataSource") DataSource dataSource
    ) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/common/**/*.xml"));
        sessionFactory.setTypeAliasesPackage("co.grap.pack.common.visitor.model");

        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(30);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
