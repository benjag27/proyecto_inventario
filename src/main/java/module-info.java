module org.phora {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens org.phora to javafx.graphics;
    opens org.phora.presentation to javafx.graphics;
    opens org.phora.infrastructure.persistence to java.sql;
    opens org.phora.domain.model to javafx.base;
}