package kiwi.blog.common.config.handler;

import kiwi.blog.common.config.authentication.AppTokenEnhancer;
import kiwi.blog.common.config.converter.CustomAccessTokenConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationSeverConfig extends AuthorizationServerConfigurerAdapter {

    private final CustomAccessTokenConverter customAccessTokenConverter;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthorizationSeverConfig(CustomAccessTokenConverter customAccessTokenConverter, PasswordEncoder passwordEncoder
            , @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager
                                    ) {
        this.customAccessTokenConverter = customAccessTokenConverter;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("zeyo_client")
                .secret(passwordEncoder.encode("iamclient"))
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("client", "read", "write")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(2592000)

                .and()
                .withClient("zeyo_admin")
                .secret(passwordEncoder.encode("iamadmin"))
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("admin", "read", "write")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(2592000)

                .and()
                .withClient("zeyo_user")
                .secret(passwordEncoder.encode("iamuser"))
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("user", "read", "write").accessTokenValiditySeconds(3600).refreshTokenValiditySeconds(2592000);
    }


    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(customAccessTokenConverter);

        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("/key_store/server.jks"), "dltnstls4$8*".toCharArray()).getKeyPair("auth");
        converter.setKeyPair(keyPair);

        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new AppTokenEnhancer();
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager);
        //.userDetailsService(userDetailsService);
    }
}
