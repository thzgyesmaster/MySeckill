package com.lifu.seckill.vo;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lifu.seckill.utils.ValidatorUtil;
import com.lifu.seckill.validator.IsMobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.require();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //判断是否是必填
        if (required){  //必填校验
            return ValidatorUtil.isMobile(value);
        }else{  //如果不是必填
            if (StringUtils.isEmpty(value)){
                return true;
            }else{  //非必填校验
                return ValidatorUtil.isMobile(value);
            }
        }
    }

}
