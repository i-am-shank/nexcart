package com.springProjects.onlineStore.validation.annotation;

import com.springProjects.onlineStore.validation.validator.ImageNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/*
@Retention - annotation  (RetentionPolicy types)
    SOURCE
        -  Availability  :  source-code  (removed by compiler - not present in .class)
        -  Usage  :  code analysis tools , compile-time processing (like Lombok)
    CLASS
        -  Availability  :  source-code , .class files
        -  Usage  :  Bytecode manipulation tools (like ASM, Javassist) ,
                Compile-time / Post compile tools (Code analyzers, Build tools, Instrumentation agents)
    RUNTIME
        -  Availability  :  source-code , .class files , runtime
        -  Usage  :  Hibernate validator (validation framework), Hibernate (ORM framework),
                Spring framework, JPA annotations, Jackson library, etc

@Target - annotation
    -  tells on what kind of elements, this annotation can be applied
    -  FIELD, PARAMETER, METHOD, CONSTRUCTOR, CLASS, etc.

@Documented - annotation
    -  tells compiler, include this annotation in generated Javadoc
    -  purely documentation-related, not functional

@Constraint - annotation
    -  core annotation from Bean Validation
    -  validatedBy  :  validator class having validation logic
    -  Validator class :
        -  implement ConstraintValidator < annotation, data-type validated >
        -  @Override boolean isValid(data-type value, ConstraintValidatorContext context) {..}
        -  returns true : valid , false : invalid
    -  If annotation has attributes, @Override initialize() to initialize variables
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ValidImageName {
    // Default error message, can also update if Annotation has some parameter passed
    String message() default "Invalid Image name: supported formats are jpg & png";

    // represents group of constraints
    Class<?>[] groups() default { };

    // additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
