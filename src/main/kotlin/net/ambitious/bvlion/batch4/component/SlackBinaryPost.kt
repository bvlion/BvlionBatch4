package net.ambitious.bvlion.batch4.component

import net.ambitious.bvlion.batch4.data.AppParams
import net.ambitious.bvlion.batch4.util.AccessUtil
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors

@Component
class SlackBinaryPost(
  private val slackHttpPost: SlackHttpPost,
  private val appParams: AppParams
) {

  fun post(
    slackChannel: String,
    url: String,
    text: String
  ): Boolean {
    val boundaryBody = "*****${UUID.randomUUID()}*****"
    val fileName = url.split("/").last()

    val con = (URL(SLACK_POST_URL).openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      doOutput = true
      doInput = true
      useCaches = false
      readTimeout = READ_TIMEOUT
      connectTimeout = CONNECTION_TIMEOUT
      setRequestProperty("Connection", "Keep-Alive")
      setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundaryBody")
    }

    val textDataMap = mapOf(
      "channels" to slackChannel,
      "title" to text,
      "token" to appParams.slackToken
    )

    var success = false
    try {
      write(con, boundaryBody, fileName, AccessUtil.getBinaryBytes(url), textDataMap)
      val data = read(con)
      val json = JSONObject(data)
      success = json.getBoolean("ok")
      if (success) {
        logger.info("Slack Binary Post response is $data")
      } else {
        logger.warn(
          """
            Slack Binary Post response is $data
            $fileName
            """.trimIndent()
        )
        slackHttpPost.send(
          "server_api",
          "BOT Twitter",
          "画像送信でエラーが発生しました。\\nファイル名 -> $fileName",
          "http://4s.ambitious-i.net/icon/syobon.png"
        )
      }
    } catch (e: IOException) {
      e.printStackTrace()
    } catch (e: JSONException) {
      e.printStackTrace()
    }
    con.disconnect()
    return success
  }

  /** データの書き込みを行う  */
  @Throws(IOException::class)
  private fun write(
    con: HttpURLConnection,
    boundaryBody: String,
    fileName: String,
    fileData: ByteArray,
    textDataMap: Map<String, String>
  ) = DataOutputStream(con.outputStream).use { request ->
    request.writeBytes(BOUNDARY_HEADER + boundaryBody + CRLF)
    request.writeBytes(
      "Content-Disposition: form-data; name=\"file\";filename=\""
          + fileName + "\"" + CRLF
    )
    request.writeBytes(CRLF)
    request.write(fileData)
    request.writeBytes(CRLF)
    // テキストデータの設定
    textDataMap.forEach { (key, value) ->
      request.writeBytes(BOUNDARY_HEADER + boundaryBody + CRLF)
      request.writeBytes("Content-Disposition: form-data; name=\"$key\"$CRLF")
      request.writeBytes("Content-Type: text/plain$CRLF")
      request.writeBytes(CRLF)
      request.write(value.toByteArray(charset(StandardCharsets.UTF_8.toString())))
      request.writeBytes(CRLF)
    }
    request.writeBytes(BOUNDARY_HEADER + boundaryBody + BOUNDARY_HEADER + CRLF)
  }

  /** レスポンスを読み込む */
  @Throws(IOException::class)
  private fun read(con: HttpURLConnection): String? =
    BufferedReader(InputStreamReader(con.inputStream, StandardCharsets.UTF_8)).use {
      it.lines().collect(Collectors.joining("\n"))
    }

  companion object {
    /** 改行コード  */
    private const val CRLF = "\r\n"
    /** バウンダリのヘッダ（ハイフン2つ）  */
    private const val BOUNDARY_HEADER = "--"
    /** ファイルアップロード先  */
    private const val SLACK_POST_URL = "https://slack.com/api/files.upload"
    /** 読み込みタイムアウト値  */
    private const val READ_TIMEOUT = 10 * 1000
    /** 接続タイムアウト値  */
    private const val CONNECTION_TIMEOUT = 10 * 1000

    @JvmStatic
    private val logger = LoggerFactory.getLogger(SlackBinaryPost::class.java)
  }
}