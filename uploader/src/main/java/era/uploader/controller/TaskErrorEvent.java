package era.uploader.controller;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class TaskErrorEvent extends Event{
    private Throwable throwable;

    public TaskErrorEvent(Throwable throwable) {
        super(EventType.ROOT);
        this.throwable = throwable;
    }

    public TaskErrorEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public TaskErrorEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
