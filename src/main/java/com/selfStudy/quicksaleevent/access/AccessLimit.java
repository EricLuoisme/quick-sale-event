package com.selfStudy.quicksaleevent.access;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    /**
     * create an annotation
     */

    int seconds();

    int maxCount();

    boolean needLogin() default true;
}
