package ru.landar.spring;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import ru.landar.spring.classes.AppClassLoader;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
public class SpringFdApplication {
	public static void main(String[] args) {
		for(String arg : args) {
            System.out.println(arg);
        }
		SpringApplication.run(SpringFdApplication.class, args);
	}
	@Value("${spring.datasource.url}")
	String url;
	@Value("${spring.datasource.username}")
	String username;
	@Value("${spring.datasource.password}")
	String password;
	@Bean
	public DataSource dataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		DataSource dataSource = null;
		try { dataSource = dataSourceLookup.getDataSource("java:jboss/datasources/fd"); } catch (Exception ex) { }
		if (dataSource == null) {
			DriverManagerDataSource ds = new DriverManagerDataSource();
			ds.setUrl(url);
			ds.setUsername(username);
			ds.setPassword(password);
			dataSource = ds;
		}
		return dataSource;
	}
	@Bean
	public PhysicalNamingStrategy physicalNamingStrategy() {
		return new PhysicalNamingStrategyCustom();
	}
	@Bean
	public AppClassLoader getAppClassLoader() {
		return new AppClassLoader();
	}
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ObjectChanged getObjectChanged() {
	    return new ObjectChanged();
	}
}

