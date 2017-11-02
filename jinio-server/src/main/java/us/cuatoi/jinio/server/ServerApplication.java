package us.cuatoi.jinio.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

    @Bean
    FilterRegistrationBean jinioFilterRegistration(@Autowired SimpleJinioFilter simpleJinioFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(simpleJinioFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
