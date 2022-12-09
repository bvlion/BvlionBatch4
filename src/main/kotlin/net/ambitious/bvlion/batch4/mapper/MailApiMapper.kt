package net.ambitious.bvlion.batch4.mapper

import net.ambitious.bvlion.batch4.data.MailApi
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface MailApiMapper {
  @Select("""
    SELECT
      target_from,
      to_folder,
      channel,
      user_name,
      icon_url,
      prefix_format
    FROM
      mail_api
    WHERE
      enable_flag = 1
  """)
  fun checkTargetMails(): List<MailApi>
}