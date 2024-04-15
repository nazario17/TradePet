package com.example.trade.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsService userDetailsService() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("select username,password,active from user where username=?")
                .authoritiesByUsernameQuery("select u.username, ur.roles from user u inner join user_roles ur on u.username = ur.user_name where u.username=?");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/trade/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/trade/my/**").hasAnyAuthority("USER","ADMIN")
                                .requestMatchers("/trade/**").hasAnyAuthority("USER","ADMIN")
                                .requestMatchers("/login").anonymous()
                                .requestMatchers("/signup").anonymous()
                                .requestMatchers("/css/**", "/js/**", "/images/**","/fonts/**").permitAll()
                                .anyRequest().denyAll()

                )
                .httpBasic(withDefaults())
                .formLogin().defaultSuccessUrl("/trade/my").and()
                .csrf().disable()
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login")
                ).build();
    }

}

