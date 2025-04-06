package ru.sbertech.dataspace.security;

import org.junit.jupiter.api.Test;
import ru.sbertech.dataspace.security.utils.GraphQLStringReplacer;
import sbp.sbt.dataspacecore.security.common.DataspaceAuthenticationToken;
import sbp.sbt.dataspacecore.security.utils.SecurityOperationType;
import sbp.sbt.dataspacecore.security.utils.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarTest {

    @Test
    void dataspaceAuthenticationTokenTest() {
        final String TEST_USER = "testUser";
        DataspaceAuthenticationToken token = new DataspaceAuthenticationToken();
// We check that by default the token is considered unauthenticated
        assertThat(token.isAuthenticated()).isFalse();
        token.setAuthenticated(true);
        assertThat(token.isAuthenticated()).isTrue();
        SecurityOperationType operationType = SecurityOperationType.UPDATE_BEFORE;
        token.setCommandType(operationType);
        assertThat(token.getCommandType()).isEqualTo(operationType);
        token.clearCommandType();
        assertThat(token.getCommandType()).isNull();
        token.setSystemRead(true);
        assertThat(token.getSystemRead()).isTrue();
        token.clearSystemRead();
        assertThat(token.getSystemRead()).isFalse();
        token.addRestriction("someTable", SecurityOperationType.UPDATE_BEFORE, "it.code $like 'abc%'");
        assertThat(token.getRestrictions()).isNotEmpty();
        token.clearRestrictions();
        assertThat(token.getRestrictions()).isNull();
        token.eraseCredentials();
        assertThat(token.getCredentials()).isNull();
    }

    @Test
    void graphQLStringReplacerTest() {
        DataspaceAuthenticationToken auth = SecurityUtils.Companion.getOrCreateCurrentToken();
        String str = GraphQLStringReplacer.Companion.screenString("123'321");
        assertThat(str).isEqualTo("'123''321'");
        Map<String, Object> vars1 = new HashMap<>();
        vars1.put("code", "abc%");
        Map<String, String> vars2 = new HashMap<>();
        vars2.put("e-mail", "mail");
        str = GraphQLStringReplacer.Companion.replaceAndReturn("it.code $like ${code}", vars1, vars2).get(0);
        assertThat(str).isEqualTo("it.code $like 'abc%'");
        str = GraphQLStringReplacer.Companion.replaceAndReturn("it.code $like '${jwt:e-mail}%'", vars1, vars2).get(0);
        assertThat(str).isEqualTo("it.code $like 'mail%'");
    }
}
