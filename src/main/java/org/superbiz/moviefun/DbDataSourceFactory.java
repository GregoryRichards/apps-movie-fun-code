package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DbDataSourceFactory {

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value( "${vcap.services}") String vcapServices )
    {
        System.out.println( vcapServices );
        return new DatabaseServiceCredentials( vcapServices );
    }

    @Bean
    public DataSource albumsDataSource( DatabaseServiceCredentials serviceCredentials )
    {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource( dataSource );
        return ds;
    }

    @Bean
    public DataSource moviesDataSource( DatabaseServiceCredentials serviceCredentials )
    {
        System.out.println( serviceCredentials );
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource( dataSource );
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter()
    {
        HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
        hjva.setDatabase(Database.MYSQL );
        hjva.setDatabasePlatform( "org.hibernate.dialect.MySQL5Dialect" );
        hjva.setGenerateDdl( true );
        return hjva;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(DataSource albumsDataSource,
                                                                                               HibernateJpaVendorAdapter hibernateJpaVendorAdapter )
    {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource( albumsDataSource );
        lcemfb.setJpaVendorAdapter( hibernateJpaVendorAdapter );
        lcemfb.setPackagesToScan( "org.superbiz.moviefun.albums");
        lcemfb.setPersistenceUnitName( "albums");
//        lcemfb.setBeanName( "albums-mysql" );
        return lcemfb;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(DataSource moviesDataSource,
                                                                                               HibernateJpaVendorAdapter hibernateJpaVendorAdapter )
    {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource( moviesDataSource );
        lcemfb.setJpaVendorAdapter( hibernateJpaVendorAdapter );
        lcemfb.setPackagesToScan( "org.superbiz.moviefun.movies");
        lcemfb.setPersistenceUnitName( "movies" );
//        lcemfb.setBeanName( "movies-mysql" );
        return lcemfb;
    }
}
