package kfu.itis.maslennikov.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/// Не нужон, на 1 паре делали
//@Configuration
//@EnableWebMvc
//@ComponentScan(basePackages = {"kfu.itis.maslennikov"})
//public class WebMvcConfig implements WebApplicationInitializer {
//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
//        context.register(WebMvcConfig.class);
//        servletContext.addListener(new ContextLoaderListener(context));
//
//        ServletRegistration.Dynamic dynamic = servletContext
//                .addServlet("dispatcher",
//                        new DispatcherServlet(new GenericWebApplicationContext()));
//        dynamic.addMapping("/");
//        dynamic.setLoadOnStartup(1);
//    }
//}
