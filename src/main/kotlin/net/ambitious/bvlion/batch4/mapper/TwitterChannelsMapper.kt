package net.ambitious.bvlion.batch4.mapper

import net.ambitious.bvlion.batch4.data.TwitterChannels
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

@Mapper
interface TwitterChannelsMapper {
    @Select("""
      SELECT
        image_type,
        slack_channel,
        search_value
      FROM
        twitter_channels
      WHERE
            enable_flag = 1
        AND jorudan_flag = #{jorudan_flag}
    """)
    fun getSearchTarget(@Param("jorudan_flag") jorudanFlag: Int): List<TwitterChannels>
}