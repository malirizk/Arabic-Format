package com.arabic.demo;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

	@Bean
	public TomcatServletWebServerFactory servletContainerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

		// factory.addConnectorCustomizers(connector -> // ((AbstractProtocol) //
		// connector.getProtocolHandler()).setConnectionTimeout(10000));

		factory.addConnectorCustomizers(connector -> connector.setURIEncoding("UTF-8"));

		return factory;
	}

/*	@EventListener(ApplicationReadyEvent.class)
	public void onStartup(ServletContext servletContext) throws ServletException {
		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter",
				new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, true, "/*");
	}*/
	
	@Bean
	public ServletContextInitializer initializer() {
	    return new ServletContextInitializer() {

	        @Override
	        public void onStartup(ServletContext servletContext) throws ServletException {
	    		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter",
	    				new CharacterEncodingFilter());
	    		encodingFilter.setInitParameter("encoding", "UTF-8");
	    		encodingFilter.setInitParameter("forceEncoding", "true");
	    		encodingFilter.addMappingForUrlPatterns(null, true, "/*");

	        }
	    };
	}

}
