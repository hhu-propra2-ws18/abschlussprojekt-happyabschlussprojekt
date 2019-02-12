package com.propra.happybay;

import com.propra.happybay.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
public class SecConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    //@Autowired
    //private PasswordEncoder encoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.GET,"/add").permitAll()
                .antMatchers(HttpMethod.GET,"/confirmationAdd").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/addUser").permitAll()
                .antMatchers("/user").hasRole("USER")
                .anyRequest().authenticated();
        http.formLogin().permitAll();
        http.logout().permitAll();
        http.userDetailsService(userDetailsService);
    }

}
