package ru.sbertech.dataspace.security.utils

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer.Companion.replaceAndReturn
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer.Companion.screenString
import java.util.Collections

class GraphQLStringReplacerTest {
    @Test
    fun screenStringTest() {
        org.junit.jupiter.api.Assertions
            .assertEquals("'123'", screenString("123"))
        org.junit.jupiter.api.Assertions
            .assertEquals("'1''2'", screenString("1'2"))
    }

    @Test
    fun replaceAndReturnTest() {
        val variablesFromGraphQL: MutableMap<String, Any> = HashMap()
        variablesFromGraphQL["str"] = "123"
        variablesFromGraphQL["int"] = 100
        variablesFromGraphQL["obj"] = Collections.singletonMap("inner", "strValue")
        val replaced =
            replaceAndReturn(
                "\${str}=='123'&&\${Integer:int}==100&&\${obj.inner}=='strValue'",
                variablesFromGraphQL,
                emptyMap(),
            )[0]
        org.junit.jupiter.api.Assertions
            .assertEquals("'123'=='123'&&100==100&&'strValue'=='strValue'", replaced)
    }

    @Test
    fun replaceFromBeginningTest() {
        val replaceAndReturn =
            replaceAndReturn("${'$'}{replace1}=='abc'") {
                GraphQLStringReplacer.SingleReplacement("'abc'")
            }
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'abc'=='abc'")
    }

    @Test
    fun replaceAtTheEndTest() {
        val replaceAndReturn =
            replaceAndReturn("'abc'==${'$'}{replace1}") {
                GraphQLStringReplacer.SingleReplacement("'abc'")
            }
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'abc'=='abc'")
    }

    @Test
    fun doNotReplaceScreenedTest() {
        val replaceAndReturn =
            replaceAndReturn("'abc'==\\${'$'}{replace1}") {
                GraphQLStringReplacer.SingleReplacement("'abc'")
            }
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'abc'==\${replace1}")
    }

    @Test
    fun doNotReplaceScreenedButReplaceNonScreenedTest() {
        val replaceAndReturn =
            replaceAndReturn("${'$'}{replace1}==\\${'$'}{replace1}") {
                GraphQLStringReplacer.SingleReplacement("'abc'")
            }
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'abc'==\${replace1}")
    }

    @Test
    fun severalReplacementsTest() {
        val replaceAndReturn =
            replaceAndReturn("${'$'}{replace1}==${'$'}{replace2}||${'$'}{replace2}!=null") {
                GraphQLStringReplacer.SingleReplacement("'$it value'")
            }
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions
            .assertThat(replaceAndReturn[0])
            .isEqualTo("'replace1 value'=='replace2 value'||'replace2 value'!=null")
    }

    @Test
    fun gqlStringObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{replace1}=='value1'",
                Collections.singletonMap("replace1", "string1"),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'string1'=='value1'")
    }

    @Test
    fun gqlLongObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{Long:replace1}==4",
                Collections.singletonMap("replace1", 505L),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("505==4")
    }

    @Test
    fun gqlDateObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{Date:replace1}==D2000-01-01T00:00:00",
                Collections.singletonMap("replace1", "2020-02-02"),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("D2020-02-02T00:00:00==D2000-01-01T00:00:00")
    }

    @Test
    fun gqlLocalDateTimeObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{LocalDateTime:replace1}==D2000-01-01T00:00:00",
                Collections.singletonMap("replace1", "2020-02-22T02:00:22.022"),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("D2020-02-22T02:00:22.022==D2000-01-01T00:00:00")
    }

    @Test
    fun gqlOffsetDateTimeObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{OffsetDateTime:replace1}==D2000-01-01T00:00:00+03:00",
                Collections.singletonMap("replace1", "2020-02-22T02:00:22.022+03:00"),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions
            .assertThat(replaceAndReturn[0])
            .isEqualTo("D2020-02-22T02:00:22.022+03:00==D2000-01-01T00:00:00+03:00")
    }

    @Test
    fun gqlTimeObjectReplacementTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{Time:replace1}==T00:00:00",
                Collections.singletonMap("replace1", "02:00:22.022"),
                Collections.emptyMap(),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("T02:00:22.022==T00:00:00")
    }

    @Test
    fun jwtReplacementsTest() {
        val replaceAndReturn =
            replaceAndReturn(
                "${'$'}{jwt:replace1}=='value1'",
                Collections.emptyMap(),
                Collections.singletonMap("replace1", "'string1'"),
            )
        Assertions.assertThat(replaceAndReturn).hasSize(1)
        Assertions.assertThat(replaceAndReturn[0]).isEqualTo("'string1'=='value1'")
    }
}
