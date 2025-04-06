import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.common.SubCharSequence

class SubCharSequenceTest {
    @Test
    fun `Simple test`() {
        val subCharSequence = SubCharSequence("0123456789")

        assertThat(subCharSequence.length).isEqualTo(10)
        assertThat(subCharSequence[5]).isEqualTo('5')
        assertThat(subCharSequence.toString()).isEqualTo("0123456789")

        val subCharSequence2 = subCharSequence.subSequence(2, 8)
        assertThat(subCharSequence2.length).isEqualTo(6)
        assertThat(subCharSequence2[3]).isEqualTo('5')
        assertThat(subCharSequence2.toString()).isEqualTo("234567")
    }
}
