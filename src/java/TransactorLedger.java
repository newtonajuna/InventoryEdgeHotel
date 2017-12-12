
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
public class TransactorLedger implements Serializable {

    private static final long serialVersionUID = 1L;

    private long TransactorLedgerId;
    private int StoreId;
    private String StoreName;
    private long TransactionId;
    private long PayId;
    private String TransactionTypeName;
    private String Description;
    private Date TransactionDate;
    private Date TransactionDate2;
    private Date AddDate;
    private Date AddDate2;
    private long TransactorId;
    private String TransactorNames;
    private String LedgerEntryType;
    private float AmountDebit;
    private float AmountCredit;
    private float SumAmountDebit;
    private float SumAmountCredit;
    private float NetDebt;
    private float NetCredit;
    private long BillTransactorId;
    private String BillTransactorNames;
    //just for display
    private float NetDebtCreditAmount;
    private String NetDebtCreditLabel;
//Hotel
    private String ItemDescription;
    private float Quantity;
    private float UnitPrice;
    private float UnitTradeDiscount;
    private String PostedBy;
    private String Remarks;

    private int GuestFolioId;

    private int NumberOfDays;
    private int NumberOfPeople;

    public int getNumberOfDays() {
        return NumberOfDays;
    }

    public int getGuestFolioId() {
        return GuestFolioId;
    }

    public void setGuestFolioId(int GuestFolioId) {
        this.GuestFolioId = GuestFolioId;
    }

    public void setNumberOfDays(int NumberOfDays) {
        this.NumberOfDays = NumberOfDays;
    }

    public int getNumberOfPeople() {
        return NumberOfPeople;
    }

    public void setNumberOfPeople(int NumberOfPeople) {
        this.NumberOfPeople = NumberOfPeople;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    private String CurrencyTypeName;
    private float ExchangeRate;

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

    public String getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(String PostedBy) {
        this.PostedBy = PostedBy;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float Quantity) {
        this.Quantity = Quantity;
    }

    public float getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(float UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    public float getUnitTradeDiscount() {
        return UnitTradeDiscount;
    }

    public void setUnitTradeDiscount(float UnitTradeDiscount) {
        this.UnitTradeDiscount = UnitTradeDiscount;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String ItemDescription) {
        this.ItemDescription = ItemDescription;
    }

    /**
     * @return the TransactorLedgerId
     */
    public long getTransactorLedgerId() {
        return TransactorLedgerId;
    }

    /**
     * @param TransactorLedgerId the TransactorLedgerId to set
     */
    public void setTransactorLedgerId(long TransactorLedgerId) {
        this.TransactorLedgerId = TransactorLedgerId;
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
     * @return the TransactionDate2
     */
    public Date getTransactionDate2() {
        return TransactionDate2;
    }

    /**
     * @param TransactionDate2 the TransactionDate2 to set
     */
    public void setTransactionDate2(Date TransactionDate2) {
        this.TransactionDate2 = TransactionDate2;
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
     * @return the LedgerEntryType
     */
    public String getLedgerEntryType() {
        return LedgerEntryType;
    }

    /**
     * @param LedgerEntryType the LedgerEntryType to set
     */
    public void setLedgerEntryType(String LedgerEntryType) {
        this.LedgerEntryType = LedgerEntryType;
    }

    /**
     * @return the AmountDebit
     */
    public float getAmountDebit() {
        return AmountDebit;
    }

    /**
     * @param AmountDebit the AmountDebit to set
     */
    public void setAmountDebit(float AmountDebit) {
        this.AmountDebit = AmountDebit;
    }

    /**
     * @return the AmountCredit
     */
    public float getAmountCredit() {
        return AmountCredit;
    }

    /**
     * @param AmountCredit the AmountCredit to set
     */
    public void setAmountCredit(float AmountCredit) {
        this.AmountCredit = AmountCredit;
    }

    /**
     * @return the SumAmountDebit
     */
    public float getSumAmountDebit() {
        return SumAmountDebit;
    }

    /**
     * @param SumAmountDebit the SumAmountDebit to set
     */
    public void setSumAmountDebit(float SumAmountDebit) {
        this.SumAmountDebit = SumAmountDebit;
    }

    /**
     * @return the SumAmountCredit
     */
    public float getSumAmountCredit() {
        return SumAmountCredit;
    }

    /**
     * @param SumAmountCredit the SumAmountCredit to set
     */
    public void setSumAmountCredit(float SumAmountCredit) {
        this.SumAmountCredit = SumAmountCredit;
    }

    /**
     * @return the NetDebt
     */
    public float getNetDebt() {
        return NetDebt;
    }

    /**
     * @param NetDebt the NetDebt to set
     */
    public void setNetDebt(float NetDebt) {
        this.NetDebt = NetDebt;
    }

    /**
     * @return the NetCredit
     */
    public float getNetCredit() {
        return NetCredit;
    }

    /**
     * @param NetCredit the NetCredit to set
     */
    public void setNetCredit(float NetCredit) {
        this.NetCredit = NetCredit;
    }

    /**
     * @return the NetDebtCreditAmount
     */
    public float getNetDebtCreditAmount() {
        return NetDebtCreditAmount;
    }

    /**
     * @param NetDebtCreditAmount the NetDebtCreditAmount to set
     */
    public void setNetDebtCreditAmount(float NetDebtCreditAmount) {
        this.NetDebtCreditAmount = NetDebtCreditAmount;
    }

    /**
     * @return the NetDebtCreditLabel
     */
    public String getNetDebtCreditLabel() {
        return NetDebtCreditLabel;
    }

    /**
     * @param NetDebtCreditLabel the NetDebtCreditLabel to set
     */
    public void setNetDebtCreditLabel(String NetDebtCreditLabel) {
        this.NetDebtCreditLabel = NetDebtCreditLabel;
    }

    /**
     * @return the BillTransactorId
     */
    public long getBillTransactorId() {
        return BillTransactorId;
    }

    @ManagedProperty(value = "#{guestFolioBean}")
    private GuestFolioBean guestFolioBean;

    public GuestFolioBean getGuestFolioBean() {
        return guestFolioBean;
    }

    public void setGuestFolioBean(GuestFolioBean guestFolioBean) {
        this.guestFolioBean = guestFolioBean;
    }

    /**
     * @param BillTransactorId the BillTransactorId to set
     */
    public void setBillTransactorId(long BillTransactorId) {
        this.BillTransactorId = BillTransactorId;
        //guestFolioBean.set_guest_folio_null();
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

}
