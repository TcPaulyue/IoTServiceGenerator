package com.servicematrix.autofactory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface APIConfigration {
    String url() default "";
    String Authorization() default "";
    String entity_id() default "";
}
