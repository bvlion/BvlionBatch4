package net.ambitious.bvlion.batch4.component

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Component
class FirebasePut {
  fun putMessage(
    endPoint: String,
    text: String
  ) {
    val url = URL(endPoint)
    val con: HttpURLConnection = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "PUT"
      useCaches = false
      doInput = true
      doOutput = true
    }

    DataOutputStream(con.outputStream).use { wr ->
      wr.writeBytes(
      "\"${URLEncoder.encode(text, StandardCharsets.UTF_8)}\""
      )
    }

    BufferedReader(InputStreamReader(con.inputStream, StandardCharsets.UTF_8)).use { br ->
      logger.info(br.lines().collect(Collectors.joining("\n")))
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(FirebasePut::class.java)
  }
}