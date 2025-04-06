import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.replaceTo

class CharSequenceTest {
    @Nested
    inner class `replaceTo` {
        @Test
        fun `Simple test`() {
            val stringBuilder = StringBuilder()
            "1 aa 2 aa 3 aa 4 aa 5".replaceTo(stringBuilder, "aa", "b", 3, 18)
            assertThat(stringBuilder.toString()).isEqualTo("a 2 b 3 b 4 a")
        }
    }
}
