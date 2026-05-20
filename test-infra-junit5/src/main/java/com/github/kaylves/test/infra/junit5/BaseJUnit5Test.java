package com.github.kaylves.test.infra.junit5;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
public abstract class BaseJUnit5Test {

    @InjectSoftAssertions
    protected SoftAssertions softly;

    protected SoftAssertions softly() {
        return softly;
    }
}
