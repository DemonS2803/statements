package ru.light.statements.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.light.statements.enums.UserRole;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests((authz) -> authz
//                        .requestMatchers("/*").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // .requestMatchers("/api/statement/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.OPERATOR.name(), UserRole.USER.name())
                        .requestMatchers("/api/statement/edit").hasAuthority(UserRole.USER.name())
                        .requestMatchers("/api/statement/status/**").hasAuthority(UserRole.OPERATOR.name())

                        // .requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
                        // .requestMatchers("/api/operator/**").hasAnyRole(UserRole.OPERATOR.name(), UserRole.ADMIN.name())
                        // .requestMatchers("/api/user/**").hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .cors().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPointJwt).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200");
            }
        };
    }
}
