package co.grap.pack.admin.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 통합 운영 포털 기본 DB MyBatis 설정이다.
 */
@org.springframework.context.annotation.Configuration
@MapperScan(
        basePackages = {
                "co.grap.pack.admin.auth.mapper",
                "co.grap.pack.admin.common.mapper",
                "co.grap.pack.admin.dashboard.mapper",
                "co.grap.pack.admin.content.mapper",
                "co.grap.pack.admin.qr.mapper"
        },
        sqlSessionFactoryRef = "adminSqlSessionFactory"
)
public class AdminMyBatisConfig {

    @Bean(name = "adminSqlSessionFactory")
    public SqlSessionFactory adminSqlSessionFactory(@Qualifier("grapDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/admin/**/*.xml"));
        sessionFactory.setTypeAliasesPackage("co.grap.pack.admin");

        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(30);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
