package era.uploader.controller;

import era.uploader.data.viewmodel.AssignmentPrintoutMetaData;
import era.uploader.service.AssignmentCreationService;
import era.uploader.service.assignment.AveryTemplate;
import javafx.event.EventHandler;

import java.util.List;

public class AssignmentCreationTask extends BackendTask<Void> {
    private final AssignmentCreationService service;
    // terrible variable name, but that is the convention
    private final List<AssignmentPrintoutMetaData> apmds;
    private final AveryTemplate printoutTemplate;

    private AssignmentCreationTask(Builder builder) {
        super(builder);
        this.apmds = builder.apmds;
        this.service = builder.assignmentCreationService;
        this.printoutTemplate = builder.printoutTemplate;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Void call() {
        service.printAndSaveQRCodes(apmds, printoutTemplate);
        return null;
    }

    public static class Builder extends AbstractBuilder<Void> {

        private List<AssignmentPrintoutMetaData> apmds;
        private AveryTemplate printoutTemplate;
        private AssignmentCreationService assignmentCreationService;

        public AssignmentCreationTask create() {
            return new AssignmentCreationTask(this);
        }

        public Builder apmds(List<AssignmentPrintoutMetaData> apmds) {
            this.apmds = apmds;
            return this;
        }

        public Builder printoutTemplate(AveryTemplate printoutTemplate) {
            this.printoutTemplate = printoutTemplate;
            return this;
        }

        public Builder assignmentCreationService(AssignmentCreationService service) {
            this.assignmentCreationService = service;
            return this;
        }

        @Override
        public Builder warningHandler(EventHandler<TaskWarningEvent> warningHandler) {
            return (Builder) super.warningHandler(warningHandler);
        }

        @Override
        public Builder errorHandler(EventHandler<TaskErrorEvent> errorHandler) {
            return (Builder) super.errorHandler(errorHandler);
        }

        @Override
        public Builder successHandler(EventHandler<TaskSuccessEvent<Void>> successHandler) {
            return (Builder) super.successHandler(successHandler);
        }
    }

}
