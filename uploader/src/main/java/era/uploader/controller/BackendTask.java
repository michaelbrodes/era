package era.uploader.controller;

import javafx.concurrent.Task;
import javafx.event.EventHandler;

public abstract class BackendTask<T> extends Task<T> {
    private final EventHandler<TaskWarningEvent> warningHandler;

    protected BackendTask(AbstractBuilder<T> builder) {
        this.warningHandler = builder.warningHandler;
        this.setOnFailed((workerState) -> builder.errorHandler.handle(new TaskErrorEvent(this.exceptionProperty().get())));
        this.setOnSucceeded((workerState) -> builder.successHandler.handle(new TaskSuccessEvent<>(this.valueProperty().get())));
    }

    public void onWarning(String warningString) {
        warningHandler.handle(new TaskWarningEvent(warningString));
    }

    protected static class AbstractBuilder<T> {
        private EventHandler<TaskWarningEvent> warningHandler;
        private EventHandler<TaskErrorEvent> errorHandler;
        private EventHandler<TaskSuccessEvent<T>> successHandler;

        protected AbstractBuilder() {

        }

        public AbstractBuilder warningHandler(EventHandler<TaskWarningEvent> warningHandler) {
            this.warningHandler = warningHandler;
            return this;
        }
        public AbstractBuilder errorHandler(EventHandler<TaskErrorEvent> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public AbstractBuilder successHandler(EventHandler<TaskSuccessEvent<T>> successHandler) {
            this.successHandler = successHandler;
            return this;
        }
    }
}
