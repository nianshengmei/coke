package pers.warren.ioc.condition;

public @interface ConditionOnClass {

    Class<?>[] value() default {};

    /**
     * The classes names that must be present.
     * @return the class names that must be present.
     */
    String[] name() default {};
}
