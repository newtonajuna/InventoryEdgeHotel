
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
public class ReportStockSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String StoreName;
    private String CategoryName;
    private String SubCategoryName;
    private float SumCurrentQty;
    private float SumCostValue;
    private float SumWholesaleValue;
    private float SumRetailsaleValue;

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
     * @return the SumCurrentQty
     */
    public float getSumCurrentQty() {
        return SumCurrentQty;
    }

    /**
     * @param SumCurrentQty the SumCurrentQty to set
     */
    public void setSumCurrentQty(float SumCurrentQty) {
        this.SumCurrentQty = SumCurrentQty;
    }

    /**
     * @return the SumCostValue
     */
    public float getSumCostValue() {
        return SumCostValue;
    }

    /**
     * @param SumCostValue the SumCostValue to set
     */
    public void setSumCostValue(float SumCostValue) {
        this.SumCostValue = SumCostValue;
    }

    /**
     * @return the SumWholesaleValue
     */
    public float getSumWholesaleValue() {
        return SumWholesaleValue;
    }

    /**
     * @param SumWholesaleValue the SumWholesaleValue to set
     */
    public void setSumWholesaleValue(float SumWholesaleValue) {
        this.SumWholesaleValue = SumWholesaleValue;
    }

    /**
     * @return the SumRetailsaleValue
     */
    public float getSumRetailsaleValue() {
        return SumRetailsaleValue;
    }

    /**
     * @param SumRetailsaleValue the SumRetailsaleValue to set
     */
    public void setSumRetailsaleValue(float SumRetailsaleValue) {
        this.SumRetailsaleValue = SumRetailsaleValue;
    }

}
