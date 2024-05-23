package com.lifu.seckill.validator;

/**
 * 验证手机号自定义注解
 */
import com.lifu.seckill.validator.IsMobile;
import com.lifu.seckill.vo.IsMobileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class}
)
public @interface IsMobile {
    boolean require() default true; //必填
    String message() default "手机号码格式错误"; //默认返回消息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

