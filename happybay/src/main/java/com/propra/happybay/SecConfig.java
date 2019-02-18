package com.propra.happybay;

import com.propra.happybay.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST,"/add").permitAll()
                .antMatchers("/confirmationAdd").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/addUser").permitAll()
                .antMatchers("/about").permitAll()
                .antMatchers("/profile").hasRole("USER")
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.logout().permitAll();
        http.userDetailsService(userDetailsService);
        http.csrf().disable();
        http.formLogin().loginPage("/login").permitAll();
    }
}
