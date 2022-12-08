package net.ambitious.bvlion.batch4.util

import net.ambitious.bvlion.batch4.data.AppParams
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Component
class SlackHttpPost(private val appParams: AppParams) {

  @Throws(IOException::class)
  fun send(
    channel: String,
    userName: String,
    text: String,
    iconUrl: String
  ) {
    val payload = "payload=" + URLEncoder.encode("""
        {
          "channel": "#$channel",
          "as_user": "true",
          "username": "$userName",
          "text": "$text",
          "icon_url": "$iconUrl"
        }
      """.trimIndent() ,StandardCharsets.UTF_8)
    val url = URL(appParams.slackWebhookUrl)
    val con = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      useCaches = false
      doInput = true
      doOutput = true
    }
    DataOutputStream(con.outputStream).use { wr -> wr.writeBytes(payload) }
    BufferedReader(InputStreamReader(con.inputStream, StandardCharsets.UTF_8)).use { br ->
      logger.info("""
            Slack Post response is ${br.lines().collect(Collectors.joining("\n"))}
            user_name:$userName
            text:$text
            """.trimIndent()
      )
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(SlackHttpPost::class.java)
  }
}