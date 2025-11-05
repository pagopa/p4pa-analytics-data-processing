package it.gov.pagopa.analytics.process.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandExecutorTest {

  private final CommandExecutor commandExecutor = new CommandExecutorImpl();

  @Test
  void test(){
    Integer exitStatus = commandExecutor.executeSampleActivity(new String[]{"pwd"});

    Assertions.assertEquals(0, exitStatus);
  }
}
