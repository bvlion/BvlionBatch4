package net.ambitious.bvlion.batch4.controller

import net.ambitious.bvlion.batch4.data.AppParams
import net.ambitious.bvlion.batch4.util.AccessUtil
import net.ambitious.bvlion.batch4.util.FirebasePut
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SpeakController(
  private val firebasePut: FirebasePut,
  private val appParams: AppParams
) {

  @PutMapping("/speak-time")
  fun speakTime(): String {
    val text = "時刻は${AccessUtil.getNow("HH:mm")}です"
    val message = "$text … ${AccessUtil.getNow("yyyyMMddHHmmss")} … 45 … home"

    firebasePut.putMessage(appParams.speakTextUrl, message)

    return text
  }
}