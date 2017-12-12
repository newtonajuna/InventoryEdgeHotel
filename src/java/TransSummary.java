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
public class TransSummary implements Serializable {

    private static final long serialVersionUID = 1L;
    private String FieldName;
    private float SumTotalTradeDiscount;
    private float SumTotalVat;
    private float SumCashDiscount;
    private float SumGrandTotal;
    private float SumTotalStdVatableAmount;
    private float SumTotalZeroVatableAmount;
    private float SumTotalExemptVatableAmount;
    private float SumTotalProfitMargin;
    private int StoreId;
    private int TransactionTypeId;
    private int EarnUserId;
    private float TotalEarnAmount;
    

    /**
     * @return the FieldName
     */
    public String getFieldName() {
        return FieldName;
    }

    /**
     * @param FieldName the FieldName to set
     */
    public void setFieldName(String FieldName) {
        this.FieldName = FieldName;
    }

    /**
     * @return the SumTotalTradeDiscount
     */
    public float getSumTotalTradeDiscount() {
        return SumTotalTradeDiscount;
    }

    /**
     * @param SumTotalTradeDiscount the SumTotalTradeDiscount to set
     */
    public void setSumTotalTradeDiscount(float SumTotalTradeDiscount) {
        this.SumTotalTradeDiscount = SumTotalTradeDiscount;
    }

    /**
     * @return the SumTotalVat
     */
    public float getSumTotalVat() {
        return SumTotalVat;
    }

    /**
     * @param SumTotalVat the SumTotalVat to set
     */
    public void setSumTotalVat(float SumTotalVat) {
        this.SumTotalVat = SumTotalVat;
    }

    /**
     * @return the SumCashDiscount
     */
    public float getSumCashDiscount() {
        return SumCashDiscount;
    }

    /**
     * @param SumCashDiscount the SumCashDiscount to set
     */
    public void setSumCashDiscount(float SumCashDiscount) {
        this.SumCashDiscount = SumCashDiscount;
    }

    /**
     * @return the SumGrandTotal
     */
    public float getSumGrandTotal() {
        return SumGrandTotal;
    }

    /**
     * @param SumGrandTotal the SumGrandTotal to set
     */
    public void setSumGrandTotal(float SumGrandTotal) {
        this.SumGrandTotal = SumGrandTotal;
    }

    /**
     * @return the SumTotalStdVatableAmount
     */
    public float getSumTotalStdVatableAmount() {
        return SumTotalStdVatableAmount;
    }

    /**
     * @param SumTotalStdVatableAmount the SumTotalStdVatableAmount to set
     */
    public void setSumTotalStdVatableAmount(float SumTotalStdVatableAmount) {
        this.SumTotalStdVatableAmount = SumTotalStdVatableAmount;
    }

    /**
     * @return the SumTotalZeroVatableAmount
     */
    public float getSumTotalZeroVatableAmount() {
        return SumTotalZeroVatableAmount;
    }

    /**
     * @param SumTotalZeroVatableAmount the SumTotalZeroVatableAmount to set
     */
    public void setSumTotalZeroVatableAmount(float SumTotalZeroVatableAmount) {
        this.SumTotalZeroVatableAmount = SumTotalZeroVatableAmount;
    }

    /**
     * @return the SumTotalExemptVatableAmount
     */
    public float getSumTotalExemptVatableAmount() {
        return SumTotalExemptVatableAmount;
    }

    /**
     * @param SumTotalExemptVatableAmount the SumTotalExemptVatableAmount to set
     */
    public void setSumTotalExemptVatableAmount(float SumTotalExemptVatableAmount) {
        this.SumTotalExemptVatableAmount = SumTotalExemptVatableAmount;
    }

    /**
     * @return the SumTotalProfitMargin
     */
    public float getSumTotalProfitMargin() {
        return SumTotalProfitMargin;
    }

    /**
     * @param SumTotalProfitMargin the SumTotalProfitMargin to set
     */
    public void setSumTotalProfitMargin(float SumTotalProfitMargin) {
        this.SumTotalProfitMargin = SumTotalProfitMargin;
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
     * @return the TransactionTypeId
     */
    public int getTransactionTypeId() {
        return TransactionTypeId;
    }

    /**
     * @param TransactionTypeId the TransactionTypeId to set
     */
    public void setTransactionTypeId(int TransactionTypeId) {
        this.TransactionTypeId = TransactionTypeId;
    }

    /**
     * @return the EarnUserId
     */
    public int getEarnUserId() {
        return EarnUserId;
    }

    /**
     * @param EarnUserId the EarnUserId to set
     */
    public void setEarnUserId(int EarnUserId) {
        this.EarnUserId = EarnUserId;
    }

    /**
     * @return the TotalEarnAmount
     */
    public float getTotalEarnAmount() {
        return TotalEarnAmount;
    }

    /**
     * @param TotalEarnAmount the TotalEarnAmount to set
     */
    public void setTotalEarnAmount(float TotalEarnAmount) {
        this.TotalEarnAmount = TotalEarnAmount;
    }

}
