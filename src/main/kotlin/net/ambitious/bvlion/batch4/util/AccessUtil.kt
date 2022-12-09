package net.ambitious.bvlion.batch4.util

import org.apache.commons.lang3.time.FastDateFormat
import org.apache.http.client.fluent.Request
import org.apache.http.client.fluent.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

object AccessUtil {

  val TOKYO: TimeZone = TimeZone.getTimeZone("Asia/Tokyo")

  fun getNow(format: String): String =
    formatMessage(format, Calendar.getInstance(TOKYO).time)
  fun formatMessage(
    format: String,
    date: Date
  ): String = FastDateFormat.getInstance(format, TOKYO)
    .format(date)

  fun getHoroscopeMessage(): String {
    val today = getNow("yyyy/MM/dd")
    val message = StringBuilder()
    val request = Request.Get("http://api.jugemkey.jp/api/horoscope/free/$today")
    var res: Response? = null
    try {
      res = request.execute()
      val json = JSONObject(String(res.returnContent().asBytes(), StandardCharsets.UTF_8))
      val horoscope: JSONObject = json.getJSONObject("horoscope")
      val todayData: JSONArray = horoscope.getJSONArray(today)
      for (i in 0 until todayData.length()) {
        val `object`: JSONObject = todayData.getJSONObject(i)
        if ("双子座" == `object`.getString("sign")) {
          message.append(today)
          message.append("の双子座の運勢は第")
          message.append(`object`.getInt("rank"))
          message.append("位！\\n")
          message.append(`object`.getString("content"))
          message.append("\\n")
          message.append("ラッキーカラーは「")
          message.append(`object`.getString("color"))
          message.append("」、")
          message.append("ラッキーアイテムは「")
          message.append(`object`.getString("item"))
          message.append("」だよ。\\n\\n")
          message.append("金運：")
          message.append(`object`.getInt("money"))
          message.append("\\n")
          message.append("仕事運：")
          message.append(`object`.getInt("job"))
          message.append("\\n")
          message.append("恋愛運：")
          message.append(`object`.getInt("love"))
          message.append("\\n")
          message.append("総合評価：")
          message.append(`object`.getInt("total"))
          break
        }
      }
    } catch (e: IOException) {
      logger.error("Horoscope Get Error", e)
    } catch (e: JSONException) {
      logger.error("Horoscope Get Error", e)
    } finally {
      res?.discardContent()
    }
    if (message.isEmpty()) {
      message.append(today)
      message.append("の双子座の運勢は取得できませんでした(´･ω･`)")
    }
    return message.toString()
  }

  @JvmStatic
  private val logger = LoggerFactory.getLogger(AccessUtil::class.java)
}