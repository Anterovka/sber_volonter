import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.expr.dsl.expr
import support.exprGrammar
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class ExprGrammarTest {
    @Nested
    inner class `toString` {
        @Nested
        inner class `Value` {
            @Test
            fun `Char`() {
                assertThat(exprGrammar.toString(expr { value('A') })).isEqualTo("'A'")
            }

            @Test
            fun `String`() {
                assertThat(exprGrammar.toString(expr { value("Hello") })).isEqualTo("'Hello'")
                assertThat(exprGrammar.toString(expr { value("I'm a user") })).isEqualTo("'I''m a user'")
            }

            @Test
            fun `Byte`() {
                assertThat(exprGrammar.toString(expr { value(123.toByte()) })).isEqualTo("123")
            }

            @Test
            fun `Short`() {
                assertThat(exprGrammar.toString(expr { value(12345.toShort()) })).isEqualTo("12345")
            }

            @Test
            fun `Int`() {
                assertThat(exprGrammar.toString(expr { value(1234567890) })).isEqualTo("1234567890")
            }

            @Test
            fun `Long`() {
                assertThat(exprGrammar.toString(expr { value(123456789012345L) })).isEqualTo("123456789012345")
            }

            @Test
            fun `Float`() {
                assertThat(exprGrammar.toString(expr { value(123.4567f) })).isEqualTo("123.4567")
            }

            @Test
            fun `Double`() {
                assertThat(exprGrammar.toString(expr { value(1234567890.12345) })).isEqualTo("1.23456789012345E9")
            }

            @Test
            fun `BigDecimal`() {
                assertThat(exprGrammar.toString(expr { value(BigDecimal("1234567890123456789.1234567890123456789")) }))
                    .isEqualTo("1234567890123456789.1234567890123456789")
            }

            @Test
            fun `LocalDate`() {
                assertThat(exprGrammar.toString(expr { value(LocalDate.of(2021, 9, 9)) })).isEqualTo("D2021-09-09")
            }

            @Test
            fun `LocalTime`() {
                assertThat(exprGrammar.toString(expr { value(LocalTime.of(19, 53, 10, 123456000)) })).isEqualTo("T19:53:10.123456")
            }

            @Test
            fun `LocalDateTime`() {
                assertThat(exprGrammar.toString(expr { value(LocalDateTime.of(2021, 9, 9, 19, 53, 10, 123456000)) }))
                    .isEqualTo("D2021-09-09T19:53:10.123456")
            }

            @Test
            fun `OffsetDateTime`() {
                assertThat(
                    exprGrammar.toString(expr { value(OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.of("+06:00"))) }),
                ).isEqualTo("D2021-09-09T19:53:10.123456+06:00")
                assertThat(exprGrammar.toString(expr { value(OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.UTC)) }))
                    .isEqualTo("D2021-09-09T19:53:10.123456Z")
            }

            @Test
            fun `Boolean`() {
                assertThat(exprGrammar.toString(expr { value(true) })).isEqualTo("true")
            }
        }

        @Test
        fun `Current element`() {
            assertThat(exprGrammar.toString(expr { cur })).isEqualTo("it")
        }

        @Test
        fun `Root element`() {
            assertThat(exprGrammar.toString(expr { root })).isEqualTo("root")
        }

        @Test
        fun `Property`() {
            assertThat(exprGrammar.toString(expr { cur["code"] })).isEqualTo("it.code")
        }

        @Test
        fun `Equals`() {
            assertThat(exprGrammar.toString(expr { cur eq cur })).isEqualTo("it==it")
        }

        @Test
        fun `And`() {
            assertThat(exprGrammar.toString(expr { cur and cur })).isEqualTo("it&&it")
        }

        @Test
        fun `Or`() {
            assertThat(exprGrammar.toString(expr { cur or cur })).isEqualTo("it||it")
        }
    }

    @Nested
    inner class `parse` {
        @Nested
        inner class `Value` {
            @Test
            fun `String`() {
                assertThat(exprGrammar.parse("'A'")).isEqualTo(expr { value("A") })
                assertThat(exprGrammar.parse("'Hello'")).isEqualTo(expr { value("Hello") })
                assertThat(exprGrammar.parse("'I''m a user'")).isEqualTo(expr { value("I'm a user") })
            }

            @Test
            fun `Long`() {
                assertThat(exprGrammar.parse("123")).isEqualTo(expr { value(123L) })
                assertThat(exprGrammar.parse("12345")).isEqualTo(expr { value(12345L) })
                assertThat(exprGrammar.parse("1234567890")).isEqualTo(expr { value(1234567890L) })
                assertThat(exprGrammar.parse("123456789012345")).isEqualTo(expr { value(123456789012345L) })
            }

            @Test
            fun `BigDecimal`() {
                assertThat(exprGrammar.parse("123.4567")).isEqualTo(expr { value(BigDecimal("123.4567")) })
                assertThat(exprGrammar.parse("1.23456789012345E9")).isEqualTo(expr { value(BigDecimal("1.23456789012345E9")) })
                assertThat(exprGrammar.parse("1234567890123456789.1234567890123456789"))
                    .isEqualTo(expr { value(BigDecimal("1234567890123456789.1234567890123456789")) })
            }

            @Test
            fun `LocalDate`() {
                assertThat(exprGrammar.parse("D2021-09-09")).isEqualTo(expr { value(LocalDate.of(2021, 9, 9)) })
            }

            @Test
            fun `LocalTime`() {
                assertThat(exprGrammar.parse("T19:53:10.123456")).isEqualTo(expr { value(LocalTime.of(19, 53, 10, 123456000)) })
            }

            @Test
            fun `LocalDateTime`() {
                assertThat(exprGrammar.parse("D2021-09-09T19:53:10.123456"))
                    .isEqualTo(expr { value(LocalDateTime.of(2021, 9, 9, 19, 53, 10, 123456000)) })
            }

            @Test
            fun `OffsetDateTime`() {
                assertThat(exprGrammar.parse("D2021-09-09T19:53:10.123456+06:00"))
                    .isEqualTo(expr { value(OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.of("+06:00"))) })
                assertThat(exprGrammar.parse("D2021-09-09T19:53:10.123456Z"))
                    .isEqualTo(expr { value(OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.UTC)) })
            }

            @Test
            fun `Boolean`() {
                assertThat(exprGrammar.parse("true")).isEqualTo(expr { value(true) })
            }
        }

        @Test
        fun `Current element`() {
            assertThat(exprGrammar.parse("it")).isEqualTo(expr { cur })
            assertThat(exprGrammar.parse("elem")).isEqualTo(expr { cur })
        }

        @Test
        fun `Root element`() {
            assertThat(exprGrammar.parse("root")).isEqualTo(expr { root })
        }

        @Test
        fun `Property`() {
            assertThat(exprGrammar.parse("it.code")).isEqualTo(expr { cur["code"] })
        }

        @Test
        fun `Equals`() {
            assertThat(exprGrammar.parse("it==it")).isEqualTo(expr { cur eq cur })
        }

        @Test
        fun `And`() {
            assertThat(exprGrammar.parse("it&&it")).isEqualTo(expr { cur and cur })
        }

        @Test
        fun `Or`() {
            assertThat(exprGrammar.parse("it||it")).isEqualTo(expr { cur or cur })
        }
    }
}
