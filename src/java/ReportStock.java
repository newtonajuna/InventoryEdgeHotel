
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
public class ReportStock implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private long ItemId;
    private String ItemCode;
    private String Description;
    private int CategoryId;
    private int SubCategoryId;
    private int UnitId;
    private String UnitSymbol;
    private int ReorderLevel;
    private float UnitCostPrice;
    private float UnitRetailsalePrice;
    private float UnitWholesalePrice;
    private String IsSuspended;
    private String VatRated;
    private String ItemImgUrl;
    private String Batchno;
    private float CurrentQty;
    private Date ItemMnfDate;
    private Date ItemExpDate;
    private String StoreName;
    private String CategoryName;
    private String UnitName;
    private String SubCategoryName;
    private int StoreId;
    private Date ItemMnfDate2;
    private Date ItemExpDate2;
    private String ReorderFilter;
    private float CostValue;
    private float RetailsaleValue;
    private float WholesaleValue;

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
     * @return the ItemCode
     */
    public String getItemCode() {
        return ItemCode;
    }

    /**
     * @param ItemCode the ItemCode to set
     */
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
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
     * @return the CategoryId
     */
    public int getCategoryId() {
        return CategoryId;
    }

    /**
     * @param CategoryId the CategoryId to set
     */
    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    /**
     * @return the SubCategoryId
     */
    public int getSubCategoryId() {
        return SubCategoryId;
    }

    /**
     * @param SubCategoryId the SubCategoryId to set
     */
    public void setSubCategoryId(int SubCategoryId) {
        this.SubCategoryId = SubCategoryId;
    }

    /**
     * @return the UnitId
     */
    public int getUnitId() {
        return UnitId;
    }

    /**
     * @param UnitId the UnitId to set
     */
    public void setUnitId(int UnitId) {
        this.UnitId = UnitId;
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

    /**
     * @return the ReorderLevel
     */
    public int getReorderLevel() {
        return ReorderLevel;
    }

    /**
     * @param ReorderLevel the ReorderLevel to set
     */
    public void setReorderLevel(int ReorderLevel) {
        this.ReorderLevel = ReorderLevel;
    }

    /**
     * @return the UnitCostPrice
     */
    public float getUnitCostPrice() {
        return UnitCostPrice;
    }

    /**
     * @param UnitCostPrice the UnitCostPrice to set
     */
    public void setUnitCostPrice(float UnitCostPrice) {
        this.UnitCostPrice = UnitCostPrice;
    }

    /**
     * @return the UnitRetailsalePrice
     */
    public float getUnitRetailsalePrice() {
        return UnitRetailsalePrice;
    }

    /**
     * @param UnitRetailsalePrice the UnitRetailsalePrice to set
     */
    public void setUnitRetailsalePrice(float UnitRetailsalePrice) {
        this.UnitRetailsalePrice = UnitRetailsalePrice;
    }

    /**
     * @return the UnitWholesalePrice
     */
    public float getUnitWholesalePrice() {
        return UnitWholesalePrice;
    }

    /**
     * @param UnitWholesalePrice the UnitWholesalePrice to set
     */
    public void setUnitWholesalePrice(float UnitWholesalePrice) {
        this.UnitWholesalePrice = UnitWholesalePrice;
    }

    /**
     * @return the IsSuspended
     */
    public String getIsSuspended() {
        return IsSuspended;
    }

    /**
     * @param IsSuspended the IsSuspended to set
     */
    public void setIsSuspended(String IsSuspended) {
        this.IsSuspended = IsSuspended;
    }

    /**
     * @return the VatRated
     */
    public String getVatRated() {
        return VatRated;
    }

    /**
     * @param VatRated the VatRated to set
     */
    public void setVatRated(String VatRated) {
        this.VatRated = VatRated;
    }

    /**
     * @return the ItemImgUrl
     */
    public String getItemImgUrl() {
        return ItemImgUrl;
    }

    /**
     * @param ItemImgUrl the ItemImgUrl to set
     */
    public void setItemImgUrl(String ItemImgUrl) {
        this.ItemImgUrl = ItemImgUrl;
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
     * @return the CurrentQty
     */
    public float getCurrentQty() {
        return CurrentQty;
    }

    /**
     * @param CurrentQty the CurrentQty to set
     */
    public void setCurrentQty(float CurrentQty) {
        this.CurrentQty = CurrentQty;
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
     * @return the CategoryName
     */
    public String getCategoryName() {
        return CategoryName;
    }

    /**
     * @param CategoryName the CategoryName to set
     */
    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    /**
     * @return the UnitName
     */
    public String getUnitName() {
        return UnitName;
    }

    /**
     * @param UnitName the UnitName to set
     */
    public void setUnitName(String UnitName) {
        this.UnitName = UnitName;
    }

    /**
     * @return the SubCategoryName
     */
    public String getSubCategoryName() {
        return SubCategoryName;
    }

    /**
     * @param SubCategoryName the SubCategoryName to set
     */
    public void setSubCategoryName(String SubCategoryName) {
        this.SubCategoryName = SubCategoryName;
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
     * @return the ItemMnfDate2
     */
    public Date getItemMnfDate2() {
        return ItemMnfDate2;
    }

    /**
     * @param ItemMnfDate2 the ItemMnfDate2 to set
     */
    public void setItemMnfDate2(Date ItemMnfDate2) {
        this.ItemMnfDate2 = ItemMnfDate2;
    }

    /**
     * @return the ItemExpDate2
     */
    public Date getItemExpDate2() {
        return ItemExpDate2;
    }

    /**
     * @param ItemExpDate2 the ItemExpDate2 to set
     */
    public void setItemExpDate2(Date ItemExpDate2) {
        this.ItemExpDate2 = ItemExpDate2;
    }

    /**
     * @return the ReorderFilter
     */
    public String getReorderFilter() {
        return ReorderFilter;
    }

    /**
     * @param ReorderFilter the ReorderFilter to set
     */
    public void setReorderFilter(String ReorderFilter) {
        this.ReorderFilter = ReorderFilter;
    }

    /**
     * @return the CostValue
     */
    public float getCostValue() {
        return CostValue;
    }

    /**
     * @param CostValue the CostValue to set
     */
    public void setCostValue(float CostValue) {
        this.CostValue = CostValue;
    }

    /**
     * @return the RetailsaleValue
     */
    public float getRetailsaleValue() {
        return RetailsaleValue;
    }

    /**
     * @param RetailsaleValue the RetailsaleValue to set
     */
    public void setRetailsaleValue(float RetailsaleValue) {
        this.RetailsaleValue = RetailsaleValue;
    }

    /**
     * @return the WholesaleValue
     */
    public float getWholesaleValue() {
        return WholesaleValue;
    }

    /**
     * @param WholesaleValue the WholesaleValue to set
     */
    public void setWholesaleValue(float WholesaleValue) {
        this.WholesaleValue = WholesaleValue;
    }

}
