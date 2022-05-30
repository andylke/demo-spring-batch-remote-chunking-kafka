package com.github.andylke.demo.randomuser;

import java.util.Map;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.chunk.ChunkResponse;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ImportRandomUserManagerStepConfig {

  @Autowired private RemoteChunkingManagerStepBuilderFactory stepBuilderFactory;

  @Autowired private ImportRandomUserProperties properties;

  @Autowired private FormattingConversionService conversionService;

  @Autowired private RandomUserRepository randomUserRepository;

  @Bean
  public Step importRandomUserManagerStep() {
    return stepBuilderFactory
        .get("importRandomUserManager")
        .chunk(properties.getChunkSize())
        .reader(randomUserRepositoryReader())
        .outputChannel(importRandomUserMasterRequestsChannel())
        .inputChannel(importRandomUserMasterRepliesChannel())
        .build();
  }

  @Bean
  @StepScope
  public RepositoryItemReader<RandomUser> randomUserRepositoryReader() {
    return new RepositoryItemReaderBuilder<RandomUser>()
        .name("randomUserRepositoryReader")
        .repository(randomUserRepository)
        .methodName("findAll")
        .sorts(Map.of("id", Direction.ASC))
        .build();
  }

  @Bean
  public DirectChannel importRandomUserMasterRequestsChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserMasterRequestsFlow(
      KafkaTemplate<String, String> kafkaTemplate) {
    return IntegrationFlows.from(importRandomUserMasterRequestsChannel())
        .transform(source -> conversionService.convert(source, byte[].class))
        .handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic("import-random-user-requests"))
        .get();
  }

  @Bean
  public QueueChannel importRandomUserMasterRepliesChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserMasterRepliesFlow(
      ConsumerFactory<String, String> consumerFactory) {
    return IntegrationFlows.from(
            Kafka.messageDrivenChannelAdapter(consumerFactory, "import-random-user-replies"))
        .transform(source -> conversionService.convert(source, ChunkResponse.class))
        .channel(importRandomUserMasterRepliesChannel())
        .get();
  }
}
