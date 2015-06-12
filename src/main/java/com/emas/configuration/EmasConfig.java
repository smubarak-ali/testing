package com.emas.configuration;

import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@PropertySource({ "classpath:database.properties" })
//@Import( TransactionConfig.class )
//@DependsOn("transactionManager")
@EnableJpaRepositories( basePackages = "com.emas.jpa.repository", entityManagerFactoryRef = "emasEntityManager", transactionManagerRef = "transactionManager" )
public class EmasConfig {

	@Autowired
	private Environment env;

	@Bean
	public LocalContainerEntityManagerFactoryBean emasEntityManager() {

        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource( emasDataSource() );
        em.setPersistenceProviderClass( HibernatePersistenceProvider.class );
        em.setPersistenceUnitName( "emasPU" );
        em.setPackagesToScan(new String[] { "com.emas.jpa.entity" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter( vendorAdapter );
		em.setJpaProperties( additionalProperties() );

        return em;
    }

	@Bean//(initMethod = "init", destroyMethod = "close")
	public DataSource emasDataSource() {

		DataSource ds = null;
		try {

			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:jboss/EmasDSXA");
			System.out.println( "Connected database name =====>" + ds.getConnection().getCatalog() );

		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ds;

		/*PoolingDataSource dataSource = new PoolingDataSource();
    	dataSource.setUniqueName("emas-postgres");
    	dataSource.setClassName( "org.postgresql.xa.PGXADataSource" );
//    	dataSource.setClassName( "org.postgresql.Driver" );
//    	dataSource.setClassName( "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" );
    	dataSource.setMinPoolSize( 1 );
    	dataSource.setMaxPoolSize( 5 );
    	dataSource.setAllowLocalTransactions( true );
    	dataSource.setDriverProperties( datasourceProperties() );
//    	dataSource.init();
    	return (DataSource) dataSource;*/

		/*final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName( env.getProperty( "db.driver" ) );
		dataSource.setUrl( env.getProperty( "db.url.emas" ) );
		dataSource.setUsername( env.getProperty( "db.username" ) );
		dataSource.setPassword( env.getProperty( "db.password" ) );

		return dataSource;*/
	}

	/* @Bean
    public PlatformTransactionManager emasTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( emasEntityManager().getObject() );
        return transactionManager;
    }*/

	final Properties additionalProperties() {

		final Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty( "hibernate.hbm2ddl.auto" ) );
		hibernateProperties.setProperty( "hibernate.show_sql", env.getProperty( "hibernate.show_sql" ) );
		hibernateProperties.setProperty( "hibernate.dialect", env.getProperty( "hibernate.dialect" ) );
		hibernateProperties.setProperty( "hibernate.transaction.jta.platform" , "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform" );
		hibernateProperties.setProperty( "hibernate.transaction.factory_class", "org.hibernate.transaction.CMTTransactionFactory" );
//		hibernateProperties.setProperty( "hibernate.connection.release_mode", "after_statement" );
//		hibernateProperties.setProperty( "hibernate.current_session_context_class", "jta" );
//		hibernateProperties.setProperty( "hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.JBossTransactionManagerLookup" );
//		hibernateProperties.setProperty( "jboss.entity.manager.factory.jndi.name", "java:jboss/EmasDSXA" );
//		hibernateProperties.setProperty( "hibernate.ejb.entitymanager_factory_name", "Emas" );
//		hibernateProperties.setProperty( "jboss.entity.manager.factory.jndi.name", "java:jboss/EmasDSXA" );
		
		return hibernateProperties;
	}

	/*@Bean
    public PlatformTransactionManager emasTransactionManager() {

        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( emasEntityManager().getObject() );
        return transactionManager;
    }*/

	/*final Properties datasourceProperties() {

		final Properties datasourceProperties = new Properties();

        datasourceProperties.setProperty( "user", env.getProperty( "db.username" ) );
        datasourceProperties.setProperty( "password", env.getProperty( "db.password" ) );
//        datasourceProperties.setProperty( "url", env.getProperty( "db.url.emas" ) );
//        datasourceProperties.setProperty( "driverClassName", env.getProperty( "db.driver" ) );
        datasourceProperties.setProperty( "ServerName", env.getProperty( "db.serverName" ) );
        datasourceProperties.setProperty( "PortNumber" , env.getProperty( "db.portNumber" ) );
        datasourceProperties.setProperty( "databaseName" , env.getProperty( "db.databaseName.emas" ) );
		return datasourceProperties;
	}*/

	/*@Bean
	public PlatformTransactionManager transactionManager() {

		final JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setTransactionManagerName( "java:jboss/TransactionManager" );
		transactionManager.setUserTransactionName( "java:jboss/UserTransaction" );
		return transactionManager;
	}*/

}