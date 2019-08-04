package com.johnny.tools.autolike.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    List<Locale> LOCALES = Arrays.asList(
            new Locale("sc"),
            new Locale("tc"),
            new Locale("en")
    );

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("lang");
        return headerLang == null || headerLang.isEmpty() ? Locale.getDefault() : Locale.lookup(Locale.LanguageRange.parse(headerLang.toLowerCase()), LOCALES);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }
}
