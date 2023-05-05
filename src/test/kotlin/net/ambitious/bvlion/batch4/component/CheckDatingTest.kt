package net.ambitious.bvlion.batch4.component

import io.mockk.every
import io.mockk.mockk
import net.ambitious.bvlion.batch4.data.Dating
import net.ambitious.bvlion.batch4.mapper.DatingMapper
import net.ambitious.bvlion.batch4.util.AccessUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.NumberFormat
import java.util.*

class CheckDatingTest {

    private lateinit var datingMapper: DatingMapper
    private lateinit var slackHttpPost: SlackHttpPost

    @BeforeEach
    fun before() {
        datingMapper = mockk {
            every { allDating() } returns listOf(
                Dating("20130320", "date1 %s"),
                Dating("20190320", "date2 %s"),
                Dating("0320", "date3"),
                Dating("0319", "date4")
            )
        }
        slackHttpPost = mockk {
            every { send(any(), any(), any(), any()) } returns Unit
        }
    }

    @Test
    fun postDatingMessageTest2013_100s() {
        val checkDating = CheckDating(datingMapper, slackHttpPost)
        repeat(100) {
            val date = Calendar.getInstance(AccessUtil.TOKYO).apply {
                set(2013, 2, 20)
                add(Calendar.DAY_OF_MONTH, it * 100)
            }.timeInMillis
            val message = checkDating.postDatingMessage(date)
            assertThat(message).contains("date1 " + NumberFormat.getNumberInstance().format(it * 100))
        }
    }

    @Test
    fun postDatingMessageTest2019_100s() {
        val checkDating = CheckDating(datingMapper, slackHttpPost)
        repeat(100) {
            val date = Calendar.getInstance(AccessUtil.TOKYO).apply {
                set(2019, 2, 20)
                add(Calendar.DAY_OF_MONTH, it * 100)
            }.timeInMillis
            val message = checkDating.postDatingMessage(date)
            assertThat(message).contains("date2 " + NumberFormat.getNumberInstance().format(it * 100))
        }
    }

    @Test
    fun postDatingMessageTest0320() {
        val checkDating = CheckDating(datingMapper, slackHttpPost)
        repeat(100) {
            val date = Calendar.getInstance(AccessUtil.TOKYO).apply {
                set(2013 + it, 2, 20)
            }.timeInMillis
            val message = checkDating.postDatingMessage(date)
            assertThat(message).contains("date3")
        }
    }

    @Test
    fun postDatingMessageTest0319() {
        val checkDating = CheckDating(datingMapper, slackHttpPost)
        repeat(100) {
            val date = Calendar.getInstance(AccessUtil.TOKYO).apply {
                set(2013 + it, 2, 19)
            }.timeInMillis
            val message = checkDating.postDatingMessage(date)
            assertThat(message).contains("date4")
        }
    }
}