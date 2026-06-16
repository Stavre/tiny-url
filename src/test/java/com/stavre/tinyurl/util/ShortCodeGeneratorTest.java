package com.stavre.tinyurl.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ShortCodeGeneratorTest {

    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;

    @Test
    void generateReturnsCodeOfCorrectLength() {
        String code = ShortCodeGenerator.generate();
        assertThat(code).hasSize(CODE_LENGTH);
    }

    @Test
    void generateReturnsCodeContainingOnlyValidCharacters() {
        String code = ShortCodeGenerator.generate();
        for (char c : code.toCharArray()) {
            assertThat(VALID_CHARS).contains(String.valueOf(c));
        }
    }

    @Test
    void generateReturnsDifferentCodesOnSubsequentCalls() {
        String first = ShortCodeGenerator.generate();
        String second = ShortCodeGenerator.generate();
        assertThat(first).isNotEqualTo(second);
    }
}
