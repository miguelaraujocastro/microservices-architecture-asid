package com.ijse.bookstore.config;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.messaging.kafka.basic.consumer.DefaultKafkaConsumerFactory;
import io.eventuate.messaging.kafka.basic.consumer.KafkaConsumerFactory;
import io.eventuate.tram.consumer.common.DuplicateMessageDetector;
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector;
import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.common.DomainEventNameMapping;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableJpaRepositories("com.ijse.bookstore.repository")
@Import({
    TramJdbcKafkaConfiguration.class,
    TramEventsPublisherConfiguration.class,
    EventuateTramKafkaMessageConsumerConfiguration.class,
    TramEventSubscriberConfiguration.class
    // DefaultKafkaConsumerFactory.class
})
public class TramConfig {

  // @Bean
  // public KafkaConsumerFactory kafkaConsumerFactory() {
  //   return new DefaultKafkaConsumerFactory();
  // }

  @Bean
  public ChannelMapping channelMapping() {
    return DefaultChannelMapping.builder().build();
  }

  // @Bean
  // public DomainEventNameMapping domainEventNameMapping() {
  //   return new DomainEventNameMapping() {
  //     @Override
  //     public String eventToExternalEventType(String aggregateType, DomainEvent domainEvent) {
  //       return domainEvent.getClass().getName();
  //     }
  //     @Override
  //     public String externalEventTypeToEventClassName(String aggregateType, String externalEventType) {
  //       return externalEventType;
  //     }
  //   };
  // }

  @Bean
  public DuplicateMessageDetector duplicateMessageDetector() {
    return new NoopDuplicateMessageDetector();
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
