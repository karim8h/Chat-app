package app.controllers;

import java.util.Properties;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import app.presentation.home.UserContainer;
import app.presentation.home.UserContainerImpl;

@Configuration
@ComponentScan(basePackages={"app.controllers", "app.presentation.chat", "app.model"},
excludeFilters={ @Filter(type=FilterType.ANNOTATION,
	value=EnableWebMvc.class)
})
@EnableJpaRepositories(basePackages="app.model")
public class RootConfig {
	
	private final String PACKAGES_TO_SCAN = "app.model";
	
	@Bean
	public UserContainer getUserContainer() {
		return new UserContainerImpl(); 
	}
	
	@Bean
	public DriverManagerDataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/myschema");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		return dataSource;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		entityManagerFactory.setPackagesToScan(PACKAGES_TO_SCAN);
		entityManagerFactory.setJpaProperties(getJpaProperties());
		return entityManagerFactory;
	}
	
	private Properties getJpaProperties() {
		
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		jpaProperties.put("hibernate.show_sql", false);
		jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
		
		return jpaProperties;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}
}
