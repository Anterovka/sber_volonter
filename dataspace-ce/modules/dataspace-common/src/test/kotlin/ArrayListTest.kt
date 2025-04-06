import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.arrayListOfSize

class ArrayListTest {
    @Nested
    inner class `arrayListOfSize` {
        @Test
        fun `Simple test`() {
            val list = arrayListOfSize(3) { it }
            assertThat(list).isEqualTo(listOf(0, 1, 2))
        }
    }
}
