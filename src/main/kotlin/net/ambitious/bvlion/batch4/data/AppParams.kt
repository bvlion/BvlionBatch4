package net.ambitious.bvlion.batch4.data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app")
class AppParams {
  lateinit var speakTextUrl: String
  lateinit var slackWebhookUrl: String

  lateinit var mailHost: String
  lateinit var mailUser: String
  lateinit var mailPassword: String
}