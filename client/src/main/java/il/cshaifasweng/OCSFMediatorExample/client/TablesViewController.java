package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.Point;

public class TablesViewController {

    int numOfImages = 3;
    Image[] images;

    @FXML
    private Button TableButton1;

    @FXML
    private Button TableButton10;

    @FXML
    private Button TableButton11;

    @FXML
    private Button TableButton12;

    @FXML
    private Button TableButton13;

    @FXML
    private Button TableButton14;

    @FXML
    private Button TableButton2;

    @FXML
    private Button TableButton3;

    @FXML
    private Button TableButton4;

    @FXML
    private Button TableButton5;

    @FXML
    private Button TableButton6;

    @FXML
    private Button TableButton7;

    @FXML
    private Button TableButton8;

    @FXML
    private Button TableButton9;

    @FXML
    private ImageView imageView;

    private Button[] tableButtons;
    private static final Point[] HAIFA_TABLES = {
            new Point(38, 56), new Point(106, 56), new Point(36, 139), new Point(110, 139),
            new Point(35, 226), new Point(108, 227), new Point(249, 233), new Point(304, 233),
            new Point(23, 322), new Point(70, 325), new Point(126, 323), new Point(235, 325),
            new Point(291, 325)
    };

    private static final Point[] TEL_AVIV_TABLES = {
            new Point(116, 40), new Point(115, 133), new Point(276, 167), new Point(274, 242),
            new Point(121, 315), new Point(230, 314), new Point(29, 60), new Point(35, 312)
    };

    private static final Point[] NAHARIYA_TABLES = {
            new Point(20, 37), new Point(77, 36), new Point(20, 139), new Point(75, 139),
            new Point(239, 162), new Point(305, 164), new Point(20, 240), new Point(75, 240),
            new Point(236, 252), new Point(298, 253), new Point(20, 338), new Point(73, 339),
            new Point(241, 339), new Point(301, 337)
    };

    public static Point[] getTableCoordinates(int restaurantId) {
        return switch (restaurantId) {
            case 1 -> HAIFA_TABLES;
            case 2 -> TEL_AVIV_TABLES;
            case 3 -> NAHARIYA_TABLES;
            default -> throw new IllegalArgumentException("Invalid restaurant ID: " + restaurantId);
        };
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            tableButtons = new Button[14];

            tableButtons[0] = TableButton1;
            tableButtons[1] = TableButton2;
            tableButtons[2] = TableButton3;
            tableButtons[3] = TableButton4;
            tableButtons[4] = TableButton5;
            tableButtons[5] = TableButton6;
            tableButtons[6] = TableButton7;
            tableButtons[7] = TableButton8;
            tableButtons[8] = TableButton9;
            tableButtons[9] = TableButton10;
            tableButtons[10] = TableButton11;
            tableButtons[11] = TableButton12;
            tableButtons[12] = TableButton13;
            tableButtons[13] = TableButton14;

            int restaurantId = Client.userAtt.getRestaurantId();
            Point[] selectedTables = getTableCoordinates(restaurantId);

            for (int i = 0; i < selectedTables.length && i < tableButtons.length; i++) {
                if (tableButtons[i] != null) {
                    tableButtons[i].setLayoutX(selectedTables[i].x);
                    tableButtons[i].setLayoutY(selectedTables[i].y);
                }
            }

            switch (restaurantId) {
                case 2:
                    TableButton9.setVisible(false);
                    TableButton10.setVisible(false);
                    TableButton11.setVisible(false);
                    TableButton12.setVisible(false);
                    TableButton13.setVisible(false);
                    TableButton14.setVisible(false);
                    break;
                case 1:
                    TableButton14.setVisible(false);
                    break;
            }
            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Images/" + i + ".jpg")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageView.setImage(images[restaurantId - 1]);
        });
    }
}