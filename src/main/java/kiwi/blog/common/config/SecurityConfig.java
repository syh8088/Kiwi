package kiwi.blog.common.config;

import kiwi.blog.common.config.handler.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                //.addFilterBefore(authenticationFilter(), BasicAuthenticationFilter.class)
                .requiresChannel().anyRequest().requiresSecure()
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers( "/oauth/token").permitAll()
                .antMatchers("/tokens").permitAll()
                .and()
                .csrf().disable();
    }
/*

       // TODO 주석 처리함
       // There is no client authentication. Try adding an appropriate authentication filter. 에러 때문에....
       // C:\Users\syh80\.gradle\caches\modules-2\files-2.1\org.springframework.security.oauth\spring-security-oauth2\2.3.6.RELEASE\87d3a24789a0757574752501f33ca89c65b99804\spring-security-oauth2-2.3.6.RELEASE.jar!\org\springframework\security\oauth2\provider\endpoint\TokenEndpoint.class
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers( "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**", "/h2-console/**", "/oauth/token", "/actuator/**");
    }
*/

    @Autowired
    public void globalUserDetails(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider);
    }


    /**
     * 需要配置这个支持password模式
     * support password grant type
     * @return
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
