package poll.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * This method will configure WebSecurity, making it helpful to specify requests to ignore.
     *
     * @param webSecurity The object to adapt the Spring Security Filter Chain.
     */
    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/birds", "/birds/vote", "/voter");
    }

    /**
     * This method will detail what endpoints require security and how a user can authenticate.
     *
     * @param httpSecurity The object that can construct web based security for specified requests.
     * @throws Exception The error, if it occurs.
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic().and().authorizeRequests().anyRequest().authenticated().and().formLogin();
    }

    /**
     * This method will configure the authentication needed to access the protected endpoints.
     *
     * @param authenticationManagerBuilder The object to structure the authentication details and store location.
     * @throws Exception The error, if it occurs.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.inMemoryAuthentication().withUser("username").password("{noop}password").roles("USER");
    }
}
