
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long LocationId=0;
    private int StoreId;
    private String LocationName;

    /**
     * @return the LocationId
     */
    public long getLocationId() {
        return LocationId;
    }

    /**
     * @param LocationId the LocationId to set
     */
    public void setLocationId(long LocationId) {
        this.LocationId = LocationId;
    }

    /**
     * @return the StoreId
     */
    public int getStoreId() {
        return StoreId;
    }

    /**
     * @param StoreId the StoreId to set
     */
    public void setStoreId(int StoreId) {
        this.StoreId = StoreId;
    }

    /**
     * @return the LocationName
     */
    public String getLocationName() {
        return LocationName;
    }

    /**
     * @param LocationName the LocationName to set
     */
    public void setLocationName(String LocationName) {
        this.LocationName = LocationName;
    }
    
}
