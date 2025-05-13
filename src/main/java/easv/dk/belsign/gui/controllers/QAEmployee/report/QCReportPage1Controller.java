package easv.dk.belsign.gui.controllers.QAEmployee.report;

import easv.dk.belsign.be.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QCReportPage1Controller {


    @FXML
    private Label lblReportNo;
    @FXML private Label lblDate;
    @FXML private Label lblClient;
    @FXML private Label lblOrderNo;
    @FXML private Label lblOperator;
    @FXML private Label lblCreateTime1;

        public void setOrderDetails(Order order) {
            lblReportNo.setText("Preview – Not Saved");
            lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//            lblClient.setText(order.getCustomerEmail());
            lblOrderNo.setText(order.getOrderNumber());
//            lblOperator.setText(order.getOperatorName());
            lblCreateTime1.setText("This PDF was created at " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy, HH:mm")));
        }


    }




