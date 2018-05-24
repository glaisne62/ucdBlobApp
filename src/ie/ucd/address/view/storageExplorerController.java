package ie.ucd.address.view;



import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import ie.ucd.address.model.BlobData;
import ie.ucd.address.model.TreeViewWithItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;

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
    static BlobData root;
    
    
	//@FXML
	//TreeView<String> selectionTreeView;
	@FXML
	StackPane stack;
	//@FXML
	TreeViewWithItems selectionTreeView;
	@FXML
	private void handleButtonAction(ActionEvent event) {
	    createTree();
	}

	public void createTree(String... rootItems) {
		
		try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference("march2018");


        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error conecting to Azure");
        }

		root = new BlobData(container.getUri(),container.getName());
		selectionTreeView  = new TreeViewWithItems<BlobData>(new TreeItem<BlobData>(root));
        myArray = FXCollections.observableArrayList();
        
       
        
        addBlobs(container.listBlobs(),root);
        myArray.add(root);
        
       
       selectionTreeView.setItems(myArray);
        
       //TreeItem root = new TreeItem<String>(container.getName());
        //root.setExpanded(true);
        //root.getChildren().addAll(addBlobs(container.listBlobs()));
        //selectionTreeView.setRoot(root);
	    //create root
		//BlobData root = new BlobData(null,"Root");
		//selectionTreeView = new TreeViewWithItems<BlobData>(new TreeItem<BlobData>(root));
		
		
	    
	    //root.setExpanded(true);
	    //create child
	   
	    //root is the parent of itemChild
	    
	    //selectionTreeView.setRoot(theRoot);
	    stack.getChildren().add(selectionTreeView);
	    
	}
	static BlobData addBlobs(Iterable<ListBlobItem> plist,BlobData pData){

        for (ListBlobItem blobItem : plist)
        {
            if(blobItem.getClass().equals(CloudBlockBlob.class))
            {
                CloudBlockBlob ablob = (CloudBlockBlob) blobItem;
                String name = ablob.getUri().getPath().trim().toString();

                name = name.substring(container.getName().length() + 2, name.length());
                pData.getChildren().add(new BlobData(blobItem.getUri(), name));
                System.out.println("FILE:::::"+name);
            }

        }
        for(ListBlobItem blobItem : plist)
        {

            BlobData newBlob = new BlobData();
            if(blobItem.getClass().equals(CloudBlobDirectory.class))
            {
                CloudBlobDirectory ablob = (CloudBlobDirectory) blobItem;
                String value = ablob.getUri().toString();
                String name = ablob.getUri().getPath().trim().toString();

                name = name.substring(container.getName().length() + 2, name.length());
                newBlob.setName(name);
                newBlob.setMyUri(blobItem.getUri());
                System.out.println("DIREC:::::: "+name);
                
                try {
                    newBlob.getChildren().add(addBlobs(ablob.listBlobs(), newBlob));
                }catch (Exception e)
                {
                    System.out.print("Error here");
                }

            }

            //removes null values from out tree
            //if(newBlob.getName()!=null)
            //{
                pData.getChildren().add(newBlob);
            //}

        }

        return null;

    }
}
