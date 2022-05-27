package com.github.andylke.demo.randomuser;

import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.github.andylke.demo.user.User;
import com.github.andylke.demo.user.UserRepository;

@Configuration
public class ImportRandomUserWorkerStepConfig {

  @Autowired private RemoteChunkingWorkerBuilder<RandomUser, User> workerBuilder;

  @Autowired private UserRepository userRepository;

  @Autowired private FormattingConversionService conversionService;

  @Bean
  public IntegrationFlow importRandomUserWorkerStep() {
    return workerBuilder
        .itemProcessor(randomUserToUserProcessor())
        .itemWriter(userRepositoryWriter())
        .inputChannel(importRandomUserWorkerRequestsChannel())
        .outputChannel(importRandomUserWorkerRepliesChannel())
        .build();
  }

  @Bean
  public RandomUserToUserProcessor randomUserToUserProcessor() {
    return new RandomUserToUserProcessor();
  }

  @Bean
  public RepositoryItemWriter<User> userRepositoryWriter() {
    return new RepositoryItemWriterBuilder<User>().repository(userRepository).build();
  }

  @Bean
  public QueueChannel importRandomUserWorkerRequestsChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserWorkerRequestsFlow(
      ConsumerFactory<String, String> consumerFactory) {
    return IntegrationFlows.from(
            Kafka.messageDrivenChannelAdapter(consumerFactory, "import-random-user-requests"))
        .transform(source -> conversionService.convert(source, ChunkRequest.class))
        .channel(importRandomUserWorkerRequestsChannel())
        .get();
  }

  @Bean
  public QueueChannel importRandomUserWorkerRepliesChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserWorkerRepliesFlow(
      KafkaTemplate<String, String> kafkaTemplate) {
    return IntegrationFlows.from(importRandomUserWorkerRepliesChannel())
        .transform(source -> conversionService.convert(source, byte[].class))
        .handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic("import-random-user-replies"))
        .get();
  }
}
