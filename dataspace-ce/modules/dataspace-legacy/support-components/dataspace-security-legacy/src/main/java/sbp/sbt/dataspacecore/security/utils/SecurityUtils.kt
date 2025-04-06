package sbp.sbt.dataspacecore.security.utils

import org.springframework.security.core.context.SecurityContextHolder
import sbp.sbt.dataspacecore.configuration.utils.MultitenantMode
import sbp.sbt.dataspacecore.security.common.DataspaceAuthenticationToken
import sbp.sbt.dataspacecore.security.common.DataspacePermitAllToken

class SecurityUtils {
    companion object {
        // TODO temporary constants, should be retrieved from a more appropriate source later on (model)
        @Deprecated("Temporary constants, should be retrieved from a more appropriate place (model)")
        const val SECURITY_ROOT_ENTITY_NAME = "SysRootSecurity"
        // end of temporary constants

        const val GRAPHQL_CONTEXT_SECURE_ENABLE_NAME = "graphql-secure-enable"
        const val GRAPHQL_CONTEXT_REQEUST_NAME = "dspc-request"

        val PERMIT_ALL_TOKEN = DataspacePermitAllToken()
        var IS_TENANT_CHECKER_CREATED = false
        var multitenantMode = MultitenantMode.NONE

        fun getCurrentToken(): DataspaceAuthenticationToken {
            val token = findCurrentToken()
            return token?: throw SecurityException("Token is missing")
        }

        fun findCurrentToken(): DataspaceAuthenticationToken? {
            return SecurityContextHolder.getContext().authentication as? DataspaceAuthenticationToken
        }

        fun getOrCreateCurrentToken(): DataspaceAuthenticationToken {
            var token: DataspaceAuthenticationToken? = findCurrentToken()
            if (token != null) {
                return token
            }
            token = DataspaceAuthenticationToken()
            setToken(token)
            return token
        }

        fun setToken(token: DataspaceAuthenticationToken) {
            // we clear the context, because if we just set the token, it can be overwritten in another thread.
            //            which uses the same context.
            clearContext()
            SecurityContextHolder.getContext().authentication = token
        }

        fun clearContext() {
            SecurityContextHolder.clearContext()
        }

        fun getUserIdentifierForHistory(): String? {
            return findCurrentToken()?.identifierForNonSecurity
        }

        /** This should only be used for internal system calls to disable the control of bez, for example,
         * loading reference books at application startup */
        fun setPermitAllToken() {
            setToken(PERMIT_ALL_TOKEN)
        }

        fun effectiveIsTenantUsed(): Boolean {
            // If no SecurityDriver is created and the updateTenant flag is not set, then
            // when updateTenant SecurityDriver  is not created, but the tenant needs to be saved
            if (!IS_TENANT_CHECKER_CREATED && !(multitenantMode === MultitenantMode.PREPARE)) {
                return false
            }
            val currentToken: DataspaceAuthenticationToken = findCurrentToken() ?: return false
            return currentToken.isTenantUsed()
        }

        /**
         * Returns the tenant from the context of the stream, if any, otherwise null
         * @return tenant или null
         */
        fun effectiveTenantId(): String? {
            if (!effectiveIsTenantUsed()) {
                return null
            }
            val t = 34;
            val currentToken: DataspaceAuthenticationToken = findCurrentToken() ?: throw NullPointerException("SecurityUtils.getCurrentToken() is null...")
            return currentToken.tenant
        }

        /** For a string of the form someString[type], returns the pair &lt;someString, type&gt; */
        fun clearType(str: String): Pair<String, String?> {
            if (str[str.length - 1] != ']') {
                return Pair(str, null)
            }
            val idx = str.lastIndexOf('[')
            require(idx >= 0) { "The line ends with ']', but does not contain '['" }
            val newValue = str.substring(0, idx)
            val type = str.substring(idx + 1, str.length - 1)
            return Pair(newValue, type)
        }

        private enum class CheckBracketsStage {
            NORMAL,
            /* If met the escape symbol */
            ESCAPED,
            /* If inside the user string */
            IN_STRING;
        }

        fun checkBrackets(cond: String): Boolean {
            var stage = CheckBracketsStage.NORMAL
            var counter = 0
            var flag = true
            /* Contains the previous stage in case of encountering an escaping symbol */
            var prevStage = CheckBracketsStage.NORMAL
            for (ch in cond) {
                when (stage) {
                    CheckBracketsStage.NORMAL -> {
                        when (ch) {
                            '(' -> counter++
                            ')' -> {
                                counter--
                                if (counter < 0) return false;
                            }
                            '\\' -> {
                                stage = CheckBracketsStage.ESCAPED
                                prevStage = CheckBracketsStage.NORMAL
                            }
                            '\'' -> stage = CheckBracketsStage.IN_STRING
                            else -> continue
                        }
                    }
                    CheckBracketsStage.ESCAPED -> {
                        stage = prevStage
                    }
                    CheckBracketsStage.IN_STRING -> {
                        when (ch) {
                            '\\' -> {
                                stage = CheckBracketsStage.ESCAPED
                                prevStage = CheckBracketsStage.IN_STRING
                            }
                            '\'' -> stage = CheckBracketsStage.NORMAL
                            else -> continue
                        }
                    }
                }

            }
            // it could be written as return !(counter > 0), but this way is clearer to understand the algorithm
            if (counter > 0) {
                flag = false
            }
            return flag
        }

        fun addSecurityCondition(baseCond: String?, securityCond: String?): String? {
            var resultCond = StringBuilder();
            if (securityCond.isNullOrEmpty()) {
                return baseCond
            }

            if (baseCond != null) {
                if (!checkBrackets(baseCond)) {
                    throw SecurityException("Wrong condition string: $baseCond")
                }
                resultCond.append('(').append(baseCond).append(')').append("&&")
            }
            resultCond.append('(').append(securityCond).append(')')
            return resultCond.toString()
        }
    }
}
