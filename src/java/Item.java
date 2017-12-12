
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
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    private long ItemId;
    private String ItemCode;
    private String Description;
    private int CategoryId;
    private String CategoryName;
    private Integer SubCategoryId=null;
    private String SubCategoryName;
    private int UnitId;
    private String UnitSymbol;
    private int ReorderLevel;
    private float UnitCostPrice;
    private float UnitRetailsalePrice;
    private float UnitWholesalePrice;
    private String IsSuspended;
    private String VatRated;
    private String ItemImgUrl;
    private int CountItems;//for report only
    private String ItemType;
    
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
     * @return the SubCategoryId
     */
    public Integer getSubCategoryId() {
        return SubCategoryId;
    }

    /**
     * @param SubCategoryId the SubCategoryId to set
     */
    public void setSubCategoryId(Integer SubCategoryId) {
        this.SubCategoryId = SubCategoryId;
    }
    public void updateModelItem(Item i){
        this.ItemId=i.getItemId();
        this.UnitId=i.getUnitId();
        this.UnitSymbol=i.getUnitSymbol();
        this.UnitRetailsalePrice=i.getUnitRetailsalePrice();
    }

    /**
     * @return the CountItems
     */
    public int getCountItems() {
        return CountItems;
    }

    /**
     * @param CountItems the CountItems to set
     */
    public void setCountItems(int CountItems) {
        this.CountItems = CountItems;
    }

    /**
     * @return the ItemType
     */
    public String getItemType() {
        return ItemType;
    }

    /**
     * @param ItemType the ItemType to set
     */
    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

}
