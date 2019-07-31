package ru.landar.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ru.landar.spring.service.UserService;

@Configuration
@EnableTransactionManagement
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired 
	UserService userService;
	@Autowired
	private CustomAuthenticationProvider authenticationProvider;
	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return new CustomLoginSuccessHandler("/main");
	}
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity.httpBasic()
    	.and()
    	.authorizeRequests()
    	.antMatchers("/*", "/json/*")
    	.hasAnyRole("USER", "ADMIN")
    	.and()
    	.authorizeRequests()
    	.antMatchers("/user*", "/listUser*")
    	.hasRole("ADMIN")
    	.and()
    	.authorizeRequests()
    	.antMatchers("/load")
    	.hasRole("ADMIN")
    	.and()
    	.formLogin().loginPage("/login")
    	.permitAll()
    	.usernameParameter("username")
    	.passwordParameter("password")
    	.successHandler(successHandler())
    	.and().exceptionHandling().accessDeniedPage("/accessDenied").and().csrf().disable().cors();
    	
    	httpSecurity.httpBasic()
    	.and()
    	.authorizeRequests()
    	.antMatchers("/test*")
    	.permitAll();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     	auth.authenticationProvider(authenticationProvider);
    }
}
