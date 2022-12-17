package net.ambitious.bvlion.batch4.mapper

import net.ambitious.bvlion.batch4.data.Dating
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface DatingMapper {
  @Select("""
    SELECT
      target_date,
      message 
    FROM
      dating
  """)
  fun allDating(): List<Dating>
}