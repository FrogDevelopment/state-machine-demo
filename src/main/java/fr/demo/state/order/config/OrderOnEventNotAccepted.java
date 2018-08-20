package fr.demo.state.order.config;

import fr.demo.state.order.OrderEvent;
import org.springframework.statemachine.annotation.OnEventNotAccepted;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnEventNotAccepted
public @interface OrderOnEventNotAccepted {

    OrderEvent[] event() default {};
}
