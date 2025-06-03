package com.victor.f1bettingapp.event;

import com.victor.f1bettingapp.model.EventOutcome;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventOutcomeProcessedEvent extends ApplicationEvent {
    private final EventOutcome eventOutcome;

    public EventOutcomeProcessedEvent(Object source, EventOutcome eventOutcome) {
        super(source);
        this.eventOutcome = eventOutcome;
    }

}