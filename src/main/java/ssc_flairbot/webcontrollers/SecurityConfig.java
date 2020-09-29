package ssc_flairbot.webcontrollers;

import java.util.Arrays;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    public SecurityConfig(@Qualifier("oauth2ClientContext") OAuth2ClientContext oauth2ClientContext) {
        this.oauth2ClientContext = oauth2ClientContext;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/login**", "/greeting**", "/webjars/**", "/error**", "/js/**", "/css/**")
                .permitAll()
                .anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .permitAll();
    }

    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter discordFilter = new OAuth2ClientAuthenticationProcessingFilter("/loginReddit");
        OAuth2RestTemplate discordTemplate = new OAuth2RestTemplate(reddit(), oauth2ClientContext);

        ClientHttpRequestFactory requestFactory = new RedditHttpRequestFactory();

        OAuth2AccessTokenSupport authAccessProvider = new AuthorizationCodeAccessTokenProvider();
        authAccessProvider.setRequestFactory(requestFactory);

        AccessTokenProvider accessTokenProvider = new AccessTokenProviderChain(Arrays.<AccessTokenProvider>asList((AuthorizationCodeAccessTokenProvider) authAccessProvider));
        discordTemplate.setAccessTokenProvider(accessTokenProvider);

        discordTemplate.setRequestFactory(requestFactory);

        discordFilter.setRestTemplate(discordTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(redditResource().getUserInfoUri(), reddit().getClientId());
        tokenServices.setRestTemplate(discordTemplate);

        discordFilter.setTokenServices(tokenServices);
        return discordFilter;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("reddit.client")
    public AuthorizationCodeResourceDetails reddit() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("reddit.resource")
    public ResourceServerProperties redditResource() {
        return new ResourceServerProperties();
    }
}
