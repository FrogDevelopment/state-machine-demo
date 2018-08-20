package fr.demo.state.order.config;

import fr.demo.state.order.OrderState;
import org.springframework.statemachine.annotation.OnStateChanged;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnStateChanged
public @interface OrderOnStateChanged {

    OrderState[] source() default {};

    OrderState[] target() default {};
}
