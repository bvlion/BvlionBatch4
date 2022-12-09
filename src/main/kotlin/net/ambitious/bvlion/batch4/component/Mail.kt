package net.ambitious.bvlion.batch4.component

import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPStore
import javax.mail.Flags
import javax.mail.MessagingException
import javax.mail.Session
import net.ambitious.bvlion.batch4.data.AppParams
import net.ambitious.bvlion.batch4.mapper.MailApiMapper
import net.ambitious.bvlion.batch4.util.MailUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class Mail(
  private val appParams: AppParams,
  private val mailApiMapper: MailApiMapper,
  private val slackHttpPost: SlackHttpPost
) {

  private val session = Session.getDefaultInstance(Properties().apply {
    setProperty("mail.store.protocol", "imap")
  })
  
  fun moveAndSlack() {
    val mailApis = mailApiMapper.checkTargetMails()
    try {
      IMAPStore(session, null).use { store ->
        store.connect(appParams.mailHost, 143, appParams.mailUser, appParams.mailPassword)
        try {
          store.getFolder("INBOX").use { folder ->
            folder.open(IMAPFolder.READ_WRITE)
            mailApis.forEach { entity ->
              try {
                // 対象メッセージ一覧取得
                val messages = folder.search(MailUtil.MailAddressTerm(entity.targetFrom)) ?: return@forEach

                // 受信日でソート
                Arrays.sort(messages, MailUtil.MailComparator())

                // 適切にフォーマットしてSlackにPost
                messages
                  .map { MailUtil.setSeenFlag(it) }
                  .forEach {
                    if (entity.channel == null || entity.userName == null ||
                      entity.prefixFormat == null || entity.iconUrl == null) {
                      return@forEach
                    }
                    slackHttpPost.send(
                      entity.channel,
                      MailUtil.getSlackUserName(
                        entity.userName,
                        entity.prefixFormat,
                        it
                      ),
                      MailUtil.getPostMessage(it, folder) ?: "",
                      entity.iconUrl
                    )
                  }

                // INBOX から削除
                folder.copyMessages(messages, store.getFolder("INBOX." + entity.toFolder))
                folder.setFlags(messages, Flags(Flags.Flag.DELETED), true)

                // 削除できていないメールをログ出力
                messages
                  .filter { MailUtil.isNotSet(it) }
                  .map { MailUtil.getNotSetMessage(it) }
                  .forEach { logger.warn(it) }
              } catch (e: MessagingException) {
                logger.warn("JavaMail Each Error", e)
              }
            }
          }
        } catch (e: MessagingException) {
          logger.warn("JavaMail Folder Error", e)
        }
      }
    } catch (e: MessagingException) {
      logger.warn("JavaMail Connect Error", e)
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(Mail::class.java)
  }
}