package com.github.andylke.demo.randomuser;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "import-random-user")
public class ImportRandomUserProperties {

  private int chunkSize = 20;

  public int getChunkSize() {
    return chunkSize;
  }

  public void setChunkSize(int chunkSize) {
    this.chunkSize = chunkSize;
  }
}
