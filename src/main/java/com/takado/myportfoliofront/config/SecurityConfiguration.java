package com.takado.myportfoliofront.config;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;


@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_URL = "/oauth2/authorization/google";
    private static final String LOGOUT_URL = "/logout";
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
		// Use https connection
		.requiresChannel(channel -> channel.anyRequest().requiresSecure())
                // Allow all flow internal requests.
                .authorizeRequests().requestMatchers(SecurityConfiguration::isFrameworkInternalRequest).permitAll()
                // Allow guest page
                .antMatchers(new String[]{"/guest", "/not-restricted"}).permitAll()
                //allow login page
                .antMatchers(new String[]{"/", "/not-restricted"}).permitAll()
                // Restrict access to our application.
                .and().authorizeRequests().anyRequest().authenticated()

                // Not using Spring CSRF here to be able to use plain HTML for the login page
                .and().csrf().disable()
                // Configure logout
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                // Configure the login page.
                .and().oauth2Login().loginPage(LOGIN_URL).permitAll();
    }

    /**
     * Allows access to static resources, bypassing Spring Security.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // client-side JS code
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // web application manifest
                "/manifest.webmanifest", "/sw.js", "/offline-page.html",

                // icons and images
                "/icons/**", "/images/**");
    }

    /**
     * Tests if the request is an internal framework request. The test consists
     * of checking if the request parameter is present and if its value is
     * consistent with any of the request types know.
     *
     * @param request {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request
                .getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(ServletHelper.RequestType.values()).anyMatch(
                r -> r.getIdentifier().equals(parameterValue));
    }
}
