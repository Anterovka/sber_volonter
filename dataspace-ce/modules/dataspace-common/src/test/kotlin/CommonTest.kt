import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.uncheckedCast

class CommonTest {
    @Test
    fun `uncheckedCast`() {
        val value: Any = "Test"
        assertThat(value.uncheckedCast<String>()).isEqualTo("Test")
    }
}
