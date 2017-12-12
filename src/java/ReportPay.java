
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
public class ReportPay implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long PayId;
    private long TransactionId;
    private Date PayDate;
    private Date PayDate2;
    private float PaidAmount;
    private int PayMethodId;
    private int AddUserDetailId;
    private int EditUserDetailId;
    private Date AddDate;
    private Date AddDate2;
    private Date EditDate;
    private Date EditDate2;
    private float PointsSpent;
    private float PointsSpentAmount;
    private String AddUserNames;
    private String EditUserNames;
    private long BillTransactorId;
    private String BillTransactorNames;
    private int StoreId;
    private String StoreName;
    private int TransactionTypeId;
    private String TransactionTypeName;
    private String PayMethodName;
    private boolean OtherDates;
    
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
     * @return the PayDate2
     */
    public Date getPayDate2() {
        return PayDate2;
    }

    /**
     * @param PayDate2 the PayDate2 to set
     */
    public void setPayDate2(Date PayDate2) {
        this.PayDate2 = PayDate2;
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
     * @return the AddDate2
     */
    public Date getAddDate2() {
        return AddDate2;
    }

    /**
     * @param AddDate2 the AddDate2 to set
     */
    public void setAddDate2(Date AddDate2) {
        this.AddDate2 = AddDate2;
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
     * @return the EditDate2
     */
    public Date getEditDate2() {
        return EditDate2;
    }

    /**
     * @param EditDate2 the EditDate2 to set
     */
    public void setEditDate2(Date EditDate2) {
        this.EditDate2 = EditDate2;
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
     * @return the AddUserNames
     */
    public String getAddUserNames() {
        return AddUserNames;
    }

    /**
     * @param AddUserNames the AddUserNames to set
     */
    public void setAddUserNames(String AddUserNames) {
        this.AddUserNames = AddUserNames;
    }

    /**
     * @return the EditUserNames
     */
    public String getEditUserNames() {
        return EditUserNames;
    }

    /**
     * @param EditUserNames the EditUserNames to set
     */
    public void setEditUserNames(String EditUserNames) {
        this.EditUserNames = EditUserNames;
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
     * @return the BillTransactorNames
     */
    public String getBillTransactorNames() {
        return BillTransactorNames;
    }

    /**
     * @param BillTransactorNames the BillTransactorNames to set
     */
    public void setBillTransactorNames(String BillTransactorNames) {
        this.BillTransactorNames = BillTransactorNames;
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
     * @return the TransactionTypeName
     */
    public String getTransactionTypeName() {
        return TransactionTypeName;
    }

    /**
     * @param TransactionTypeName the TransactionTypeName to set
     */
    public void setTransactionTypeName(String TransactionTypeName) {
        this.TransactionTypeName = TransactionTypeName;
    }

    /**
     * @return the PayMethodName
     */
    public String getPayMethodName() {
        return PayMethodName;
    }

    /**
     * @param PayMethodName the PayMethodName to set
     */
    public void setPayMethodName(String PayMethodName) {
        this.PayMethodName = PayMethodName;
    }  

    /**
     * @return the OtherDates
     */
    public boolean isOtherDates() {
        return OtherDates;
    }

    /**
     * @param OtherDates the OtherDates to set
     */
    public void setOtherDates(boolean OtherDates) {
        this.OtherDates = OtherDates;
    }

    
}
