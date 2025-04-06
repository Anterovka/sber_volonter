package sbp.com.sbt.dataspace.graphqlschema;

import graphql.parser.ParserOptions;


public class GraphQLParserSettings {
    Integer maxCharacters = ParserOptions.MAX_QUERY_CHARACTERS;
    Integer maxTokens = ParserOptions.MAX_QUERY_TOKENS;

    public Integer getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(Integer maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}
