
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
public class DiscountPackageItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long DiscountPackageItemId;
    private int DiscountPackageId;
    private int StoreId;
    private long ItemId;
    private float ItemQty=1;
    private float WholesaleDiscountAmt;
    private float RetailsaleDiscountAmt;

    /**
     * @return the DiscountPackageItemId
     */
    public long getDiscountPackageItemId() {
        return DiscountPackageItemId;
    }

    /**
     * @param DiscountPackageItemId the DiscountPackageItemId to set
     */
    public void setDiscountPackageItemId(long DiscountPackageItemId) {
        this.DiscountPackageItemId = DiscountPackageItemId;
    }

    /**
     * @return the DiscountPackageId
     */
    public int getDiscountPackageId() {
        return DiscountPackageId;
    }

    /**
     * @param DiscountPackageId the DiscountPackageId to set
     */
    public void setDiscountPackageId(int DiscountPackageId) {
        this.DiscountPackageId = DiscountPackageId;
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
     * @return the ItemQty
     */
    public float getItemQty() {
        return ItemQty;
    }

    /**
     * @param ItemQty the ItemQty to set
     */
    public void setItemQty(float ItemQty) {
        this.ItemQty = ItemQty;
    }

    /**
     * @return the WholesaleDiscountAmt
     */
    public float getWholesaleDiscountAmt() {
        return WholesaleDiscountAmt;
    }

    /**
     * @param WholesaleDiscountAmt the WholesaleDiscountAmt to set
     */
    public void setWholesaleDiscountAmt(float WholesaleDiscountAmt) {
        this.WholesaleDiscountAmt = WholesaleDiscountAmt;
    }

    /**
     * @return the RetailsaleDiscountAmt
     */
    public float getRetailsaleDiscountAmt() {
        return RetailsaleDiscountAmt;
    }

    /**
     * @param RetailsaleDiscountAmt the RetailsaleDiscountAmt to set
     */
    public void setRetailsaleDiscountAmt(float RetailsaleDiscountAmt) {
        this.RetailsaleDiscountAmt = RetailsaleDiscountAmt;
    }
    
}
