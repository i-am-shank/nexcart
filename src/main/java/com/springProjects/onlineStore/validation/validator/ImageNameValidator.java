package com.springProjects.onlineStore.validation.validator;

import com.springProjects.onlineStore.validation.annotation.ValidImageName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

// Annotation for which Validator is used
// Data-type to validate
public class ImageNameValidator implements ConstraintValidator<ValidImageName, String> {
    private static final Logger logger = LoggerFactory.getLogger(ImageNameValidator.class);

    @Override
    public boolean isValid(String imageName, ConstraintValidatorContext constraintValidatorContext) {
        logger.info("Validating image name: {}", imageName);
        imageName = imageName.toLowerCase();
        return StringUtils.hasLength(imageName) && (imageName.endsWith(".jpg") ||
                imageName.endsWith(".png") || imageName.endsWith(".jpeg"));
    }
}
