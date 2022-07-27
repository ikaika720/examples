package hoge.exp.jpa.webapp;

import javax.persistence.EntityManagerFactory;
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
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "hoge.exp.jpa.webapp")
@EnableTransactionManagement
public class AppConfig {

    @Bean(destroyMethod = "close")
    protected DataSource dataSource(@Value("${datasource.url}") String url,
            @Value("${datasource.username}") String username,
            @Value("${datasource.password}") String password) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDefaultAutoCommit(false);
        ds.setDriverClassName("org.postgresql.Driver");
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
        emf.setPackagesToScan("hoge.exp.jpa.webapp");
        return emf;

    }

    @Bean
    protected JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jva = new HibernateJpaVendorAdapter();
        jva.setDatabase(Database.POSTGRESQL);
        return jva;
    }
}
