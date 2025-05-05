package com.system.watchCar.security;

import com.system.watchCar.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class HttpSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public HttpSecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Configurar as regras de segurança HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Desabilitar CSRF (se não usar)
        http.csrf().disable();

        // Sem sessão - arquiteturas sem estado
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Configuração de permissão para URLs
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // permite preflight
                .antMatchers("/api/login").permitAll() // Permitir login sem autenticação
                .antMatchers("/api/register").permitAll() // Permitir registro sem autenticação
                .antMatchers("/api/user").authenticated() // Requer autenticação para acessar /user
                .antMatchers("/h2-console/**/**").permitAll() // Permitir o acesso ao H2 Console
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/configuration/**", "/webjars/**").permitAll() // Permitir Swagger sem autenticação
                .anyRequest().authenticated(); // Qualquer outra requisição precisa de autenticação

        // Página de erro em caso de acesso negado
        http.exceptionHandling().accessDeniedPage("/login");

        // Adicionando o filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configurar WebSecurity para ignorar URLs específicas (como o Swagger e o H2 Console)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/configuration/**",
                "/webjars/**",
                "/public/**",
                "/h2-console/**/**" // Permitir o acesso ao H2 Console
        );
    }

    // Configuração do PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração do AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)  // Usando o CustomUserDetailsService
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
