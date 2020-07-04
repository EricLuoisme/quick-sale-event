package com.selfStudy.quicksaleevent.validator;

import com.alibaba.druid.util.StringUtils;
import com.selfStudy.quicksaleevent.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!required && StringUtils.isEmpty(s))
            return true; // if it's not required, we can let empty pass
        return ValidatorUtil.isMobile(s); // if it's not empty, we must check the mobile number's pattern
    }
}
