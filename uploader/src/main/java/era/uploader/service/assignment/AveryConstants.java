package era.uploader.service.assignment;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * The set of constants for calculations involving rendering a sheet of Avery
 * 2" by 4" shipping labels.
 */
public final class AveryConstants {
    private AveryConstants() {
        // no-op
    }

    public static final class Shipping {
        private Shipping() {
            // no-op
        }

        // page related constants
        /**
         * Points from the top of the page to the first label in the Y direction.
         */
        static final float POINTS_FROM_TOP       = 50.0f;
        /**
         * Points from the left hand side of the page to the first label.
         */
        static final float POINTS_FROM_LEFT_EDGE = 30.0f;
        /**
         * Points from the left hand side cell to the right hand side cell
         */
        static final float POINTS_TO_RIGHT_CELL  = 300.0f;
        /**
         * The amount of points to jump to next row. This would be multiplied by
         * the current row number to get the y coordinate to start the new row.
         */
        static final float POINTS_TO_JUMP        = 150.0f;
        /**
         * The amount of labels in each row of the sheet. With 2" by 4" shipping
         * labels this is 2.
         */
        static final int   CELLS_PER_ROW         = 2;
        /**
         * Amount of labels in one page.
         */
        static final int   CELLS_PER_PAGE        = 10;

        // QR Code related constants
        /**
         * Height of QR code in label image
         */
        static final int   QR_HEIGHT             = 100;
        /**
         * Width of QR code in label image
         */
        static final int   QR_WIDTH              = 100;
        /**
         * When we are starting a new text section of the QR code this
         * specifies margin from the top of label
         */
        static final float       TEXT_MARGIN_TOP       = 30.0f;
        static final float       FONT_SIZE             = 10.0f;
        static final float       NEW_LINE_OFFSET       = 14.0f;

    }

    public static final class Address {
        private Address() {
            // no-op
        }

        static final int   CELLS_PER_PAGE       = 30;
        static final int   CELLS_PER_ROW        = 3;
        /**
         * 1in = 72pts
         */
        static final float POINTS_TO_NEXT_ROW    = 72.25f;
        static final float POINTS_TO_NEXT_COLUMN = 200.0f;
        static final int   QR_HEIGHT             = 70;
        static final int   QR_WIDTH              = 70;
        static final float FONT_SIZE             = 8.0f;
        static final float NEW_LINE_OFFSET       = 12.0f;
        static final float TEXT_MARGIN_TOP       = 15.0f;
        static final float HEADER_MARGIN_TOP     = 15.0f;
        static final float HEADER_MARGIN_CENTER  = 60.0f;
        /**
         * Points from the top of the page to the first label in the Y direction.
         * I determined this through guess and check, because I don't know where
         * the label starts at.
         */
        static final float POINTS_FROM_TOP       = 36.875f;
        static final float POINTS_FROM_LEFT_EDGE = 20.0f;
        static final float TEXT_MARGIN_LEFT      = 5.0f;
    }

    // Typography related constants
    static final float       TEXT_MARGIN_LEFT = 10.0f;
    static final PDType1Font FONT             = PDType1Font.HELVETICA;
}
