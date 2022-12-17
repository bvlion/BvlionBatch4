package net.ambitious.bvlion.batch4.component

import net.ambitious.bvlion.batch4.mapper.TwitterChannelsMapper
import net.ambitious.bvlion.batch4.mapper.TwitterExclusionWordsMapper
import net.ambitious.bvlion.batch4.mapper.TwitterImageMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.Twitter

@Component
class TwitterSearch(
  private val twitterChannelsMapper: TwitterChannelsMapper,
  private val twitterImageMapper: TwitterImageMapper,
  private val twitterExclusionWordsMapper: TwitterExclusionWordsMapper,
  private val slackBinaryPost: SlackBinaryPost
) {
  fun postMediaSlack() {
    val postedMedia = twitterImageMapper.getPostedMedia()
    val exclusionWords = twitterExclusionWordsMapper.getExclusionWords()
    val twitter = Twitter.getInstance()
    twitterChannelsMapper.getSearchTarget(0).forEach { channel ->
      twitter.v1().timelines().getUserTimeline(channel.searchValue).forEach { status ->
        status.mediaEntities
          .filter { !postedMedia.contains(it.mediaURL) }
          .filter { exclusionWords.none { status.text.contains(it) } }
          .filter { !status.isRetweet }
          .filter { slackBinaryPost.post(channel.slackChannel, it.mediaURL, status.text) }
          .forEach {
            twitterImageMapper.savePostedMedia(channel.imageType, it.mediaURL, status.text)
            logger.info("Posted Image ${status.text}")
          }
      }
    }
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(TwitterSearch::class.java)
  }
}