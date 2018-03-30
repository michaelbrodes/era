package era.uploader.controller;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class TaskWarningEvent extends Event {
    private final String warningMessage;

    public TaskWarningEvent(String warningMessage) {
        super(EventType.ROOT);
        this.warningMessage = warningMessage;
    }
    public TaskWarningEvent(EventType<? extends Event> eventType) {
        super(eventType);
        this.warningMessage = "";
    }

    public TaskWarningEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
        this.warningMessage = "";
    }

    public String getWarningMessage() {
        return warningMessage;
    }
}
