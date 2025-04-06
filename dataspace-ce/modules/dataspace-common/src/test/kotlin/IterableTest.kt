import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.forEachSeparated

class IterableTest {
    @Nested
    inner class `forEachSeparated` {
        @Test
        fun `emptyList`() {
            val stringBuilder = StringBuilder()
            emptyList<Int>().forEachSeparated(stringBuilder, ", ") { stringBuilder.append(it) }
            assertThat(stringBuilder.toString()).isEqualTo("")
        }

        @Test
        fun `nonEmptyList`() {
            val stringBuilder = StringBuilder()
            listOf(1, 2, 3).forEachSeparated(stringBuilder, ", ") { stringBuilder.append(it) }
            assertThat(stringBuilder.toString()).isEqualTo("1, 2, 3")
        }
    }
}
