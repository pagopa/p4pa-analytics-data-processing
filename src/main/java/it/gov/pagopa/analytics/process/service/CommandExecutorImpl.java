package it.gov.pagopa.analytics.process.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

@Service
@Slf4j
public class CommandExecutorImpl implements CommandExecutor {

  @Override
  public Integer executeCommandActivity(String[] cmd) {
    return executeCommandActivity(cmd, this::logProcessOutputs);
  }

  @Override
  public Integer executeCommandActivity(String[] cmd, BiConsumer<BufferedReader, BufferedReader> processLogAndErrorConsumer) {
    try {
      Process p = Runtime.getRuntime().exec(cmd);
      log.info("Executed command '{}' with pid {}", String.join(" ", cmd), p.pid());
      try(
        BufferedReader processLogs = p.inputReader(StandardCharsets.UTF_8);
        BufferedReader errorLogs = p.errorReader(StandardCharsets.UTF_8)
        ) {
        while (p.isAlive()) {
          processLogAndErrorConsumer.accept(processLogs, errorLogs);
          //noinspection BusyWait: needed in order to check process completion
          Thread.sleep(100);
        }
        processLogAndErrorConsumer.accept(processLogs, errorLogs);
      }
      log.debug("Process terminated with exit status {}", p.exitValue());
      return p.exitValue();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot execute command", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Thread interrupted before process completion", e);
    }
  }

  private void logProcessOutputs(BufferedReader processLogs, BufferedReader errorLogs) {
    processLogs.lines()
      .forEach(l ->  log.info("[COMMAND] - {}", l));
    errorLogs.lines()
      .forEach(l ->  log.error("[COMMAND] - {}", l));
  }
}
