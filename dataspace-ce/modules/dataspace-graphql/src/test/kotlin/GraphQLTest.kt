import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

@Disabled
class GraphQLTest : BaseGraphQLTest(model) {
    @Test
    fun allTypesCreateAndSearchTest() {
        val id = UUID.randomUUID().toString()
        val charValue1 = 'c'
        val charValue2 = 'a'
        val stringValue = "stringValue"
        val textValue = "textValue"
        val byteValue: Byte = 123
        val shortValue: Short = 12345
        val integerValue = 1234567890
        val longValue1 = 1234567890123456789
        val longValue2 = 123456789016788
        val floatValue = 1234.567F
        val doubleValue = 1234567890.012345
        val bigDecimalValue1 = BigDecimal("1234567890123456789.0123456789")
        val bigDecimalValue2 = BigDecimal(1234567456789.01238)
        val dateValue = "2020-02-22T11:49:10.123"
        val localDateValue = "2020-02-22"
        val localDateTimeValue1 = "2020-02-22T11:49:10.123"
        val localDateTimeValue2 = "2020-02-22T11:49:10.124"
        val offsetDateTimeValue = "2023-12-23T08:49:10.123Z"
        val booleanValue = true
        val byteArrayValue = "SGVsbG8h"

        val ownerFirstName = "firstNameValue"
        val ownerLastName = "lastNameValue"
        val ownerGender = "MALE"

        val nickName1 = "nickNameValue1"
        val nickName2 = "nickNameValue2"

        val createQuery =
            """mutation {
                      packet {
                        createTestEntity(input: {
                          id: "$id"
                          pChar : "$charValue1",
                          pString: "$stringValue",
                          pText: "$textValue",
                          pByte: $byteValue
                          pShort: $shortValue
                          pInteger: $integerValue
                          pLong: $longValue1
                          pFloat: $floatValue
                          pDouble: $doubleValue
                          pBigDecimal: $bigDecimalValue1
                          pDate: "$dateValue"
                          pLocalDate: "$localDateValue"
                          pLocalDateTime: "$localDateTimeValue1"
                          pOffsetDateTime: "$offsetDateTimeValue"
                          pBoolean: $booleanValue
                          pByteArray: "$byteArrayValue"
                          owner: {
                            firstName:"$ownerFirstName"
                            lastName: "$ownerLastName"
                            gender: $ownerGender
                            nicknames: ["$nickName1", "$nickName2"]
                          }
                          pCharCollection: ["$charValue1","$charValue2"]
                          pStringCollection: ["$stringValue"]
                          pByteCollection: [$byteValue]
                          pShortCollection: [$shortValue]
                          pIntegerCollection: [$integerValue]
                          pLongCollection: [$longValue1, $longValue2]
                          pFloatCollection: [$floatValue]
                          pDoubleCollection: [$doubleValue]
                          pBigDecimalCollection: [$bigDecimalValue1, $bigDecimalValue2]
                          pDateCollection: ["$dateValue"]
                          pLocalDateCollection: ["$localDateValue"]
                          pLocalDateTimeCollection: ["$localDateTimeValue1", "$localDateTimeValue2"]
                          pOffsetDateTimeCollection: ["$offsetDateTimeValue"]
                        }) {
                          id
                          pChar
                          pString
                          pText
                          pByte
                          pShort
                          pInteger
                          pLong
                          pFloat
                          pDouble
                          pBigDecimal
                          pLocalDate
                          pDate
                          pLocalDateTime
                          pOffsetDateTime
                          pBoolean
                          pByteArray
                          owner {
                            firstName
                            lastName
                            gender
                            nicknames {
                                elems
                            }
                          }
                          pCharCollection {
                            elems
                          }
                          pStringCollection {
                            elems
                          }
                          pByteCollection {
                            elems
                          }
                          pShortCollection {
                            elems
                          }
                          pIntegerCollection {
                            elems
                          }
                          pLongCollection {
                            elems
                          }
                          pFloatCollection {
                            elems
                          }
                          pDoubleCollection {
                            elems
                          }
                          pBigDecimalCollection {
                            elems
                          }
                          pDateCollection {
                            elems
                          }
                          pLocalDateCollection {
                            elems
                          }
                          pLocalDateTimeCollection {
                            elems
                          }
                          pOffsetDateTimeCollection {
                            elems
                          }
                        }
                      }
                    }"""

        val result = executeQuery(createQuery)

        mapOf(
            "/data/packet/createTestEntity/id" to id,
            "/data/packet/createTestEntity/pChar" to charValue1.toString(),
            "/data/packet/createTestEntity/pString" to stringValue,
            "/data/packet/createTestEntity/pText" to textValue,
            "/data/packet/createTestEntity/pByte" to byteValue.toString(),
            "/data/packet/createTestEntity/pShort" to shortValue.toString(),
            "/data/packet/createTestEntity/pInteger" to integerValue.toString(),
            "/data/packet/createTestEntity/pLong" to longValue1.toString(),
            "/data/packet/createTestEntity/pDouble" to doubleValue.toString(),
            "/data/packet/createTestEntity/pFloat" to floatValue.toString(),
            "/data/packet/createTestEntity/pBigDecimal" to "1.23456789012345677E18",
            "/data/packet/createTestEntity/pByteArray" to byteArrayValue,
            "/data/packet/createTestEntity/pBoolean" to booleanValue.toString(),
            "/data/packet/createTestEntity/pDate" to dateValue,
            "/data/packet/createTestEntity/pLocalDate" to localDateValue,
            "/data/packet/createTestEntity/pLocalDateTime" to localDateTimeValue1,
            "/data/packet/createTestEntity/pOffsetDateTime" to offsetDateTimeValue,
            "/data/packet/createTestEntity/owner/firstName" to ownerFirstName,
            "/data/packet/createTestEntity/owner/lastName" to ownerLastName,
            "/data/packet/createTestEntity/owner/gender" to ownerGender,
            "/data/packet/createTestEntity/owner/nicknames/elems/0" to nickName1,
            "/data/packet/createTestEntity/owner/nicknames/elems/1" to nickName2,
            "/data/packet/createTestEntity/pCharCollection/elems/0" to charValue1.toString(),
            "/data/packet/createTestEntity/pCharCollection/elems/1" to charValue2.toString(),
            "/data/packet/createTestEntity/pStringCollection/elems/0" to stringValue,
            "/data/packet/createTestEntity/pByteCollection/elems/0" to byteValue.toString(),
            "/data/packet/createTestEntity/pShortCollection/elems/0" to shortValue.toString(),
            "/data/packet/createTestEntity/pIntegerCollection/elems/0" to integerValue.toString(),
            "/data/packet/createTestEntity/pLongCollection/elems/0" to longValue1.toString(),
            "/data/packet/createTestEntity/pLongCollection/elems/1" to longValue2.toString(),
            "/data/packet/createTestEntity/pDoubleCollection/elems/0" to doubleValue.toString(),
            "/data/packet/createTestEntity/pFloatCollection/elems/0" to floatValue.toString(),
            "/data/packet/createTestEntity/pBigDecimalCollection/elems/0" to "1.23456789012345677E18",
            "/data/packet/createTestEntity/pBigDecimalCollection/elems/1" to "1.2345674567890125E12",
            "/data/packet/createTestEntity/pDateCollection/elems/0" to dateValue,
            "/data/packet/createTestEntity/pLocalDateCollection/elems/0" to localDateValue,
            "/data/packet/createTestEntity/pLocalDateTimeCollection/elems/0" to localDateTimeValue1,
            "/data/packet/createTestEntity/pLocalDateTimeCollection/elems/1" to localDateTimeValue2,
            "/data/packet/createTestEntity/pOffsetDateTimeCollection/elems/0" to offsetDateTimeValue,
        ).check(result)

        val searchQuery =
            """query {
                  searchTestEntity(cond:"it.id == '$id'") {
                    elems {
                      id
                      pChar
                      pString
                      pText
                      pByte
                      pShort
                      pInteger
                      pLong
                      pFloat
                      pDouble
                      pBigDecimal
                      pLocalDate
                      pDate
                      pLocalDateTime
                      pOffsetDateTime
                      pBoolean
                      pByteArray
                      owner {
                        firstName
                        lastName
                        gender
                      }
                      pCharCollection {
                        elems
                      }
                      pStringCollection {
                        elems
                      }
                      pByteCollection {
                        elems
                      }
                      pShortCollection {
                        elems
                      }
                      pIntegerCollection {
                        elems
                      }
                      pLongCollection {
                        elems
                      }
                      pFloatCollection {
                        elems
                      }
                      pDoubleCollection {
                        elems
                      }
                      pBigDecimalCollection {
                        elems
                      }
                      pDateCollection {
                        elems
                      }
                      pLocalDateCollection {
                        elems
                      }
                      pLocalDateTimeCollection {
                        elems
                      }
                      pOffsetDateTimeCollection {
                        elems
                      }
                    }
                  }
                }"""

        val searchResult = executeQuery(searchQuery)

        mapOf(
            "/data/searchTestEntity/elems/0/id" to id,
            "/data/searchTestEntity/elems/0/pChar" to charValue1.toString(),
            "/data/searchTestEntity/elems/0/pString" to stringValue,
            "/data/searchTestEntity/elems/0/pText" to textValue,
            "/data/searchTestEntity/elems/0/pByte" to byteValue.toString(),
            "/data/searchTestEntity/elems/0/pShort" to shortValue.toString(),
            "/data/searchTestEntity/elems/0/pInteger" to integerValue.toString(),
            "/data/searchTestEntity/elems/0/pLong" to longValue1.toString(),
            "/data/searchTestEntity/elems/0/pDouble" to doubleValue.toString(),
            "/data/searchTestEntity/elems/0/pFloat" to floatValue.toString(),
            "/data/searchTestEntity/elems/0/pBigDecimal" to "1.23456789012345677E18",
            "/data/searchTestEntity/elems/0/pByteArray" to byteArrayValue,
            "/data/searchTestEntity/elems/0/pBoolean" to booleanValue.toString(),
            "/data/searchTestEntity/elems/0/pDate" to dateValue,
            "/data/searchTestEntity/elems/0/pLocalDate" to localDateValue,
            "/data/searchTestEntity/elems/0/pLocalDateTime" to localDateTimeValue1,
            "/data/searchTestEntity/elems/0/pOffsetDateTime" to offsetDateTimeValue,
            "/data/searchTestEntity/elems/0/owner/firstName" to ownerFirstName,
            "/data/searchTestEntity/elems/0/owner/lastName" to ownerLastName,
            "/data/searchTestEntity/elems/0/owner/gender" to ownerGender,
            "/data/searchTestEntity/elems/0/pCharCollection/elems/0" to charValue1.toString(),
            "/data/searchTestEntity/elems/0/pCharCollection/elems/1" to charValue2.toString(),
            "/data/searchTestEntity/elems/0/pStringCollection/elems/0" to stringValue,
            "/data/searchTestEntity/elems/0/pByteCollection/elems/0" to byteValue.toString(),
            "/data/searchTestEntity/elems/0/pShortCollection/elems/0" to shortValue.toString(),
            "/data/searchTestEntity/elems/0/pIntegerCollection/elems/0" to integerValue.toString(),
            "/data/searchTestEntity/elems/0/pLongCollection/elems/0" to longValue1.toString(),
            "/data/searchTestEntity/elems/0/pLongCollection/elems/1" to longValue2.toString(),
            "/data/searchTestEntity/elems/0/pDoubleCollection/elems/0" to doubleValue.toString(),
            "/data/searchTestEntity/elems/0/pFloatCollection/elems/0" to floatValue.toString(),
            "/data/searchTestEntity/elems/0/pBigDecimalCollection/elems/0" to "1.23456789012345677E18",
            "/data/searchTestEntity/elems/0/pBigDecimalCollection/elems/1" to "1.2345674567890125E12",
            "/data/searchTestEntity/elems/0/pDateCollection/elems/0" to dateValue,
            "/data/searchTestEntity/elems/0/pLocalDateCollection/elems/0" to localDateValue,
            "/data/searchTestEntity/elems/0/pLocalDateTimeCollection/elems/0" to localDateTimeValue1,
            "/data/searchTestEntity/elems/0/pLocalDateTimeCollection/elems/1" to localDateTimeValue2,
            "/data/searchTestEntity/elems/0/pOffsetDateTimeCollection/elems/0" to offsetDateTimeValue,
        ).check(searchResult)
    }

    @Test
    fun allTypesNullCreateTest() {
        val createQuery = """mutation m {
                                  packet {
                                    createTestEntity(input: {
                                      pChar : null,
                                      pString: null,
                                      pText: null,
                                      pByte: null
                                      pShort: null
                                      pInteger: null
                                      pLong: null
                                      pFloat: null
                                      pDouble: null
                                      pBigDecimal: null
                                      pDate: null
                                      pLocalDate: null
                                      pLocalDateTime: null
                                      pOffsetDateTime: null
                                      pBoolean: null
                                      pByteArray: null
                                      owner: {
                                        firstName: "firstName"
                                        lastName: null
                                        gender: null
                                      }
                                      pCharCollection: null
                                      pStringCollection: null
                                      pByteCollection: null
                                      pShortCollection: null
                                      pIntegerCollection: null
                                      pLongCollection: null
                                      pFloatCollection: null
                                      pDoubleCollection: null
                                      pBigDecimalCollection: null
                                      pDateCollection: null
                                      pLocalDateCollection: null
                                      pLocalDateTimeCollection: null
                                      pOffsetDateTimeCollection: null
                                    }) {
                                      id
                                      pChar
                                      pString
                                      pText
                                      pByte
                                      pShort
                                      pInteger
                                      pLong
                                      pFloat
                                      pDouble
                                      pBigDecimal
                                      pLocalDate
                                      pDate
                                      pLocalDateTime
                                      pOffsetDateTime
                                      pBoolean
                                      pByteArray
                                      owner {
                                        firstName
                                        lastName
                                        gender
                                      }
                                      pCharCollection {
                                        elems
                                      }
                                      pStringCollection {
                                        elems
                                      }
                                      pByteCollection {
                                        elems
                                      }
                                      pShortCollection {
                                        elems
                                      }
                                      pIntegerCollection {
                                        elems
                                      }
                                      pLongCollection {
                                        elems
                                      }
                                      pFloatCollection {
                                        elems
                                      }
                                      pDoubleCollection {
                                        elems
                                      }
                                      pBigDecimalCollection {
                                        elems
                                      }
                                      pDateCollection {
                                        elems
                                      }
                                      pLocalDateCollection {
                                        elems
                                      }
                                      pLocalDateTimeCollection {
                                        elems
                                      }
                                      pOffsetDateTimeCollection {
                                        elems
                                      }
                                    }
                                  }
                                }"""

        val result = executeQuery(createQuery)

        mapOf(
            "/data/packet/createTestEntity/pChar" to "null",
            "/data/packet/createTestEntity/pString" to "null",
            "/data/packet/createTestEntity/pText" to "null",
            "/data/packet/createTestEntity/pByte" to "null",
            "/data/packet/createTestEntity/pShort" to "null",
            "/data/packet/createTestEntity/pInteger" to "null",
            "/data/packet/createTestEntity/pLong" to "null",
            "/data/packet/createTestEntity/pDouble" to "null",
            "/data/packet/createTestEntity/pFloat" to "null",
            "/data/packet/createTestEntity/pBigDecimal" to "null",
            "/data/packet/createTestEntity/pByteArray" to "null",
            "/data/packet/createTestEntity/pBoolean" to "null",
            "/data/packet/createTestEntity/pDate" to "null",
            "/data/packet/createTestEntity/pLocalDate" to "null",
            "/data/packet/createTestEntity/pLocalDateTime" to "null",
            "/data/packet/createTestEntity/pOffsetDateTime" to "null",
            "/data/packet/createTestEntity/owner/firstName" to "firstName",
            "/data/packet/createTestEntity/owner/lastName" to "null",
            "/data/packet/createTestEntity/owner/gender" to "null",
        ).check(result)
    }

    @Test
    fun refTest() {
        val stringValue1 = "stringValue1"
        val stringValue2 = "stringValue2"

        val ownerFirstName = "firstNameValue"

        val createQuery =
            """mutation {
                      packet {
                        create1: createTestEntity(input: {
                          pString: "$stringValue1",
                          owner: {
                            firstName:"$ownerFirstName"
                          }
                          pStringCollection: ["$stringValue1", "$stringValue2"]
                        }) {
                          id
                          pStringAlias: pString
                          owner {
                            firstName
                          }
                          pStringCollection {
                            elems
                          }
                        }
                      create2: createTestEntity(input: {
                          pString: "ref:create1/pStringAlias",
                          owner: {
                            firstName:"ref:create1/owner/firstName"
                          }
                          pStringCollection: ["ref:create1/pStringCollection/1"]
                        }) {
                          id
                          pString
                          owner {
                            firstName
                          }
                          pStringCollection {
                            elems
                          }
                        }
                      }
                    }"""

        val result = executeQuery(createQuery)

        mapOf(
            "/data/packet/create1/pStringAlias" to stringValue1,
            "/data/packet/create1/owner/firstName" to ownerFirstName,
            "/data/packet/create1/pStringCollection/elems/0" to stringValue1,
            "/data/packet/create1/pStringCollection/elems/1" to stringValue2,
            "/data/packet/create2/pString" to stringValue1,
            "/data/packet/create2/owner/firstName" to ownerFirstName,
            "/data/packet/create2/pStringCollection/elems/0" to stringValue2,
        ).check(result)
    }

    @Test
    fun dependsOnByGetTest() {
        val id = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val query = """mutation {
                          packet {
                            get1: getTestEntity(id: "nonExistent" failOnEmpty: false) {
                              id
                            }
                            create1: createTestEntity(input: {
                              id: "$id"
                              pString: "$stringValue",
                              owner: {
                                firstName:"$firstNameValue"
                              }
                            }) @dependsOnByGet(commandId:"get1", dependency: NOT_EXISTS) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                            get2: getTestEntity(id: "ref:create1") {
                              id
                              pString
                            }
                            create2: createTestEntity(input: {
                              pString: "stringValue",
                              owner: {
                                firstName:"firstName"
                              }
                            }) @dependsOnByGet(commandId:"get1", dependency: EXISTS) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                            create3: createTestEntity(input: {
                              pString: "$stringValue",
                              owner: {
                                firstName:"$firstNameValue"
                              }
                            }) @dependsOnByGet(commandId:"get2", dependency: EXISTS)
                                    @dependsOnByGet(commandId:"get1", dependency: NOT_EXISTS) {
                              pString
                              owner {
                                firstName
                              }
                            }
                            create4: createTestEntity(input: {
                              pString: "stringValue",
                              owner: {
                                firstName:"firstName"
                              }
                            }) @dependsOnByGet(commandId:"get2", dependency: EXISTS)
                                    @dependsOnByGet(commandId:"get1", dependency: EXISTS) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                          }
                        }"""

        val result = executeQuery(query)

        mapOf(
            "/data/packet/get1" to "null",
            "/data/packet/create1/id" to id,
            "/data/packet/create1/pString" to stringValue,
            "/data/packet/create1/owner/firstName" to firstNameValue,
            "/data/packet/get2/id" to id,
            "/data/packet/get2/pString" to stringValue,
            "/data/packet/create2" to "null",
            "/data/packet/create3/pString" to stringValue,
            "/data/packet/create3/owner/firstName" to firstNameValue,
            "/data/packet/create4" to "null",
        ).check(result)
    }

    @Test
    fun getsTest() {
        val id = UUID.randomUUID().toString()
        val stringValue1 = "stringValue1"
        val stringValue2 = "stringValue2"
        val firstNameValue = "firstNameValue"

        val query = """
            mutation {
              packet {
                create1: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue1"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                    pStringCollection: ["$stringValue1", "$stringValue2"]
                  }
                ) {
                  objectId: id
                  stringAlias: pString
                  owner {
                    firstName
                  }
                  stringCollectionAlias: pStringCollection(cond: "it=='$stringValue2'") {
                    elems
                  }
                }
                get1: getTestEntity(id: "find:it.id=='$id'") {
                  id
                  pString
                  pStringCollection {
                    elems
                  }
                }
                get2: getTestEntity(id: "ref:get1/id") {
                  id
                  pString
                  pStringCollection {
                    elems
                  }
                }
                get3: getTestEntity(id: "ref:get1") {
                  id
                  pString
                  pStringCollection {
                    elems
                  }
                }
                get4: getTestEntity(id: "$id") {
                  id
                  pString
                  pStringCollection(cond: "it=='$stringValue1'") {
                    elems
                  }
                }
                get5: getTestEntity(id: "nonExistentId", failOnEmpty: false) {
                  id
                }
                get6: getTestEntity(id: "find:it.id=='nonExistentId'") {
                  id
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create1/objectId" to id,
            "/data/packet/create1/stringAlias" to stringValue1,
            "/data/packet/create1/owner/firstName" to firstNameValue,
            "/data/packet/create1/stringCollectionAlias/elems/0" to stringValue2,
            "/data/packet/get1/id" to id,
            "/data/packet/get1/pString" to stringValue1,
            "/data/packet/get1/pStringCollection/elems/0" to stringValue1,
            "/data/packet/get1/pStringCollection/elems/1" to stringValue2,
            "/data/packet/get2/id" to id,
            "/data/packet/get2/pString" to stringValue1,
            "/data/packet/get2/pStringCollection/elems/0" to stringValue1,
            "/data/packet/get2/pStringCollection/elems/1" to stringValue2,
            "/data/packet/get3/id" to id,
            "/data/packet/get3/pString" to stringValue1,
            "/data/packet/get3/pStringCollection/elems/0" to stringValue1,
            "/data/packet/get3/pStringCollection/elems/1" to stringValue2,
            "/data/packet/get4/id" to id,
            "/data/packet/get4/pString" to stringValue1,
            "/data/packet/get4/pStringCollection/elems/0" to stringValue1,
            "/data/packet/get5" to "null",
            "/data/packet/get6" to "null",
        ).check(result)
    }

    @Test
    fun updateTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val stringValueUpd = "stringValueUpd"
        val firstNameValueUpd = "firstNameValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  owner {
                    firstName
                  }
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                    owner: {
                      firstName: "$firstNameValueUpd"
                    }
                  }
                ) {
                  id
                  pString
                  owner {
                    firstName
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/pString" to stringValue,
            "/data/packet/create/owner/firstName" to firstNameValue,
            "/data/packet/update/pString" to stringValueUpd,
            "/data/packet/update/owner/firstName" to firstNameValueUpd,
        ).check(result)
    }
}
