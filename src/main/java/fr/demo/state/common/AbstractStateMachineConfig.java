package fr.demo.state.common;

import fr.demo.state.monitor.DemoStateMachineMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.AbstractStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.configurers.ConfigurationConfigurer;
import org.springframework.statemachine.config.configurers.MonitoringConfigurer;

public class AbstractStateMachineConfig<S extends Enum<S>, E extends Enum<E>> extends AbstractStateMachineConfigurerAdapter<S, E> {

    @Autowired
    private DemoStateMachineMonitor<S, E> stateMachineMonitor;

    @Override
    public void configure(StateMachineConfigurationConfigurer<S, E> config) throws Exception {
        ConfigurationConfigurer<S, E> configurer = config.withConfiguration();
        configurer.autoStartup(false);

        MonitoringConfigurer<S, E> monitoringConfigurer = config.withMonitoring();
        monitoringConfigurer.monitor(stateMachineMonitor);
    }
}

