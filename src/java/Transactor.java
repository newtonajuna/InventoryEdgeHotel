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
public class Transactor implements Serializable {

    private static final long serialVersionUID = 1L;
    private long TransactorId;
    private String TransactorType;
    private String TransactorNames;
    private String Phone;
    private String Email;
    private String Website;
    private String CpName;
    private String CpTitle;
    private String CpPhone;
    private String CpEmail;
    private String PhysicalAddress;
    private String TaxIdentity;
    private String AccountDetails;
    private String CardNumber;
    private Date DOB;
    private String IsSuspended;
    private String SuspendedReason;
    private String Category;
    private String Sex;
    private String Occupation;
    private String LocCountry;
    private String LocDistrict;
    private String LocTown;
    private Date FirstDate;
    private String FileReference;
    private String IdType;
    private String IdNumber;
    private Date IdExpiryDate;

    /**
     * @return the TransactorId
     */
    public long getTransactorId() {
        return TransactorId;
    }

    /**
     * @param TransactorId the TransactorId to set
     */
    public void setTransactorId(long TransactorId) {
        this.TransactorId = TransactorId;
    }

    /**
     * @return the TransactorType
     */
    public String getTransactorType() {
        return TransactorType;
    }

    /**
     * @param TransactorType the TransactorType to set
     */
    public void setTransactorType(String TransactorType) {
        this.TransactorType = TransactorType;
    }

    /**
     * @return the TransactorNames
     */
    public String getTransactorNames() {
        return TransactorNames;
    }

    /**
     * @param TransactorNames the TransactorNames to set
     */
    public void setTransactorNames(String TransactorNames) {
        this.TransactorNames = TransactorNames;
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
     * @return the CpName
     */
    public String getCpName() {
        return CpName;
    }

    /**
     * @param CpName the CpName to set
     */
    public void setCpName(String CpName) {
        this.CpName = CpName;
    }

    /**
     * @return the CpTitle
     */
    public String getCpTitle() {
        return CpTitle;
    }

    /**
     * @param CpTitle the CpTitle to set
     */
    public void setCpTitle(String CpTitle) {
        this.CpTitle = CpTitle;
    }

    /**
     * @return the CpPhone
     */
    public String getCpPhone() {
        return CpPhone;
    }

    /**
     * @param CpPhone the CpPhone to set
     */
    public void setCpPhone(String CpPhone) {
        this.CpPhone = CpPhone;
    }

    /**
     * @return the CpEmail
     */
    public String getCpEmail() {
        return CpEmail;
    }

    /**
     * @param CpEmail the CpEmail to set
     */
    public void setCpEmail(String CpEmail) {
        this.CpEmail = CpEmail;
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
     * @return the DOB
     */
    public Date getDOB() {
        return DOB;
    }

    /**
     * @param DOB the DOB to set
     */
    public void setDOB(Date DOB) {
        this.DOB = DOB;
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
     * @return the SuspendedReason
     */
    public String getSuspendedReason() {
        return SuspendedReason;
    }

    /**
     * @param SuspendedReason the SuspendedReason to set
     */
    public void setSuspendedReason(String SuspendedReason) {
        this.SuspendedReason = SuspendedReason;
    }

    /**
     * @return the Category
     */
    public String getCategory() {
        return Category;
    }

    /**
     * @param Category the Category to set
     */
    public void setCategory(String Category) {
        this.Category = Category;
    }

    /**
     * @return the Sex
     */
    public String getSex() {
        return Sex;
    }

    /**
     * @param Sex the Sex to set
     */
    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    /**
     * @return the Occupation
     */
    public String getOccupation() {
        return Occupation;
    }

    /**
     * @param Occupation the Occupation to set
     */
    public void setOccupation(String Occupation) {
        this.Occupation = Occupation;
    }

    /**
     * @return the LocCountry
     */
    public String getLocCountry() {
        return LocCountry;
    }

    /**
     * @param LocCountry the LocCountry to set
     */
    public void setLocCountry(String LocCountry) {
        this.LocCountry = LocCountry;
    }

    /**
     * @return the LocDistrict
     */
    public String getLocDistrict() {
        return LocDistrict;
    }

    /**
     * @param LocDistrict the LocDistrict to set
     */
    public void setLocDistrict(String LocDistrict) {
        this.LocDistrict = LocDistrict;
    }

    /**
     * @return the LocTown
     */
    public String getLocTown() {
        return LocTown;
    }

    /**
     * @param LocTown the LocTown to set
     */
    public void setLocTown(String LocTown) {
        this.LocTown = LocTown;
    }

    /**
     * @return the FirstDate
     */
    public Date getFirstDate() {
        return FirstDate;
    }

    /**
     * @param FirstDate the FirstDate to set
     */
    public void setFirstDate(Date FirstDate) {
        this.FirstDate = FirstDate;
    }

    /**
     * @return the FileReference
     */
    public String getFileReference() {
        return FileReference;
    }

    /**
     * @param FileReference the FileReference to set
     */
    public void setFileReference(String FileReference) {
        this.FileReference = FileReference;
    }

    /**
     * @return the IdType
     */
    public String getIdType() {
        return IdType;
    }

    /**
     * @param IdType the IdType to set
     */
    public void setIdType(String IdType) {
        this.IdType = IdType;
    }

    /**
     * @return the IdNumber
     */
    public String getIdNumber() {
        return IdNumber;
    }

    /**
     * @param IdNumber the IdNumber to set
     */
    public void setIdNumber(String IdNumber) {
        this.IdNumber = IdNumber;
    }

    /**
     * @return the IdExpiryDate
     */
    public Date getIdExpiryDate() {
        return IdExpiryDate;
    }

    /**
     * @param IdExpiryDate the IdExpiryDate to set
     */
    public void setIdExpiryDate(Date IdExpiryDate) {
        this.IdExpiryDate = IdExpiryDate;
    }

}
