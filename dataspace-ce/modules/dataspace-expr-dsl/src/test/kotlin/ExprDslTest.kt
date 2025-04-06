import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.expr.dsl.expr

class ExprDslTest {
    @Test
    fun `Value`() {
        assertThat(expr { value('A') }).isEqualTo(Expr.Value('A'))
    }

    @Test
    fun `Current element`() {
        assertThat(expr { cur }).isEqualTo(Expr.Cur)
    }

    @Test
    fun `Root element`() {
        assertThat(expr { root }).isEqualTo(Expr.Root)
    }

    @Test
    fun `Property`() {
        assertThat(expr { cur["code"] }).isEqualTo(Expr.Property(Expr.Cur, "code"))
    }

    @Test
    fun `Equals`() {
        assertThat(expr { cur eq cur }).isEqualTo(Expr.Eq(Expr.Cur, Expr.Cur))
    }

    @Test
    fun `And`() {
        assertThat(expr { cur and cur }).isEqualTo(Expr.And(Expr.Cur, Expr.Cur))
    }

    @Test
    fun `Or`() {
        assertThat(expr { cur or cur }).isEqualTo(Expr.Or(Expr.Cur, Expr.Cur))
    }
}
