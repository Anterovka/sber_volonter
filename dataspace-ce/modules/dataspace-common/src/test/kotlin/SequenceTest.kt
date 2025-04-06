import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.generateSequence

class SequenceTest {
    @Test
    fun `generateSequence`() {
        assertThat(generateSequence("Test").toList()).isEqualTo(listOf("Test"))
        assertThat(generateSequence(null).toList()).isEmpty()
    }
}
