package spring.io.rest.recipes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spring.io.rest.recipes.security.jwt.JwtTokenFilter;
import spring.io.rest.recipes.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import spring.io.rest.recipes.security.oauth2.OAuth2AuthenticationFailureHandler;
import spring.io.rest.recipes.security.oauth2.OAuth2AuthenticationSuccessHandler;
import spring.io.rest.recipes.services.CustomOAuth2UserService;
import spring.io.rest.recipes.services.CustomUserDetailsService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService appUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenFilter jwtTokenFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final String uriPrefix;
    private final String adminIp4Address;
    private final String adminIp6Address;

    @Autowired
    public SecurityConfig(@Lazy CustomUserDetailsService appUserDetailsService,JwtTokenFilter jwtTokenFilter,
                          CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
                          @Value("${app.uri.prefix}") String uriPrefix,
                          @Value("${app.admin.ip4-address}") String adminIp4Address,
                          @Value("${app.admin.ip6-address}") String adminIp6address){
        this.appUserDetailsService = appUserDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
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
//        http
//        .csrf().disable().cors().and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .exceptionHandling().and()
//                .and().anonymous().and()
//                .authorizeRequests()
//                .anyRequest().permitAll()
//                .and().addFilter(corsFilter());
//                .antMatchers("/").permitAll()
//                .antMatchers("/h2-console/**").permitAll()
//                .antMatchers(uriPrefix+"/public/**", uriPrefix+"/oauth2/**", uriPrefix+"/login**").permitAll()
//                .antMatchers(uriPrefix+"/admin/**")
//                                        .access("hasIpAddress('"+adminIp4Address+"') or hasIpAddress('"+adminIp6Address+"')")
//                .antMatchers("/v2/api-docs", "/configuration/ui", "/configuration/security").permitAll()
//                .antMatchers("/swagger-resources/**", "/webjars/**", "/swagger-ui.html", "/swagger-ui.html/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                .authorizationEndpoint()
//                .baseUri("/oauth2/authorize")
//                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                .and()
//                .redirectionEndpoint()
//                .baseUri("/oauth2/callback/*")
//                .and()
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService)
//                .and()
//                .successHandler(oAuth2AuthenticationSuccessHandler)
//                .failureHandler(oAuth2AuthenticationFailureHandler);
//
//            http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers("/", "/h2-console", "/h2-console/**").permitAll()
                .antMatchers( "/oauth2/**", "/auth/**", uriPrefix+"/public/**").permitAll()
                .antMatchers(uriPrefix+"/admin/**")
                                        .access("hasIpAddress('"+adminIp4Address+"') or hasIpAddress('"+adminIp6Address+"')")
                .antMatchers("/v2/api-docs", "/configuration/ui", "/configuration/security").permitAll()
                .antMatchers("/swagger-resources/**", "/webjars/**", "/swagger-ui.html", "/swagger-ui.html/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore( jwtTokenFilter, UsernamePasswordAuthenticationFilter.class )
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository( cookieAuthorizationRequestRepository() )
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler( oAuth2AuthenticationSuccessHandler )
                .failureHandler(oAuth2AuthenticationFailureHandler);
                http.headers().frameOptions().disable();;
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }



    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods( Collections.singletonList( "*" ) );
        config.setAllowedOrigins( Collections.singletonList( "*" ) );
        config.setAllowedHeaders( Collections.singletonList( "*" ) );
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration( "/**", config );
        return source;
    }

//    private void successHandler(HttpServletRequest request,
//                                HttpServletResponse response, Authentication authentication ) throws IOException {
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        String token = jwtTokenUtil.generateToken( PayloadDetails.createPayloadDetails(userPrincipal));
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        PrintWriter out = response.getWriter();
//
//        String jsonString = mapper.writeValueAsString(Collections.singletonMap("accessToken", token));
//        out.print(jsonString);
//        out.flush();
//    }

//    @Bean
//    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterRegistration(JwtTokenFilter filter) {
//        FilterRegistrationBean<JwtTokenFilter> registration = new FilterRegistrationBean<>(filter);
//        registration.setEnabled(false);
//        return registration;
//    }
}
