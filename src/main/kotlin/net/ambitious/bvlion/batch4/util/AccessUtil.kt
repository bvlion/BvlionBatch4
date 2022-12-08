package net.ambitious.bvlion.batch4.util

import java.text.SimpleDateFormat
import java.util.*

object AccessUtil {
  fun getNow(format: String): String = SimpleDateFormat(format).format(Date())
}