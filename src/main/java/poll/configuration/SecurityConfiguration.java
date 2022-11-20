package poll.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests().anyRequest().authenticated()
//                .and().formLogin().and().httpBasic();
        httpSecurity.httpBasic().and().authorizeRequests().antMatchers("/voter","/voter/**","/birds","/birds/**")
                .permitAll().anyRequest().authenticated().and().formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.inMemoryAuthentication()
                .withUser("username").password("{noop}password").roles("USER");
    }
}
