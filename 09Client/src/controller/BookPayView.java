package controller;

import entity.Customer;
import entity.Movie;
import entity.Screening;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import property.PropertyReader;
import testuj.BookingManagerRemote;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;

public class BookPayView {

    private static final Logger LOG = Logger.getLogger(BookPayView.class.getName());

    Movie movie;
    Screening screening;
    Customer customer;
    BookingManagerRemote bmr;
    List<Integer> seats;
    byte[] byteFile;
    BookingView bookingView;

    public Button backButton;
    public Label Movie_title;
    public Label LDate;
    public Label LTime;
    public Label LSeat;
    public Label LAudi;
    public Label LPrice;
    public ImageView image_view;
    public Button ReserveB;
    public Button PayB;
    //
    public Label cardL;
    public TextField cardF;
    public Label expL;
    public TextField expF1;
    public TextField expF2;
    public Label expF3;
    public Label CodeL;
    public TextField CodeF;
    public Button ConfirmB;
    //
    public Label dateLabel;
    public Label timeLabel;
    public Text bookingsLabel;
    public Label priceLabel;
    public Label seatsLabel;
    public AnchorPane pane;

    private double xOffset;
    private double yOffset;

    public void backToPrevScene(ActionEvent actionEvent) {
        ((javafx.scene.Node) (actionEvent.getSource())).getScene().getWindow().hide();
        bookingView.refresh();
    }

    public void reserve(ActionEvent actionEvent) {

        LOG.entering(this.getClass().getName(),"entering booking");
        byteFile = bmr.setReservation(customer.getId(),screening.getId(),false,seats);
        if(byteFile == null){
            LOG.log(Level.SEVERE,"not able to crete pdf");
            showAlert("We are really sorry, but someone fucked it up :( Try again, later");
        }
        else{
            showSucces(actionEvent);
        }
        LOG.exiting(this.getClass().getName(),"exiting booking");

    }

    public void payy(ActionEvent actionEvent) {
        cardL.setVisible(true);
        cardF.setVisible(true);
        expF1.setVisible(true);
        expF2.setVisible(true);
        expF3.setVisible(true);
        expL.setVisible(true);
        CodeF.setVisible(true);
        CodeL.setVisible(true);
        ConfirmB.setVisible(true);
        PayB.setVisible(false);
        ReserveB.setVisible(false);
    }

    public void confirm(ActionEvent actionEvent) {
        try {
            Long.parseLong(cardF.getText());
            Integer.parseInt(expF1.getText());
            Integer.parseInt(expF2.getText());
            Integer.parseInt(CodeF.getText());
        }catch (NumberFormatException nfe){
            LOG.log(Level.WARNING,"wrong card numbers");
            showAlert("Wrong card data. Please check your card number.");
            return;
        }

        LOG.entering(this.getClass().getName(),"entering booking");
        byteFile = bmr.setReservation(customer.getId(),screening.getId(),true,seats);
        if(byteFile == null){
            LOG.log(Level.SEVERE,"not able to crete pdf");
            showAlert("We are really sorry, but someone fucked it up :( Try again, later");
        }
        else{
            showSucces(actionEvent);
        }
        LOG.exiting(this.getClass().getName(),"exiting booking");

    }

    public void setPane(){
        pane.setOnMousePressed(e->{
                    xOffset = e.getSceneX();
                    yOffset = e.getSceneY();
                }
        );
        pane.setOnMouseDragged(e->{
                    pane.getScene().getWindow().setX(e.getScreenX() - xOffset);
                    pane.getScene().getWindow().setY(e.getScreenY() - yOffset);
                }
        );
    }

    public void init(Movie movie, Screening screening, List<Integer> seats, BookingManagerRemote bmr,String lan,BookingView bookingView){
        setPane();
        ResourceBundle rb =	ResourceBundle.getBundle("Label", Locale.forLanguageTag(lan));
        this.bookingView = bookingView;
        backButton.setText(rb.getString("backButton"));
        dateLabel.setText(rb.getString("dateLabel"));
        timeLabel.setText(rb.getString("timeLabel"));
        bookingsLabel.setText(rb.getString("bookingsLabel"));
        priceLabel.setText(rb.getString("priceLabel"));
        seatsLabel.setText(rb.getString("seatsLabel"));
        PayB.setText(rb.getString("payButton"));
        ReserveB.setText(rb.getString("onlyReserve"));

        ConfirmB.setText(rb.getString("confirmButton"));
        expL.setText(rb.getString("expireLabel"));
        cardL.setText(rb.getString("cardLabel"));
        CodeL.setText(rb.getString("codeLabel"));

        this.bmr = bmr;
        this.movie = movie;
        this.customer = SceneCreator.getCurrentCustomer();
        this.seats = seats;
        System.out.println(customer.toString());
        this.screening = screening;
        image_view.setImage(SwingFXUtils.toFXImage(convertImage(movie.getImage()),null));
        Movie_title.setText(movie.getTitle());
        LDate.setText(screening.getScreeningStart().getDate() + "." + screening.getScreeningStart().getMonth());
        LTime.setText(screening.getScreeningStart().getHours() + ":" + screening.getScreeningStart().getMinutes());
        StringBuilder stringBuilder = new StringBuilder();
        seats.stream().forEach(e -> stringBuilder.append(e + " "));
        LSeat.setText(stringBuilder.toString());
        LAudi.setText("NIC");
        LPrice.setText(seats.size()*Integer.valueOf(PropertyReader.read("seat.price")) + " $");
    }

    private BufferedImage convertImage(byte[] pole) {
        InputStream in = new ByteArrayInputStream(pole);
        try {
            return ImageIO.read(in);
        } catch (IOException e) {
            LOG.log(Level.WARNING,"image can not be converted");
        }
        return null;
    }

    private void showAlert(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR Dialog");
        alert.setHeaderText(text);
        alert.setContentText("Please, try again");
        alert.showAndWait();
    }
    private void showSucces(ActionEvent act) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Reservation Succes");
        alert.setHeaderText("Thank you from your Reservation. Ticket was sent to your email.");
        alert.setContentText("Would you like to download it ?");
        alert.initStyle(StageStyle.UNDECORATED);

        ButtonType buttonTypeOne = new ButtonType("Download");
        ButtonType buttonTypeTwo = new ButtonType("Close");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            OutputStream outputStream = null;
            LOG.entering(this.getClass().getName(),"entering downloading pdf");
            try {
                outputStream = new FileOutputStream("out" + System.currentTimeMillis() + ".pdf");
                outputStream.write(byteFile);
            }catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE,"cannot create pdf");
            } catch (IOException e) {
                LOG.log(Level.SEVERE,"cannot create pdf");
            }finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else{
            //close
        }
        ((javafx.scene.Node) (act.getSource())).getScene().getWindow().hide();
        bookingView.refresh();
    }


}
