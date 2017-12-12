
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.joda.time.Days;
import org.joda.time.LocalDate;

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
public class TransItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long TransactionItemId;
    private long TransactionId;
    private long ItemId;
    private String Batchno;
    private float ItemQty;
    private float UnitPrice;
    private float Amount;
    private Date ItemExpryDate;
    private Date ItemMnfDate;
    private float UnitVat;
    private float UnitTradeDiscount;
    private String VatRated;
    private float VatPerc;
    private String ItemCode;
    private int keyCode;
    private float UnitPriceIncVat;
    private float UnitPriceExcVat;
    private float AmountIncVat;
    private float AmountExcVat;
    private long ItemId2;
    private float ItemQty2;
    private float FractionQty;
    private String StockEffect;
    private String IsTradeDiscountVatLiable;
    
    private boolean OverridePrices;
    private float UnitPrice2;
    private float UnitTradeDiscount2;
    private String VatRated2;

    //variables for report ONLY
    private String Description;
    private String UnitSymbol;
    private int StoreId;
    private int Store2Id;
    private Date TransactionDate;
    private Date AddDate;
    private Date EditDate;
    private long TransactorId;
    private int TransactionTypeId;
    private int TransactionReasonId;
    private int AddUserDetailId;
    private int EditUserDetailId;
    private String StoreName;
    private String StoreName2;
    private String AddUserDetailName;
    private String EditUserDetailName;
    private String TransactorNames;
    private String TransactionTypeName;
    private String TransactionReasonName;
    private int TransactionUserDetailId;
    private String TransactionUserDetailName;
    private long BillTransactorId;
    private String BillTransactorName;

    //for profit margin
    private float UnitCostPrice;
    private float UnitProfitMargin;

    //for user transaction earning
    private float EarnPerc;
    private float EarnAmount;

    //for bill repprt summary
    private String CategoryName;
    private float SumAmountIncVat;
    private float SumAmountExcVat;

    /*Hotel Items*/
    private int CurrencyTypeId;
    private float ExchangeRate;
    private String Remarks;
    
    public String getRemarks() {
        return Remarks;
    }
    
    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
    
    public int getCurrencyTypeId() {
        return CurrencyTypeId;
    }
    
    public void setCurrencyTypeId(int CurrencyTypeId) {
        this.CurrencyTypeId = CurrencyTypeId;
    }
    
    public float getExchangeRate() {
        return ExchangeRate;
    }
    
    public void setExchangeRate(float ExchangeRate) {
        this.ExchangeRate = ExchangeRate;
    }

    /**
     * @return the TransactionItemId
     */
    public long getTransactionItemId() {
        return TransactionItemId;
    }

    /**
     * @param TransactionItemId the TransactionItemId to set
     */
    public void setTransactionItemId(long TransactionItemId) {
        this.TransactionItemId = TransactionItemId;
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
     * @return the ItemId
     */
    public long getItemId() {
        return ItemId;
    }

    /**
     * @param ItemId the ItemId to set
     */
    public void setItemId(long ItemId) {
        this.ItemId = ItemId;
    }

    /**
     * @return the Batchno
     */
    public String getBatchno() {
        return Batchno;
    }

    /**
     * @param Batchno the Batchno to set
     */
    public void setBatchno(String Batchno) {
        this.Batchno = Batchno;
    }

    /**
     * @return the ItemQty
     */
    public float getItemQty() {
        return ItemQty;
    }
    
    /**
     * @param ItemQty the ItemQty to set
     */
    public void setItemQty(float ItemQty) {
        this.ItemQty = ItemQty;
    }

    /**
     * @return the UnitPrice
     */
    public float getUnitPrice() {
        return UnitPrice;
    }

    /**
     * @param UnitPrice the UnitPrice to set
     */
    public void setUnitPrice(float UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    /**
     * @return the Amount
     */
    public float getAmount() {
        return Amount;
    }

    /**
     * @param Amount the Amount to set
     */
    public void setAmount(float Amount) {
        this.Amount = Amount;
    }

    /**
     * @return the ItemExpryDate
     */
    public Date getItemExpryDate() {
        return ItemExpryDate;
    }

    /**
     * @param ItemExpryDate the ItemExpryDate to set
     */
    public void setItemExpryDate(Date ItemExpryDate) {
        this.ItemExpryDate = ItemExpryDate;
    }

    /**
     * @return the ItemMnfDate
     */
    public Date getItemMnfDate() {
        return ItemMnfDate;
    }

    /**
     * @param ItemMnfDate the ItemMnfDate to set
     */
    public void setItemMnfDate(Date ItemMnfDate) {
        this.ItemMnfDate = ItemMnfDate;
    }

    /**
     * @return the UnitVat
     */
    public float getUnitVat() {
        return UnitVat;
    }

    /**
     * @param UnitVat the UnitVat to set
     */
    public void setUnitVat(float UnitVat) {
        this.UnitVat = UnitVat;
    }

    /**
     * @return the UnitTradeDiscount
     */
    public float getUnitTradeDiscount() {
        return UnitTradeDiscount;
    }

    /**
     * @param UnitTradeDiscount the UnitTradeDiscount to set
     */
    public void setUnitTradeDiscount(float UnitTradeDiscount) {
        this.UnitTradeDiscount = UnitTradeDiscount;
    }

    /**
     * @return the VatRated
     */
    public String getVatRated() {
        return VatRated;
    }

    /**
     * @param VatRated the VatRated to set
     */
    public void setVatRated(String VatRated) {
        this.VatRated = VatRated;
    }

    /**
     * @return the VatPerc
     */
    public float getVatPerc() {
        return VatPerc;
    }

    /**
     * @param VatPerc the VatPerc to set
     */
    public void setVatPerc(float VatPerc) {
        this.VatPerc = VatPerc;
    }

    /**
     * @return the ItemCode
     */
    public String getItemCode() {
        return ItemCode;
    }

    /**
     * @param ItemCode the ItemCode to set
     */
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    /**
     * @return the keyCode
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * @param keyCode the keyCode to set
     */
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * @return the UnitPriceIncVat
     */
    public float getUnitPriceIncVat() {
        return UnitPriceIncVat;
    }

    /**
     * @param UnitPriceIncVat the UnitPriceIncVat to set
     */
    public void setUnitPriceIncVat(float UnitPriceIncVat) {
        this.UnitPriceIncVat = UnitPriceIncVat;
    }

    /**
     * @return the UnitPriceExcVat
     */
    public float getUnitPriceExcVat() {
        return UnitPriceExcVat;
    }

    /**
     * @param UnitPriceExcVat the UnitPriceExcVat to set
     */
    public void setUnitPriceExcVat(float UnitPriceExcVat) {
        this.UnitPriceExcVat = UnitPriceExcVat;
    }

    /**
     * @return the AmountIncVat
     */
    public float getAmountIncVat() {
        return AmountIncVat;
    }

    /**
     * @param AmountIncVat the AmountIncVat to set
     */
    public void setAmountIncVat(float AmountIncVat) {
        this.AmountIncVat = AmountIncVat;
    }

    /**
     * @return the AmountExcVat
     */
    public float getAmountExcVat() {
        return AmountExcVat;
    }

    /**
     * @param AmountExcVat the AmountExcVat to set
     */
    public void setAmountExcVat(float AmountExcVat) {
        this.AmountExcVat = AmountExcVat;
    }

    /**
     * @return the ItemId2
     */
    public long getItemId2() {
        return ItemId2;
    }

    /**
     * @param ItemId2 the ItemId2 to set
     */
    public void setItemId2(long ItemId2) {
        this.ItemId2 = ItemId2;
    }

    /**
     * @return the ItemQty2
     */
    public float getItemQty2() {
        return ItemQty2;
    }

    /**
     * @param ItemQty2 the ItemQty2 to set
     */
    public void setItemQty2(float ItemQty2) {
        this.ItemQty2 = ItemQty2;
    }

    /**
     * @return the FractionQty
     */
    public float getFractionQty() {
        return FractionQty;
    }

    /**
     * @param FractionQty the FractionQty to set
     */
    public void setFractionQty(float FractionQty) {
        this.FractionQty = FractionQty;
    }

    /**
     * @return the StockEffect
     */
    public String getStockEffect() {
        return StockEffect;
    }

    /**
     * @param StockEffect the StockEffect to set
     */
    public void setStockEffect(String StockEffect) {
        this.StockEffect = StockEffect;
    }

    /**
     * @return the IsTradeDiscountVatLiable
     */
    public String getIsTradeDiscountVatLiable() {
        return IsTradeDiscountVatLiable;
    }

    /**
     * @param IsTradeDiscountVatLiable the IsTradeDiscountVatLiable to set
     */
    public void setIsTradeDiscountVatLiable(String IsTradeDiscountVatLiable) {
        this.IsTradeDiscountVatLiable = IsTradeDiscountVatLiable;
    }

    /**
     * @return the OverridePrices
     */
    public boolean isOverridePrices() {
        return OverridePrices;
    }

    /**
     * @param OverridePrices the OverridePrices to set
     */
    public void setOverridePrices(boolean OverridePrices) {
        this.OverridePrices = OverridePrices;
    }

    /**
     * @return the UnitPrice2
     */
    public float getUnitPrice2() {
        return UnitPrice2;
    }

    /**
     * @param UnitPrice2 the UnitPrice2 to set
     */
    public void setUnitPrice2(float UnitPrice2) {
        this.UnitPrice2 = UnitPrice2;
    }

    /**
     * @return the UnitTradeDiscount2
     */
    public float getUnitTradeDiscount2() {
        return UnitTradeDiscount2;
    }

    /**
     * @param UnitTradeDiscount2 the UnitTradeDiscount2 to set
     */
    public void setUnitTradeDiscount2(float UnitTradeDiscount2) {
        this.UnitTradeDiscount2 = UnitTradeDiscount2;
    }

    /**
     * @return the VatRated2
     */
    public String getVatRated2() {
        return VatRated2;
    }

    /**
     * @param VatRated2 the VatRated2 to set
     */
    public void setVatRated2(String VatRated2) {
        this.VatRated2 = VatRated2;
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
     * @return the UnitSymbol
     */
    public String getUnitSymbol() {
        return UnitSymbol;
    }

    /**
     * @param UnitSymbol the UnitSymbol to set
     */
    public void setUnitSymbol(String UnitSymbol) {
        this.UnitSymbol = UnitSymbol;
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
     * @return the Store2Id
     */
    public int getStore2Id() {
        return Store2Id;
    }

    /**
     * @param Store2Id the Store2Id to set
     */
    public void setStore2Id(int Store2Id) {
        this.Store2Id = Store2Id;
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
     * @return the StoreName2
     */
    public String getStoreName2() {
        return StoreName2;
    }

    /**
     * @param StoreName2 the StoreName2 to set
     */
    public void setStoreName2(String StoreName2) {
        this.StoreName2 = StoreName2;
    }

    /**
     * @return the AddUserDetailName
     */
    public String getAddUserDetailName() {
        return AddUserDetailName;
    }

    /**
     * @param AddUserDetailName the AddUserDetailName to set
     */
    public void setAddUserDetailName(String AddUserDetailName) {
        this.AddUserDetailName = AddUserDetailName;
    }

    /**
     * @return the EditUserDetailName
     */
    public String getEditUserDetailName() {
        return EditUserDetailName;
    }

    /**
     * @param EditUserDetailName the EditUserDetailName to set
     */
    public void setEditUserDetailName(String EditUserDetailName) {
        this.EditUserDetailName = EditUserDetailName;
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
     * @return the TransactionReasonName
     */
    public String getTransactionReasonName() {
        return TransactionReasonName;
    }

    /**
     * @param TransactionReasonName the TransactionReasonName to set
     */
    public void setTransactionReasonName(String TransactionReasonName) {
        this.TransactionReasonName = TransactionReasonName;
    }

    /**
     * @return the UnitCostPrice
     */
    public float getUnitCostPrice() {
        return UnitCostPrice;
    }

    /**
     * @param UnitCostPrice the UnitCostPrice to set
     */
    public void setUnitCostPrice(float UnitCostPrice) {
        this.UnitCostPrice = UnitCostPrice;
    }

    /**
     * @return the UnitProfitMargin
     */
    public float getUnitProfitMargin() {
        return UnitProfitMargin;
    }

    /**
     * @param UnitProfitMargin the UnitProfitMargin to set
     */
    public void setUnitProfitMargin(float UnitProfitMargin) {
        this.UnitProfitMargin = UnitProfitMargin;
    }

    /**
     * @return the TransactionUserDetailId
     */
    public int getTransactionUserDetailId() {
        return TransactionUserDetailId;
    }

    /**
     * @param TransactionUserDetailId the TransactionUserDetailId to set
     */
    public void setTransactionUserDetailId(int TransactionUserDetailId) {
        this.TransactionUserDetailId = TransactionUserDetailId;
    }

    /**
     * @return the TransactionUserDetailName
     */
    public String getTransactionUserDetailName() {
        return TransactionUserDetailName;
    }

    /**
     * @param TransactionUserDetailName the TransactionUserDetailName to set
     */
    public void setTransactionUserDetailName(String TransactionUserDetailName) {
        this.TransactionUserDetailName = TransactionUserDetailName;
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
     * @return the BillTransactorName
     */
    public String getBillTransactorName() {
        return BillTransactorName;
    }

    /**
     * @param BillTransactorName the BillTransactorName to set
     */
    public void setBillTransactorName(String BillTransactorName) {
        this.BillTransactorName = BillTransactorName;
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
     * @return the EarnAmount
     */
    public float getEarnAmount() {
        return EarnAmount;
    }

    /**
     * @param EarnAmount the EarnAmount to set
     */
    public void setEarnAmount(float EarnAmount) {
        this.EarnAmount = EarnAmount;
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
     * @return the SumAmountIncVat
     */
    public float getSumAmountIncVat() {
        return SumAmountIncVat;
    }

    /**
     * @param SumAmountIncVat the SumAmountIncVat to set
     */
    public void setSumAmountIncVat(float SumAmountIncVat) {
        this.SumAmountIncVat = SumAmountIncVat;
    }

    /**
     * @return the SumAmountExcVat
     */
    public float getSumAmountExcVat() {
        return SumAmountExcVat;
    }

    /**
     * @param SumAmountExcVat the SumAmountExcVat to set
     */
    public void setSumAmountExcVat(float SumAmountExcVat) {
        this.SumAmountExcVat = SumAmountExcVat;
    }
    
}
