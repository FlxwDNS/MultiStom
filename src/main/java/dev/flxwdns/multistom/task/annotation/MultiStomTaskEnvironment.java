package dev.flxwdns.multistom.task.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultiStomTaskEnvironment {
    String name();
    String version() default "1.0.0";

    String description() default "No description provided.";

    String[] authors();
}
