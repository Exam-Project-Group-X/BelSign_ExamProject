        package easv.dk.belsign.gui.controllers.QAEmployee;

        import easv.dk.belsign.gui.ViewManagement.FXMLPath;
        import easv.dk.belsign.gui.ViewManagement.ViewManager;
        import easv.dk.belsign.gui.models.PhotosModel;
        import easv.dk.belsign.gui.models.QAEmployeeModel;
        import javafx.event.ActionEvent;

        import easv.dk.belsign.dal.web.ProductPhotosDAO;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Node;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.control.TextField;
        import javafx.scene.image.Image;
        import javafx.scene.image.ImageView;
        import javafx.scene.layout.HBox;
        import javafx.stage.Stage;

        import java.io.ByteArrayInputStream;
        import java.io.IOException;
        import java.sql.SQLException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;




        public class PhotoReviewController {

            public Button closeBtn;
            public Button ApproveBtn;
            public Button RejectBtn;
            @FXML
            private ImageView mainImg;
            @FXML
            private HBox thumbStrip;
            @FXML
            private TextField captionField;

            private PhotosModel photosModel;
            private QAEmployeeModel qamodel;
            private int currentOrderId;
            private String currentAngle;


            private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();
            private final List<Image> loadedImages = new ArrayList<>();

            public void setModel(PhotosModel photosModel) {
                this.photosModel = photosModel;

            }
            public void setQAEmployeeModel(QAEmployeeModel model) {
                this.qamodel = model;
            }

            public void setOrderId(int orderId) {
                this.currentOrderId = orderId;
            }
            public void setCurrentAngle(String angle) {
                this.currentAngle = angle;
            }

            public void setCaption(String caption) {
                captionField.setText(caption);
            }

            public void loadPhotosForOrder(int orderId) {
                try {
                    Map<String, byte[]> photoMap = photoDAO.getPhotosByOrderId(orderId);
                    thumbStrip.getChildren().clear();
                    loadedImages.clear();

                    for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
                        String angle = entry.getKey();
                        byte[] photoBytes = entry.getValue();
                        if (photoBytes == null) continue;

                        Image img = new Image(new ByteArrayInputStream(photoBytes));
                        loadedImages.add(img);

                        ImageView thumb = new ImageView(img);
                        thumb.setFitWidth(200);
                        thumb.setFitHeight(150);
                        thumb.setPreserveRatio(true);
                        thumb.setPickOnBounds(true);

                        thumb.setOnMouseClicked(e -> {
                            mainImg.setImage(img);
                            currentAngle = angle;
                            captionField.setText("Angle: " + angle);
                        });
                        thumbStrip.getChildren().add(thumb);
                    }

                    // Default image
                    if (!loadedImages.isEmpty()) {
                        mainImg.setImage(loadedImages.get(0));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            public void onClickLogoutBtn(ActionEvent actionEvent) {
                ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
            }

            public void onCloseBtnClick(ActionEvent actionEvent) {
                ViewManager.INSTANCE.showScene(FXMLPath.QA_EMPLOYEE_VIEW);
            }




            /// TODO add debug to see : print ("Photo approved:")
            public void ApprovePhoto(ActionEvent actionEvent) {
                try{
                    photosModel.approvePhoto(currentOrderId,currentAngle);
                    System.out.println("Photo Approved for: " + currentOrderId + " " + currentAngle);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }

            public void RejectPhoto(ActionEvent actionEvent) {

                try{
                    photosModel.rejectPhoto(currentOrderId,currentAngle);
                    System.out.println("Photo Rejected for: " + currentOrderId + " " + currentAngle);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }

            public void OnClickFinishButton(ActionEvent actionEvent) {

                try {
                    qamodel.setOrderToCompleted(currentOrderId);
                    /// todo refactor
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_EMPLOYEE_VIEW));
                    Parent root = loader.load();
                    Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    currentStage.setScene(new Scene(root));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }




        }
