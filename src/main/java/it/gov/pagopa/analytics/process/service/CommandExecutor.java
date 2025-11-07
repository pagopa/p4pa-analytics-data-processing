package it.gov.pagopa.analytics.process.service;

import java.io.BufferedReader;
import java.util.function.BiConsumer;

/** Utility to run command on local command line */
public interface CommandExecutor {
  /** It will run {@link #executeSampleActivity(String[], BiConsumer)} logging outputs on console */
  Integer executeSampleActivity(String[] cmd);

  /**
   * It will run a command awaiting its completion.
   * @param cmd the command to execute
   * @param processLogAndErrorConsumer to obtain access on logs during execution
   * @return exitCode
   */
  Integer executeSampleActivity(String[] cmd, BiConsumer<BufferedReader, BufferedReader> processLogAndErrorConsumer);
}
