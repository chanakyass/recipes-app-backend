package spring.io.rest.recipes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import spring.io.rest.recipes.security.jwt.JwtTokenFilter;
import spring.io.rest.recipes.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService appUserDetailsService;
    private final JwtTokenFilter jwtTokenFilter;
    private final String uriPrefix;
    private final String adminIp4Address;
    private final String adminIp6Address;

    @Autowired
    public SecurityConfig(@Lazy CustomUserDetailsService appUserDetailsService, JwtTokenFilter jwtTokenFilter,
                          @Value("${app.uri.prefix}") String uriPrefix,
                          @Value("${app.admin.ip4-address}") String adminIp4Address,
                          @Value("${app.admin.ip6-address}") String adminIp6address){
        this.appUserDetailsService = appUserDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.uriPrefix = uriPrefix;
        this.adminIp4Address = adminIp4Address;
        this.adminIp6Address = adminIp6address;
    }


    @Bean
    public static PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appUserDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling()
                .and().anonymous().and()
                .authorizeRequests()
//                .anyRequest().permitAll()
//                .and().addFilter(corsFilter());
                .antMatchers("/").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers(uriPrefix+"/public/**").permitAll()
                .antMatchers(uriPrefix+"/admin/**")
                                        .access("hasIpAddress('"+adminIp4Address+"') or hasIpAddress('"+adminIp6Address+"')")
                .antMatchers("/v2/api-docs", "/configuration/ui", "/configuration/security").permitAll()
                .antMatchers("/swagger-resources/**", "/webjars/**", "/swagger-ui.html", "/swagger-ui.html/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterRegistration(JwtTokenFilter filter) {
        FilterRegistrationBean<JwtTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
