package easv.dk.belsign.gui.ViewManagement;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.controllers.Operator.CameraController;
import easv.dk.belsign.gui.controllers.QAEmployee.OrderCardController;
import easv.dk.belsign.gui.controllers.QAEmployee.PhotoReviewController;
import easv.dk.belsign.gui.controllers.QAEmployee.report.QCReportMainController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

// Navigation utility used to switch scenes via StageManagerProvider.
// Use (Parent root) versions when you need to inject controller data.
// Use plain versions when the view self-loads its data.


public class Navigation {


    /// ─────────────────────────────────────
    /// LOGIN + LOGOUT
    /// ─────────────────────────────────────

    public static void goToTitleScreen() {
        StageManagerProvider.get().getSceneManager().switchScene(FXMLPath.TITLE_SCREEN);
    }

    public static void goToLoginScreen() {
        StageManagerProvider.get().getSceneManager().switchScene(FXMLPath.LOGIN);
    }



    /// ─────────────────────────────────────
    /// OPERATOR NAVIGATION
    /// ─────────────────────────────────────

    // If we need to pass the operator (user) object to the controller we use:

    public static void goToOperatorDashboardWithRoot(Parent root) {
        StageManagerProvider.get().switchScene(root);
    }

    // Else:

    public static void goToOperatorDashboard() {
        StageManagerProvider.get().getSceneManager().switchScene(FXMLPath.OPERATOR_DASHBOARD);
    }


    public static void goToCameraView(Order order) {
        try {
            Pair<Parent, CameraController> pair =
                    FXMLManager.INSTANCE.getFXML(FXMLPath.CAMERA_VIEW);

            CameraController controller = pair.getValue();
            controller.setSelectedOrder(order);

            StageManagerProvider.get().switchScene(pair.getKey());

        } catch (Exception e) {
            System.err.println("Error navigating to camera view for order: " + order.getOrderNumber());
            e.printStackTrace();
        }
    }


    /// ─────────────────────────────────────
    /// QA EMPLOYEE NAVIGATION
    /// ─────────────────────────────────────

    // Used when controller is already prepared (e.g. after login) - to pass User Object
    public static void goToQAEmployeeView(Parent root) {
        StageManagerProvider.get().switchScene(root);
    }

    //Else:

    public static void goToQAEmployeeView() {
        StageManagerProvider.get().getSceneManager().switchScene(FXMLPath.QA_EMPLOYEE_VIEW);
    }



    /// ─────────────────────────────────────
    /// Photo Review
    /// ─────────────────────────────────────
    public static void goToPhotoReviewView(Order order, User loggedInUser) {
        try {
            Pair<Parent, PhotoReviewController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_PHOTO_REVIEW);
            PhotoReviewController controller = pair.getValue();

            controller.setup(loggedInUser, order.getOrderID());

            StageManagerProvider.get().switchScene(pair.getKey());
        } catch (Exception e) {
            System.err.println("Failed to load photo review for Order: " + order.getOrderNumber());
            e.printStackTrace();
        }
    }

    /// ─────────────────────────────────────
    /// QC Report
    /// ─────────────────────────────────────
    /*public static void openQCReportPreview(Order order) {
        try {
            Pair<Parent, QCReportMainController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_REPORT_PREVIEW);
            pair.getValue().setSelectedOrder(order);

            Stage stage = new Stage();
            stage.setTitle("QC Report Preview - Order #" + order.getOrderNumber());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(pair.getKey()));
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to open QC Report Preview for Order: " + order.getOrderNumber());
            e.printStackTrace();
        }
    }*/

    public static void openQCReportPreview(Order order,
                                           OrderCardController caller) {

        try {
            Pair<Parent, QCReportMainController> pair =
                    FXMLManager.INSTANCE.getFXML(FXMLPath.QA_REPORT_PREVIEW);

            QCReportMainController ctrl = pair.getValue();
            ctrl.setSelectedOrder(order);                     // what you had
            ctrl.setReportSaveListener(caller::onReportSaved);/* NEW callback */

            Stage s = new Stage();
            s.setTitle("QC Report Preview – Order " + order.getOrderNumber());
            s.initModality(Modality.APPLICATION_MODAL);
            s.setScene(new Scene(pair.getKey()));
            s.show();

        } catch (Exception ex) {
            System.err.println("Failed to open QC Report Preview for Order: "
                    + order.getOrderNumber());
            ex.printStackTrace();
        }
    }

    /* keep the old no-callback version for existing callers */
    public static void openQCReportPreview(Order order) {
        openQCReportPreview(order, null);          // simply ignore callback
    }





    /// ─────────────────────────────────────
    /// ADMIN NAVIGATION
    /// ─────────────────────────────────────

    // Used when controller is already prepared (e.g. after login) - to pass User Object
    public static void goToAdminView(Parent root) {
        StageManagerProvider.get().switchScene(root);
    }

    //Else:
    public static void goToAdminView() {
        StageManagerProvider.get().getSceneManager().switchScene(FXMLPath.ADMIN_DASHBOARD);
    }

    public static void goToCreateUserView(Parent root) {
        StageManagerProvider.get().switchScene(root);
    }
    public static void goToEditUserView(Parent root) {
        StageManagerProvider.get().switchScene(root);
    }



}
