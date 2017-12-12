import java.io.Serializable;
import javax.faces.bean.*;

@ManagedBean
@SessionScoped
public class TransactionType implements Serializable {

    private static final long serialVersionUID = 1L;
    private int TransactionTypeId;
    private String TransactionTypeName;
    private String TransactorLabel;
    private String TransactionNumberLabel;
    private String TransactionOutputLabel;
    private String BillTransactorLabel;
    private String TransactionRefLabel;
    private String TransactionDateLabel;
    private String TransactionUserLabel;
    private String IsTransactorMandatory ;
    private String IsTransactionUserMandatory;
    private String IsTransactionRefMandatory;
    private String IsAuthoriseUserMandatory;
    private String IsAuthoriseDateMandatory;
    private String IsDeliveryAddressMandatory;
    private String IsDeliveryDateMandatory;
    private String IsPayDueDateMandatory;
    private String IsExpiryDateMandatory;

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
     * @return the TransactorLabel
     */
    public String getTransactorLabel() {
        return TransactorLabel;
    }

    /**
     * @param TransactorLabel the TransactorLabel to set
     */
    public void setTransactorLabel(String TransactorLabel) {
        this.TransactorLabel = TransactorLabel;
    }

    /**
     * @return the TransactionNumberLabel
     */
    public String getTransactionNumberLabel() {
        return TransactionNumberLabel;
    }

    /**
     * @param TransactionNumberLabel the TransactionNumberLabel to set
     */
    public void setTransactionNumberLabel(String TransactionNumberLabel) {
        this.TransactionNumberLabel = TransactionNumberLabel;
    }

    /**
     * @return the TransactionOutputLabel
     */
    public String getTransactionOutputLabel() {
        return TransactionOutputLabel;
    }

    /**
     * @param TransactionOutputLabel the TransactionOutputLabel to set
     */
    public void setTransactionOutputLabel(String TransactionOutputLabel) {
        this.TransactionOutputLabel = TransactionOutputLabel;
    }

    /**
     * @return the BillTransactorLabel
     */
    public String getBillTransactorLabel() {
        return BillTransactorLabel;
    }

    /**
     * @param BillTransactorLabel the BillTransactorLabel to set
     */
    public void setBillTransactorLabel(String BillTransactorLabel) {
        this.BillTransactorLabel = BillTransactorLabel;
    }

    /**
     * @return the TransactionRefLabel
     */
    public String getTransactionRefLabel() {
        return TransactionRefLabel;
    }

    /**
     * @param TransactionRefLabel the TransactionRefLabel to set
     */
    public void setTransactionRefLabel(String TransactionRefLabel) {
        this.TransactionRefLabel = TransactionRefLabel;
    }

    /**
     * @return the TransactionDateLabel
     */
    public String getTransactionDateLabel() {
        return TransactionDateLabel;
    }

    /**
     * @param TransactionDateLabel the TransactionDateLabel to set
     */
    public void setTransactionDateLabel(String TransactionDateLabel) {
        this.TransactionDateLabel = TransactionDateLabel;
    }

    /**
     * @return the TransactionUserLabel
     */
    public String getTransactionUserLabel() {
        return TransactionUserLabel;
    }

    /**
     * @param TransactionUserLabel the TransactionUserLabel to set
     */
    public void setTransactionUserLabel(String TransactionUserLabel) {
        this.TransactionUserLabel = TransactionUserLabel;
    }

    /**
     * @return the IsTransactorMandatory
     */
    public String getIsTransactorMandatory() {
        return IsTransactorMandatory;
    }

    /**
     * @param IsTransactorMandatory the IsTransactorMandatory to set
     */
    public void setIsTransactorMandatory(String IsTransactorMandatory) {
        this.IsTransactorMandatory = IsTransactorMandatory;
    }

    /**
     * @return the IsTransactionUserMandatory
     */
    public String getIsTransactionUserMandatory() {
        return IsTransactionUserMandatory;
    }

    /**
     * @param IsTransactionUserMandatory the IsTransactionUserMandatory to set
     */
    public void setIsTransactionUserMandatory(String IsTransactionUserMandatory) {
        this.IsTransactionUserMandatory = IsTransactionUserMandatory;
    }

    /**
     * @return the IsTransactionRefMandatory
     */
    public String getIsTransactionRefMandatory() {
        return IsTransactionRefMandatory;
    }

    /**
     * @param IsTransactionRefMandatory the IsTransactionRefMandatory to set
     */
    public void setIsTransactionRefMandatory(String IsTransactionRefMandatory) {
        this.IsTransactionRefMandatory = IsTransactionRefMandatory;
    }

    /**
     * @return the IsAuthoriseUserMandatory
     */
    public String getIsAuthoriseUserMandatory() {
        return IsAuthoriseUserMandatory;
    }

    /**
     * @param IsAuthoriseUserMandatory the IsAuthoriseUserMandatory to set
     */
    public void setIsAuthoriseUserMandatory(String IsAuthoriseUserMandatory) {
        this.IsAuthoriseUserMandatory = IsAuthoriseUserMandatory;
    }

    /**
     * @return the IsAuthoriseDateMandatory
     */
    public String getIsAuthoriseDateMandatory() {
        return IsAuthoriseDateMandatory;
    }

    /**
     * @param IsAuthoriseDateMandatory the IsAuthoriseDateMandatory to set
     */
    public void setIsAuthoriseDateMandatory(String IsAuthoriseDateMandatory) {
        this.IsAuthoriseDateMandatory = IsAuthoriseDateMandatory;
    }

    /**
     * @return the IsDeliveryAddressMandatory
     */
    public String getIsDeliveryAddressMandatory() {
        return IsDeliveryAddressMandatory;
    }

    /**
     * @param IsDeliveryAddressMandatory the IsDeliveryAddressMandatory to set
     */
    public void setIsDeliveryAddressMandatory(String IsDeliveryAddressMandatory) {
        this.IsDeliveryAddressMandatory = IsDeliveryAddressMandatory;
    }

    /**
     * @return the IsDeliveryDateMandatory
     */
    public String getIsDeliveryDateMandatory() {
        return IsDeliveryDateMandatory;
    }

    /**
     * @param IsDeliveryDateMandatory the IsDeliveryDateMandatory to set
     */
    public void setIsDeliveryDateMandatory(String IsDeliveryDateMandatory) {
        this.IsDeliveryDateMandatory = IsDeliveryDateMandatory;
    }

    /**
     * @return the IsPayDueDateMandatory
     */
    public String getIsPayDueDateMandatory() {
        return IsPayDueDateMandatory;
    }

    /**
     * @param IsPayDueDateMandatory the IsPayDueDateMandatory to set
     */
    public void setIsPayDueDateMandatory(String IsPayDueDateMandatory) {
        this.IsPayDueDateMandatory = IsPayDueDateMandatory;
    }

    /**
     * @return the IsExpiryDateMandatory
     */
    public String getIsExpiryDateMandatory() {
        return IsExpiryDateMandatory;
    }

    /**
     * @param IsExpiryDateMandatory the IsExpiryDateMandatory to set
     */
    public void setIsExpiryDateMandatory(String IsExpiryDateMandatory) {
        this.IsExpiryDateMandatory = IsExpiryDateMandatory;
    }

}
