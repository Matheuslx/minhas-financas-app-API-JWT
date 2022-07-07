package com.aroque.minhasfinancas.config;

import com.aroque.minhasfinancas.JwtTokenFilter;
import com.aroque.minhasfinancas.service.Impl.SecurityUserDetailsService;
import com.aroque.minhasfinancas.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.hibernate.criterion.Restrictions.and;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityUserDetailsService userDetailsService;

    @Autowired
    JwtService jwtService;


    // Criptografar a senha
    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder enconder = new BCryptPasswordEncoder();
        return enconder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // É aq q vamos fazer a configuração para fazer as autentificações do usuário
        // nele que vamos fazer a configuração para buscar os usuários no BD

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(){
        return new JwtTokenFilter(jwtService, userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Aq estou falando q qualquer requisição tem q estar autenticado
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .anyRequest().authenticated()
        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(){

        List<String> all = Arrays.asList("*");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(all);
        config.setAllowedOrigins(all);
        config.setAllowedHeaders(all);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        CorsFilter corsfilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corsfilter);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filter;
    }


}
