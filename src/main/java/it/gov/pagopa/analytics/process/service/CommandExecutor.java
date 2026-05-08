package it.gov.pagopa.analytics.process.service;

import java.io.BufferedReader;
import java.util.function.BiConsumer;

/** Utility to run command on local command line */
public interface CommandExecutor {
  /** It will run {@link #executeCommandActivity(String[], BiConsumer)} logging outputs on console */
  Integer executeCommandActivity(String[] cmd);

  /**
   * It will run a command awaiting its completion.
   * @param cmd the command to execute
   * @param processLogAndErrorConsumer to obtain access on logs during execution
   * @return exitCode
   */
  Integer executeCommandActivity(String[] cmd, BiConsumer<BufferedReader, BufferedReader> processLogAndErrorConsumer);
}
