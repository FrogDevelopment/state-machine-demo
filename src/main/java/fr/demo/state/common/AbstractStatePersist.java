package fr.demo.state.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public abstract class AbstractStatePersist<S extends DemoState, E extends DemoEvent> extends DefaultLifecycleProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatePersist.class);

    public static final String HN_CODE = "code";

    private StateMachineFactory<S, E> stateMachineFactory;
    protected What what;

    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected AbstractStatePersist(StateMachineFactory<S, E> stateMachineFactory, What what) {
        super();
        this.stateMachineFactory = stateMachineFactory;
        this.what = what;
    }

    /**
     * @param code  code of the entity to change state
     * @param event event to send
     * @return the new {@link DemoState} if event was accepted
     */
    @Transactional(propagation = Propagation.REQUIRED)
    synchronized public S change(String code, E event) {
        LOGGER.debug("call change state : code={}, event={}", code, event);

        S currentState;
        try {
            currentState = getState(code);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("No data for request", e);
            throw new IllegalArgumentException("Unknown code [" + code + "] for entity '" + what.name() + "'");
        }

        // CREATE STATE MACHINE FROM ENTITY
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine(what.name());

        StateMachineAccess<S, E> stateMachineAccess = stateMachine.getStateMachineAccessor().withRegion();
        // ADD INTERCEPTOR
        stateMachineAccess.addStateMachineInterceptor(interceptor);
        // RESETING IN CURRENT STATE
        LOGGER.debug("Reseting machine for {} with state {}", what, currentState);
        stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(currentState, null, null, null, null, what.name()));

        // STARTING
        LOGGER.debug("Starting machine for {}", what);
        stateMachine.start();

        // SENDING EVENT
        Message<E> message = MessageBuilder.withPayload(event)
                .setHeader(HN_CODE, code)
                .build();
        boolean accepted = stateMachine.sendEvent(message);

        // STOPPING
        LOGGER.debug("Stopping machine for {}", what);
        stateMachine.stop();

        if (accepted) {
            // return the new state saved on base
            return getState(code);
        } else {
            LOGGER.error("Change of state refused: what={}, code={}, event={}, current state={}", what, code, event, currentState);
            throw new StateException(what, code, event, currentState);
        }
    }

    protected abstract S getState(String key);

    protected abstract void updateState(String key, S state);

    // ****************************************** \\
    //      PERSIST ENUM STATE MACHINE HANDLER    \\
    // ****************************************** \\

    // http://docs.spring.io/spring-statemachine/docs/current/reference/htmlsingle/#statemachine-examples-persist
    // https://github.com/spring-projects/spring-statemachine/tree/master/spring-statemachine-samples/persist/src/main/java/demo/persist
    // http://blog.mimacom.com/introducing-spring-state-machine/

    private final StateMachineInterceptorAdapter<S, E> interceptor = new StateMachineInterceptorAdapter<>() {

        @Override
        public void preStateChange(State<S, E> state, Message<E> message, Transition<S, E> transition, StateMachine<S, E> stateMachine) {
            MessageHeaders headers = message.getHeaders();
            if (headers.containsKey(HN_CODE)) {
                String code = headers.get(HN_CODE, String.class);

                updateState(code, state.getId());
            }
        }

        @Override
        public void postStateChange(State<S, E> state, Message<E> message, Transition<S, E> transition, StateMachine<S, E> stateMachine) {
            MessageHeaders headers = message.getHeaders();
            if (headers.containsKey(HN_CODE)) {
                String code = headers.get(HN_CODE, String.class);

                LOGGER.info("Entity state changed: what={}, code={}, stateFrom={}, stateTo={}", what, code, transition.getSource().getId(),  transition.getTarget().getId());
            }
        }
    };
}
