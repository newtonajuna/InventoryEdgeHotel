
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
public class ItemLocation implements Serializable {

    private static final long serialVersionUID = 1L;
    private long ItemLocationId;
    private long ItemId;
    private long LocationId;
    
    private String StoreName;//for report only
    private String Description;//for report only
    private String LocationName;//for report only
    private String UnitSymbol;//for report only

    /**
     * @return the ItemLocationId
     */
    public long getItemLocationId() {
        return ItemLocationId;
    }

    /**
     * @param ItemLocationId the ItemLocationId to set
     */
    public void setItemLocationId(long ItemLocationId) {
        this.ItemLocationId = ItemLocationId;
    }

    /**
     * @return the ItemId
     */
    public long getItemId() {
        return ItemId;
    }

    /**
     * @param ItemId the ItemId to set
     */
    public void setItemId(long ItemId) {
        this.ItemId = ItemId;
    }

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
     * @return the StoreName
     */
    public String getStoreName() {
        return StoreName;
    }

    /**
     * @param StoreName the StoreName to set
     */
    public void setStoreName(String StoreName) {
        this.StoreName = StoreName;
    }

    /**
     * @return the Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param Description the Description to set
     */
    public void setDescription(String Description) {
        this.Description = Description;
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

    /**
     * @return the UnitSymbol
     */
    public String getUnitSymbol() {
        return UnitSymbol;
    }

    /**
     * @param UnitSymbol the UnitSymbol to set
     */
    public void setUnitSymbol(String UnitSymbol) {
        this.UnitSymbol = UnitSymbol;
    }
}
