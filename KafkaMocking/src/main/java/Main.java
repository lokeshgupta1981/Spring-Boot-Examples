import consumer.MyKafkaConsumer;
import constants.GlobalConstants;
import producer.MyKafkaProducer;
import topic.MyTopic;

import java.util.concurrent.ExecutionException;

public class Main
{
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        MyTopic.createTopic(GlobalConstants.TOPIC);

        MyKafkaProducer producer = new MyKafkaProducer(MyKafkaProducer.getProducer());

        MyKafkaConsumer consumer = new MyKafkaConsumer(MyKafkaConsumer.getConsumer());

        Thread consumerThread = new Thread(() -> consumer.startPolling(GlobalConstants.TOPIC));

        consumerThread.start();

        for (int i = 0; i < 10; i++)
        {
            System.out.println("Message Sent to the topic: " +
                    producer.send(GlobalConstants.TOPIC, "Hello", "Hello World " + i).get().topic());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::stopPolling));

        consumerThread.join();
    }
}
