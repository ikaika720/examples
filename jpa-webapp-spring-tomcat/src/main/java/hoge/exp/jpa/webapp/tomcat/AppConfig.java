package hoge.exp.jpa.webapp.tomcat;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "hoge.exp.jpa.webapp.tomcat")
@EnableTransactionManagement
public class AppConfig {

    @Bean(destroyMethod = "close")
    protected DataSource dataSource(
            @Value("${datasource.url:jdbc:postgresql://localhost:5432/mydb}") String url,
            @Value("${datasource.username:user1}") String username,
            @Value("${datasource.password:password}") String password) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDefaultAutoCommit(false);
        ds.setDriverClassName("org.postgresql.Driver");

        String envUrl = System.getenv("DATASOURCE_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            url = envUrl;
        }

        ds.setPassword(password);
        ds.setUrl(url);
        ds.setUsername(username);
        return ds;
    }

    @Bean
    protected JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    protected LocalContainerEntityManagerFactoryBean entityManagerFactory(
            JpaVendorAdapter jva, DataSource ds) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setJpaVendorAdapter(jva);
        emf.setPackagesToScan("hoge.exp.jpa.webapp.tomcat");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        emf.setJpaProperties(jpaProperties);

        return emf;
    }

    @Bean
    protected JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }
}
