package de.muenchen.raumreservierung;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({ "PMD.TestClassWithoutTestCases" })
public final class TestConstants {

    public static final String SPRING_TEST_PROFILE = "test";

    public static final String SPRING_NO_SECURITY_PROFILE = "no-security";

    public static final String TESTCONTAINERS_POSTGRES_IMAGE = "postgres:17.4-alpine3.21";

    public static final String TESTCONTAINERS_MAILPIT_IMAGE = "axllent/mailpit:v1.29.7";

}
