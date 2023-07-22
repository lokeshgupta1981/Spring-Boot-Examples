package com.howtodoinjava.demo.kafka.streams.service.service;

import static com.howtodoinjava.demo.kafka.streams.service.topology.OrdersTopology.ORDERS_COUNT;

import com.howtodoinjava.demo.kafka.streams.models.OrdersCountPerStoreDTO;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

  @Autowired
  private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

  public List<OrdersCountPerStoreDTO> ordersCount() {
    ReadOnlyKeyValueStore<String, Long> ordersStoreData = streamsBuilderFactoryBean.getKafkaStreams()
        .store(StoreQueryParameters.fromNameAndType(
            ORDERS_COUNT,
            QueryableStoreTypes.keyValueStore()
        ));

    var orders = ordersStoreData.all();
    var spliterator = Spliterators.spliteratorUnknownSize(orders, 0);
    return StreamSupport.stream(spliterator, false)
        .map(data -> new OrdersCountPerStoreDTO(data.key, data.value))
        .collect(Collectors.toList());
  }

}
