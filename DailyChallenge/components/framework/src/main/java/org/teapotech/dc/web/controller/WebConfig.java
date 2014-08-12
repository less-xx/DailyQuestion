/**
 * 
 */
package org.teapotech.dc.web.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author jiangl
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
	@Override
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping handlerMapping = super
		        .requestMappingHandlerMapping();
		handlerMapping.setUseSuffixPatternMatch(false);
		handlerMapping.setUseTrailingSlashMatch(false);
		return handlerMapping;
	}

	@Override
	public ContentNegotiationManager mvcContentNegotiationManager() {
		ContentNegotiationManager cm = super.mvcContentNegotiationManager();
		return cm;
	}
}
