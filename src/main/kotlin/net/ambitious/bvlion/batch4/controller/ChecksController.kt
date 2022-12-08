package net.ambitious.bvlion.batch4.controller

import net.ambitious.bvlion.batch4.util.AccessUtil
import net.ambitious.bvlion.batch4.util.SlackHttpPost
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChecksController(private val slackHttpPost: SlackHttpPost) {

  @PutMapping("/horoscope")
  fun horoscope() {
    slackHttpPost.send(
      "horoscope-api",
      "horoscope-api-" + AccessUtil.getNow("yyyyMMdd"),
      AccessUtil.getHoroscopeMessage(),
      "https://4s.ambitious-i.net/icon/1434076.png"
    )
  }
}