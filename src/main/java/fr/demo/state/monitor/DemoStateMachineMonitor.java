package fr.demo.state.monitor;

import fr.demo.state.common.What;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.transition.Transition;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DemoStateMachineMonitor<S extends Enum<S>, E extends Enum<E>> implements StateMachineMonitor<S, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoStateMachineMonitor.class);

    //
    private static final Map<What, Monitor> MONITORS = Arrays.stream(What.values())
                                                             .filter(w -> !w.equals(What.TEST))
                                                             .collect(Collectors.toMap(w -> w, w -> new Monitor()));

    @Override
    public void transition(StateMachine<S, E> stateMachine, Transition<S, E> transition, long duration) {
        if (transition.getSource() == null) {
            return;
        }

        // /!\ stateMachine stateMachineId set when reseting StateMachine on AbstractStatrPersist
        String stateMachineId = stateMachine.getId();

        What what = What.valueOf(stateMachineId);

        Monitor monitor = MONITORS.get(what);

        monitor.addDuration(duration);
        LOGGER.debug("Transition monitor of [{}] : from={}, to={}, duration={}ms, averageDuration={}", what, transition.getSource().getId(), transition.getTarget().getId(), duration, monitor.averageDuration);
    }

    @Override
    public void action(StateMachine<S, E> stateMachine, Action<S, E> action, long duration) {
        // todo ?
    }

    public static void resetMonitor(What what) {
        MONITORS.get(what).resetCount();
    }

    public static Monitor getMonitor(What what) {
        return MONITORS.get(what);
    }

    public static class Monitor implements Serializable {

        private static final long serialVersionUID = 1296435819456057044L;

        private LocalDateTime since = LocalDateTime.now();
        private int count = 0;
        private int totalDuration = 0;
        private long averageDuration = 0;

        public LocalDateTime getSince() {
            return since;
        }

        public void setSince(LocalDateTime since) {
            this.since = since;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        public void setTotalDuration(int totalDuration) {
            this.totalDuration = totalDuration;
        }

        public long getAverageDuration() {
            return averageDuration;
        }

        public void setAverageDuration(long averageDuration) {
            this.averageDuration = averageDuration;
        }

        private void addDuration(long duration) {
            count++;
            totalDuration += duration;
            averageDuration = totalDuration / count;
        }

        private void resetCount() {
            setSince(LocalDateTime.now());
            setCount(0);
            setTotalDuration(0);
            setAverageDuration(0);
        }
    }
}
