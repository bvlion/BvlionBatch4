package net.ambitious.bvlion.batch4.component

import net.ambitious.bvlion.batch4.data.AppParams
import org.apache.http.HttpStatus
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
        |{
        |  "channel": "#$channel",
        |  "as_user": "true",
        |  "username": "$userName",
        |  "text": "$text",
        |  "icon_url": "$iconUrl"
        |}
      """.trimMargin() ,StandardCharsets.UTF_8)
    val url = URL(appParams.slackWebhookUrl)
    val con = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      useCaches = false
      doInput = true
      doOutput = true
    }
    DataOutputStream(con.outputStream).use { wr -> wr.writeBytes(payload) }
    val responseCode = con.responseCode
    BufferedReader(InputStreamReader(
      if (responseCode == HttpStatus.SC_OK) {
        con.inputStream
      } else {
        con.errorStream
      },
      StandardCharsets.UTF_8
    )).use { br ->
      val responseMessage = "Slack Post response is $responseCode, ${br.lines().collect(Collectors.joining("\n"))}"
      """
        |$responseMessage
        |user_name:$userName
        |text:$text
      """.trimMargin().let {
        if (responseCode == HttpStatus.SC_OK) {
          logger.info(it)
        } else {
          logger.warn(it)
          throw IllegalStateException(responseMessage)
        }
      }
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(SlackHttpPost::class.java)
  }
}