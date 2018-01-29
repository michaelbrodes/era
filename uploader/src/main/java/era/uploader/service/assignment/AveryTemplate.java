package era.uploader.service.assignment;

public enum AveryTemplate {
    SHIPPING_LABELS("2\" by 4\" Shipping Labels", AveryConstants.Shipping.QR_HEIGHT, AveryConstants.Shipping.QR_WIDTH),
    ADDRESS_LABELS("1\" by 2.625\" Address Labels", AveryConstants.Address.QR_HEIGHT, AveryConstants.Address.QR_WIDTH);

    private final String description;
    private final int qrHeight;
    private final int qrWidth;

    AveryTemplate(String description, int qrHeight, int qrWidth) {
        this.description = description;
        this.qrHeight = qrHeight;
        this.qrWidth = qrWidth;
    }

    public String description() {
        return description;
    }

    public int getQRHeight() {
        return qrHeight;
    }

    public int getQRWidth() {
        return qrWidth;
    }
}
