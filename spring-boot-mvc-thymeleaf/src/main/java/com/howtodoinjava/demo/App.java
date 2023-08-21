package com.howtodoinjava.demo;

import com.howtodoinjava.demo.dao.ItemRepository;
import com.howtodoinjava.demo.dao.model.Item;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class App implements CommandLineRunner, WebApplicationInitializer {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Autowired
  ItemRepository itemRepository;

  @Override
  public void run(String... args) throws Exception {
    itemRepository.save(new Item(1, "Item - 1"));
    itemRepository.save(new Item(2, "Item - 2"));
  }

  @Override
  public void onStartup(ServletContext sc) throws ServletException {
    AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();

    root.scan("com.howtodoinjava.demo");
    sc.addListener(new ContextLoaderListener(root));

    ServletRegistration.Dynamic appServlet = sc.addServlet("mvc",
        new DispatcherServlet(new GenericWebApplicationContext()));
    appServlet.setLoadOnStartup(1);
    appServlet.addMapping("/");
  }
}
