
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class Trans implements Serializable {

    private static final long serialVersionUID = 1L;

    private long TransactionId;
    private long TransactionId2;
    private long TransactionId3;
    private Date TransactionDate;
    private int StoreId;
    private String StoreName;
    private int Store2Id;
    private String Store2Name;
    private long TransactorId;
    private String TransactorName;
    private int TransactionTypeId;
    private String TransactionTypeName;
    private int TransactionReasonId;
    private String TransactionReasonName;
    private float SubTotal;
    private float TotalTradeDiscount;
    private float TotalVat;
    private float GrandTotal;
    private String TransactionComment;
    private int AddUserDetailId;
    private String AddUserDetailName;
    private Date AddDate;
    private int EditUserDetailId;
    private String EditUserDetailName;
    private Date EditDate;
    private String TransactionRef;
    private float AmountTendered;
    private float ChangeAmount;
    private int PayMethod;
    private float CashDiscount;
    private float PointsAwarded;
    private float BalancePoints;
    private float SpendPoints;
    private float BalancePointsAmount;
    private float SpendPointsAmount;
    private String CardNumber;
    private String CardHolder;
    private long PointsCardId;

    private String ApproveUserName;
    private String ApproveUserPassword;

    private float TotalStdVatableAmount;
    private float TotalZeroVatableAmount;
    private float TotalExemptVatableAmount;
    private float VatPerc;
    private String IsCashDiscountVatLiable;
    //for report
    private Date TransactionDate2;
    private Date AddDate2;
    private Date EditDate2;

    private boolean OtherDates;
    //for profit margin
    private float TotalProfitMargin;

    private int TransactionUserDetailId;
    private String TransactionUserDetailName;
    private long BillTransactorId;
    private String BillTransactorName;
    private boolean BillOther;

    private long SchemeTransactorId;
    private String SchemeTransactorName;
    private String PrincSchemeMember;
    private String SchemeCardNumber;

    private String TransactionNumber;
    private String TransactionNumber2;
    private String TransactionNumber3;
    private Date DeliveryDate;
    private String DeliveryAddress;
    private String PayTerms;
    private String TermsConditions;
    private int AuthorisedByUserDetailId;
    private Date AuthoriseDate;
    private Date PayDueDate;
    private Date ExpiryDate;

    //for hotel attributes
    private int Room_Package_Id;
    private String ReservedBy;
    private int NumberOfPersons;
    private Date StartDate;
    private Date EndDate;
    private Room SelectedRoom = null;
    private String RoomOccupancyStatus = "";
    private int CurrencyTypeId;

    private List<RoomOccupancy> RoomOccupancyList = new ArrayList<>();

    public List<RoomOccupancy> getRoomOccupancyList() {
        if (NumberOfPersons == 0) {
            RoomOccupancyList = new ArrayList<>();
        }
        return RoomOccupancyList;
    }

    public void set_room_occupancy_list() {
        RoomOccupancyList = new ArrayList<>();
        for (int x = 0; x < NumberOfPersons; x++) {
            RoomOccupancy roomOccupancy = new RoomOccupancy();
            RoomOccupancyList.add(roomOccupancy);
        }
        set_qty_group_checkin();
    }

    public void setRoomOccupancyList(List<RoomOccupancy> RoomOccupancyList) {
        this.RoomOccupancyList = RoomOccupancyList;
    }

    public int getCurrencyTypeId() {
        return CurrencyTypeId;
    }

    public void setCurrencyTypeId(int CurrencyTypeId) {
        this.CurrencyTypeId = CurrencyTypeId;
    }

    public String getRoomOccupancyStatus() {
        return RoomOccupancyStatus;
    }

    public void setRoomOccupancyStatus(String RoomOccupancyStatus) {
        this.RoomOccupancyStatus = RoomOccupancyStatus;
    }

    public Room getSelectedRoom() {
        return SelectedRoom;
    }

    public void setSelectedRoom(Room SelectedRoom) {
        this.SelectedRoom = SelectedRoom;
    }

    public Date getStartDate() {
        return StartDate;
    }

    @ManagedProperty("#{transItem}")
    private TransItem transItem;

    public TransItem getTransItem() {
        return transItem;
    }

    public void setTransItem(TransItem transItem) {
        this.transItem = transItem;
    }

    public void set_qty() {
        if (getEndDate() != null && getStartDate() != null) {
            int days = Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays();
            if (days == 0) {
                transItem.setItemQty(1);
            } else {
                transItem.setItemQty(Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays());
            }
            transItem.setAmount(transItem.getItemQty() * transItem.getUnitPrice());
        }
    }

    public void set_qty_edit(int aTransTypeNameId, Trans aTrans, List<TransItem> aActiveTransItems) {
        if (getEndDate() != null && getStartDate() != null) {
            for (TransItem item : aActiveTransItems) {
                int days = Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays();
                if (days == 0) {
                    item.setItemQty(1);
                } else {
                    item.setItemQty(Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays());
                }
                //item.setAmount(item.getItemQty() * item.getUnitPrice());
                editTransItem(aTransTypeNameId, aTrans, aActiveTransItems, item);
            }
        }
    }
    
    public void editTransItem(int aTransTypeNameId, Trans aTrans, List<TransItem> aActiveTransItems, TransItem ti) {
        if (ti.getItemQty() < 0) {
            ti.setItemQty(0);
        }
        if (aTransTypeNameId == 2) {//SALE INVOICE
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 10) {//SALE QUOTATION
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 11) {//SALE ORDER
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 12) {//GOODS DELIVERY
            ti.setAmount(0);
            ti.setAmountIncVat(0);
            ti.setAmountExcVat(0);
        }

        if (aTransTypeNameId == 1) {//PURCHASE INVOICE
            ti.setAmount(ti.getItemQty() * (ti.getUnitPrice() + ti.getUnitVat() - ti.getUnitTradeDiscount()));
            ti.setAmountIncVat(ti.getAmount());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 8) {//PURCHASE ORDER
            ti.setAmount(ti.getItemQty() * (ti.getUnitPrice() + ti.getUnitVat() - ti.getUnitTradeDiscount()));
            ti.setAmountIncVat(ti.getAmount());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }
        if (aTransTypeNameId == 9) {//GOODS RECEIVED
            ti.setAmount(0);
            ti.setAmountIncVat(0);
            ti.setAmountExcVat(0);
        }
        if (aTransTypeNameId == 3) {//DISPOSE
            aTrans.setCashDiscount(0);
            ti.setAmount(ti.getUnitPrice() * ti.getItemQty());
            ti.setAmountIncVat((ti.getUnitPriceIncVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
            ti.setAmountExcVat((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) * ti.getItemQty());
        }

        //for profit margin
        if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            ti.setUnitCostPrice(ti.getUnitCostPrice());
            ti.setUnitProfitMargin((ti.getUnitPriceExcVat() - ti.getUnitTradeDiscount()) - ti.getUnitCostPrice());
        } else {
            ti.setUnitCostPrice(0);
            ti.setUnitProfitMargin(0);
        }

        //update totals
        new TransBean().setTransTotalsAndUpdate(aTrans, aActiveTransItems);
    }

    public void set_qty_group_checkin() {
        if (getEndDate() != null && getStartDate() != null) {
            int days = Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays();
            if (days == 0) {
                transItem.setItemQty(1 * NumberOfPersons);
            } else {
                transItem.setItemQty(Days.daysBetween(new LocalDate(getStartDate()), new LocalDate(getEndDate())).getDays() * NumberOfPersons);
            }
            transItem.setAmount(transItem.getItemQty() * transItem.getUnitPrice());
        }
    }

    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date EndDate) {
        this.EndDate = EndDate;
    }

    public int getNumberOfPersons() {
        return NumberOfPersons;
    }

    public void setNumberOfPersons(int NumberOfPersons) {
        this.NumberOfPersons = NumberOfPersons;
    }

    public String getReservedBy() {
        return ReservedBy;
    }

    public void setReservedBy(String ReservedBy) {
        this.ReservedBy = ReservedBy;
    }

    public int getRoom_Package_Id() {
        return Room_Package_Id;
    }

    public void setRoom_Package_Id(int Room_Package_Id) {
        this.Room_Package_Id = Room_Package_Id;
    }

    /**
     * End For Hotel
     */
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
     * @return the Store2Name
     */
    public String getStore2Name() {
        return Store2Name;
    }

    /**
     * @param Store2Name the Store2Name to set
     */
    public void setStore2Name(String Store2Name) {
        this.Store2Name = Store2Name;
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
     * @return the TransactorName
     */
    public String getTransactorName() {
        return TransactorName;
    }

    /**
     * @param TransactorName the TransactorName to set
     */
    public void setTransactorName(String TransactorName) {
        this.TransactorName = TransactorName;
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
     * @return the SubTotal
     */
    public float getSubTotal() {
        return SubTotal;
    }

    /**
     * @param SubTotal the SubTotal to set
     */
    public void setSubTotal(float SubTotal) {
        this.SubTotal = SubTotal;
    }

    /**
     * @return the TotalTradeDiscount
     */
    public float getTotalTradeDiscount() {
        return TotalTradeDiscount;
    }

    /**
     * @param TotalTradeDiscount the TotalTradeDiscount to set
     */
    public void setTotalTradeDiscount(float TotalTradeDiscount) {
        this.TotalTradeDiscount = TotalTradeDiscount;
    }

    /**
     * @return the TotalVat
     */
    public float getTotalVat() {
        return TotalVat;
    }

    /**
     * @param TotalVat the TotalVat to set
     */
    public void setTotalVat(float TotalVat) {
        this.TotalVat = TotalVat;
    }

    /**
     * @return the GrandTotal
     */
    public float getGrandTotal() {
        return GrandTotal;
    }

    /**
     * @param GrandTotal the GrandTotal to set
     */
    public void setGrandTotal(float GrandTotal) {
        this.GrandTotal = GrandTotal;
    }

    /**
     * @return the TransactionComment
     */
    public String getTransactionComment() {
        return TransactionComment;
    }

    /**
     * @param TransactionComment the TransactionComment to set
     */
    public void setTransactionComment(String TransactionComment) {
        this.TransactionComment = TransactionComment;
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
     * @return the TransactionRef
     */
    public String getTransactionRef() {
        return TransactionRef;
    }

    /**
     * @param TransactionRef the TransactionRef to set
     */
    public void setTransactionRef(String TransactionRef) {
        this.TransactionRef = TransactionRef;
    }

    /**
     * @return the AmountTendered
     */
    public float getAmountTendered() {
        return AmountTendered;
    }

    /**
     * @param AmountTendered the AmountTendered to set
     */
    public void setAmountTendered(float AmountTendered) {
        this.AmountTendered = AmountTendered;
    }

    /**
     * @return the ChangeAmount
     */
    public float getChangeAmount() {
        return ChangeAmount;
    }

    /**
     * @param ChangeAmount the ChangeAmount to set
     */
    public void setChangeAmount(float ChangeAmount) {
        this.ChangeAmount = ChangeAmount;
    }

    /**
     * @return the PayMethod
     */
    public int getPayMethod() {
        return PayMethod;
    }

    /**
     * @param PayMethod the PayMethod to set
     */
    public void setPayMethod(int PayMethod) {
        this.PayMethod = PayMethod;
    }

    /**
     * @return the CashDiscount
     */
    public float getCashDiscount() {
        return CashDiscount;
    }

    /**
     * @param CashDiscount the CashDiscount to set
     */
    public void setCashDiscount(float CashDiscount) {
        this.CashDiscount = CashDiscount;
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
     * @return the BalancePoints
     */
    public float getBalancePoints() {
        return BalancePoints;
    }

    /**
     * @param BalancePoints the BalancePoints to set
     */
    public void setBalancePoints(float BalancePoints) {
        this.BalancePoints = BalancePoints;
    }

    /**
     * @return the SpendPoints
     */
    public float getSpendPoints() {
        return SpendPoints;
    }

    /**
     * @param SpendPoints the SpendPoints to set
     */
    public void setSpendPoints(float SpendPoints) {
        this.SpendPoints = SpendPoints;
    }

    /**
     * @return the BalancePointsAmount
     */
    public float getBalancePointsAmount() {
        return BalancePointsAmount;
    }

    /**
     * @param BalancePointsAmount the BalancePointsAmount to set
     */
    public void setBalancePointsAmount(float BalancePointsAmount) {
        this.BalancePointsAmount = BalancePointsAmount;
    }

    /**
     * @return the SpendPointsAmount
     */
    public float getSpendPointsAmount() {
        return SpendPointsAmount;
    }

    /**
     * @param SpendPointsAmount the SpendPointsAmount to set
     */
    public void setSpendPointsAmount(float SpendPointsAmount) {
        this.SpendPointsAmount = SpendPointsAmount;
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
     * @return the ApproveUserName
     */
    public String getApproveUserName() {
        return ApproveUserName;
    }

    /**
     * @param ApproveUserName the ApproveUserName to set
     */
    public void setApproveUserName(String ApproveUserName) {
        this.ApproveUserName = ApproveUserName;
    }

    /**
     * @return the ApproveUserPassword
     */
    public String getApproveUserPassword() {
        return ApproveUserPassword;
    }

    /**
     * @param ApproveUserPassword the ApproveUserPassword to set
     */
    public void setApproveUserPassword(String ApproveUserPassword) {
        this.ApproveUserPassword = ApproveUserPassword;
    }

    /**
     * @return the TotalStdVatableAmount
     */
    public float getTotalStdVatableAmount() {
        return TotalStdVatableAmount;
    }

    /**
     * @param TotalStdVatableAmount the TotalStdVatableAmount to set
     */
    public void setTotalStdVatableAmount(float TotalStdVatableAmount) {
        this.TotalStdVatableAmount = TotalStdVatableAmount;
    }

    /**
     * @return the TotalZeroVatableAmount
     */
    public float getTotalZeroVatableAmount() {
        return TotalZeroVatableAmount;
    }

    /**
     * @param TotalZeroVatableAmount the TotalZeroVatableAmount to set
     */
    public void setTotalZeroVatableAmount(float TotalZeroVatableAmount) {
        this.TotalZeroVatableAmount = TotalZeroVatableAmount;
    }

    /**
     * @return the TotalExemptVatableAmount
     */
    public float getTotalExemptVatableAmount() {
        return TotalExemptVatableAmount;
    }

    /**
     * @param TotalExemptVatableAmount the TotalExemptVatableAmount to set
     */
    public void setTotalExemptVatableAmount(float TotalExemptVatableAmount) {
        this.TotalExemptVatableAmount = TotalExemptVatableAmount;
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
     * @return the IsCashDiscountVatLiable
     */
    public String getIsCashDiscountVatLiable() {
        return IsCashDiscountVatLiable;
    }

    /**
     * @param IsCashDiscountVatLiable the IsCashDiscountVatLiable to set
     */
    public void setIsCashDiscountVatLiable(String IsCashDiscountVatLiable) {
        this.IsCashDiscountVatLiable = IsCashDiscountVatLiable;
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

    /**
     * @return the TransactionId2
     */
    public long getTransactionId2() {
        return TransactionId2;
    }

    /**
     * @param TransactionId2 the TransactionId2 to set
     */
    public void setTransactionId2(long TransactionId2) {
        this.TransactionId2 = TransactionId2;
    }

    /**
     * @return the TransactionId3
     */
    public long getTransactionId3() {
        return TransactionId3;
    }

    /**
     * @param TransactionId3 the TransactionId3 to set
     */
    public void setTransactionId3(long TransactionId3) {
        this.TransactionId3 = TransactionId3;
    }

    /**
     * @return the TotalProfitMargin
     */
    public float getTotalProfitMargin() {
        return TotalProfitMargin;
    }

    /**
     * @param TotalProfitMargin the TotalProfitMargin to set
     */
    public void setTotalProfitMargin(float TotalProfitMargin) {
        this.TotalProfitMargin = TotalProfitMargin;
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
     * @return the BillOther
     */
    public boolean isBillOther() {
        return BillOther;
    }

    /**
     * @param BillOther the BillOther to set
     */
    public void setBillOther(boolean BillOther) {
        this.BillOther = BillOther;
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
     * @return the SchemeTransactorId
     */
    public long getSchemeTransactorId() {
        return SchemeTransactorId;
    }

    /**
     * @param SchemeTransactorId the SchemeTransactorId to set
     */
    public void setSchemeTransactorId(long SchemeTransactorId) {
        this.SchemeTransactorId = SchemeTransactorId;
    }

    /**
     * @return the SchemeTransactorName
     */
    public String getSchemeTransactorName() {
        return SchemeTransactorName;
    }

    /**
     * @param SchemeTransactorName the SchemeTransactorName to set
     */
    public void setSchemeTransactorName(String SchemeTransactorName) {
        this.SchemeTransactorName = SchemeTransactorName;
    }

    /**
     * @return the PrincSchemeMember
     */
    public String getPrincSchemeMember() {
        return PrincSchemeMember;
    }

    /**
     * @param PrincSchemeMember the PrincSchemeMember to set
     */
    public void setPrincSchemeMember(String PrincSchemeMember) {
        this.PrincSchemeMember = PrincSchemeMember;
    }

    /**
     * @return the SchemeCardNumber
     */
    public String getSchemeCardNumber() {
        return SchemeCardNumber;
    }

    /**
     * @param SchemeCardNumber the SchemeCardNumber to set
     */
    public void setSchemeCardNumber(String SchemeCardNumber) {
        this.SchemeCardNumber = SchemeCardNumber;
    }

    /**
     * @return the TransactionNumber
     */
    public String getTransactionNumber() {
        return TransactionNumber;
    }

    /**
     * @param TransactionNumber the TransactionNumber to set
     */
    public void setTransactionNumber(String TransactionNumber) {
        this.TransactionNumber = TransactionNumber;
    }

    /**
     * @return the DeliveryDate
     */
    public Date getDeliveryDate() {
        return DeliveryDate;
    }

    /**
     * @param DeliveryDate the DeliveryDate to set
     */
    public void setDeliveryDate(Date DeliveryDate) {
        this.DeliveryDate = DeliveryDate;
    }

    /**
     * @return the DeliveryAddress
     */
    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    /**
     * @param DeliveryAddress the DeliveryAddress to set
     */
    public void setDeliveryAddress(String DeliveryAddress) {
        this.DeliveryAddress = DeliveryAddress;
    }

    /**
     * @return the PayTerms
     */
    public String getPayTerms() {
        return PayTerms;
    }

    /**
     * @param PayTerms the PayTerms to set
     */
    public void setPayTerms(String PayTerms) {
        this.PayTerms = PayTerms;
    }

    /**
     * @return the TermsConditions
     */
    public String getTermsConditions() {
        return TermsConditions;
    }

    /**
     * @param TermsConditions the TermsConditions to set
     */
    public void setTermsConditions(String TermsConditions) {
        this.TermsConditions = TermsConditions;
    }

    /**
     * @return the AuthorisedByUserDetailId
     */
    public int getAuthorisedByUserDetailId() {
        return AuthorisedByUserDetailId;
    }

    /**
     * @param AuthorisedByUserDetailId the AuthorisedByUserDetailId to set
     */
    public void setAuthorisedByUserDetailId(int AuthorisedByUserDetailId) {
        this.AuthorisedByUserDetailId = AuthorisedByUserDetailId;
    }

    /**
     * @return the AuthoriseDate
     */
    public Date getAuthoriseDate() {
        return AuthoriseDate;
    }

    /**
     * @param AuthoriseDate the AuthoriseDate to set
     */
    public void setAuthoriseDate(Date AuthoriseDate) {
        this.AuthoriseDate = AuthoriseDate;
    }

    /**
     * @return the PayDueDate
     */
    public Date getPayDueDate() {
        return PayDueDate;
    }

    /**
     * @param PayDueDate the PayDueDate to set
     */
    public void setPayDueDate(Date PayDueDate) {
        this.PayDueDate = PayDueDate;
    }

    /**
     * @return the ExpiryDate
     */
    public Date getExpiryDate() {
        return ExpiryDate;
    }

    /**
     * @param ExpiryDate the ExpiryDate to set
     */
    public void setExpiryDate(Date ExpiryDate) {
        this.ExpiryDate = ExpiryDate;
    }

    /**
     * @return the TransactionNumber2
     */
    public String getTransactionNumber2() {
        return TransactionNumber2;
    }

    /**
     * @param TransactionNumber2 the TransactionNumber2 to set
     */
    public void setTransactionNumber2(String TransactionNumber2) {
        this.TransactionNumber2 = TransactionNumber2;
    }

    /**
     * @return the TransactionNumber3
     */
    public String getTransactionNumber3() {
        return TransactionNumber3;
    }

    /**
     * @param TransactionNumber3 the TransactionNumber3 to set
     */
    public void setTransactionNumber3(String TransactionNumber3) {
        this.TransactionNumber3 = TransactionNumber3;
    }

}
