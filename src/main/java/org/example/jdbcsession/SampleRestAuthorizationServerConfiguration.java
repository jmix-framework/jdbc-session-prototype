package org.example.jdbcsession;

import org.example.jdbcsession.rest.SessionTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
public class SampleRestAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected AuthenticationKeyGenerator keyGenerator;

//    @Autowired
//    protected AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore());
    }

//    @Bean
//    public SessionTokenEnhancer tokenEnhancer() {
//        return new SessionTokenEnhancer();
//    }

//    @Primary
    @Bean
    protected TokenStore tokenStore() {
        JdbcTokenStore tokenStore = new JdbcTokenStore(dataSource);
        tokenStore.setAuthenticationKeyGenerator(keyGenerator);
        return tokenStore;
    }
}
