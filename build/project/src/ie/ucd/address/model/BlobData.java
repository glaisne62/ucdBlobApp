package ie.ucd.address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URI;

/**
 * Created by GMcKevitt
 * on 13/04/2018
 * COMMENTS ABOUT PROGRAM HERE
 */
public class BlobData implements HierarchyData<BlobData> {

	public String name;
	public URI myUri;
	
    public BlobData(URI pUri,String pname){
        super();
        this.myUri =pUri;
        this.name = pname ;
    }
    public BlobData(){
        this(null,"");
    }
        
    public void setMyUri(URI pUri){myUri=pUri;}
    public void setName(String pName) {name = pName;}
    public String getName() {return name;}
   
private ObservableList<BlobData> children = FXCollections.observableArrayList();

    @Override
    public ObservableList<BlobData> getChildren() {
        return children;
    }

    public String toString(){
        return ""+name;
    }
}