package com.sbt.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DictionaryCheckParams {

    private List<String> warnings;
    private boolean exceptionOnDictionaryReference;

    public static class Builder {

        private DictionaryCheckParams instance;

        private Builder() {
            this.instance = new DictionaryCheckParams();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setWarnings(List<String> warnings) {
            instance.setWarnings(warnings);
            return this;
        }

        public Builder setExceptionOnDictionaryReference(boolean exceptionOnDictionaryReference) {
            instance.setExceptionOnDictionaryReference(exceptionOnDictionaryReference);
            return this;
        }

        public DictionaryCheckParams build() {
            return this.instance;
        }
    }
}
