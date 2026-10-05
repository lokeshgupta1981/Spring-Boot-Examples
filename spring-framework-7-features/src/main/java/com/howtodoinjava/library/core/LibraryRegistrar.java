package com.howtodoinjava.library.core;

import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.core.env.Environment;

public class LibraryRegistrar implements BeanRegistrar {

  @Override
  public void register(BeanRegistry registry, Environment env) {
    registry.registerBean(ShelfScanner.class);
    registry.registerBean("shelfClient", ShelfClient.class,
        spec -> spec.supplier(ctx -> new ShelfClient(ctx.bean(ShelfScanner.class))));

    String[] branches = env.getProperty("library.branches", String[].class, new String[0]);
    for (String branch : branches) {                          // library.branches=north,south
      registry.registerBean(branch + "Desk", HelpDesk.class,  // northDesk, southDesk
          spec -> spec.supplier(ctx -> new HelpDesk(branch)));
    }
  }
}
