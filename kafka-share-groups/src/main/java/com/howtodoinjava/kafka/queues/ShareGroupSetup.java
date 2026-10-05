package com.howtodoinjava.kafka.queues;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

/**
 * A new share group starts reading at the latest offset, so records sent before the first
 * consumer joins are skipped. This class sets share.auto.offset.reset=earliest on the group
 * before the listener containers start.
 */
@Component
public class ShareGroupSetup implements SmartInitializingSingleton {

  private final KafkaAdmin kafkaAdmin;

  public ShareGroupSetup(KafkaAdmin kafkaAdmin) {
    this.kafkaAdmin = kafkaAdmin;
  }

  @Override
  public void afterSingletonsInstantiated() {
    try (Admin admin = Admin.create(kafkaAdmin.getConfigurationProperties())) {
      var group = new ConfigResource(ConfigResource.Type.GROUP, QueueConfig.SHARE_GROUP);
      var earliest = new AlterConfigOp(new ConfigEntry("share.auto.offset.reset", "earliest"),
          AlterConfigOp.OpType.SET);
      admin.incrementalAlterConfigs(Map.of(group, List.of(earliest))).all().get();
    } catch (Exception e) {
      throw new IllegalStateException("Could not configure share group " + QueueConfig.SHARE_GROUP, e);
    }
  }
}
