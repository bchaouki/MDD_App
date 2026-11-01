package com.openclassrooms.mddapi.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @ParameterizedTest
    @ValueSource(strings = {"Passw0rd!", "Abcdef1$", "Un Mot2Passe#Long"})
    void acceptsStrongPasswords(String password) {
        assertThat(validator.isValid(password, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pa0!", "password1!", "PASSWORD1!", "Password!", "Password1"})
    void rejectsWeakPasswords(String password) {
        assertThat(validator.isValid(password, null)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void leavesEmptinessToNotBlank(String password) {
        assertThat(validator.isValid(password, null)).isTrue();
    }
}
