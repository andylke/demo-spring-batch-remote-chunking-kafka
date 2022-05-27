package com.github.andylke.demo.randomuser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class DownloadRandomUserStepConfig {

  public static final String RANDOM_USER_FILE_PATH = "target/random-user.csv";

  public static final String[] RANDOM_USER_FIELD_NAMES =
      new String[] {
        "gender",
        "name.title",
        "name.first",
        "name.last",
        "location.street.number",
        "location.street.name",
        "location.city",
        "location.state",
        "location.country",
        "location.postcode",
        "location.coordinates.latitude",
        "location.coordinates.longitude",
        "location.timezone.offset",
        "location.timezone.description",
        "email",
        "login.uuid",
        "login.username",
        "login.password",
        "login.salt",
        "login.md5",
        "login.sha1",
        "login.sha256",
        "nat"
      };

  @Autowired private StepBuilderFactory stepBuilderFactory;

  @Autowired private DownloadRandomUserProperties properties;

  @Bean
  public Step downloadRandomUserStep() {
    return stepBuilderFactory
        .get("downloadRandomUserFile")
        .<RandomUser, RandomUser>chunk(properties.getChunkSize())
        .reader(randomUserRestServiceReader())
        .writer(randomUserFileWriter())
        .build();
  }

  @Bean
  @StepScope
  public RandomUserRestServiceReader randomUserRestServiceReader() {
    return new RandomUserRestServiceReader(properties.getTotalPage(), properties.getPageSize());
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<? super RandomUser> randomUserFileWriter() {
    return new FlatFileItemWriterBuilder<RandomUser>()
        .name("randomUserFileWriter")
        .resource(new FileSystemResource(RANDOM_USER_FILE_PATH))
        .encoding("UTF-8")
        .shouldDeleteIfExists(true)
        .delimited()
        .delimiter("|")
        .names(RANDOM_USER_FIELD_NAMES)
        .headerCallback(callback -> callback.write(StringUtils.join(RANDOM_USER_FIELD_NAMES, ",")))
        .build();
  }
}
