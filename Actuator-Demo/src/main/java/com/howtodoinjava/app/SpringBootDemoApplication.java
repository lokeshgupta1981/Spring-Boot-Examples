package com.howtodoinjava.app;

//@SpringBootApplication
public class SpringBootDemoApplication {

  public static void main(String[] args) throws InterruptedException {
    //SpringApplication.run(SpringBootDemoApplication.class, args);

    Runnable runnable = new Runnable() {
      @lombok.SneakyThrows
      @Override
      public void run() {
        while (true) {
          Thread.sleep(1000);
          System.out.println(Thread.currentThread().getState());
        }
      }
    };

    Thread thread = new Thread(runnable);
    thread.start();

    Thread.sleep(5000);
  }


}
