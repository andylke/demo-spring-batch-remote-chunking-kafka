package com.github.andylke.demo.support;

import java.io.IOException;

import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.integration.chunk.ChunkResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.support.SerializationFailedException;

@Configuration(proxyBeanMethods = false)
class RemoteChunkingConfiguration {

  private final DefaultSerializer serializer = new DefaultSerializer();

  private final DefaultDeserializer deserializer = new DefaultDeserializer();

  @Bean
  public Converter<ChunkRequest<?>, byte[]> chunkRequestToByteArrayConverter() {
    return new Converter<ChunkRequest<?>, byte[]>() {

      @Override
      public byte[] convert(ChunkRequest<?> source) {
        try {
          return serializer.serializeToByteArray(source);
        } catch (IOException e) {
          throw new SerializationFailedException("", e);
        }
      }
    };
  }

  @Bean
  public Converter<byte[], ChunkRequest<?>> byteArrayToChunkRequestConverter() {
    return new Converter<byte[], ChunkRequest<?>>() {

      @Override
      public ChunkRequest<?> convert(byte[] source) {
        try {
          return (ChunkRequest<?>) deserializer.deserializeFromByteArray(source);
        } catch (IOException e) {
          throw new DeserializationFailedException("", e);
        }
      }
    };
  }

  @Bean
  public Converter<ChunkResponse, byte[]> chunkResponseToByteArrayConverter() {
    return new Converter<ChunkResponse, byte[]>() {

      @Override
      public byte[] convert(ChunkResponse source) {
        try {
          return serializer.serializeToByteArray(source);
        } catch (IOException e) {
          throw new SerializationFailedException("", e);
        }
      }
    };
  }

  @Bean
  public Converter<byte[], ChunkResponse> byteArrayToChunkResponseConverter() {
    return new Converter<byte[], ChunkResponse>() {

      @Override
      public ChunkResponse convert(byte[] source) {
        try {
          return (ChunkResponse) deserializer.deserializeFromByteArray(source);
        } catch (IOException e) {
          throw new DeserializationFailedException("", e);
        }
      }
    };
  }
}
