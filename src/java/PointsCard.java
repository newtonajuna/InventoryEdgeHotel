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
public class PointsCard implements Serializable {
    private static final long serialVersionUID = 1L;
    private long PointsCardId;
    private String CardNumber;
    private String CardHolder;
    private String Email;
    private String Phone;
    private String Website;
    private String Cpname;
    private String Cptitle;
    private String Cpphone;
    private String Cpemail;
    private String PhysicalAddress;
    private String TaxIdentity;
    private String AccountDetails;
    private int RegBranchId;
    private float PointsBalance;
    private Date AddDate;
    private String AddUser;
    private Date EditDate;
    private String EditUser;

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
     * @return the CardNumber
     */
    public String getCardNumber() {
        return CardNumber;
    }

    /**
     * @param CardNumber the CardNumber to set
     */
    public void setCardNumber(String CardNumber) {
        this.CardNumber = CardNumber;
    }

    /**
     * @return the CardHolder
     */
    public String getCardHolder() {
        return CardHolder;
    }

    /**
     * @param CardHolder the CardHolder to set
     */
    public void setCardHolder(String CardHolder) {
        this.CardHolder = CardHolder;
    }

    /**
     * @return the Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param Email the Email to set
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * @return the Phone
     */
    public String getPhone() {
        return Phone;
    }

    /**
     * @param Phone the Phone to set
     */
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    /**
     * @return the Website
     */
    public String getWebsite() {
        return Website;
    }

    /**
     * @param Website the Website to set
     */
    public void setWebsite(String Website) {
        this.Website = Website;
    }

    /**
     * @return the Cpname
     */
    public String getCpname() {
        return Cpname;
    }

    /**
     * @param Cpname the Cpname to set
     */
    public void setCpname(String Cpname) {
        this.Cpname = Cpname;
    }

    /**
     * @return the Cptitle
     */
    public String getCptitle() {
        return Cptitle;
    }

    /**
     * @param Cptitle the Cptitle to set
     */
    public void setCptitle(String Cptitle) {
        this.Cptitle = Cptitle;
    }

    /**
     * @return the Cpphone
     */
    public String getCpphone() {
        return Cpphone;
    }

    /**
     * @param Cpphone the Cpphone to set
     */
    public void setCpphone(String Cpphone) {
        this.Cpphone = Cpphone;
    }

    /**
     * @return the Cpemail
     */
    public String getCpemail() {
        return Cpemail;
    }

    /**
     * @param Cpemail the Cpemail to set
     */
    public void setCpemail(String Cpemail) {
        this.Cpemail = Cpemail;
    }

    /**
     * @return the PhysicalAddress
     */
    public String getPhysicalAddress() {
        return PhysicalAddress;
    }

    /**
     * @param PhysicalAddress the PhysicalAddress to set
     */
    public void setPhysicalAddress(String PhysicalAddress) {
        this.PhysicalAddress = PhysicalAddress;
    }

    /**
     * @return the TaxIdentity
     */
    public String getTaxIdentity() {
        return TaxIdentity;
    }

    /**
     * @param TaxIdentity the TaxIdentity to set
     */
    public void setTaxIdentity(String TaxIdentity) {
        this.TaxIdentity = TaxIdentity;
    }

    /**
     * @return the AccountDetails
     */
    public String getAccountDetails() {
        return AccountDetails;
    }

    /**
     * @param AccountDetails the AccountDetails to set
     */
    public void setAccountDetails(String AccountDetails) {
        this.AccountDetails = AccountDetails;
    }

    /**
     * @return the RegBranchId
     */
    public int getRegBranchId() {
        return RegBranchId;
    }

    /**
     * @param RegBranchId the RegBranchId to set
     */
    public void setRegBranchId(int RegBranchId) {
        this.RegBranchId = RegBranchId;
    }

    /**
     * @return the PointsBalance
     */
    public float getPointsBalance() {
        return PointsBalance;
    }

    /**
     * @param PointsBalance the PointsBalance to set
     */
    public void setPointsBalance(float PointsBalance) {
        this.PointsBalance = PointsBalance;
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
}
