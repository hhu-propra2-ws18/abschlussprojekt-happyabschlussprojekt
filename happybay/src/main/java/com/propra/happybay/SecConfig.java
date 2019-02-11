package com.propra.happybay;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/login").authenticated()
                .antMatchers("/bezahlen").authenticated()
                .anyRequest().permitAll();
        http.formLogin().permitAll();
        http.logout().permitAll();
    }
}
