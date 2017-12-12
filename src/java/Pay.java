
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
public class Pay implements Serializable {

    private static final long serialVersionUID = 1L;
    private long PayId;
    private long TransactionId;
    private Date PayDate;
    private float PaidAmount;
    private int PayMethodId;
    private int AddUserDetailId;
    private int EditUserDetailId;
    private Date AddDate;
    private Date EditDate;
    private float PointsSpent;
    private float PointsSpentAmount;
    private long DeletePayId;
    private String PayRefNo;
    private String PayCategory;
    private long BillTransactorId;
    private int TransactionTypeId;
    private int TransactionReasonId;
    private int StoreId;
    private int CurrencyTypeId;
    private String CurrencyTypeName;
    private float ExchangeRate;
    
    private float surcharge;
    private float total_amount;

    public String getCurrencyTypeName() {
        return CurrencyTypeName;
    }

    public void setCurrencyTypeName(String CurrencyTypeName) {
        this.CurrencyTypeName = CurrencyTypeName;
    }

    public float getExchangeRate() {
        return ExchangeRate;
    }

    public void setExchangeRate(float ExchangeRate) {
        this.ExchangeRate = ExchangeRate;
    }

    public int getCurrencyTypeId() {
        return CurrencyTypeId;
    }

    public void setCurrencyTypeId(int CurrencyTypeId) {
        this.CurrencyTypeId = CurrencyTypeId;
    }

    /**
     * @return the PayId
     */
    public long getPayId() {
        return PayId;
    }

    /**
     * @param PayId the PayId to set
     */
    public void setPayId(long PayId) {
        this.PayId = PayId;
    }

    /**
     * @return the TransactionId
     */
    public long getTransactionId() {
        return TransactionId;
    }

    /**
     * @param TransactionId the TransactionId to set
     */
    public void setTransactionId(long TransactionId) {
        this.TransactionId = TransactionId;
    }

    /**
     * @return the PayDate
     */
    public Date getPayDate() {
        return PayDate;
    }

    /**
     * @param PayDate the PayDate to set
     */
    public void setPayDate(Date PayDate) {
        this.PayDate = PayDate;
    }

    /**
     * @return the PaidAmount
     */
    public float getPaidAmount() {
        return PaidAmount;
    }

    /**
     * @param PaidAmount the PaidAmount to set
     */
    public void setPaidAmount(float PaidAmount) {
        this.PaidAmount = PaidAmount;
    }

    /**
     * @return the PayMethodId
     */
    public int getPayMethodId() {
        return PayMethodId;
    }

    /**
     * @param PayMethodId the PayMethodId to set
     */
    public void setPayMethodId(int PayMethodId) {
        this.PayMethodId = PayMethodId;
    }

    /**
     * @return the AddUserDetailId
     */
    public int getAddUserDetailId() {
        return AddUserDetailId;
    }

    /**
     * @param AddUserDetailId the AddUserDetailId to set
     */
    public void setAddUserDetailId(int AddUserDetailId) {
        this.AddUserDetailId = AddUserDetailId;
    }

    /**
     * @return the EditUserDetailId
     */
    public int getEditUserDetailId() {
        return EditUserDetailId;
    }

    /**
     * @param EditUserDetailId the EditUserDetailId to set
     */
    public void setEditUserDetailId(int EditUserDetailId) {
        this.EditUserDetailId = EditUserDetailId;
    }

    /**
     * @return the AddDate
     */
    public Date getAddDate() {
        return AddDate;
    }

    /**
     * @param AddDate the AddDate to set
     */
    public void setAddDate(Date AddDate) {
        this.AddDate = AddDate;
    }

    /**
     * @return the EditDate
     */
    public Date getEditDate() {
        return EditDate;
    }

    /**
     * @param EditDate the EditDate to set
     */
    public void setEditDate(Date EditDate) {
        this.EditDate = EditDate;
    }

    /**
     * @return the PointsSpentAmount
     */
    public float getPointsSpentAmount() {
        return PointsSpentAmount;
    }

    /**
     * @param PointsSpentAmount the PointsSpentAmount to set
     */
    public void setPointsSpentAmount(float PointsSpentAmount) {
        this.PointsSpentAmount = PointsSpentAmount;
    }

    /**
     * @return the DeletePayId
     */
    public long getDeletePayId() {
        return DeletePayId;
    }

    /**
     * @param DeletePayId the DeletePayId to set
     */
    public void setDeletePayId(long DeletePayId) {
        this.DeletePayId = DeletePayId;
    }

    /**
     * @return the PointsSpent
     */
    public float getPointsSpent() {
        return PointsSpent;
    }

    /**
     * @param PointsSpent the PointsSpent to set
     */
    public void setPointsSpent(float PointsSpent) {
        this.PointsSpent = PointsSpent;
    }

    /**
     * @return the PayRefNo
     */
    public String getPayRefNo() {
        return PayRefNo;
    }

    /**
     * @param PayRefNo the PayRefNo to set
     */
    public void setPayRefNo(String PayRefNo) {
        this.PayRefNo = PayRefNo;
    }

    /**
     * @return the PayCategory
     */
    public String getPayCategory() {
        return PayCategory;
    }

    /**
     * @param PayCategory the PayCategory to set
     */
    public void setPayCategory(String PayCategory) {
        this.PayCategory = PayCategory;
    }

    /**
     * @return the BillTransactorId
     */
    public long getBillTransactorId() {
        return BillTransactorId;
    }

    /**
     * @param BillTransactorId the BillTransactorId to set
     */
    public void setBillTransactorId(long BillTransactorId) {
        this.BillTransactorId = BillTransactorId;
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
     * @return the TransactionReasonId
     */
    public int getTransactionReasonId() {
        return TransactionReasonId;
    }

    /**
     * @param TransactionReasonId the TransactionReasonId to set
     */
    public void setTransactionReasonId(int TransactionReasonId) {
        this.TransactionReasonId = TransactionReasonId;
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

    public float getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(float surcharge) {
        this.surcharge = surcharge;
    }

    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }
    
    
    
}
