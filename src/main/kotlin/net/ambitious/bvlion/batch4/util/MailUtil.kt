package net.ambitious.bvlion.batch4.util

import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.UIDFolder
import javax.mail.internet.MimeUtility
import javax.mail.search.AddressStringTerm
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.util.*

object MailUtil {
  fun getNotSetMessage(message: Message): String =
    "Message # $message not deleted"

  fun isNotSet(message: Message): Boolean =
    try {
      !message.isSet(Flags.Flag.DELETED)
    } catch (e: MessagingException) {
      false
    }

  @Throws(MessagingException::class, UnsupportedEncodingException::class)
  fun getSubject(msg: Message): String =
    if (!msg.subject.isNullOrEmpty()) {
      MimeUtility.decodeText(msg.subject)
    } else ""

  @Throws(MessagingException::class, IOException::class)
  fun getBody(message: Message): String {
    if (message.isMimeType("text/plain")) {
      return message.content.toString()
    } else if (message.isMimeType("multipart/*")) {
      val mp = message.content as Multipart
      return mp.getBodyPart(0).content.toString()
    }
    return ""
  }

  fun getPostMessage(msg: Message, folder: Folder): String? =
    try {
      val subject = getSubject(msg)
      val messageId = (folder as UIDFolder).getUID(msg)
      val message = (folder as UIDFolder).getMessageByUID(messageId)
      val body = getBody(message)
      """
        |件名：$subject
        |----------
        |${body.replace("\"", "\\\"")}
        |----------
      """.trimMargin()
    } catch (e: MessagingException) {
      logger.warn("Can't get subject & body", e)
      null
    } catch (e: IOException) {
      logger.warn("Can't get subject & body", e)
      null
    }

  fun getSlackUserName(
    userName: String,
    prefixFormat: String,
    message: Message
  ): String {
    if (prefixFormat.isNotEmpty()) {
      try {
        return (userName + AccessUtil.formatMessage(prefixFormat, message.receivedDate))
      } catch (e: MessagingException) {
        logger.warn("Can't get ReceivedDate", e)
      }
    }
    return userName
  }

  fun setSeenFlag(message: Message): Message {
    try {
      message.setFlag(Flags.Flag.SEEN, true)
    } catch (e: MessagingException) {
      logger.warn("Can't set seen flag", e)
    }
    return message
  }

  class MailComparator : Comparator<Message>, Serializable {
    override fun compare(message1: Message, message2: Message): Int {
      return try {
        message1.receivedDate.compareTo(message2.receivedDate)
      } catch (e: MessagingException) {
        0
      }
    }
  }

  class MailAddressTerm(pattern: String?) : AddressStringTerm(pattern) {
    override fun match(msg: Message): Boolean {
      var addressText = ""
      var subject = ""
      try {
        val address = msg.from
        if (address != null) {
          addressText = MimeUtility.decodeText(address[0].toString())
        }
        subject = getSubject(msg)
      } catch (e: MessagingException) {
        logger.warn("MailAddressTerm Error", e)
      } catch (e: UnsupportedEncodingException) {
        logger.warn("MailAddressTerm Error", e)
      }
      return contains(addressText) || contains(subject)
    }

    private operator fun contains(target: String): Boolean =
      target.lowercase(Locale.getDefault()).contains(getPattern().lowercase(Locale.getDefault()))
  }

  @JvmStatic
  private val logger = LoggerFactory.getLogger(MailUtil::class.java)
}