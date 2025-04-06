import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.getOrSet

class MutableListTest {
    @Nested
    inner class `getOrSet` {
        @Test
        fun `Simple test`() {
            val list = arrayListOf("0", null, "2")
            list.getOrSet(0) { "10" }
            list.getOrSet(1) { "11" }
            assertThat(list).isEqualTo(listOf("0", "11", "2"))
        }
    }
}
