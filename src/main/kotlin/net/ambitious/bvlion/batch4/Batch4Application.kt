package net.ambitious.bvlion.batch4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Batch4Application

fun main(args: Array<String>) {
  val logOutDir = "LOG_OUT_DIR"
  if (System.getProperty(logOutDir).isNullOrEmpty()) {
    System.setProperty(logOutDir, ".")
  }
  runApplication<Batch4Application>(*args)
}
