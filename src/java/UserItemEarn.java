
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
public class UserItemEarn implements Serializable {

    private static final long serialVersionUID = 1L;
    private long UserItemEarnId;
    private int TransactionTypeId;
    private int TransactionReasonId;
    private int UserCategoryId;
    private int ItemCategoryId;
    private int ItemSubCategoryId;
    private float EarnPerc;

    /**
     * @return the UserItemEarnId
     */
    public long getUserItemEarnId() {
        return UserItemEarnId;
    }

    /**
     * @param UserItemEarnId the UserItemEarnId to set
     */
    public void setUserItemEarnId(long UserItemEarnId) {
        this.UserItemEarnId = UserItemEarnId;
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
     * @return the UserCategoryId
     */
    public int getUserCategoryId() {
        return UserCategoryId;
    }

    /**
     * @param UserCategoryId the UserCategoryId to set
     */
    public void setUserCategoryId(int UserCategoryId) {
        this.UserCategoryId = UserCategoryId;
    }

    /**
     * @return the ItemCategoryId
     */
    public int getItemCategoryId() {
        return ItemCategoryId;
    }

    /**
     * @param ItemCategoryId the ItemCategoryId to set
     */
    public void setItemCategoryId(int ItemCategoryId) {
        this.ItemCategoryId = ItemCategoryId;
    }

    /**
     * @return the ItemSubCategoryId
     */
    public int getItemSubCategoryId() {
        return ItemSubCategoryId;
    }

    /**
     * @param ItemSubCategoryId the ItemSubCategoryId to set
     */
    public void setItemSubCategoryId(int ItemSubCategoryId) {
        this.ItemSubCategoryId = ItemSubCategoryId;
    }

    /**
     * @return the EarnPerc
     */
    public float getEarnPerc() {
        return EarnPerc;
    }

    /**
     * @param EarnPerc the EarnPerc to set
     */
    public void setEarnPerc(float EarnPerc) {
        this.EarnPerc = EarnPerc;
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

}
