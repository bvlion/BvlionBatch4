package net.ambitious.bvlion.batch4.data

data class MailApi(
  val targetFrom: String,
  val toFolder: String,
  val channel: String?,
  val userName: String?,
  val iconUrl: String?,
  val prefixFormat: String?
)
