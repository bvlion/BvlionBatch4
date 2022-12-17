package net.ambitious.bvlion.batch4.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface TwitterExclusionWordsMapper {
    @Select("""
    SELECT
      target_word
    FROM
      twitter_exclusion_words
    WHERE
      enable_flag = 1
  """)
    fun getExclusionWords(): List<String>
}