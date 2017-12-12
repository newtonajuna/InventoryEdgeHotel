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
public class PointsTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private long PointsTransactionId;
    private long PointsCardId;
    private Date TransactionDate;
    private float PointsAwarded;
    private float PointsSpent;
    private long TransactionId;
    private int TransBranchId;
    private Date AddDate;
    private String AddUser;
    private Date EditDate;
    private String EditUser;
    private float PointsSpentAmount;

    /**
     * @return the PointsTransactionId
     */
    public long getPointsTransactionId() {
        return PointsTransactionId;
    }

    /**
     * @param PointsTransactionId the PointsTransactionId to set
     */
    public void setPointsTransactionId(long PointsTransactionId) {
        this.PointsTransactionId = PointsTransactionId;
    }

    /**
     * @return the PointsCardId
     */
    public long getPointsCardId() {
        return PointsCardId;
    }

    /**
     * @param PointsCardId the PointsCardId to set
     */
    public void setPointsCardId(long PointsCardId) {
        this.PointsCardId = PointsCardId;
    }

    /**
     * @return the TransactionDate
     */
    public Date getTransactionDate() {
        return TransactionDate;
    }

    /**
     * @param TransactionDate the TransactionDate to set
     */
    public void setTransactionDate(Date TransactionDate) {
        this.TransactionDate = TransactionDate;
    }

    /**
     * @return the PointsAwarded
     */
    public float getPointsAwarded() {
        return PointsAwarded;
    }

    /**
     * @param PointsAwarded the PointsAwarded to set
     */
    public void setPointsAwarded(float PointsAwarded) {
        this.PointsAwarded = PointsAwarded;
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
     * @return the TransBranchId
     */
    public int getTransBranchId() {
        return TransBranchId;
    }

    /**
     * @param TransBranchId the TransBranchId to set
     */
    public void setTransBranchId(int TransBranchId) {
        this.TransBranchId = TransBranchId;
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
     * @return the AddUser
     */
    public String getAddUser() {
        return AddUser;
    }

    /**
     * @param AddUser the AddUser to set
     */
    public void setAddUser(String AddUser) {
        this.AddUser = AddUser;
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
     * @return the EditUser
     */
    public String getEditUser() {
        return EditUser;
    }

    /**
     * @param EditUser the EditUser to set
     */
    public void setEditUser(String EditUser) {
        this.EditUser = EditUser;
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
}