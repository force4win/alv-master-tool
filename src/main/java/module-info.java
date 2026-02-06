module com.alv.mastertools {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.alv.mastertools.controllers to javafx.fxml;
    opens com.alv.mastertools.models to javafx.base;

    exports com.alv.mastertools;
}
