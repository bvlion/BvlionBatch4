package net.ambitious.bvlion.batch4.component

import net.ambitious.bvlion.batch4.mapper.DatingMapper
import net.ambitious.bvlion.batch4.util.AccessUtil
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class CheckDating(
  private val datingMapper: DatingMapper,
  private val slackHttpPost: SlackHttpPost
) {
  fun postDatingMessage() {
    val message = datingMapper.allDating()
      .map {
        if (it.targetDate.length == 8) {
          try {
            val anniversary = DateUtils.parseDate(it.targetDate, "yyyyMMdd")
            val totalDays = TimeUnit.DAYS.convert(
              Calendar.getInstance(AccessUtil.TOKYO).timeInMillis - anniversary.time, TimeUnit.MILLISECONDS
            ).toInt()
            if (totalDays % 100 == 0) {
              return@map String.format(
                it.message,
                NumberFormat.getNumberInstance().format(totalDays)
              )
            }
          } catch (e: ParseException) {
            logger.warn(it.targetDate, e)
          }
        } else {
          if (AccessUtil.getNow("MMdd") == it.targetDate) {
            return@map it.message
          }
        }
        ""
      }.joinToString("\n")

    if (message.trim().isNotEmpty()) {
      logger.info("DatingBatch:$message")
      slackHttpPost.send(
        "horoscope-api",
        "horoscope-api-" + AccessUtil.getNow("yyyyMMdd"),
        message,
        "https://4s.ambitious-i.net/icon/1434076.png"
      )
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(CheckDating::class.java)
  }
}