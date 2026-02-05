module com.alv.mastertools {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.alv.mastertools.controllers to javafx.fxml;

    exports com.alv.mastertools;
}
