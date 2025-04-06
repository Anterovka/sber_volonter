package ru.sbertech.dataspace.security.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.security.model.dto.Operation

class GraphQLHashHelperTest {
    /**
     * Checking that the algorithm cannot be fooled by passing the fragment name as the field name
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun fragmentNamesAreNotEqualToFieldsTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                id
                            }
                    }
                }                fragment id on ProductParty {
                    id, name, code
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...id
                            }
                    }
                }                fragment id on ProductParty {
                    id, name, code
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Checking that inserting one field works perfectly
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun fragmentInsertionSimpleTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                id
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...id
                            }
                    }
                }                fragment id on ProductParty {
                    id
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Check that two fragments with different names and a different order of fields will be processed equally
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun fragmentWithDifferentNamesInsertionTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...uno
                            }
                    }
                }                fragment uno on ProductParty {
                    id, code
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...dos
                            }
                    }
                }                fragment dos on ProductParty {
                    id, code
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Check that iterating over fields and inserting a fragment with a different field order will be processed equally
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun fragmentInsertionWithSortingTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                id
                                name
                                code
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...frag
                            }
                    }
                }                fragment frag on ProductParty {
                    id, name, code
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Check that iterating over fields and inserting several nested fragments with a different field order will be processed equally
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun innerFragmentInsertionWithSortingTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                id
                                name
                                code
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...frag1
                            }
                    }
                }                fragment frag1 on ProductParty {
                    id, ...frag2
                }                fragment frag2 on ProductParty {
                    name, code
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Check that an object request with a condition and insertion of a similar fragment will be processed equally
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun fragmentWithConditionTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                id
                                name
                                service(cond:"it.code!=null") {
                                    id
                                }
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                ...frag1
                            }
                    }
                }                fragment frag1 on ProductParty {
                    id, name
                    service(cond:"it.code!=null") {
                        id
                    }
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    /**
     * Checking if the value of cond on the link participates in the hash calculation
     */
    @Test
    @Throws(JsonProcessingException::class)
    fun conditionOnLinksTest() {
        val stringMutation1 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                service(cond:"it.code!='123'") {
                                    id
                                }
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation withFragmentUsage(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                service(cond:"it.code!='456'") {
                                    id
                                }
                            }
                    }
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun ignoreOperationNameTest() {
        val stringMutation1 = """mutation ignoreOperationName1(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                service(cond:"it.code!='123'") {
                                    id
                                }
                            }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation ignoreOperationName2(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        g1: getProductParty(id: "ref:p") {
                                service(cond:"it.code!='123'") {
                                    id
                                }
                            }
                    }
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun ignoreTypenamePropertyTest() {
        val mutationWithoutTypenames = """mutation createMember(${"$"}input: _CreateMemberInput!) {
  packet {
    createMember(input: ${"$"}input) {
      ...MemberAttributes
    }
  }
}

fragment MemberAttributes on _E_Member {
  id
  name
  roles{
    elems
  }
}
"""
        val jsonMutationWithoutTypenames: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createMember")
                .put("body", mutationWithoutTypenames)
        val mutationWithTypenames = """mutation createMember(${"$"}input: _CreateMemberInput!) {
  packet {
    createMember(input: ${"$"}input) {
      ...MemberAttributes
      __typename
    }
    __typename
  }
}

fragment MemberAttributes on _E_Member {
  id
  __typename
  name
  roles {
    elems
    __typename
  }
}
"""
        val jsonMutationWithTypenames: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createMember")
                .put("body", mutationWithTypenames)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutationWithoutTypenames, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutationWithTypenames, Operation::class.java)
        Assertions.assertEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun referenceDiffersFromString() {
        val queryWithReference = """query HeroForEpisode(${"$"}ep: Episode!) {
  hero(episode: ${"$"}ep) {
    name
    }
  director(episode: ${"$"}ep) {
    name
    }
}"""
        val jsonQueryWithReference: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "HeroForEpisode")
                .put("body", queryWithReference)
        val queryWithString = """query HeroForEpisode(${"$"}ep: Episode!) {
  hero(episode: "ep") {
    name
    }
  director(episode: ${"$"}ep) {
    name
    }
}"""
        val jsonQueryWithString: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "HeroForEpisode")
                .put("body", queryWithString)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonQueryWithReference, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonQueryWithString, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun inlineFragmentTest() {
        val queryWithInlineFragments = """query HeroForEpisode(${"$"}ep: Episode!) {
  hero(episode: ${"$"}ep) {
    name
    ... on Droid {
      primaryFunction
    }
    ... on Human {
      height
    }
  }
}"""
        val jsonWithFragments: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "HeroForEpisode")
                .put("body", queryWithInlineFragments)
        val queryWithoutInlineFragments = """query HeroForEpisode(${"$"}ep: Episode!) {
  hero(episode: ${"$"}ep) {
    name
  }
}"""
        val jsonWithoutInlineFragments: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "HeroForEpisode")
                .put("body", queryWithoutInlineFragments)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonWithFragments, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonWithoutInlineFragments, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun nonNullTypeIsProperlyUsedInHashCalculationTest() {
        val queryWithIntType = """mutation createPizza(${"$"}radius: Integer!) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithIntType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizza")
                .put("body", queryWithIntType)
        val queryWithLongType = """mutation createPizza(${"$"}radius: Long!) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithLongType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizza")
                .put("body", queryWithLongType)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonWithIntType, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonWithLongType, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun listTypeIsProperlyUsedInHashCalculationTest() {
        val queryWithIntListType = """mutation createPizzas(${"$"}radiuses: [Integer]) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithIntListType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithIntListType)
        val queryWithLongListType = """mutation createPizzas(${"$"}radiuses: [Long]) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithLongListType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithLongListType)
        val queryWithIntType = """mutation createPizzas(${"$"}radiuses: Integer) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithIntType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithIntType)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonWithIntListType, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonWithLongListType, Operation::class.java)
        val operation3 = OBJECT_MAPPER.treeToValue(jsonWithIntType, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation3),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation2),
            GraphQLHashHelper.calculateHash(operation3),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun listTypeWithNonNullTypesIsProperlyUsedInHashCalculationTest() {
        val queryWithIntListType = """mutation createPizzas(${"$"}radiuses: [Integer!]) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithIntListType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithIntListType)
        val queryWithLongListType = """mutation createPizzas(${"$"}radiuses: [Long!]) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithLongListType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithLongListType)
        val queryWithNullableIntListType = """mutation createPizzas(${"$"}radiuses: [Integer]) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithNullableIntListType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizzas")
                .put("body", queryWithNullableIntListType)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonWithIntListType, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonWithLongListType, Operation::class.java)
        val operation3 = OBJECT_MAPPER.treeToValue(jsonWithNullableIntListType, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation3),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation2),
            GraphQLHashHelper.calculateHash(operation3),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun nonNullTypeDefinitionTest() {
        val queryWithNonNullType = """mutation createPizza(${"$"}radius: Integer!) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithNonNullType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizza")
                .put("body", queryWithNonNullType)
        val queryWithNullableType = """mutation createPizza(${"$"}radius: Integer) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithNullableType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizza")
                .put("body", queryWithNullableType)
        val queryWithNullType = """mutation createPizza(${"$"}radius: null) {
  createPizza(radius: ${"$"}radius) {
    id
  }
}"""
        val jsonWithNullType: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createPizza")
                .put("body", queryWithNullType)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonWithNonNullType, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonWithNullableType, Operation::class.java)
        val operation3 = OBJECT_MAPPER.treeToValue(jsonWithNullType, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation3),
        )
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation2),
            GraphQLHashHelper.calculateHash(operation3),
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun testForErrorMessage() {
        // Debug test
        val mutationWithSyntaxError = """mutation createMember() {
  packet {
    createMember {
      id
      __typename
    }
    __typename
  }
}

"""
        val jsonMutationWithSyntaxError: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "createMember")
                .put("body", mutationWithSyntaxError)
        val operation = OBJECT_MAPPER.treeToValue(jsonMutationWithSyntaxError, Operation::class.java)
        Assertions.assertThrows(RuntimeException::class.java) { GraphQLHashHelper.calculateHash(operation) }
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun twoSameNamedOperationsTest() {
        val stringMutation1 = """mutation twoSameNamedOperations(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        createPerformedService(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }
                    }
                }"""
        val jsonMutation1: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation1)
        val stringMutation2 = """mutation twoSameNamedOperations(${"$"}stringDeclaredInMutation:String) {
                    packet {
                        p: createProductParty(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        createPerformedService(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }

                        createPerformedService(input: {
                            code: ${"$"}stringDeclaredInMutation
                        }) {
                            id
                        }
                    }
                }"""
        val jsonMutation2: JsonNode =
            OBJECT_MAPPER
                .createObjectNode()
                .put("name", "withFragmentUsage")
                .put("body", stringMutation2)
        val operation1 = OBJECT_MAPPER.treeToValue(jsonMutation1, Operation::class.java)
        val operation2 = OBJECT_MAPPER.treeToValue(jsonMutation2, Operation::class.java)
        Assertions.assertNotEquals(
            GraphQLHashHelper.calculateHash(operation1),
            GraphQLHashHelper.calculateHash(operation2),
        )
    }

    companion object {
        private val OBJECT_MAPPER = ObjectMapper()
    }
}
