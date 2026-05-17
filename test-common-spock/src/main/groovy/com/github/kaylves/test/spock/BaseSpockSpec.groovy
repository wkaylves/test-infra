package com.github.kaylves.test.spock

import spock.lang.Specification

abstract class BaseSpockSpec extends Specification {

    protected <T> T mock(Class<T> type) {
        Mock(type)
    }

    protected <T> T stub(Class<T> type) {
        Stub(type)
    }

    protected <T> T spy(T instance) {
        Spy(instance)
    }
}
