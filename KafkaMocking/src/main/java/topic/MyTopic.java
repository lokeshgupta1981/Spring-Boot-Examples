package topic;

import constants.GlobalConstants;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;

public class MyTopic
{
    public static void createTopic(String topicName)
    {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, GlobalConstants.BOOTSTRAP_SERVER
        );

        try (Admin admin = Admin.create(properties))
        {
            int partitions = 3;
            short replicationFactor = 1;

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            admin.createTopics(
                    Collections.singleton(newTopic)
            );
        }
    }
}
