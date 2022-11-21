package poll.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * This method will create a mapping between URL endpoints and view names.
     *
     * @param registry The object to assist registering view controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("admin-client.html");
        registry.addViewController("/voter").setViewName("voter-client.html");
        registry.addViewController("/static/javascript/admin.js").setViewName("../static/javascript/admin.js");
    }
}
