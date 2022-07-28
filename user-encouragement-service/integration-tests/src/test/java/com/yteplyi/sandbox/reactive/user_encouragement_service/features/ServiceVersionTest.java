package com.yteplyi.sandbox.reactive.user_encouragement_service.features;

import com.intuit.karate.junit5.Karate;

public class ServiceVersionTest {

    @Karate.Test
    Karate test() {
        return Karate.run().relativeTo(getClass());
    }

}
