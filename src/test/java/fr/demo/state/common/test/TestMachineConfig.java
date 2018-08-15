package fr.demo.state.common.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "testMachine")
public class TestMachineConfig extends EnumStateMachineConfigurerAdapter<TestState, TestEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<TestState, TestEvent> config) throws Exception {
        config.withConfiguration()
              .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<TestState, TestEvent> states) throws Exception {
        states.withStates()
              .initial(TestState.CREATED)
              .states(EnumSet.allOf(TestState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TestState, TestEvent> transitions) throws Exception {
        transitions
                .withExternal().source(TestState.CREATED).target(TestState.IN_PROGRESS).event(TestEvent.START)
                .and()
                .withExternal().source(TestState.IN_PROGRESS).target(TestState.FINISHED).event(TestEvent.FINISH)
        ;
    }


}
