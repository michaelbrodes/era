package era.uploader.controller;

import era.uploader.data.model.Course;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.Collection;

public class TaskSuccessEvent<T> extends Event {
    private final T result;

    public TaskSuccessEvent(T taskResult) {
        super(EventType.ROOT);
        this.result = taskResult;
    }

    public TaskSuccessEvent(EventType<? extends Event> eventType) {
        super(eventType);
        result = null;
    }

    public TaskSuccessEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
        result = null;
    }

    public T getResult() {
        return result;
    }
}
