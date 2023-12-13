package com.manager.validation;

import com.manager.model.request.TaskStatusEditRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueOfEnumValidatorTest {
    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "IN_PROGRESS", "COMPLETED"})
    public void whenAllAcceptable_thenShouldNotGiveConstraintViolations(String status) {
        var taskStatusEditRequest = new TaskStatusEditRequest(status);

        var violations = validator.validate(taskStatusEditRequest);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void whenStringNull_thenShouldNotReportConstraintViolations() {
        var taskStatusEditRequest = new TaskStatusEditRequest(null);

        var violations = validator.validate(taskStatusEditRequest);

        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"pending", "IN PROGRESS", "finished"})
    public void whenStringNotAnyOfEnum_thenShouldGiveOccurrenceOfConstraintViolations(String status) {
        var taskStatusEditRequest = new TaskStatusEditRequest(status);

        var violations = validator.validate(taskStatusEditRequest);

        assertThat(violations.size()).isEqualTo(1);
    }
}
