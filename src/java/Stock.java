
import java.io.Serializable;
import java.util.Date;
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
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;
    private long StockId;
    private int StoreId;
    private long ItemId;
    private String Batchno;
    private float Currentqty;
    private Date ItemMnfDate;
    private Date ItemExpDate;

    /**
     * @return the StockId
     */
    public long getStockId() {
        return StockId;
    }

    /**
     * @param StockId the StockId to set
     */
    public void setStockId(long StockId) {
        this.StockId = StockId;
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
     * @return the Batchno
     */
    public String getBatchno() {
        return Batchno;
    }

    /**
     * @param Batchno the Batchno to set
     */
    public void setBatchno(String Batchno) {
        this.Batchno = Batchno;
    }

    /**
     * @return the Currentqty
     */
    public float getCurrentqty() {
        return Currentqty;
    }

    /**
     * @param Currentqty the Currentqty to set
     */
    public void setCurrentqty(float Currentqty) {
        this.Currentqty = Currentqty;
    }

    /**
     * @return the ItemMnfDate
     */
    public Date getItemMnfDate() {
        return ItemMnfDate;
    }

    /**
     * @param ItemMnfDate the ItemMnfDate to set
     */
    public void setItemMnfDate(Date ItemMnfDate) {
        this.ItemMnfDate = ItemMnfDate;
    }

    /**
     * @return the ItemExpDate
     */
    public Date getItemExpDate() {
        return ItemExpDate;
    }

    /**
     * @param ItemExpDate the ItemExpDate to set
     */
    public void setItemExpDate(Date ItemExpDate) {
        this.ItemExpDate = ItemExpDate;
    }
}
