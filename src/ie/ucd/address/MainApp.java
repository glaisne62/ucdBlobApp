package ie.ucd.address;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import ie.ucd.address.model.BlobData;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane storageExplorer;
    
    // ... AFTER THE OTHER VARIABLES ...

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<BlobData> storageData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() {
        // Add some sample data
      storageData.add(new BlobData(null,"Glaisne"));
      storageData.add(new BlobData(null,"Winnie"));
      storageData.add(new BlobData(null,"Paul"));
      storageData.add(new BlobData(null,"Michael"));
      }

    /**
     * Returns the data as an observable list of Persons. 
     * @return
     */
    public ObservableList<BlobData> getPersonData() {
        return storageData;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        initRootLayout();

        showStorageExplorer();
        
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showStorageExplorer() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/storageExplorer.fxml"));
            AnchorPane storageExplorer = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(storageExplorer);
            
            Text text = new Text();
            
    	    //selectionTreeView.setRoot(theRoot);
           rootLayout.setOnDragOver(evt -> {
               if (evt.getDragboard().hasFiles()) {
                   evt.acceptTransferModes(TransferMode.LINK);
               }
           });
           rootLayout.setOnDragDropped(evt -> {
               System.out.println("HAppened");
               evt.setDropCompleted(true);
           });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}