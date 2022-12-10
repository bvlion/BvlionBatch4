package net.ambitious.bvlion.batch4.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.util.Date

@Mapper
interface TwitterImageMapper {
  @Select("""
    SELECT
      media_url
    FROM
      twitter_image
  """)
  fun getPostedMedia(): List<String>

  @Insert("""
    INSERT INTO twitter_image
      (image_type, media_url, text, posted_date)
    VALUES
      (#{image_type}, #{media_url}, #{text}, NOW())
  """)
  fun savePostedMedia(
    @Param("image_type") imageType: Int,
    @Param("media_url") mediaUrl: String,
    @Param("text") text: String
  )
}