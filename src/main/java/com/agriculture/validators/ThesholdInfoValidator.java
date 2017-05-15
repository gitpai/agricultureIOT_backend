package com.agriculture.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.agriculture.models.ThresholdInfo;

/**
 * Created by dean on 4/16/17.
 */
@Component
public class ThesholdInfoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ThresholdInfo.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ThresholdInfo info = (ThresholdInfo)o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gatewayId", "thresholdinfo.empty.gatewayid");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nodeAddr", "thresholdinfo.empty.nodeaddr");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "channel", "thresholdinfo.empty.channel");
        if(info.getLowerLimit() > info.getUpperLimit()) {
            errors.rejectValue("lowerLimit","thresholdinfo.invalid.lowerupper","下限阈值不能大于上限阈值");
        }
    }
}
