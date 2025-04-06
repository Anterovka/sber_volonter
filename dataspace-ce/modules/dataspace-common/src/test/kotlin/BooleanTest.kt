import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.onFalse
import ru.sbertech.dataspace.common.onTrue

class BooleanTest {
    @Test
    fun `onTrue`() {
        var value = 0
        true.onTrue { value += 1 }
        false.onTrue { value += 2 }
        assertThat(value).isEqualTo(1)
    }

    @Test
    fun `onFalse`() {
        var value = 0
        true.onFalse { value += 1 }
        false.onFalse { value += 2 }
        assertThat(value).isEqualTo(2)
    }
}
