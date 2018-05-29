package ie.ucd.address.view;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import ie.ucd.address.model.BlobData;
import ie.ucd.address.model.TreeViewWithItems;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class storageExplorerController {
	
	static CloudStorageAccount storageAccount;
    static CloudBlobClient blobClient;
    static CloudBlobContainer container;
    

    static public String glashAccName="wryan";
    static public String glashAccKey="m//Fum+C6Np88+mLnGuGXh9STctkOpgm5jK6aKsF4rOao6ALApEBYJY1b4ABD0T1tXaDirrSvO6c2N12fbF3ww==";
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName="+glashAccName+";" +
                    "AccountKey="+glashAccKey+";";

    static  ObservableList<BlobData> myArray;
    public static TreeItem<String> root;
    static HashMap<String,String> data;
    static final long MaxAccSize =3000;
    public static  long AccContentsSize = 0L;
    
    
    @FXML
    ProgressBar pb;   
    @FXML
    Label progressLabel;
	@FXML
	StackPane stack;
	//@FXML
	TreeView<String> selectionTreeView;
	@FXML
	private void handleButtonAction(ActionEvent event) {
	    createTree();
	  
	}

	public void createTree(String... rootItems) {
		
		try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference("march2018");
            selectionTreeView = new TreeView();


        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error conecting to Azure");
        }

		populateTree();
		double progress =(double)( (double)AccContentsSize / (double)MaxAccSize);
		pb.setProgress(progress);
		System.out.println(AccContentsSize);
		System.out.println(MaxAccSize);
		System.out.println(progress);
		progressLabel.setText("You've used "+Math.round(100*progress)+"% of Your Storage");
		


       stack.setOnDragDropped(evt -> {
       	   
    		     //String filename = evt.getDragboard().getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining("\n")).substring(evt.getDragboard().getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining("\n")).lastIndexOf("\\")+1);
    	   		String filename = evt.getDragboard().getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining("\n"));
    		     File sourceFile = new File(filename);
    		     
    		     if(sourceFile.isDirectory()){
    		    	 System.out.print("Direct");
    		    	 for(File thefile:sourceFile.listFiles()){
    		    		 
    		    		 try{
    		    			 CloudBlockBlob uploadBlob = container.getBlockBlobReference(thefile.getParentFile().getName()+"\\"+ thefile.getName());
    	                       uploadBlob.uploadFromFile(thefile.getPath());
    	                       stack.getChildren().clear();
    	      		    	 populateTree();
    		    		 }catch(Exception e)
    		    		 {
    		    			 e.printStackTrace();
    		    		 }}
    		    	
    		     }
    		     else{
    		    		 try {
    		    			 CloudBlockBlob uploadblob = container.getBlockBlobReference(sourceFile.getName());
    	                        uploadblob.uploadFromFile(sourceFile.getPath());
    	                        stack.getChildren().clear();
    	       		    	 populateTree();
    		    		 }catch(Exception e)
    		    		 {
    		    			 e.printStackTrace();
    		    		 }
    		    		 
    		    		 
    		    	 } 		     
    	   
    		  populateTree();
    		  createTree();
       	 
    	   System.out.println(evt.getDragboard().getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining("\n")));
    	   
           System.out.println("HAppened");
           
           evt.setDropCompleted(true);
       });
       
	    stack.getChildren().add(selectionTreeView);


	    
	}
	
	//Method to create Tree and popoulate with Data object references
	 void populateTree() {
		
		 root = new TreeItem<String>("Test");
	        root.setExpanded(true);
	        root.getChildren().addAll(createDir(container.listBlobs()));
	        selectionTreeView.setRoot(root);
	        selectionTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>()
	        {
	            public void changed(ObservableValue<? extends TreeItem<String>> observable,
	            TreeItem<String> old_val, TreeItem<String> new_val) {  TreeItem<String> selectedItem = (TreeItem<String>) new_val;
	                System.out.println("Selected Text : " + selectedItem.getValue());
	                System.out.println("Value from key :"+data.values());
	                //AzureStorage.underLab.setText(new_val.getValue().toString());

	            }
	        });
	        
	        
	     //selectionTreeView  = new TreeViewWithItems<BlobData>(new TreeItem<BlobData>(root));
	     myArray = FXCollections.observableArrayList();      
	    // addBlobs(container.listBlobs(),root);
	     //myArray.add(root);      
	     //selectionTreeView.setItems(myArray);
	     
		
	}
	
	 static ObservableList<TreeItem<String>> createDir(Iterable<ListBlobItem> blobList) {


	        ObservableList<TreeItem<String>> myArray = FXCollections.observableArrayList();
	        data = new HashMap<String, String>();

	        for (ListBlobItem blobItem : blobList) {

	            if (blobItem.getClass().equals(CloudBlockBlob.class)) {
	                CloudBlockBlob ablob = (CloudBlockBlob) blobItem;
	                AccContentsSize = AccContentsSize + ablob.getProperties().getLength();
	                String value = ablob.getUri().toString();
	                String name = ablob.getUri().getPath().trim().toString();

	                name = name.substring(container.getName().length() + 2, name.length());
	                TreeItem<String> treeitem = new TreeItem<String>(name);
	                data.put(name,value);

	                myArray.add(treeitem);


	            }
	        }

	        for (ListBlobItem blobItem : blobList)
	        {
	            if (blobItem.getClass().equals(CloudBlobDirectory.class))
	            {
	                CloudBlobDirectory ablob = (CloudBlobDirectory) blobItem;

	                String name = blobItem.getUri().getPath().trim().toString();

	                name = name.substring(container.getName().length() + 2, name.length());
	                TreeItem<String> treeitem = new TreeItem<String>(name);
	                treeitem.setExpanded(true);
	                try {
	                    treeitem.getChildren().addAll(createDir(ablob.listBlobs()));
	                }catch (Exception e)
	                {
	                    System.out.print("Error in direc blob");
	                }
	                myArray.add(treeitem);


	            }
	        }
	                return myArray;
	    }
}
