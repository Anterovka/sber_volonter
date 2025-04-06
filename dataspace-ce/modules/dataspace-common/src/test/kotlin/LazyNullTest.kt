import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.LazyNull

class LazyNullTest {
    @Test
    fun `Simple test`() {
        val lazy: Lazy<String?> = LazyNull
        assertThat(lazy.isInitialized()).isTrue()
        assertThat(lazy.value).isNull()
    }
}
