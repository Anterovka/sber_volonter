package sbp.sbt.dataspacecore.security.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AbstractAuthenticationToken
import sbp.sbt.dataspacecore.security.utils.SecurityOperationType
import java.util.Collections
import java.util.EnumMap
import kotlin.collections.HashMap

/**
 * Implementation of the interface {@link org.springframework.security.core.Authentication} specifically for the needs of Dataspace.
 * Does not work with the remaining components of Spring Security!
 *
 * Contains security information for one endpoint call.
 * IMPORTANT!!!
 * The call to the endpoint is not equal to the UoW package. One call can contain several UoW packages.
 * That's why {@link #COMMAND_TYPE} is a ThreadLocal variable - each UoW package has its own command type.
 * Within the execution of the package {@link #COMMAND_TYPE}, it changes depending on the operation being performed.
 *
 * The same story with the rest of the ThreadLocal variables - within the UoW, we use them to store the execution context
 */
open class DataspaceAuthenticationToken: AbstractAuthenticationToken {
    private val serialVersionUID = 1L
    private val COMMAND_TYPE: ThreadLocal<SecurityOperationType> = ThreadLocal<SecurityOperationType>()

    /** The indicator that the call to the database is an in-system call and security predicates should not be applied,
     * for example, when reading security predicates directly from tables, to be used later. */
    var systemRead: Boolean = false
    // TODO rework to create an object directly within the Thread?
    private val RESTRICTIONS: ThreadLocal<MutableMap<SecurityOperationType, MutableMap<String, String>>?> =
        ThreadLocal<MutableMap<SecurityOperationType, MutableMap<String, String>>?>()

    var tenant: String? = null

    /** Used to disable agregatocentricity check. For compatibility support with old multitenancy,
     * used in DsLab  */
    protected var suppressTenantCheck = false
    protected var suppressGqlCheck: Boolean? = null
    protected var supressSecurityDriverCheck: Boolean? = null

    //For identification of the user for non-security purposes
    var identifierForNonSecurity: String? = null

    // parsed attributes of token (jwt)
    var attributes: MutableMap<String, String> = HashMap()
        get() = Collections.unmodifiableMap(field)
        set(value) {
            field.clear()
            field.putAll(value)
        }

    // original token (jwt)
    var token: String? = null

    // original request
    var request: HttpServletRequest? = null

    constructor(
        attributes: Map<String, String>?,
        token: String?
    ) : super(null) {
        fillAttributes(attributes)
        this.token = token
        request = null
    }

    constructor(request: HttpServletRequest) : super(null) {
        this.request = request
    }

    constructor() : this(null, null) {}

    constructor(attributes: Map<String, String>?)
        : this(attributes, null) {}

    private fun fillAttributes(attributes: Map<String, String>?) {
        if (attributes != null && attributes.isNotEmpty()) {
            for ((key, value) in attributes) {
                this.attributes[key] = value
            }
        }
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): String? = null

    fun setCommandType(commandType: SecurityOperationType) {
        COMMAND_TYPE.set(commandType)
    }

    fun getCommandType(): SecurityOperationType? {
        return COMMAND_TYPE.get()
    }

    fun clearCommandType() {
        COMMAND_TYPE.remove()
    }

    fun getRestrictions(): Map<SecurityOperationType, Map<String, String>>? {
        return RESTRICTIONS.get()
    }

    fun addRestriction(tableName: String, operationType: SecurityOperationType, restriction: String) {
        if (RESTRICTIONS.get() == null) {
            val operationAndRestrictionMap: MutableMap<String, String> = HashMap()
            operationAndRestrictionMap[tableName] = restriction
            val restrictionsMap: MutableMap<SecurityOperationType, MutableMap<String, String>> =
                EnumMap(SecurityOperationType::class.java)
            restrictionsMap[operationType] = operationAndRestrictionMap
            RESTRICTIONS.set(restrictionsMap)
        } else {
            val currentRestrictions: MutableMap<SecurityOperationType, MutableMap<String, String>>? = RESTRICTIONS.get()
            currentRestrictions?.computeIfAbsent(operationType) { HashMap() }!!.put(tableName, restriction)
        }
    }

    fun clearRestrictions() {
        RESTRICTIONS.remove()
    }

    /** If False, then AggregateCompareAndStatusCheckerSecurityDriver disables tenant control */
    fun isTenantUsed(): Boolean {
        return java.lang.Boolean.FALSE == suppressTenantCheck
    }

    val isSuppressGqlCheck
        get() = java.lang.Boolean.TRUE == suppressGqlCheck

    fun isSupressSecurityDriverCheck(): Boolean? {
        return java.lang.Boolean.TRUE == supressSecurityDriverCheck
    }

    fun clearSystemRead() {
        systemRead = false;
    }
}
