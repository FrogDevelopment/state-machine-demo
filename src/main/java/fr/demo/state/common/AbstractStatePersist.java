package fr.demo.state.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractStatePersist<S extends DemoState, E extends DemoEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatePersist.class);

    public static final String HN_CODE = "code";

    private StateMachineFactory<S, E> stateMachineFactory;
    private What what;

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
     * @throws StateException if the change of state is refused
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void change(String code, E event) throws StateException {
        LOGGER.debug("Call change state : code={}, event={}", code, event);

        S currentState;
        try {
            currentState = getState(code);
            LOGGER.debug("Current state for code={} : state={}", code, currentState);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("No data for request", e);
            throw new IllegalArgumentException("Unknown code [" + code + "] for entity '" + what.name() + "'");
        }

        // GET STATE MACHINE FOR ENTITY
        StateMachine<S, E> stateMachine = acquireStateMachine(currentState);

        // SENDING EVENT
        Message<E> message = MessageBuilder.withPayload(event)
                .setHeader(HN_CODE, code)
                .build();
        LOGGER.debug("Sending event {}", message);
        boolean accepted = stateMachine.sendEvent(message);

        // STOPPING
        LOGGER.trace("Stopping machine for {}", what);
        stateMachine.stop();

        if (!accepted) {
            LOGGER.error("Change of state refused: what={}, code={}, event={}, current state={}", what, code, event, currentState);
            throw new StateException(what, code, event, currentState);
        }
    }

    /**
     * @param code  code of the entity to change state
     * @param event event to send
     * @return the new {@link DemoState} if event was accepted
     * @throws StateException if the change of state is refused
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public S changeAndGetNewState(String code, E event) throws StateException {

        change(code, event);

        return getState(code);
    }

    private StateMachine<S, E> acquireStateMachine(S currentState) {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine(what.name());

        LOGGER.trace("Stopping machine for {}", what);
        stateMachine.stop();

        LOGGER.trace("Resetting machine for {}", what);
        stateMachine.getStateMachineAccessor().doWithAllRegions(function -> {
            // add interceptor
            function.addStateMachineInterceptor(interceptor);

            // reset the state machine in current entity state
            function.resetStateMachine(new DemoStateMachineContext<>(currentState, what));
        });

        LOGGER.trace("Starting machine for {}", what);
        stateMachine.start();

        return stateMachine;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    protected abstract S getState(String key);

    @Transactional(propagation = Propagation.MANDATORY)
    protected abstract void updateState(String key, S state);

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

                LOGGER.info("Entity state changed: what={}, code={}, stateFrom={}, stateTo={}", what, code,
                        transition.getSource().getId(), transition.getTarget().getId());
            }
        }
    };
}
