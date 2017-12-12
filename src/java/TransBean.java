
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
//import org.primefaces.component.commandbutton.CommandButton;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author btwesigye
 */
@ManagedBean(name = "transBean")
@SessionScoped
public class TransBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Trans> Transs;
    private String ActionMessage = null;
    private Trans SelectedTrans = null;
    private long SelectedTransactionId;
    private long SelectedTransactionId2;
    private String SearchTrans = "";
    private String TypedTransactorName;
    List<Trans> ReportTrans = new ArrayList<Trans>();
    List<TransSummary> ReportTransSummary = new ArrayList<TransSummary>();
    private boolean AutoPrintAfterSave;
    Map<String, Object> options;
    private String SRCInvoice;
    private float ReportGrandTotal;
    private UserDetail AuthorisedByUserDetail;
    private UserDetail TransUserDetail;
    private List<Trans> TransactorTranss = new ArrayList<Trans>();
    private int OverridePrintVersion;

    private boolean issaleroom = false;
    private boolean is_group_check_in = false;

    public boolean isIs_group_check_in() {
        return is_group_check_in;
    }

    public void setIs_group_check_in(boolean is_group_check_in) {
        this.is_group_check_in = is_group_check_in;
    }

    public boolean isIssaleroom() {
        return issaleroom;
    }

    public void setIssaleroom(boolean issaleroom) {
        this.issaleroom = issaleroom;
    }

    public void RetrieveAndUpdateTransAndItems(int aRetrieveTransTypeId, Trans aTrans, List<TransItem> aTransItems) {
        int CurrStoreId = 0;
        int CurrTransTypeId = 0;
        try {
            Trans RetrievedTrans = new Trans();
            RetrievedTrans = this.getTransByTransNumber(aTrans.getTransactionRef());
            if (RetrievedTrans != null && aRetrieveTransTypeId == RetrievedTrans.getTransactionTypeId()) {
                CurrStoreId = new GeneralUserSetting().getCurrentStore().getStoreId();
                CurrTransTypeId = new GeneralUserSetting().getCurrentTransactionTypeId();
                if ((CurrTransTypeId != 4 && CurrStoreId == RetrievedTrans.getStoreId()) || (CurrTransTypeId == 4 && CurrStoreId == RetrievedTrans.getStore2Id())) {
                    //transactor
                    if (RetrievedTrans.getTransactorId() > 0) {
                        aTrans.setTransactorId(RetrievedTrans.getTransactorId());
                    }
                    //trans items
                    new TransItemBean().assignTransItemsByTransactionId(RetrievedTrans.getTransactionId(), aTransItems);
                    //reset TransItemId=0 for trans such as:
                    //a)When Making Goods Received Trans
                    if (new GeneralUserSetting().getCurrentTransactionTypeId() == 9) {
                        new TransItemBean().resetTransactionItem(2, aTransItems);
                    }
                    //b)When Making Purchase Order Trans
                    if (new GeneralUserSetting().getCurrentTransactionTypeId() == 1) {
                        new TransItemBean().resetTransactionItem(1, aTransItems);
                    }
                    //c)When Making Goods Delivery Trans
                    if (new GeneralUserSetting().getCurrentTransactionTypeId() == 12) {
                        new TransItemBean().resetTransactionItem(2, aTransItems);
                    }
                    //c)When Making Stock Transfers
                    if (new GeneralUserSetting().getCurrentTransactionTypeId() == 4) {
                        if (RetrievedTrans.getStoreId() > 0) {
                            aTrans.setStore2Id(RetrievedTrans.getStoreId());
                        }
                        new TransItemBean().resetTransactionItem(2, aTransItems);
                    }
                }
            } else {
                aTrans.setTransactorId(0);
                //aTrans.setTransactionRef("");
                aTransItems.clear();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public void saveTrans(Trans trans, List<TransItem> aActiveTransItems, Transactor aSelectedTransactor, Transactor aSelectedBillTransactor, UserDetail aTransUserDetail, Transactor aSelectedSchemeTransactor, UserDetail aAuthorisedByUserDetail) {
        String sql = null;
        String sql2 = null;
        String msg = "";

        TransactionType CurrentTransactionType = new TransactionTypeBean().getTransactionType(new GeneralUserSetting().getCurrentTransactionTypeId());

        TransItemBean TransItemBean = new TransItemBean();
        PointsTransactionBean NewPointsTransactionBean = new PointsTransactionBean();
        PointsTransaction NewPointsTransaction = new PointsTransaction();

        //for TransactorLedger
        TransactorLedger NewTransactorLedger;
        TransactorLedgerBean NewTransactorLedgerBean;

        //first clear current trans and pay ids in session
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
        httpSession.setAttribute("CURRENT_PAY_ID", 0);

        String ItemMessage = "";
        ItemMessage = new TransItemBean().getAnyItemTotalQtyGreaterThanCurrentQty(aActiveTransItems, new GeneralUserSetting().getCurrentStore().getStoreId(), CurrentTransactionType.getTransactionTypeName());

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (is_group_check_in && trans.getBillTransactorId() != 0) {
            trans.setTransactorId(trans.getBillTransactorId());
            trans.setTransactorName(trans.getTransactorName());
        }

        if (is_group_check_in) {
            if (trans.getRoomOccupancyList().isEmpty()) {
                this.setActionMessage("");
                msg = "AT LEAST ONE GUEST MUST BE ENTERED FOR GROUP CHECK IN";
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            }

            for (RoomOccupancy roomOccupancy : trans.getRoomOccupancyList()) {
                if (roomOccupancy.getTransactor() == null || roomOccupancy.getRoom() == null || roomOccupancy.getRoomPackageId() == 0 || roomOccupancy.getRoomOccupancyStatus().isEmpty()) {
                    this.setActionMessage("");
                    msg = "PLEASE ENSURE THAT ALL THE DETAILS FOR THE GUEST(S) IN THE GROUP CHECK IN HAVE BEEN CAPTURED (ALL DETAILS ARE COMPULSARY APART FROM REMARKS!)";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                    break;
                }
            }
        }

        if (null == CurrentTransactionType) {
            this.setActionMessage("");
            msg = "-.-.-. INVALID TRANSACTION -.-.-.";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getRoom_Package_Id() == 0 && issaleroom) {
            this.setActionMessage("");
            msg = "ROOM PACKAGE SHOULD NOT BE EMPTY...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getNumberOfPersons() == 0 && (issaleroom || is_group_check_in)) {
            this.setActionMessage("");
            msg = "NO. PAX SHOULD NOT BE ZER0";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getStartDate() == null && issaleroom) {
            this.setActionMessage("");
            msg = "START DATE SHOULD NOT BE EMPTY...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getSelectedRoom() == null && issaleroom) {
            this.setActionMessage("");
            msg = "ROOM NUMBER SHOULD NOT BE EMPTY...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getTransactionId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, grb.getFunctionByTransType(CurrentTransactionType.getTransactionTypeName(), new GeneralUserSetting().getCurrentSaleType()), "Add") == 0) {
            this.setActionMessage("");
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (!ItemMessage.equals("")) {
            this.setActionMessage("");
            msg = "INSUFFICIENT STOCK FOR ITEM(" + ItemMessage + ")...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && "COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType()) && trans.getTransactorId() == 0) {
            this.setActionMessage("");
            msg = "PLEASE SELECT " + CurrentTransactionType.getBillTransactorLabel();
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && "No".equals(CompanySetting.getIsCashDiscountVatLiable()) && trans.getCashDiscount() > (trans.getSubTotal() - trans.getTotalTradeDiscount() + trans.getTotalVat())) {
            this.setActionMessage("");
            msg = "Cash Discount is Invalid, it cannot exceed grand total, please check company settings... ";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && "Yes".equals(CompanySetting.getIsCashDiscountVatLiable()) && trans.getCashDiscount() > (trans.getSubTotal() - trans.getTotalTradeDiscount())) {
            this.setActionMessage("");
            msg = "Cash Discount is set as a liability, it cannot extend to VAT or exceed grand total, please check company settings... ";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getCashDiscount() < 0 || trans.getAmountTendered() < 0 || trans.getSpendPointsAmount() < 0 || trans.getGrandTotal() < 0) {
            this.setActionMessage("");
            msg = "Either [Cash Discount] or [Amount Tendered] or [Points Spent Amount] is INVALID, please check... ";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("PURCHASE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getCashDiscount() < 0) {
            this.setActionMessage("");
            msg = "[Cash Discount] is INVALID, please check... ";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getCashDiscount() > 0 && new GeneralUserSetting().getIsApproveDiscountNeeded() == 1 && !"APPROVED".equals(new GeneralUserSetting().getCurrentApproveDiscountStatus())) {
            this.setActionMessage("");
            msg = "YOU ARE NOT ALLOWED TO ISSUE CASH DISCOUNT, SEEK APPROVAL...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getSpendPointsAmount() > 0 && new GeneralUserSetting().getIsApprovePointsNeeded() == 1 && !"APPROVED".equals(new GeneralUserSetting().getCurrentApprovePointsStatus())) {
            this.setActionMessage("");
            msg = "YOU ARE NOT ALLOWED TO SPEND POINTS, SEEK APPROVAL...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getTransactionDate() == null) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getTransactionDateLabel();
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            if ("UNPACK".equals(CurrentTransactionType.getTransactionTypeName())) {
                aActiveTransItems.clear();
            }
        } else if (new GeneralUserSetting().getDaysFromDateToLicenseExpiryDate(trans.getTransactionDate()) <= 0 || new GeneralUserSetting().getDaysFromDateToLicenseExpiryDate(CompanySetting.getCURRENT_SERVER_DATE()) <= 0) {
            this.setActionMessage("");
            msg = "INCORRECT SERVER DATE or LICENSE HAS EXPIRED !";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getTransactorId() == 0 && CurrentTransactionType.getIsTransactorMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getTransactorLabel();
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (aActiveTransItems.size() < 1 & !"UNPACK".equals(CurrentTransactionType.getTransactionTypeName())) {
            this.setActionMessage("");
            msg = "No item(s) found for this " + CurrentTransactionType.getTransactionOutputLabel();
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getPayMethod() == 0) {
            this.setActionMessage("");
            msg = "Select Payment Method";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && "No".equals(CompanySetting.getIsAllowDebt()) && (trans.getAmountTendered() + trans.getSpendPointsAmount()) < trans.getGrandTotal()) {
            this.setActionMessage("");
            msg = "Amount tendered is LESS THAN grand total";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getSpendPointsAmount() > trans.getBalancePointsAmount()) {
            this.setActionMessage("");
            msg = "Amount entered for spending POINTS exceeds the available balance on points available for this customer; please edit accordingly";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && "Yes".equals(CompanySetting.getIsAllowDebt()) && (trans.getAmountTendered() + trans.getSpendPointsAmount()) < trans.getGrandTotal() && trans.getTransactorId() == 0 && !is_group_check_in && !issaleroom) {
            this.setActionMessage("");
            msg = "Amount tendered is LESS THAN grand total, you MUST select a valid Customer for this Debt Sale";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && (is_group_check_in) && trans.getBillTransactorId() == 0) {
            this.setActionMessage("");
            msg = "MUST select a valid Customer for this Bill";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && (issaleroom) && trans.getTransactorId() == 0) {
            this.setActionMessage("");
            msg = "MUST select a valid Customer for this Bill";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (("TRANSFER".equals(CurrentTransactionType.getTransactionTypeName()) || "TRANSFER REQUEST".equals(CurrentTransactionType.getTransactionTypeName())) && trans.getStore2Id() == 0) {
            this.setActionMessage("");
            msg = "Select the store to which item(s) are requested from / transferred to...";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (("TRANSFER".equals(CurrentTransactionType.getTransactionTypeName()) || "TRANSFER REQUEST".equals(CurrentTransactionType.getTransactionTypeName())) && new GeneralUserSetting().getCurrentStore().getStoreId() == trans.getStore2Id()) {
            this.setActionMessage("");
            msg = "You cannot request/trasfer item(s) to and from the same store...";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("Yes".equals(CurrentTransactionType.getIsTransactionUserMandatory()) && trans.getTransactionUserDetailId() == 0) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getTransactionUserLabel();
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("Yes".equals(CurrentTransactionType.getIsTransactionRefMandatory()) && trans.getTransactionRef().equals("")) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getTransactionRefLabel();
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("Yes".equals(CurrentTransactionType.getIsTransactionRefMandatory()) && trans.getTransactionRef().equals("")) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getTransactionRefLabel();
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.isBillOther() && trans.getBillTransactorId() == 0) {
            this.setActionMessage("");
            msg = "Select " + CurrentTransactionType.getBillTransactorLabel();
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getAuthorisedByUserDetailId() == 0 && CurrentTransactionType.getIsAuthoriseUserMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select Authorise User";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getAuthoriseDate() == null && CurrentTransactionType.getIsAuthoriseDateMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select Authorise Date";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getDeliveryAddress().length() == 0 && CurrentTransactionType.getIsDeliveryAddressMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Specify Delivery Address";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getDeliveryDate() == null && CurrentTransactionType.getIsDeliveryDateMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select Delivery Date";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getPayDueDate() == null && CurrentTransactionType.getIsPayDueDateMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select Pay Due Date";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (trans.getExpiryDate() == null && CurrentTransactionType.getIsExpiryDateMandatory().equals("Yes")) {
            this.setActionMessage("");
            msg = "Select Expiry Date for this " + CurrentTransactionType.getTransactionOutputLabel();
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (aSelectedTransactor != null && aSelectedTransactor.getIsSuspended().equals("Yes")) {
            this.setActionMessage("");
            msg = aSelectedTransactor.getTransactorNames() + " IS SUSPENDED [ " + aSelectedTransactor.getSuspendedReason() + " ]";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (aSelectedBillTransactor != null && aSelectedBillTransactor.getIsSuspended().equals("Yes")) {
            this.setActionMessage("");
            msg = aSelectedBillTransactor.getTransactorNames() + " IS SUSPENDED [ " + aSelectedBillTransactor.getSuspendedReason() + " ]";
            this.setActionMessage("Transaction NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (trans.getTransactionId() == 0) {
                sql = "{call sp_insert_transaction(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (trans.getTransactionId() > 0) {
                sql = "{call sp_update_transaction(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            }
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (trans.getTransactionId() == 0) {
                    cs.setDate("in_transaction_date", new java.sql.Date(trans.getTransactionDate().getTime()));
                    cs.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                    cs.setInt("in_store2_id", trans.getStore2Id());
                    cs.setLong("in_transactor_id", trans.getTransactorId());
                    cs.setInt("in_transaction_type_id", new GeneralUserSetting().getCurrentTransactionTypeId());
                    cs.setInt("in_transaction_reason_id", trans.getTransactionReasonId());
                    cs.setFloat("in_cash_discount", trans.getCashDiscount());
                    cs.setFloat("in_total_vat", trans.getTotalVat());
                    cs.setString("in_transaction_comment", trans.getTransactionComment());
                    cs.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                    cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());//will be made null by the SP
                    cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));//will be made null by the SP
                    cs.setString("in_transaction_ref", trans.getTransactionRef());
                    cs.registerOutParameter("out_transaction_id", VARCHAR);
                    cs.setFloat("in_sub_total", trans.getSubTotal());
                    cs.setFloat("in_grand_total", trans.getGrandTotal());
                    cs.setFloat("in_total_trade_discount", trans.getTotalTradeDiscount());
                    cs.setFloat("in_points_awarded", trans.getPointsAwarded());
                    cs.setString("in_card_number", trans.getCardNumber());
                    cs.setFloat("in_total_std_vatable_amount", trans.getTotalStdVatableAmount());
                    cs.setFloat("in_total_zero_vatable_amount", trans.getTotalZeroVatableAmount());
                    cs.setFloat("in_total_exempt_vatable_amount", trans.getTotalExemptVatableAmount());
                    cs.setFloat("in_vat_perc", CompanySetting.getVatPerc());
                    cs.setFloat("in_amount_tendered", trans.getAmountTendered());
                    cs.setFloat("in_change_amount", trans.getChangeAmount());
                    cs.setString("in_is_cash_discount_vat_liable", CompanySetting.getIsCashDiscountVatLiable());
                    cs.setFloat("in_change_amount", trans.getChangeAmount());

                    /**
                     * For reservation
                     */
                    cs.setString("in_reserved_by", trans.getReservedBy());
                    cs.setInt("in_number_of_persons", trans.getNumberOfPersons());
                    cs.setInt("in_room_package_id", trans.getRoom_Package_Id());

                    if (issaleroom || is_group_check_in) {
                        cs.setDate("in_start_date", new java.sql.Date(trans.getStartDate().getTime()));
                        cs.setDate("in_end_date", new java.sql.Date(trans.getEndDate().getTime()));
                        if (issaleroom) {
                            if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName())) {
                                cs.setInt("in_issaleroom", 1);
                            } else {
                                cs.setInt("in_issaleroom", 0);
                            }
                            if (trans.getSelectedRoom() != null) {
                                cs.setInt("in_room_id", trans.getSelectedRoom().getRoomId());
                            } else {
                                cs.setInt("in_room_id", 0);
                            }
                            cs.setString("in_room_occupancy_status", trans.getRoomOccupancyStatus());
                        } else {
                            cs.setInt("in_issaleroom", 0);
                            cs.setInt("in_room_id", 0);
                            cs.setString("in_room_occupancy_status", "");
                        }
                    } else {
                        cs.setDate("in_start_date", null);
                        cs.setDate("in_end_date", null);
                        cs.setInt("in_issaleroom", 0);
                        cs.setInt("in_room_id", 0);
                        cs.setString("in_room_occupancy_status", "");
                    }

                    //for profit matgin
                    cs.setFloat("in_total_profit_margin", trans.getTotalProfitMargin());
                    try {
                        if (trans.getTransactionUserDetailId() == 0) {
                            trans.setTransactionUserDetailId(new GeneralUserSetting().getCurrentUser().getUserDetailId());
                        }
                    } catch (NullPointerException npe) {
                        trans.setTransactionUserDetailId(new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    }
                    cs.setInt("in_transaction_user_detail_id", trans.getTransactionUserDetailId());
                    try {
                        if (trans.getBillTransactorId() == 0) {
                            trans.setBillTransactorId(trans.getTransactorId());
                        }
                    } catch (NullPointerException npe) {
                        trans.setBillTransactorId(trans.getTransactorId());
                    }
                    cs.setLong("in_bill_transactor_id", trans.getBillTransactorId());
                    try {
                        cs.setLong("in_scheme_transactor_id", trans.getSchemeTransactorId());
                    } catch (NullPointerException npe) {
                        cs.setLong("in_scheme_transactor_id", 0);
                    }
                    try {
                        cs.setString("in_princ_scheme_member", trans.getPrincSchemeMember());
                    } catch (NullPointerException npe) {
                        cs.setString("in_princ_scheme_member", "");
                    }
                    try {
                        cs.setString("in_scheme_card_number", trans.getSchemeCardNumber());
                    } catch (NullPointerException npe) {
                        cs.setString("in_scheme_card_number", "");
                    }
                    try {
                        cs.setString("in_transaction_number", trans.getTransactionNumber());
                    } catch (NullPointerException npe) {
                        cs.setString("in_transaction_number", "");
                    }
                    try {
                        cs.setDate("in_delivery_date", new java.sql.Date(trans.getDeliveryDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_delivery_date", null);
                    }
                    try {
                        cs.setString("in_delivery_address", trans.getDeliveryAddress());
                    } catch (NullPointerException npe) {
                        cs.setString("in_delivery_address", "");
                    }
                    try {
                        cs.setString("in_pay_terms", trans.getPayTerms());
                    } catch (NullPointerException npe) {
                        cs.setString("in_pay_terms", "");
                    }
                    try {
                        cs.setString("in_terms_conditions", trans.getTermsConditions());
                    } catch (NullPointerException npe) {
                        cs.setString("in_terms_conditions", "");
                    }
                    try {
                        cs.setInt("in_authorised_by_user_detail_id", trans.getAuthorisedByUserDetailId());
                    } catch (NullPointerException npe) {
                        cs.setInt("in_authorised_by_user_detail_id", 0);
                    }
                    try {
                        cs.setDate("in_authorise_date", new java.sql.Date(trans.getAuthoriseDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_authorise_date", null);
                    }
                    try {
                        cs.setDate("in_pay_due_date", new java.sql.Date(trans.getPayDueDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_pay_due_date", null);
                    }
                    try {
                        cs.setDate("in_expiry_date", new java.sql.Date(trans.getExpiryDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_expiry_date", null);
                    }
                    //save
                    cs.executeUpdate();

                    //set store2 for transfer in session!
                    if ("TRANSFER".equals(CurrentTransactionType.getTransactionTypeName())) {
                        httpSession.setAttribute("CURRENT_STORE2_ID", trans.getStore2Id());
                    } else {
                        httpSession.setAttribute("CURRENT_STORE2_ID", 0);
                    }

                    //save trans items
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", cs.getLong("out_transaction_id"));
                    TransItemBean tib = new TransItemBean();

                    //save group check in room occupancy
                    for (RoomOccupancy roomOccupancy : trans.getRoomOccupancyList()) {
                        //IN in_room_id int,IN in_transactor_id bigint,IN in_transaction_id bigint,IN in_room_package_id int,IN in_start_date date,IN in_end_date date, IN in_room_occupancy_status varchar(100),IN in_add_user_detail_id int
                        String sql3 = "{call sp_insert_room_occupancy(?,?,?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn.prepareCall(sql3);) {
                            cs2.setInt("in_room_id", roomOccupancy.getRoom().getRoomId());
                            cs2.setLong("in_transactor_id", roomOccupancy.getTransactor().getTransactorId());
                            cs2.setLong("in_transaction_id", new GeneralUserSetting().getCurrentTransactionId());
                            cs2.setInt("in_room_package_id", roomOccupancy.getRoomPackageId());
                            cs2.setDate("in_start_date", new java.sql.Date(trans.getStartDate().getTime()));
                            cs2.setDate("in_end_date", new java.sql.Date(trans.getEndDate().getTime()));
                            cs2.setString("in_room_occupancy_status", roomOccupancy.getRoomOccupancyStatus());
                            cs2.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                            //save
                            cs2.executeUpdate();
                        } catch (SQLException se) {
                            System.err.println(se.getMessage() + Arrays.toString(se.getStackTrace()));
                            this.setActionMessage("Transaction NOT saved");
                            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Transaction NOT saved! Double check details, ensure transaction ref numbers have not been captured already"));
                        }
                    }
                    //end group check in

                    tib.saveTransItems(trans, aActiveTransItems, new GeneralUserSetting().getCurrentTransactionId());

                    //insert Sale/Purchase TransactorLedger
                    //if (trans.getTransactorId() != 0 && ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) || "PURCHASE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()))) {
                    if ((trans.getTransactorId() != 0 || trans.getBillTransactorId() != 0) && ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) || "PURCHASE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()))) {
                        NewTransactorLedger = new TransactorLedger();
                        NewTransactorLedgerBean = new TransactorLedgerBean();

                        NewTransactorLedger.setStoreId(new GeneralUserSetting().getCurrentStore().getStoreId());
                        NewTransactorLedger.setStoreName(new GeneralUserSetting().getCurrentStore().getStoreName());
                        NewTransactorLedger.setTransactionId(new GeneralUserSetting().getCurrentTransactionId());
                        NewTransactorLedger.setPayId(0);
                        if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName())) {
                            NewTransactorLedger.setTransactionTypeName("SALE INVOICE");
                            NewTransactorLedger.setDescription("Bought Item(s)");
                            NewTransactorLedger.setLedgerEntryType("D");
                            NewTransactorLedger.setAmountDebit(trans.getGrandTotal());
                            NewTransactorLedger.setAmountCredit(0);
                        } else if ("PURCHASE INVOICE".equals(CurrentTransactionType.getTransactionTypeName())) {
                            NewTransactorLedger.setTransactionTypeName("PURCHASE INVOICE");
                            NewTransactorLedger.setDescription("Supplied Item(s)");
                            NewTransactorLedger.setLedgerEntryType("C");
                            NewTransactorLedger.setAmountDebit(0);
                            NewTransactorLedger.setAmountCredit(trans.getGrandTotal());
                        }
                        NewTransactorLedger.setTransactionDate(trans.getTransactionDate());
                        NewTransactorLedger.setTransactorId(trans.getTransactorId());
                        NewTransactorLedger.setBillTransactorId(trans.getBillTransactorId());
                        try {
                            NewTransactorLedger.setTransactorNames(new TransactorBean().findTransactor(trans.getTransactorId()).getTransactorNames());
                        } catch (NullPointerException npe) {
                            NewTransactorLedger.setTransactorNames("");
                        }
                        try {
                            NewTransactorLedger.setBillTransactorNames(new TransactorBean().findTransactor(trans.getBillTransactorId()).getTransactorNames());
                        } catch (NullPointerException npe) {
                            NewTransactorLedger.setBillTransactorNames("");
                        }
                        NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);

                        NewTransactorLedger = null;
                        NewTransactorLedgerBean = null;
                    }

                    //save payment
                    if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName())) {
                        sql2 = "{call sp_insert_pay(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                        try (
                                Connection conn2 = DBConnection.getMySQLConnection();
                                CallableStatement cs2 = conn2.prepareCall(sql2);) {
                            cs2.setLong("in_transaction_id", new GeneralUserSetting().getCurrentTransactionId());
                            cs2.setDate("in_pay_date", new java.sql.Date(trans.getTransactionDate().getTime()));
                            if ((trans.getChangeAmount() > 0)) {
                                cs2.setFloat("in_paid_amount", trans.getGrandTotal() - trans.getSpendPointsAmount());
                            } else {
                                cs2.setFloat("in_paid_amount", trans.getAmountTendered());
                            }
                            cs2.setInt("in_pay_method_id", trans.getPayMethod());
                            cs2.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                            cs2.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                            cs2.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                            cs2.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                            cs2.setFloat("in_points_spent", trans.getSpendPoints());
                            cs2.setFloat("in_points_spent_amount", trans.getSpendPointsAmount());
                            cs2.setString("in_pay_ref_no", "");
                            cs2.setString("in_pay_category", "IN");
                            cs2.setLong("in_bill_transactor_id", trans.getBillTransactorId());
                            cs2.setInt("in_transaction_type_id", new GeneralUserSetting().getCurrentTransactionTypeId());
                            cs2.setInt("in_transaction_reason_id", trans.getTransactionReasonId());
                            cs2.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                            cs2.setInt("in_currency_type_id", trans.getCurrencyTypeId());
                            //define output
                            cs2.setLong("in_delete_pay_id", 0);
                            cs2.registerOutParameter("out_pay_id", VARCHAR);
                            cs2.executeUpdate();
                            httpSession.setAttribute("CURRENT_PAY_ID", cs2.getLong("out_pay_id"));

                            //insert Sale Payment Made TransactorLedger
                            //if (trans.getTransactorId() != 0) {
                            if (trans.getTransactorId() != 0 || trans.getBillTransactorId() != 0) {
                                NewTransactorLedger = new TransactorLedger();
                                NewTransactorLedgerBean = new TransactorLedgerBean();

                                NewTransactorLedger.setStoreId(new GeneralUserSetting().getCurrentStore().getStoreId());
                                NewTransactorLedger.setStoreName(new GeneralUserSetting().getCurrentStore().getStoreName());
                                NewTransactorLedger.setTransactionId(new GeneralUserSetting().getCurrentTransactionId());
                                NewTransactorLedger.setPayId(new GeneralUserSetting().getCurrentPayId());
                                NewTransactorLedger.setTransactionTypeName("SALE INVOICE");
                                NewTransactorLedger.setDescription("Payment Made for Bought Item(s)");
                                NewTransactorLedger.setLedgerEntryType("C");
                                NewTransactorLedger.setTransactionDate(trans.getTransactionDate());
                                if ((trans.getChangeAmount() > 0)) {
                                    NewTransactorLedger.setAmountCredit(trans.getGrandTotal() - trans.getSpendPointsAmount());
                                } else {
                                    NewTransactorLedger.setAmountCredit(trans.getAmountTendered());
                                }
                                NewTransactorLedger.setAmountDebit(0);
                                NewTransactorLedger.setTransactorId(trans.getTransactorId());
                                NewTransactorLedger.setBillTransactorId(trans.getBillTransactorId());
                                try {
                                    NewTransactorLedger.setTransactorNames(new TransactorBean().findTransactor(trans.getTransactorId()).getTransactorNames());
                                } catch (NullPointerException npe) {
                                    NewTransactorLedger.setTransactorNames("");
                                }
                                try {
                                    NewTransactorLedger.setBillTransactorNames(new TransactorBean().findTransactor(trans.getBillTransactorId()).getTransactorNames());
                                } catch (NullPointerException npe) {
                                    NewTransactorLedger.setBillTransactorNames("");
                                }
                                NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                                //resend for points paid
                                if (trans.getSpendPointsAmount() > 0) {
                                    NewTransactorLedger.setAmountCredit(trans.getSpendPointsAmount());
                                    NewTransactorLedger.setAmountDebit(0);
                                    NewTransactorLedger.setDescription("Points Spent Amount for Bought Item(s)");
                                    NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                                }

                                NewTransactorLedger = null;
                                NewTransactorLedgerBean = null;
                            }

                        }
                    }
                    //insert PointsTransaction for both the awarded and spent points to the stage area
                    if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && (trans.getPointsAwarded() != 0 || trans.getSpendPoints() != 0)) {
                        if (trans.getPointsCardId() != 0 && !trans.getCardNumber().equals("")) {
                            //1. insert PointsTransaction for both the awarded and spent points to the stage area
                            NewPointsTransaction.setPointsCardId(trans.getPointsCardId());
                            NewPointsTransaction.setTransactionDate(new java.sql.Date(trans.getTransactionDate().getTime()));
                            NewPointsTransaction.setPointsAwarded(trans.getPointsAwarded());
                            NewPointsTransaction.setPointsSpent(trans.getSpendPoints());
                            NewPointsTransaction.setTransactionId(new GeneralUserSetting().getCurrentTransactionId());
                            NewPointsTransaction.setTransBranchId(CompanySetting.getBranchId());
                            NewPointsTransaction.setPointsSpentAmount(trans.getSpendPoints() * CompanySetting.getSpendAmountPerPoint());
                            NewPointsTransactionBean.addPointsTransactionToStage(NewPointsTransaction);
                        }
                    }
                    //Move all, if any PointsTransactions in the stage area to the live server
                    //The move function after deletes all if any in the stage area, so only the stage area will have those records not committed due to failure to connect to the server
                    //Transs will be moved if connectivity to the InterBranch DB is ON
                    if (new DBConnection().isINTER_BRANCH_MySQLConnectionAvailable().equals("ON")) {
                        NewPointsTransactionBean.movePointsTransactionsFromStageToLive();
                    }

                    //insert approvals
                    if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getCashDiscount() > 0 && new GeneralUserSetting().getIsApproveDiscountNeeded() == 1 && "APPROVED".equals(new GeneralUserSetting().getCurrentApproveDiscountStatus())) {
                        this.insertApproveTrans(new GeneralUserSetting().getCurrentTransactionId(), "DISCOUNT", new GeneralUserSetting().getCurrentApproveUserId());
                    }
                    if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName()) && trans.getSpendPointsAmount() > 0 && new GeneralUserSetting().getIsApprovePointsNeeded() == 1 && "APPROVED".equals(new GeneralUserSetting().getCurrentApprovePointsStatus())) {
                        this.insertApproveTrans(new GeneralUserSetting().getCurrentTransactionId(), "SPEND POINT", new GeneralUserSetting().getCurrentApproveUserId());
                    }

                    //this.clearAll(trans, aActiveTransItems, null, null, aSelectedTransactor, 2);
                    this.clearAll2(trans, aActiveTransItems, null, null, aSelectedTransactor, 2, aSelectedBillTransactor, aTransUserDetail, aSelectedSchemeTransactor, aAuthorisedByUserDetail);

                    TransItemBean = null;
                    NewPointsTransactionBean = null;
                    NewPointsTransaction = null;

                    //clean stock
                    StockBean.deleteZeroQtyStock();

                    this.setActionMessage("Saved Successfully ( TransactionId : " + new GeneralUserSetting().getCurrentTransactionId() + " )");

                    //Invoice
                    //1. Update Invoice
                    //2. Auto Printing Invoice
                    if ("SALE INVOICE".equals(CurrentTransactionType.getTransactionTypeName())) {
                        //1. Update Invoice
                        //---SalesInvoiceBean.initSalesInvoiceBean();
                        //2. Auto Printing Invoice
                        if (this.AutoPrintAfterSave) {
                            org.primefaces.context.RequestContext.getCurrentInstance().execute("doPrintHiddenClick()");
                        }
                    }

                } else if (trans.getTransactionId() > 0) {
                    //editing shall go here
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage() + Arrays.toString(se.getStackTrace()));
                this.setActionMessage("Transaction NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Transaction NOT saved! Double check details, ensure transaction ref numbers have not been captured already"));
            }
        }
    }

    public void saveTransAutoUnpack(TransItem aTransItem) {
        String sql = null;
        String sql2 = null;
        int SystemUserId = 0;
        SystemUserId = UserDetailBean.getSystemUserDetailId();
        if (SystemUserId == 0) {
            SystemUserId = new GeneralUserSetting().getCurrentUser().getUserDetailId();
        }
        sql = "{call sp_insert_transaction(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        if (aTransItem != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                cs.setDate("in_transaction_date", new java.sql.Date(CompanySetting.getCURRENT_SERVER_DATE().getTime()));
                cs.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                cs.setInt("in_store2_id", 0);
                cs.setLong("in_transactor_id", 0);
                cs.setInt("in_transaction_type_id", 7);
                cs.setInt("in_transaction_reason_id", 9);
                cs.setFloat("in_cash_discount", 0);
                cs.setFloat("in_total_vat", 0);
                cs.setString("in_transaction_comment", "Auto.Unpack");
                cs.setInt("in_add_user_detail_id", SystemUserId);
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setInt("in_edit_user_detail_id", SystemUserId);
                cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                cs.setString("in_transaction_ref", "Auto.Unpack");
                cs.registerOutParameter("out_transaction_id", VARCHAR);
                cs.setFloat("in_sub_total", 0);
                cs.setFloat("in_grand_total", 0);
                cs.setFloat("in_total_trade_discount", 0);
                cs.setFloat("in_points_awarded", 0);
                cs.setString("in_card_number", "");
                cs.setFloat("in_total_std_vatable_amount", 0);
                cs.setFloat("in_total_zero_vatable_amount", 0);
                cs.setFloat("in_total_exempt_vatable_amount", 0);
                cs.setFloat("in_vat_perc", 0);
                cs.setFloat("in_amount_tendered", 0);
                cs.setFloat("in_change_amount", 0);
                cs.setString("in_is_cash_discount_vat_liable", "");
                //for profit margin
                cs.setFloat("in_total_profit_margin", 0);
                cs.setInt("in_transaction_user_detail_id", 0);
                cs.setLong("in_bill_transactor_id", 0);
                cs.setLong("in_scheme_transactor_id", 0);
                cs.setString("in_princ_scheme_member", "");
                cs.setString("in_scheme_card_number", "");
                //save
                cs.executeUpdate();

                //save trans item
                aTransItem.setTransactionId(cs.getLong("out_transaction_id"));
                TransItemBean tib = new TransItemBean();
                tib.saveTransItemAutoUnpack(aTransItem);
            } catch (SQLException se) {
                System.err.println("autoUnpackItem:" + se.getMessage());
            }
        }
    }

    public void callUpdateTrans(Trans aNewTrans, List<TransItem> aNewTransItems, Pay aPay) {
        try {
            //confirm trans type
            String aTransTypeName = "";
            if (aNewTrans != null) {
                aTransTypeName = new TransactionTypeBean().getTransactionType(aNewTrans.getTransactionTypeId()).getTransactionTypeName();
                new NavigationBean().defineTransactionTypes(aNewTrans.getTransactionTypeId(), aTransTypeName, "", "");
                if (aNewTrans.getTransactionTypeId() == 5 || aNewTrans.getTransactionTypeId() == 6 || aNewTrans.getTransactionTypeId() == 7) {
                    this.setActionMessage("THIS TRANSACTION TYPE CANNOT BE UPDATED!");
                } else if (aNewTrans.getTransactionTypeId() == 2 && aNewTrans.getBillTransactorId() == 0 && (aNewTrans.getAmountTendered() + aNewTrans.getSpendPointsAmount()) != aNewTrans.getGrandTotal()) {
                    FacesContext.getCurrentInstance().addMessage("Update", new FacesMessage("TenderedAmount plus PiontsSpentAmount should equal the new GrandTotal!"));
                    this.setActionMessage("TenderAmount plus PiontsSpentAmount is should equal the new GrandTotal!");
                } else if (aNewTrans.getTransactionTypeId() == 2 && aNewTrans.getSpendPointsAmount() > aNewTrans.getBalancePointsAmount()) {
                    FacesContext.getCurrentInstance().addMessage("Update", new FacesMessage("PiontsSpentAmount cannot exceed BalancePointsAmount!"));
                    this.setActionMessage("PiontsSpentAmount cannot exceed BalancePointsAmount!");
                } else {
                    this.updateTrans(aNewTrans, aNewTransItems, aPay);
                }
            } else {
                this.setActionMessage("THIS TRANSACTION IS INVALID!");
            }
        } catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
        }
    }

    public void updateTrans(Trans aNewTrans, List<TransItem> aNewTransItems, Pay aPay) {
        //1. copy
        //2. reverse stock(for TIs)
        //3. update trans item
        //4. reverse Tra(for ledgers, etc)
        //5. update trans

        String sql = null;
        String msg = "";
        long TransHistId = 0;
        boolean isTransCopySuccess = false;
        boolean isTransItemCopySuccess = false;
        boolean isTransUpdateSuccess = false;
        boolean isTransItemReverseSuccess = false;

        TransItemBean TransItemBean = new TransItemBean();
        Trans OldTrans = new Trans();
        OldTrans = new TransBean().getTrans(aNewTrans.getTransactionId());
        this.setTransTotalsAndUpdate(OldTrans, new TransItemBean().getTransItemsByTransactionId(aNewTrans.getTransactionId()));

        //for TransactorLedger
        TransactorLedger NewTransactorLedger;
        TransactorLedgerBean NewTransactorLedgerBean;

        //first clear current trans and pay ids in session
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
        httpSession.setAttribute("CURRENT_PAY_ID", 0);

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, grb.getFcnName(aNewTrans.getTransactionTypeId(), aNewTrans.getTransactionReasonId()), "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            this.setActionMessage("Transaction NOT Updated");
        } else {
            //Copy Trans
            sql = "{call sp_copy_transaction(?,?,?)}";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_transaction_id", aNewTrans.getTransactionId());
                cs.setString("in_hist_flag", "Edit");
                cs.registerOutParameter("out_transaction_hist_id", VARCHAR);
                cs.executeUpdate();
                TransHistId = cs.getLong("out_transaction_hist_id");
                isTransCopySuccess = true;
            } catch (SQLException se) {
                isTransCopySuccess = false;
                System.err.println("CopyTrans:" + se.getMessage());
            }
            //Copy TransItem
            if (isTransCopySuccess) {
                List<TransItem> TransItemsToCopy = new ArrayList<TransItem>();
                TransItemsToCopy = new TransItemBean().getTransItemsByTransactionId(aNewTrans.getTransactionId());
                int i = 0;
                int n = TransItemsToCopy.size();
                //now copy item by item
                sql = "{call sp_copy_transaction_item(?,?,?)}";
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        CallableStatement cs = conn.prepareCall(sql);) {
                    while (i < n) {
                        cs.setLong("in_transaction_id", aNewTrans.getTransactionId());
                        cs.setLong("in_transaction_hist_id", TransHistId);
                        cs.setLong("in_transaction_item_id", TransItemsToCopy.get(i).getTransactionItemId());
                        cs.executeUpdate();
                        i = i + 1;
                    }
                    isTransItemCopySuccess = true;
                } catch (SQLException se) {
                    isTransItemCopySuccess = false;
                    System.err.println("CopyTransItem:" + se.getMessage());
                }

            }

            //Update by Reversing trans items qty differences
            TransItemBean tib = new TransItemBean();
            isTransItemReverseSuccess = tib.updateTransItems(aNewTrans.getTransactionId(), TransHistId, aNewTransItems);

            //Update Sale/Purchase TransactorLedger
            if ((aNewTrans.getTransactorId() != 0 || aNewTrans.getBillTransactorId() != 0) && ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()))) {
                NewTransactorLedger = new TransactorLedger();
                NewTransactorLedgerBean = new TransactorLedgerBean();
                NewTransactorLedger = NewTransactorLedgerBean.getTransactorLedgerByTransIdTransType(aNewTrans.getTransactionId(), new GeneralUserSetting().getCurrentTransactionTypeName());
                if (NewTransactorLedger != null) {
                    if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        NewTransactorLedger.setAmountDebit(aNewTrans.getGrandTotal());
                    } else if ("PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                        NewTransactorLedger.setAmountCredit(aNewTrans.getGrandTotal());
                    }
                    NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                    NewTransactorLedger = null;
                    NewTransactorLedgerBean = null;
                }
            }

            //update trans
            if (isTransCopySuccess && isTransItemCopySuccess && isTransItemReverseSuccess) {
                String newSQL = "{call sp_update_transaction2(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        CallableStatement cs = conn.prepareCall(newSQL);) {
                    cs.setLong("in_transaction_id", aNewTrans.getTransactionId());
                    cs.setFloat("in_cash_discount", aNewTrans.getCashDiscount());
                    cs.setFloat("in_total_vat", aNewTrans.getTotalVat());
                    cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.setFloat("in_sub_total", aNewTrans.getSubTotal());
                    cs.setFloat("in_grand_total", aNewTrans.getGrandTotal());
                    cs.setFloat("in_total_trade_discount", aNewTrans.getTotalTradeDiscount());
                    cs.setFloat("in_points_awarded", aNewTrans.getPointsAwarded());
                    cs.setString("in_card_number", aNewTrans.getCardNumber());
                    cs.setFloat("in_total_std_vatable_amount", aNewTrans.getTotalStdVatableAmount());
                    cs.setFloat("in_total_zero_vatable_amount", aNewTrans.getTotalZeroVatableAmount());
                    cs.setFloat("in_total_exempt_vatable_amount", aNewTrans.getTotalExemptVatableAmount());
                    cs.setFloat("in_amount_tendered", aNewTrans.getAmountTendered());
                    cs.setFloat("in_change_amount", aNewTrans.getChangeAmount());
                    cs.setFloat("in_total_profit_margin", aNewTrans.getTotalProfitMargin());
                    try {
                        cs.setDate("in_start_date", new java.sql.Date(aNewTrans.getStartDate().getTime()));
                    } catch (Exception ex) {
                        cs.setDate("in_start_date", null);
                    }
                    try {
                        cs.setDate("in_end_date", new java.sql.Date(aNewTrans.getEndDate().getTime()));
                    } catch (Exception ex) {
                        cs.setDate("in_end_date", null);
                    }
                    cs.setInt("in_number_of_persons", aNewTrans.getNumberOfPersons());
                    cs.executeUpdate();
                    isTransUpdateSuccess = true;
                } catch (SQLException se) {
                    isTransUpdateSuccess = false;
                    System.err.println("UpdateTrans:" + se.getMessage());
                }
            }

            //Update the first Payment Record for this sale/purchase transaction
            Pay oldPay = new Pay();
            oldPay = PayBean.getTransactionFirstPay(aNewTrans.getTransactionId());
            Pay newPay = new Pay();
            newPay = PayBean.getTransactionFirstPay(aNewTrans.getTransactionId());
            //if (null != newPay && isTransCopySuccess && isTransItemCopySuccess && isTransItemReverseSuccess && isTransUpdateSuccess && (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE") || new GeneralUserSetting().getCurrentTransactionTypeName().equals("PURCHASE INVOICE"))) {
            if (null != newPay && isTransCopySuccess && isTransItemCopySuccess && isTransItemReverseSuccess && isTransUpdateSuccess && new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE")) {
                newPay.setPaidAmount(aNewTrans.getAmountTendered());
                newPay.setPointsSpentAmount(aNewTrans.getSpendPointsAmount());
                newPay.setPointsSpent(aNewTrans.getSpendPoints());
                boolean outp = new PayBean().updatePay(newPay);
                //Update Sale/Purchase - Payment TransactorLedger
                if ((aNewTrans.getTransactorId() != 0 || aNewTrans.getBillTransactorId() != 0) && oldPay.getDeletePayId() != 1) {
                    NewTransactorLedger = new TransactorLedger();
                    NewTransactorLedgerBean = new TransactorLedgerBean();
                    //1. for sale
                    if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE")) {
                        //1.a. update for the cash payment
                        NewTransactorLedger = NewTransactorLedgerBean.getTransactorLedgerByMinTransIdTransTypeDesc(aNewTrans.getTransactionId(), "SALE INVOICE", "Payment Made for Bought Item(s)");
                        if (NewTransactorLedger != null) {
                            NewTransactorLedger.setAmountCredit(newPay.getPaidAmount());
                            NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                        }
                        //1.b. update for the points payment
                        NewTransactorLedger = NewTransactorLedgerBean.getTransactorLedgerByMinTransIdTransTypeDesc(aNewTrans.getTransactionId(), "SALE INVOICE", "Points Spent Amount for Bought Item(s)");
                        if (NewTransactorLedger != null) {
                            NewTransactorLedger.setAmountCredit(newPay.getPointsSpentAmount());
                            NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                        }
                    }

                    //2. for purchase
                    /*
                     if (new GeneralUserSetting().getCurrentTransactionTypeName().equals("PURCHASE INVOICE")) {
                     //2.a. update for the cash payment
                     NewTransactorLedger = NewTransactorLedgerBean.getTransactorLedgerByMinTransIdTransTypeDesc(aNewTrans.getTransactionId(), "PURCHASE INVOICE", "Payment Received for Supplied Item(s)");
                     if (NewTransactorLedger != null) {
                     NewTransactorLedger.setAmountDebit(newPay.getPaidAmount());
                     NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                     }
                     //1.b. update for the points payment -- N/A
                     }
                     */
                    NewTransactorLedger = null;
                    NewTransactorLedgerBean = null;
                }
            }

            //update for the points card transcation
            //insert PointsTransaction for both the awarded and spent points to the stage area
            if (null != newPay && isTransCopySuccess && isTransItemCopySuccess && isTransItemReverseSuccess && isTransUpdateSuccess && new GeneralUserSetting().getCurrentTransactionTypeName().equals("SALE INVOICE")) {
                PointsCard pc = new PointsCard();
                pc = new PointsCardBean().getPointsCardByCardNumber(aNewTrans.getCardNumber());
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && null != pc) {
                    //1. insert PointsTransaction for both the awarded and spent points to the stage area
                    PointsTransaction NewPointsTransaction = new PointsTransaction();
                    NewPointsTransaction.setPointsCardId(pc.getPointsCardId());
                    NewPointsTransaction.setTransactionDate(new java.sql.Date(aNewTrans.getTransactionDate().getTime()));
                    //for points awarded=New - Hist
                    NewPointsTransaction.setPointsAwarded(aNewTrans.getPointsAwarded() - OldTrans.getPointsAwarded());
                    //for points spent=Hist - New
                    NewPointsTransaction.setPointsSpent(newPay.getPointsSpent() - oldPay.getPointsSpent());
                    NewPointsTransaction.setTransactionId(aNewTrans.getTransactionId());
                    NewPointsTransaction.setTransBranchId(CompanySetting.getBranchId());
                    NewPointsTransaction.setPointsSpentAmount(NewPointsTransaction.getPointsSpent() * CompanySetting.getSpendAmountPerPoint());
                    new PointsTransactionBean().addPointsTransactionToStage(NewPointsTransaction);
                }
                //Move all, if any PointsTransactions in the stage area to the live server
                //The move function after deletes all if any in the stage area, so only the stage area will have those records not committed due to failure to connect to the server
                //Transs will be moved if connectivity to the InterBranch DB is ON
                if (new DBConnection().isINTER_BRANCH_MySQLConnectionAvailable().equals("ON")) {
                    new PointsTransactionBean().movePointsTransactionsFromStageToLive();
                }
            }

            TransItemBean = null;
            //clean stock
            StockBean.deleteZeroQtyStock();
            if (isTransCopySuccess && isTransItemCopySuccess && isTransItemReverseSuccess && isTransUpdateSuccess) {
                this.setActionMessage("Transaction Updated Successfully");
            } else {
                this.setActionMessage("Transaction NOT Updated");
            }
        }
    }

//    public void callPrintButton() {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        UIViewRoot root = facesContext.getViewRoot();
//        CommandButton button = (CommandButton) root.findComponent("cmdbPrint");
//        ActionEvent actionEvent = new ActionEvent(button);
//        actionEvent.queue();
//    }
    public Trans getTrans(long aTransactionId) {
        String sql = "{call sp_search_transaction_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransFromResultset(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    public Trans getTransByTransNumber(String aTransactionNumber) {
        String sql = "{call sp_search_transaction_by_number(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aTransactionNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransFromResultset(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    public Trans getTransByIdType(long aTransactionId, int aTransactionTypeId) {
        String sql = "{call sp_search_transaction_by_id_type(?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            ps.setInt(2, aTransactionTypeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransFromResultset(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    public Trans getTransByNumberType(String aTransactionNumber, int aTransactionTypeId) {
        String sql = "{call sp_search_transaction_by_number_type(?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aTransactionNumber);
            ps.setInt(2, aTransactionTypeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransFromResultset(rs);
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    public void setTransByStoreIdType(int aStoreId, long aTransactionId, int aTransactionTypeId, Trans aTrans, List<TransItem> aActiveTransItems) {
        String sql = "{call sp_search_transaction_by_store_id_type(?,?,?)}";
        ResultSet rs = null;

        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (aTransactionTypeId == 2 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "RETAIL SALE INVOICE", "View") == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "WHOLE SALE INVOICE", "View") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            this.setActionMessage(msg);
        } else if (aTransactionTypeId == 1 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "PURCHASE INVOICE", "View") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            this.setActionMessage(msg);
        } else if (aTransactionTypeId == 3 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "DISPOSE", "View") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            this.setActionMessage(msg);
        } else if (aTransactionTypeId == 7 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "UNPACK", "View") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            this.setActionMessage(msg);
        } else if (aTransactionTypeId == 4 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "TRANSFER", "View") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            this.setActionMessage(msg);
        } else {
            //msg = "";
            //FacesContext.getCurrentInstance().addMessage("View", new FacesMessage(msg));
            //this.setActionMessage(msg);
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, aStoreId);
                ps.setLong(2, aTransactionId);
                ps.setInt(3, aTransactionTypeId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    this.setTransFromResultset(aTrans, rs);

                    //update for the first payment
                    Pay firstPay = new Pay();
                    firstPay = PayBean.getTransactionFirstPay(aTransactionId);
                    try {
                        aTrans.setPayMethod(firstPay.getPayMethodId());
                    } catch (NullPointerException npe) {
                        aTrans.setPayMethod(0);
                    }
                    try {
                        aTrans.setSpendPointsAmount(firstPay.getPointsSpentAmount());
                    } catch (NullPointerException npe) {
                        aTrans.setSpendPointsAmount(0);
                    }
                    try {
                        aTrans.setSpendPoints(firstPay.getPointsSpent());
                    } catch (NullPointerException npe) {
                        aTrans.setSpendPoints(0);
                    }
                    //update for the loyality card point
                    PointsCard pc = new PointsCard();
                    pc = new PointsCardBean().getPointsCardByCardNumber(aTrans.getCardNumber());
                    try {
                        aTrans.setCardHolder(pc.getCardHolder());
                    } catch (NullPointerException npe) {
                        aTrans.setCardHolder("");
                    }
                    try {
                        aTrans.setBalancePointsAmount(pc.getPointsBalance() * new CompanySetting().getESpendAmountPerPoint());
                    } catch (NullPointerException npe) {
                        aTrans.setBalancePointsAmount(0);
                    }

                    new NavigationBean().defineTransactionTypes(aTransactionTypeId, new TransactionTypeBean().getTransactionType(aTransactionTypeId).getTransactionTypeName(), "", "");
                } else {
                    this.clearTransEdit(aTrans);
                    aTrans = null;
                    new NavigationBean().defineTransactionTypes(0, "", "", "");
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                aTrans = null;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
            //trans items
            if (aTrans != null) {
                new TransItemBean().setTransItemsByTransactionId(aActiveTransItems, aTrans.getTransactionId());
            } else {
                new TransItemBean().setTransItemsByTransactionId(aActiveTransItems, 0);
            }
        }
    }

    public void setTransByStoreNumberType(int aStoreId, String aTransactionNumber, int aTransactionTypeId, Trans aTrans, List<TransItem> aActiveTransItems) {
        String sql = "{call sp_search_transaction_by_store_number_type(?,?,?)}";
        ResultSet rs = null;
        String msg;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aStoreId);
            ps.setString(2, aTransactionNumber);
            ps.setInt(3, aTransactionTypeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                this.setTransFromResultset(aTrans, rs);
                //update for the first payment
                Pay firstPay = new Pay();
                firstPay = PayBean.getTransactionFirstPayByTransNo(aTransactionNumber);
                try {
                    aTrans.setPayMethod(firstPay.getPayMethodId());
                } catch (NullPointerException npe) {
                    aTrans.setPayMethod(0);
                }
                try {
                    aTrans.setSpendPointsAmount(firstPay.getPointsSpentAmount());
                } catch (NullPointerException npe) {
                    aTrans.setSpendPointsAmount(0);
                }
                try {
                    aTrans.setSpendPoints(firstPay.getPointsSpent());
                } catch (NullPointerException npe) {
                    aTrans.setSpendPoints(0);
                }
                //update for the loyality card point
                PointsCard pc = new PointsCard();
                pc = new PointsCardBean().getPointsCardByCardNumber(aTrans.getCardNumber());
                try {
                    aTrans.setCardHolder(pc.getCardHolder());
                } catch (NullPointerException npe) {
                    aTrans.setCardHolder("");
                }
                try {
                    aTrans.setBalancePointsAmount(pc.getPointsBalance() * new CompanySetting().getESpendAmountPerPoint());
                } catch (NullPointerException npe) {
                    aTrans.setBalancePointsAmount(0);
                }

                new NavigationBean().defineTransactionTypes(aTransactionTypeId, new TransactionTypeBean().getTransactionType(aTransactionTypeId).getTransactionTypeName(), "", "");
            } else {
                this.clearTransEdit(aTrans);
                aTrans = null;
                new NavigationBean().defineTransactionTypes(0, "", "", "");
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            aTrans = null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        //trans items
        if (aTrans != null) {
            new TransItemBean().setTransItemsByTransactionNumber(aActiveTransItems, aTrans.getTransactionNumber());
        } else {
            new TransItemBean().setTransItemsByTransactionNumber(aActiveTransItems, "");
        }
    }

    public Trans getTransFromResultset(ResultSet aResultSet) {
        try {
            Trans trans = new Trans();
            trans.setTransactionId(aResultSet.getLong("transaction_id"));
            trans.setTransactionDate(new Date(aResultSet.getDate("transaction_date").getTime()));
            trans.setStoreId(aResultSet.getInt("store_id"));

            try {
                trans.setStore2Id(aResultSet.getInt("store2_id"));
            } catch (NullPointerException npe) {
                trans.setStore2Id(0);
            }
            try {
                trans.setTransactorId(aResultSet.getLong("transactor_id"));
            } catch (NullPointerException npe) {
                trans.setTransactorId(0);
            }
            try {
                trans.setTransactionTypeId(aResultSet.getInt("transaction_type_id"));
            } catch (NullPointerException npe) {
                trans.setTransactionTypeId(0);
            }
            try {
                trans.setTransactionReasonId(aResultSet.getInt("transaction_reason_id"));
            } catch (NullPointerException npe) {
                trans.setTransactionReasonId(0);
            }
            try {
                trans.setSubTotal(aResultSet.getFloat("sub_total"));
            } catch (NullPointerException npe) {
                trans.setSubTotal(0);
            }
            try {
                trans.setTotalTradeDiscount(aResultSet.getFloat("total_trade_discount"));
            } catch (NullPointerException npe) {
                trans.setTotalTradeDiscount(0);
            }
            try {
                trans.setTotalVat(aResultSet.getFloat("total_vat"));
            } catch (NullPointerException npe) {
                trans.setTotalVat(0);
            }
            try {
                trans.setCashDiscount(aResultSet.getFloat("cash_discount"));
            } catch (NullPointerException npe) {
                trans.setCashDiscount(0);
            }
            try {
                trans.setGrandTotal(aResultSet.getFloat("grand_total"));
            } catch (NullPointerException npe) {
                trans.setGrandTotal(0);
            }
            try {
                trans.setTransactionRef(aResultSet.getString("transaction_ref"));
            } catch (NullPointerException npe) {
                trans.setTransactionRef("");
            }
            try {
                trans.setTransactionComment(aResultSet.getString("transaction_comment"));
            } catch (NullPointerException npe) {
                trans.setTransactionComment("");
            }
            try {
                trans.setAddUserDetailId(aResultSet.getInt("add_user_detail_id"));
            } catch (NullPointerException npe) {
                trans.setAddUserDetailId(0);
            }
            try {
                trans.setAddDate(new Date(aResultSet.getTimestamp("add_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setAddDate(null);
            }
            try {
                trans.setEditUserDetailId(aResultSet.getInt("edit_user_detail_id"));
            } catch (NullPointerException npe) {
                trans.setEditUserDetailId(0);
            }
            try {
                trans.setEditDate(new Date(aResultSet.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setEditDate(null);
            }
            try {
                trans.setPointsAwarded(aResultSet.getFloat("points_awarded"));
            } catch (NullPointerException npe) {
                trans.setPointsAwarded(0);
            }
            try {
                trans.setCardNumber(aResultSet.getString("card_number"));
            } catch (NullPointerException npe) {
                trans.setCardNumber("");
            }
            try {
                trans.setTotalStdVatableAmount(aResultSet.getFloat("total_std_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalStdVatableAmount(0);
            }
            try {
                trans.setTotalZeroVatableAmount(aResultSet.getFloat("total_zero_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalZeroVatableAmount(0);
            }
            try {
                trans.setTotalExemptVatableAmount(aResultSet.getFloat("total_exempt_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalExemptVatableAmount(0);
            }
            try {
                trans.setVatPerc(aResultSet.getFloat("vat_perc"));
            } catch (NullPointerException npe) {
                trans.setVatPerc(0);
            }
            try {
                trans.setAmountTendered(aResultSet.getFloat("amount_tendered"));
            } catch (NullPointerException npe) {
                trans.setAmountTendered(0);
            }
            try {
                trans.setChangeAmount(aResultSet.getFloat("change_amount"));
            } catch (NullPointerException npe) {
                trans.setChangeAmount(0);
            }
            try {
                trans.setIsCashDiscountVatLiable(aResultSet.getString("is_cash_discount_vat_liable"));
            } catch (NullPointerException npe) {
                trans.setIsCashDiscountVatLiable("");
            }

            //for report only
            try {
                trans.setStoreName(aResultSet.getString("store_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setStoreName("");
            }
            try {
                trans.setStore2Name(aResultSet.getString("store_name2"));
            } catch (NullPointerException | SQLException npe) {
                trans.setStore2Name("");
            }
            try {
                trans.setTransactorName(aResultSet.getString("transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactorName("");
            }
            try {
                trans.setBillTransactorName(aResultSet.getString("bill_transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                trans.setBillTransactorName("");
            }
            try {
                trans.setTransactionTypeName(aResultSet.getString("transaction_type_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionTypeName("");
            }
            try {
                trans.setTransactionReasonName(aResultSet.getString("transaction_reason_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionReasonName("");
            }
            try {
                trans.setAddUserDetailName(aResultSet.getString("add_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setAddUserDetailName("");
            }
            try {
                trans.setEditUserDetailName(aResultSet.getString("edit_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setEditUserDetailName("");
            }
            try {
                trans.setTransactionUserDetailName(aResultSet.getString("transaction_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionUserDetailName("");
            }

            try {
                trans.setTotalProfitMargin(aResultSet.getFloat("total_profit_margin"));
            } catch (NullPointerException npe) {
                trans.setTotalProfitMargin(0);
            }

            try {
                trans.setTransactionUserDetailId(aResultSet.getInt("transaction_user_detail_id"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionUserDetailId(0);
            }

            try {
                trans.setBillTransactorId(aResultSet.getLong("bill_transactor_id"));
            } catch (NullPointerException | SQLException npe) {
                trans.setBillTransactorId(0);
            }

            try {
                trans.setSchemeTransactorId(aResultSet.getLong("scheme_transactor_id"));
            } catch (NullPointerException | SQLException npe) {
                trans.setSchemeTransactorId(0);
            }

            try {
                trans.setSchemeTransactorName(aResultSet.getString("scheme_transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                trans.setSchemeTransactorName("");
            }

            try {
                trans.setPrincSchemeMember(aResultSet.getString("princ_scheme_member"));
            } catch (NullPointerException | SQLException npe) {
                trans.setPrincSchemeMember("");
            }

            try {
                trans.setSchemeCardNumber(aResultSet.getString("scheme_card_number"));
            } catch (NullPointerException | SQLException npe) {
                trans.setSchemeCardNumber("");
            }
            try {
                trans.setTransactionNumber(aResultSet.getString("transaction_number"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionNumber("");
            }
            try {
                trans.setDeliveryDate(new Date(aResultSet.getDate("delivery_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setDeliveryDate(null);
            }
            try {
                trans.setDeliveryAddress(aResultSet.getString("delivery_address"));
            } catch (NullPointerException | SQLException npe) {
                trans.setDeliveryAddress("");
            }
            try {
                trans.setPayTerms(aResultSet.getString("pay_terms"));
            } catch (NullPointerException | SQLException npe) {
                trans.setPayTerms("");
            }
            try {
                trans.setTermsConditions(aResultSet.getString("terms_conditions"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTermsConditions("");
            }
            try {
                trans.setAuthorisedByUserDetailId(aResultSet.getInt("authorised_by_user_detail_id"));
            } catch (NullPointerException | SQLException npe) {
                trans.setAuthorisedByUserDetailId(0);
            }
            try {
                trans.setAuthoriseDate(new Date(aResultSet.getDate("authorise_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setAuthoriseDate(null);
            }
            try {
                trans.setPayDueDate(new Date(aResultSet.getDate("pay_due_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setPayDueDate(null);
            }
            try {
                trans.setExpiryDate(new Date(aResultSet.getDate("expiry_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setExpiryDate(null);
            }
            try {
                trans.setStartDate(new Date(aResultSet.getDate("start_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setStartDate(null);
            }
            try {
                trans.setEndDate(new Date(aResultSet.getDate("end_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setEndDate(null);
            }
            try {
                trans.setReservedBy(aResultSet.getString("reserved_by"));
            } catch (NullPointerException | SQLException npe) {
                trans.setReservedBy("");
            }
            try {
                trans.setNumberOfPersons(aResultSet.getInt("number_of_persons"));
            } catch (NullPointerException | SQLException npe) {
                trans.setNumberOfPersons(0);
            }

            return trans;

        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }
    }

    public void setTransFromResultset(Trans trans, ResultSet aResultSet) {
        try {
            //Trans trans = new Trans();
            trans.setTransactionId(aResultSet.getLong("transaction_id"));
            trans.setTransactionDate(new Date(aResultSet.getDate("transaction_date").getTime()));
            trans.setStoreId(aResultSet.getInt("store_id"));

            try {
                trans.setStartDate(new Date(aResultSet.getDate("start_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setStartDate(null);
            }
            try {
                trans.setEndDate(new Date(aResultSet.getDate("end_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setEndDate(null);
            }

            try {
                trans.setStore2Id(aResultSet.getInt("store2_id"));
            } catch (NullPointerException npe) {
                trans.setStore2Id(0);
            }
            try {
                trans.setTransactorId(aResultSet.getLong("transactor_id"));
            } catch (NullPointerException npe) {
                trans.setTransactorId(0);
            }
            try {
                trans.setTransactionTypeId(aResultSet.getInt("transaction_type_id"));
            } catch (NullPointerException npe) {
                trans.setTransactionTypeId(0);
            }
            try {
                trans.setTransactionReasonId(aResultSet.getInt("transaction_reason_id"));
            } catch (NullPointerException npe) {
                trans.setTransactionReasonId(0);
            }
            try {
                trans.setSubTotal(aResultSet.getFloat("sub_total"));
            } catch (NullPointerException npe) {
                trans.setSubTotal(0);
            }
            try {
                trans.setTotalTradeDiscount(aResultSet.getFloat("total_trade_discount"));
            } catch (NullPointerException npe) {
                trans.setTotalTradeDiscount(0);
            }
            try {
                trans.setTotalVat(aResultSet.getFloat("total_vat"));
            } catch (NullPointerException npe) {
                trans.setTotalVat(0);
            }
            try {
                trans.setCashDiscount(aResultSet.getFloat("cash_discount"));
            } catch (NullPointerException npe) {
                trans.setCashDiscount(0);
            }
            try {
                trans.setGrandTotal(aResultSet.getFloat("grand_total"));
            } catch (NullPointerException npe) {
                trans.setGrandTotal(0);
            }
            try {
                trans.setTransactionRef(aResultSet.getString("transaction_ref"));
            } catch (NullPointerException npe) {
                trans.setTransactionRef("");
            }
            try {
                trans.setTransactionComment(aResultSet.getString("transaction_comment"));
            } catch (NullPointerException npe) {
                trans.setTransactionComment("");
            }
            try {
                trans.setAddUserDetailId(aResultSet.getInt("add_user_detail_id"));
            } catch (NullPointerException npe) {
                trans.setAddUserDetailId(0);
            }
            try {
                trans.setAddDate(new Date(aResultSet.getTimestamp("add_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setAddDate(null);
            }
            try {
                trans.setEditUserDetailId(aResultSet.getInt("edit_user_detail_id"));
            } catch (NullPointerException npe) {
                trans.setEditUserDetailId(0);
            }
            try {
                trans.setEditDate(new Date(aResultSet.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException npe) {
                trans.setEditDate(null);
            }
            try {
                trans.setPointsAwarded(aResultSet.getFloat("points_awarded"));
            } catch (NullPointerException npe) {
                trans.setPointsAwarded(0);
            }
            try {
                trans.setCardNumber(aResultSet.getString("card_number"));
            } catch (NullPointerException npe) {
                trans.setCardNumber("");
            }
            try {
                trans.setTotalStdVatableAmount(aResultSet.getFloat("total_std_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalStdVatableAmount(0);
            }
            try {
                trans.setTotalZeroVatableAmount(aResultSet.getFloat("total_zero_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalZeroVatableAmount(0);
            }
            try {
                trans.setTotalExemptVatableAmount(aResultSet.getFloat("total_exempt_vatable_amount"));
            } catch (NullPointerException npe) {
                trans.setTotalExemptVatableAmount(0);
            }
            try {
                trans.setVatPerc(aResultSet.getFloat("vat_perc"));
            } catch (NullPointerException npe) {
                trans.setVatPerc(0);
            }
            try {
                trans.setAmountTendered(aResultSet.getFloat("amount_tendered"));
            } catch (NullPointerException npe) {
                trans.setAmountTendered(0);
            }
            try {
                trans.setChangeAmount(aResultSet.getFloat("change_amount"));
            } catch (NullPointerException npe) {
                trans.setChangeAmount(0);
            }
            try {
                trans.setIsCashDiscountVatLiable(aResultSet.getString("is_cash_discount_vat_liable"));
            } catch (NullPointerException npe) {
                trans.setIsCashDiscountVatLiable("");
            }

            //for report only
            try {
                trans.setStoreName(aResultSet.getString("store_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setStoreName("");
            }
            try {
                trans.setStore2Name(aResultSet.getString("store_name2"));
            } catch (NullPointerException | SQLException npe) {
                trans.setStore2Name("");
            }
            try {
                trans.setTransactorName(aResultSet.getString("transactor_names"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactorName("");
            }
            try {
                trans.setTransactionTypeName(aResultSet.getString("transaction_type_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionTypeName("");
            }
            try {
                trans.setTransactionReasonName(aResultSet.getString("transaction_reason_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionReasonName("");
            }
            try {
                trans.setAddUserDetailName(aResultSet.getString("add_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setAddUserDetailName("");
            }
            try {
                trans.setEditUserDetailName(aResultSet.getString("edit_user_detail_name"));
            } catch (NullPointerException | SQLException npe) {
                trans.setEditUserDetailName("");
            }

            try {
                trans.setTotalProfitMargin(aResultSet.getFloat("total_profit_margin"));
            } catch (NullPointerException npe) {
                trans.setTotalProfitMargin(0);
            }

            try {
                trans.setTransactionUserDetailId(aResultSet.getInt("transaction_user_detail_id"));
            } catch (NullPointerException npe) {
                trans.setTransactionUserDetailId(0);
            }

            try {
                trans.setBillTransactorId(aResultSet.getLong("bill_transactor_id"));
            } catch (NullPointerException npe) {
                trans.setBillTransactorId(0);
            }

            try {
                trans.setSchemeTransactorId(aResultSet.getLong("scheme_transactor_id"));
            } catch (NullPointerException npe) {
                trans.setSchemeTransactorId(0);
            }

            try {
                trans.setPrincSchemeMember(aResultSet.getString("princ_scheme_member"));
            } catch (NullPointerException npe) {
                trans.setPrincSchemeMember("");
            }

            try {
                trans.setSchemeCardNumber(aResultSet.getString("scheme_card_number"));
            } catch (NullPointerException | SQLException npe) {
                trans.setSchemeCardNumber("");
            }

            try {
                trans.setTransactionNumber(aResultSet.getString("transaction_number"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTransactionNumber("");
            }
            try {
                trans.setDeliveryDate(new Date(aResultSet.getDate("delivery_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setDeliveryDate(null);
            }
            try {
                trans.setDeliveryAddress(aResultSet.getString("delivery_address"));
            } catch (NullPointerException | SQLException npe) {
                trans.setDeliveryAddress("");
            }
            try {
                trans.setPayTerms(aResultSet.getString("pay_terms"));
            } catch (NullPointerException | SQLException npe) {
                trans.setPayTerms("");
            }
            try {
                trans.setTermsConditions(aResultSet.getString("terms_conditions"));
            } catch (NullPointerException | SQLException npe) {
                trans.setTermsConditions("");
            }
            try {
                trans.setAuthorisedByUserDetailId(aResultSet.getInt("authorised_by_user_detail_id"));
            } catch (NullPointerException | SQLException npe) {
                trans.setAuthorisedByUserDetailId(0);
            }
            try {
                trans.setAuthoriseDate(new Date(aResultSet.getDate("authorise_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setAuthoriseDate(null);
            }
            try {
                trans.setPayDueDate(new Date(aResultSet.getDate("pay_due_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setPayDueDate(null);
            }
            try {
                trans.setExpiryDate(new Date(aResultSet.getDate("expiry_date").getTime()));
            } catch (NullPointerException | SQLException npe) {
                trans.setExpiryDate(null);
            }
            try {
                trans.setNumberOfPersons(aResultSet.getInt("number_of_persons"));
            } catch (NullPointerException | SQLException npe) {
                trans.setNumberOfPersons(0);
            }
            //return trans;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            ///return null;
        }

    }

    public TransSummary getTransSummaryFromResultset(ResultSet aResultSet) {
        try {
            TransSummary transSummary = new TransSummary();
            try {
                transSummary.setStoreId(aResultSet.getInt("store_id"));
            } catch (Exception e) {
                transSummary.setStoreId(0);
            }
            try {
                transSummary.setTransactionTypeId(aResultSet.getInt("transaction_type_id"));
            } catch (Exception e) {
                transSummary.setTransactionTypeId(0);
            }
            try {
                transSummary.setSumTotalProfitMargin(aResultSet.getFloat("sum_total_profit_margin"));
            } catch (Exception e) {
                transSummary.setSumTotalProfitMargin(0);
            }
            try {
                transSummary.setFieldName(aResultSet.getString("field_name"));
            } catch (Exception e) {
                transSummary.setFieldName("-");
            }
            try {
                transSummary.setSumTotalTradeDiscount(aResultSet.getFloat("sum_total_trade_discount"));
            } catch (NullPointerException npe) {
                transSummary.setSumTotalTradeDiscount(0);
            }
            try {
                transSummary.setSumTotalVat(aResultSet.getLong("sum_total_vat"));
            } catch (NullPointerException npe) {
                transSummary.setSumTotalVat(0);
            }
            try {
                transSummary.setSumCashDiscount(aResultSet.getFloat("sum_cash_discount"));
            } catch (NullPointerException npe) {
                transSummary.setSumCashDiscount(0);
            }
            try {
                transSummary.setSumGrandTotal(aResultSet.getFloat("sum_grand_total"));
            } catch (NullPointerException npe) {
                transSummary.setSumGrandTotal(0);
            }
            try {
                transSummary.setSumTotalStdVatableAmount(aResultSet.getFloat("sum_total_std_vatable_amount"));
            } catch (NullPointerException npe) {
                transSummary.setSumTotalStdVatableAmount(0);
            }
            try {
                transSummary.setSumTotalZeroVatableAmount(aResultSet.getFloat("sum_total_zero_vatable_amount"));
            } catch (NullPointerException npe) {
                transSummary.setSumTotalZeroVatableAmount(0);
            }
            try {
                transSummary.setSumTotalExemptVatableAmount(aResultSet.getFloat("sum_total_exempt_vatable_amount"));
            } catch (NullPointerException npe) {
                transSummary.setSumTotalExemptVatableAmount(0);
            }

            return transSummary;

        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }

    public void deleteTrans(Trans trans) {
        String sql = "DELETE FROM trans WHERE transaction_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, trans.getTransactionId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearTrans(trans);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Trans NOT deleted");
        }
    }

    public void clearTrans(Trans trans) {
        trans.setTransactionId(0);
        trans.setTransactionDate(CompanySetting.getCURRENT_SERVER_DATE());
        trans.setStoreId(0);
        trans.setStore2Id(0);
        trans.setTransactorId(0);
        //trans.setTransactionTypeId(0);//
        //trans.setTransactionReasonId(0);//
        trans.setCashDiscount(0);
        trans.setTotalVat(0);
        trans.setTransactionComment("");
        trans.setAddUserDetailId(0);
        //trans.setAddDate(null);//
        trans.setEditUserDetailId(0);
        //trans.setEditDate(null);//
        trans.setTransactionRef("");
        trans.setAmountTendered(0);
        trans.setChangeAmount(0);
        trans.setChangeAmount(0);
        trans.setTotalTradeDiscount(0);
        trans.setPointsAwarded(0);
        trans.setSpendPoints(0);
        trans.setSpendPointsAmount(0);
        trans.setBalancePoints(0);
        trans.setBalancePointsAmount(0);
        trans.setCardHolder("");
        trans.setCardNumber("");
        trans.setSubTotal(0);

        trans.setSubTotal(0);
        trans.setTotalTradeDiscount(0);
        trans.setTotalVat(0);
        trans.setGrandTotal(0);
        trans.setSpendPoints(0);

        trans.setTotalStdVatableAmount(0);
        trans.setTotalZeroVatableAmount(0);
        trans.setTotalExemptVatableAmount(0);
        trans.setVatPerc(0);
        //trans.setIsCashDiscountVatLiable("");
        trans.setApproveUserName("");
        trans.setApproveUserPassword("");
        //clear current trans and pay ids in session
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("APPROVE_USER_ID", 0);
        httpSession.setAttribute("APPROVE_DISCOUNT_STATUS", "");
        httpSession.setAttribute("APPROVE_POINTS_STATUS", "");
        //for profit margin
        trans.setTotalProfitMargin(0);

        trans.setTransactionUserDetailId(0);
        trans.setBillTransactorId(0);
        trans.setSchemeTransactorId(0);
        trans.setPrincSchemeMember("");
        trans.setSchemeCardNumber("");
        trans.setBillOther(false);

        trans.setTransactionNumber("");
        trans.setTransactionNumber2("");
        trans.setTransactionNumber3("");
        trans.setDeliveryDate(null);
        trans.setDeliveryAddress("");
        trans.setPayTerms("");
        trans.setTermsConditions("");
        trans.setAuthorisedByUserDetailId(0);
        trans.setAuthoriseDate(null);
        trans.setPayDueDate(null);
        trans.setExpiryDate(null);
        trans.setReservedBy("");
        trans.setRoom_Package_Id(0);
        trans.setNumberOfPersons(0);
        trans.setSelectedRoom(null);
        trans.setStartDate(null);
        trans.setEndDate(null);
        trans.setRoomOccupancyStatus("");
        trans.setCurrencyTypeId(0);
        trans.setRoomOccupancyList(new ArrayList<RoomOccupancy>());
    }

    public void clearTransEdit(Trans trans) {
        trans.setTransactionId(0);
        trans.setTransactionDate(CompanySetting.getCURRENT_SERVER_DATE());
        trans.setStoreId(0);
        trans.setStore2Id(0);
        trans.setTransactorId(0);
        trans.setTransactionTypeId(0);//
        trans.setTransactionReasonId(0);//
        trans.setCashDiscount(0);
        trans.setTotalVat(0);
        trans.setTransactionComment("");
        trans.setAddUserDetailId(0);
        trans.setAddDate(null);//
        trans.setEditUserDetailId(0);
        trans.setEditDate(null);//
        trans.setTransactionRef("");
        trans.setAmountTendered(0);
        trans.setChangeAmount(0);
        trans.setChangeAmount(0);
        trans.setTotalTradeDiscount(0);
        trans.setPointsAwarded(0);
        trans.setSpendPoints(0);
        trans.setSpendPointsAmount(0);
        trans.setBalancePoints(0);
        trans.setBalancePointsAmount(0);
        trans.setCardHolder("");
        trans.setCardNumber("");
        trans.setSubTotal(0);

        trans.setSubTotal(0);
        trans.setTotalTradeDiscount(0);
        trans.setTotalVat(0);
        trans.setGrandTotal(0);
        trans.setSpendPoints(0);

        trans.setTotalStdVatableAmount(0);
        trans.setTotalZeroVatableAmount(0);
        trans.setTotalExemptVatableAmount(0);
        trans.setVatPerc(0);
        //trans.setIsCashDiscountVatLiable("");
        trans.setApproveUserName("");
        trans.setApproveUserPassword("");
        //clear current trans and pay ids in session
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("APPROVE_USER_ID", 0);
        httpSession.setAttribute("APPROVE_DISCOUNT_STATUS", "");
        httpSession.setAttribute("APPROVE_POINTS_STATUS", "");

        //for profit margin
        trans.setTotalProfitMargin(0);

        trans.setTransactionUserDetailId(0);
        trans.setBillTransactorId(0);
        trans.setSchemeTransactorId(0);
        trans.setPrincSchemeMember("");
        trans.setSchemeCardNumber("");
        trans.setTransactionNumber("");
        trans.setDeliveryDate(null);
        trans.setDeliveryAddress("");
        trans.setPayTerms("");
        trans.setTermsConditions("");
        trans.setAuthorisedByUserDetailId(0);
        trans.setAuthoriseDate(null);
        trans.setPayDueDate(null);
        trans.setExpiryDate(null);
    }

    public void clearAll(Trans t, List<TransItem> aActiveTransItems, TransItem ti, Item aSelectedItem, Transactor aSelectedTransactor, int ClearNo) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all  
        TransItemBean tib = new TransItemBean();
        ItemBean itmB = new ItemBean();
        TransactorBean trB = new TransactorBean();
        issaleroom = false;
        is_group_check_in = false;
        if (ClearNo == 1 || ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
            //clear autoCompletetd item
            itmB.clearSelectedItem();
            itmB.clearItem(aSelectedItem);
            //clear the selcted trans item
            tib.clearTransItem(ti);
        }
        if (ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
            //put code for clearing customer/supplier/transactor
            trB.clearSelectedTransactor();
            trB.clearTransactor(aSelectedTransactor);
            //clear all the item LIST
            //--//tib.getActiveTransItems().clear();
            aActiveTransItems.clear();

            //clear Trans inc. payments
            this.clearTrans(t);
        }
    }

    public void clearAll2(Trans t, List<TransItem> aActiveTransItems, TransItem ti, Item aSelectedItem, Transactor aSelectedTransactor, int ClearNo, Transactor aSelectedBillTransactor, UserDetail aTransUserDetail, Transactor aSelectedSchemeTransactor, UserDetail aAuthorisedByUserDetail) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all  
        TransItemBean tib = new TransItemBean();
        ItemBean itmB = new ItemBean();
        TransactorBean trB = new TransactorBean();
        issaleroom = false;
        if (ClearNo == 1 || ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
            //clear autoCompletetd item
            itmB.clearSelectedItem();
            itmB.clearItem(aSelectedItem);
            //clear the selcted trans item
            tib.clearTransItem(ti);
        }
        if (ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
            trB.clearTransactor(aSelectedTransactor);
            //code for clearing BILL customer/supplier/transactor
            //trB.clearSelectedBillTransactor();
            trB.clearTransactor(aSelectedBillTransactor);
            trB.clearTransactor(aSelectedSchemeTransactor);
            //clear all the item LIST
            //--//tib.getActiveTransItems().clear();
            aActiveTransItems.clear();

            //clear Trans inc. payments
            this.clearTrans(t);

            //clear TransUser / Service Offered by
            new UserDetailBean().clearUserDetail(aTransUserDetail);

            //clear Authorised By UserDetail
            new UserDetailBean().clearUserDetail(aAuthorisedByUserDetail);
        }
    }

    public void initClearAll(Trans t, List<TransItem> aActiveTransItems, TransItem ti, Item aSelectedItem, Transactor aSelectedTransactor, int ClearNo) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all  
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            TransItemBean tib = new TransItemBean();
            ItemBean itmB = new ItemBean();
            TransactorBean trB = new TransactorBean();

            if (ClearNo == 1 || ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //clear autoCompletetd item
                itmB.clearSelectedItem();
                itmB.clearItem(aSelectedItem);
                //clear the selcted trans item
                tib.clearTransItem(ti);
            }
            if (ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //code for clearing customer/supplier/transactor
                trB.clearSelectedTransactor();
                trB.clearTransactor(aSelectedTransactor);
                //clear all the item LIST
                //--//tib.getActiveTransItems().clear();
                aActiveTransItems.clear();

                //clear Trans
                this.clearTrans(t);

                //clear action message
                this.ActionMessage = "";

                //clear current trans and pay ids in session
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);

            }
        }
    }

    public void initClearAll2(Trans t, List<TransItem> aActiveTransItems, TransItem ti, Item aSelectedItem, Transactor aSelectedTransactor, int ClearNo, Transactor aSelectedBillTransactor, UserDetail aTransUserDetail, Transactor aSelectedSchemeTransactor) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all  
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            TransItemBean tib = new TransItemBean();
            ItemBean itmB = new ItemBean();
            TransactorBean trB = new TransactorBean();

            if (ClearNo == 1 || ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //clear autoCompletetd item
                itmB.clearSelectedItem();
                itmB.clearItem(aSelectedItem);
                //clear the selcted trans item
                tib.clearTransItem(ti);
            }
            if (ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //code for clearing customer/supplier/transactor
                trB.clearSelectedTransactor();
                trB.clearTransactor(aSelectedTransactor);
                //code for clearing customer/supplier/transactor
                //trB.clearSelectedBillTransactor();
                trB.clearTransactor(aSelectedBillTransactor);
                trB.clearTransactor(aSelectedSchemeTransactor);
                //clear all the item LIST
                //--//tib.getActiveTransItems().clear();
                aActiveTransItems.clear();

                //clear Trans
                this.clearTrans(t);

                //clear transaction user / service offered by
                new UserDetailBean().clearUserDetail(aTransUserDetail);

                //clear action message
                this.ActionMessage = "";

                //clear current trans and pay ids in session
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);

            }
        }
    }

    public void initClearAllEdit(Trans t, List<TransItem> aActiveTransItems, TransItem ti, Item aSelectedItem, Transactor aSelectedTransactor, int ClearNo) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all  
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            TransItemBean tib = new TransItemBean();
            ItemBean itmB = new ItemBean();
            TransactorBean trB = new TransactorBean();

            if (ClearNo == 1 || ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //clear autoCompletetd item
                itmB.clearSelectedItem();
                itmB.clearItem(aSelectedItem);
                //clear the selcted trans item
                tib.clearTransItem(ti);
            }
            if (ClearNo == 2) {//Clear No: 0-do not clear, 1 - clear trans item only, 2 - clear all
                //put code for clearing customer/supplier/transactor
                trB.clearSelectedTransactor();
                trB.clearTransactor(aSelectedTransactor);
                //clear all the item LIST
                //--//tib.getActiveTransItems().clear();
                aActiveTransItems.clear();

                //clear Trans
                this.clearTransEdit(t);

                //clear action message
                this.ActionMessage = "";

                //clear current trans and pay ids in session
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);

            }
        }
    }

    public void initClearTransReport(Trans trans) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            //clear Trans
            trans.setTransactionId(0);
            trans.setTransactionDate(null);
            trans.setTransactionDate2(null);
            trans.setStoreId(0);
            trans.setStore2Id(0);
            trans.setTransactorId(0);
            trans.setAddUserDetailId(0);
            trans.setAddDate(null);
            trans.setAddDate2(null);
            trans.setEditUserDetailId(0);
            trans.setEditDate(null);
            trans.setEditDate2(null);
            //clear action message
            this.ActionMessage = "";
            trans.setTransactionUserDetailId(0);
            trans.setBillTransactorId(0);
            trans.setSchemeTransactorId(0);
            trans.setPrincSchemeMember("");
            trans.setSchemeCardNumber("");
        }
    }

    /**
     * @return the trass
     */
    public List<Trans> getTranss() {
        String sql;
        if (this.SearchTrans.length() > 0) {
            sql = "SELECT * FROM trans WHERE transaction_id=" + this.SearchTrans + " ORDER BY transaction_id DESC LIMIT 5";
        } else {
            sql = "SELECT * FROM trans ORDER BY transaction_id DESC LIMIT 5";
        }
        ResultSet rs = null;
        Transs = new ArrayList<Trans>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Trans trans = new Trans();
                trans.setTransactionId(rs.getLong("transaction_id"));
                trans.setTransactionDate(new Date(rs.getDate("transaction_date").getTime()));
                trans.setStoreId(rs.getInt("store_id"));
                trans.setStore2Id(rs.getInt("store2_id"));
                trans.setTransactorId(rs.getLong("transactor_id"));
                trans.setTransactionTypeId(rs.getInt("transaction_type_id"));
                trans.setTransactionReasonId(rs.getInt("transaction_reason_id"));
                trans.setSubTotal(rs.getFloat("sub_total"));
                trans.setSubTotal(rs.getFloat("sub_total"));
                trans.setCashDiscount(rs.getFloat("cash_discount"));
                trans.setTotalVat(rs.getFloat("total_vat"));
                trans.setTotalVat(rs.getFloat("total_vat"));
                //trans.setGrandTotal(rs.getFloat("grand_total"));
                trans.setGrandTotal(rs.getFloat("grand_total"));
                trans.setTransactionComment(rs.getString("transaction_comment"));
                trans.setAddUserDetailId(rs.getInt("add_user_detail_id"));
                trans.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                trans.setEditUserDetailId(rs.getInt("edit_user_detail_id"));
                trans.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
                trans.setTransactionRef(rs.getString("transaction_ref"));
                trans.setTotalTradeDiscount(rs.getFloat("total_trade_discount"));
                trans.setTotalTradeDiscount(rs.getFloat("total_trade_discount"));
                trans.setPointsAwarded(rs.getFloat("points_awarded"));
                trans.setPointsAwarded(rs.getFloat("points_awarded"));
                trans.setCardNumber(rs.getString("card_number"));
                trans.setTotalStdVatableAmount(rs.getFloat("total_std_vatable_amount"));
                trans.setTotalZeroVatableAmount(rs.getFloat("total_zero_vatable_amount"));
                trans.setTotalExemptVatableAmount(rs.getFloat("total_exempt_vatable_amount"));
                trans.setVatPerc(rs.getFloat("vat_perc"));
                trans.setAmountTendered(rs.getFloat("amount_tendered"));
                trans.setChangeAmount(rs.getFloat("change_amount"));
                trans.setIsCashDiscountVatLiable(rs.getString("is_cash_discount_vat_liable"));
                trans.setTotalProfitMargin(rs.getFloat("total_profit_margin"));
                try {
                    trans.setTransactionUserDetailId(rs.getInt("transaction_user_detail_id"));
                } catch (NullPointerException npe) {
                    trans.setTransactionUserDetailId(0);
                }
                try {
                    trans.setBillTransactorId(rs.getLong("bill_transactor_id"));
                } catch (NullPointerException npe) {
                    trans.setBillTransactorId(0);
                }
                try {
                    trans.setSchemeTransactorId(rs.getLong("scheme_transactor_id"));
                } catch (NullPointerException npe) {
                    trans.setSchemeTransactorId(0);
                }
                try {
                    trans.setPrincSchemeMember(rs.getString("princ_scheme_member"));
                } catch (NullPointerException npe) {
                    trans.setPrincSchemeMember("");
                }

                try {
                    trans.setSchemeCardNumber(rs.getString("scheme_card_number"));
                } catch (NullPointerException | SQLException npe) {
                    trans.setSchemeCardNumber("");
                }
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Transs;
    }

    public List<Trans> getTranss(long TransactorId) {
        String sql;
        sql = "{call sp_search_transaction_by_transactor_id(?)}";
        ResultSet rs = null;
        Transs = new ArrayList<Trans>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransactorId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Transs.add(this.getTransFromResultset(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Transs;
    }

    public List<Trans> getTranssByTrTcTt(long aTransactionId, long aTransactorId, String aTransactorType) {
        List<Trans> aTranss = new ArrayList<Trans>();
        if (aTransactorId != 0) {
            aTranss = this.getTranssByTransactorTransType(aTransactorId, aTransactorType);
        } else if (aTransactionId != 0) {
            Trans aTrans = new Trans();
            try {
                if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 2);//SALE
                } else if (aTransactorType.equals("SUPPLIER")) {
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 1);//PURCHASE
                }
            } catch (NullPointerException npe) {
                aTrans = null;
            }
            aTranss.add(aTrans);
        }
        return aTranss;
    }

    public List<Trans> getTranssByBillTrTcTt(long aTransactionId, long aBillTransactorId, String aTransactorType) {
        List<Trans> aTranss = new ArrayList<Trans>();
        if (aBillTransactorId != 0) {
            aTranss = this.getTranssByBillTransactorTransType(aBillTransactorId, aTransactorType);
        } else if (aTransactionId != 0) {
            Trans aTrans = new Trans();
            try {
                if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 2);//SALE
                } else if (aTransactorType.equals("SUPPLIER")) {
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 1);//PURCHASE
                }
            } catch (NullPointerException npe) {
                aTrans = null;
            }
            aTranss.add(aTrans);
        }
        return aTranss;
    }

    public List<Trans> getTranssByBillTrPayCat(long aTransactionId, long aBillTransactorId, String aPayCategory) {
        List<Trans> aTranss = new ArrayList<Trans>();
        if (aBillTransactorId != 0) {
            if (aPayCategory.equals("IN")) {//for IN(CUSTOMER, SCHEME)
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 2);//SALE INVOICE
            } else if (aPayCategory.equals("OUT")) {
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 1);//for IN(PURCHASE INVOICE)
            } else {
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 33);//for INVALID
            }
        } else if (aTransactionId != 0) {
            Trans aTrans = new Trans();
            try {
                if (aPayCategory.equals("IN")) {//for IN(CUSTOMER, SCHEME)
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 2);//SALE INVOICE
                } else if (aPayCategory.equals("OUT")) {
                    aTrans = new TransBean().getTransByIdType(aTransactionId, 1);//for IN(PURCHASE INVOICE)
                }
            } catch (NullPointerException npe) {
                aTrans = null;
            }
            aTranss.add(aTrans);
        }
        return aTranss;
    }

    public List<Trans> getTranssByBillTrPayCatTransNo(String aTransactionNumber, long aBillTransactorId, String aPayCategory) {
        List<Trans> aTranss = new ArrayList<Trans>();
        if (aBillTransactorId != 0) {
            if (aPayCategory.equals("IN")) {//for IN(CUSTOMER, SCHEME)
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 2);//SALE INVOICE
            } else if (aPayCategory.equals("OUT")) {
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 1);//for IN(PURCHASE INVOICE)
            } else {
                aTranss = this.getTranssByTransactorTransactionType(aBillTransactorId, 33);//for INVALID
            }
        } else if (aTransactionNumber.length() > 0) {
            Trans aTrans = new Trans();
            try {
                if (aPayCategory.equals("IN")) {//for IN(CUSTOMER, SCHEME)
                    aTrans = new TransBean().getTransByNumberType(aTransactionNumber, 2);//SALE INVOICE
                } else if (aPayCategory.equals("OUT")) {
                    aTrans = new TransBean().getTransByNumberType(aTransactionNumber, 1);//for IN(PURCHASE INVOICE)
                }
            } catch (NullPointerException npe) {
                aTrans = null;
            }
            aTranss.add(aTrans);
        }
        this.setTransactorTranss(aTranss);
        return aTranss;
    }

    public List<Trans> getTranssByTransactorTransType(long aTransactorId, String aTransactorType) {
        String sql;
        sql = "{call sp_search_transaction_by_transactor_transtype(?,?)}";
        ResultSet rs = null;
        Transs = new ArrayList<Trans>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactorId);
            if (aTransactorType.equals("SUPPLIER")) {
                ps.setInt(2, 1);
            } else if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                ps.setInt(2, 2);
            } else {
                ps.setInt(2, 33);//invalid one
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Transs.add(this.getTransFromResultset(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Transs;
    }

    public List<Trans> getTranssByTransactorTransactionType(long aTransactorId, int aTransactionTypeId) {
        String sql;
        sql = "{call sp_search_transaction_by_transactor_transtype(?,?)}";
        ResultSet rs = null;
        Transs = new ArrayList<Trans>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactorId);
            ps.setInt(2, aTransactionTypeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Transs.add(this.getTransFromResultset(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Transs;
    }

    public List<Trans> getTranssByBillTransactorTransType(long aBillTransactorId, String aTransactorType) {
        String sql;
        sql = "{call sp_search_transaction_by_bill_transactor_transtype(?,?)}";
        ResultSet rs = null;
        Transs = new ArrayList<Trans>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aBillTransactorId);
            if (aTransactorType.equals("SUPPLIER")) {
                ps.setInt(2, 1);
            } else if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME") || aTransactorType.equals("PROVIDER")) {
                ps.setInt(2, 2);
            } else {
                ps.setInt(2, 33);//invalid one
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Transs.add(this.getTransFromResultset(rs));
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return Transs;
    }

    public List<Trans> getTranssByAddUser(int aAddUserDetailId, int aTransactionTypeId, int aStoreId) {
        String sql;
        sql = "{call sp_search_transaction_by_add_user_detail_id(?,?,?)}";
        ResultSet rs = null;
        if (aAddUserDetailId != 0) {
            Transs = new ArrayList<Trans>();
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, aAddUserDetailId);
                ps.setInt(2, aTransactionTypeId);
                ps.setInt(3, aStoreId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Transs.add(this.getTransFromResultset(rs));
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        } else {
            Transs.clear();
        }
        return Transs;
    }

    public List<Trans> getReportTrans(Trans aTrans) {
        String sql;
        sql = "{call sp_report_transaction(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTrans.clear();
        if (aTrans != null) {
            if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() == null
                    && aTrans.getAddDate() == null && aTrans.getAddDate2() == null
                    && aTrans.getEditDate() == null && aTrans.getEditDate2() == null) {
                this.ActionMessage = (("Atleast one date range(TransactionDate,AddDate,EditDate) is needed..."));
            } else if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() != null) {
                this.ActionMessage = (("Trans Date(From) is needed..."));
            } else if (aTrans.getTransactionDate() != null && aTrans.getTransactionDate2() == null) {
                this.ActionMessage = (("Trans Date(T0) is needed..."));
            } else if (aTrans.getAddDate() == null && aTrans.getAddDate2() != null) {
                this.ActionMessage = (("Add Date(From) is needed..."));
            } else if (aTrans.getAddDate() != null && aTrans.getAddDate2() == null) {
                this.ActionMessage = (("Add Date(To) is needed..."));
            } else if (aTrans.getEditDate() == null && aTrans.getEditDate2() != null) {
                this.ActionMessage = (("Edit Date(From) is needed..."));
            } else if (aTrans.getEditDate() != null && aTrans.getEditDate2() == null) {
                this.ActionMessage = (("Edit Date(To) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setInt(4, aTrans.getStore2Id());
                    } catch (NullPointerException npe) {
                        ps.setInt(4, 0);
                    }
                    try {
                        ps.setLong(5, aTrans.getTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(5, 0);
                    }
                    try {
                        ps.setInt(6, aTrans.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    try {
                        ps.setInt(7, aTrans.getTransactionReasonId());
                    } catch (NullPointerException npe) {
                        ps.setInt(7, 0);
                    }
                    try {
                        ps.setInt(8, aTrans.getAddUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(8, 0);
                    }
                    try {
                        ps.setTimestamp(9, new java.sql.Timestamp(aTrans.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(9, null);
                    }
                    try {
                        ps.setTimestamp(10, new java.sql.Timestamp(aTrans.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(10, null);
                    }
                    try {
                        ps.setInt(11, aTrans.getEditUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(11, 0);
                    }
                    try {
                        ps.setTimestamp(12, new java.sql.Timestamp(aTrans.getEditDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(12, null);
                    }
                    try {
                        ps.setTimestamp(13, new java.sql.Timestamp(aTrans.getEditDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(13, null);
                    }
                    try {
                        ps.setInt(14, aTrans.getTransactionUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(14, 0);
                    }
                    try {
                        ps.setLong(15, aTrans.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(15, 0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ReportTrans.add(this.getTransFromResultset(rs));
                    }
                    this.ActionMessage = ((""));
                } catch (SQLException se) {
                    System.err.println(se.getMessage());
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        return this.ReportTrans;
    }

    public List<Trans> getReportBill(Trans aTrans) {
        String sql;
        sql = "{call sp_report_bill(?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTrans.clear();
        if (aTrans != null) {
            if (aTrans.getTransactionDate() == null || aTrans.getTransactionDate2() == null) {
                //this.ActionMessage = (("Both From and To Dates are needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setLong(4, aTrans.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(4, 0);
                    }

                    rs = ps.executeQuery();
                    while (rs.next()) {
                        this.ReportTrans.add(this.getTransFromResultset(rs));
                    }
                    this.ActionMessage = ((""));
                } catch (SQLException se) {
                    System.err.println(se.getMessage());
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        //this.ReportGrandTotal=this.getReportBillGrandTotal(ReportTrans);
        return this.ReportTrans;
    }

    public float getReportBillTotal(Trans aTrans) {
        float gTotal = 0;
        String sql;
        sql = "{call sp_report_bill_summary(?,?,?,?)}";
        ResultSet rs = null;
        if (aTrans != null) {
            if (aTrans.getTransactionDate() == null || aTrans.getTransactionDate2() == null) {
                //this.ActionMessage = (("Both From and To Dates are needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setLong(4, aTrans.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(4, 0);
                    }

                    rs = ps.executeQuery();
                    if (rs.next()) {
                        gTotal = rs.getFloat("sum_grand_total");
                    }
                } catch (SQLException se) {
                    System.err.println(se.getMessage());
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        return gTotal;
    }

    public long getReportTransCount() {
        return this.ReportTrans.size();
    }

    public List<TransSummary> getReportTransSummary(Trans aTrans, TransSummary aTransSummary) {
        String sql;
        sql = "{call sp_report_transaction_summary(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransSummary.clear();
        if (aTrans != null && aTransSummary != null) {
            //if(aTransSummary.getFieldName().equals("")){
            //nothing
            //}else 
            if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() == null
                    && aTrans.getAddDate() == null && aTrans.getAddDate2() == null
                    && aTrans.getEditDate() == null && aTrans.getEditDate2() == null) {
                //this.ActionMessage=(("Atleast one date range(TransactionDate,AddDate,EditDate) is needed..."));
            } else if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() != null) {
                //this.ActionMessage=(("Trans Date(From) is needed..."));
            } else if (aTrans.getTransactionDate() != null && aTrans.getTransactionDate2() == null) {
                //this.ActionMessage=(("Trans Date(T0) is needed..."));
            } else if (aTrans.getAddDate() == null && aTrans.getAddDate2() != null) {
                //this.ActionMessage=(("Add Date(From) is needed..."));
            } else if (aTrans.getAddDate() != null && aTrans.getAddDate2() == null) {
                //this.ActionMessage=(("Add Date(To) is needed..."));
            } else if (aTrans.getEditDate() == null && aTrans.getEditDate2() != null) {
                //this.ActionMessage=(("Edit Date(From) is needed..."));
            } else if (aTrans.getEditDate() != null && aTrans.getEditDate2() == null) {
                //this.ActionMessage=(("Edit Date(To) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setInt(4, aTrans.getStore2Id());
                    } catch (NullPointerException npe) {
                        ps.setInt(4, 0);
                    }
                    try {
                        ps.setLong(5, aTrans.getTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(5, 0);
                    }
                    try {
                        ps.setInt(6, aTrans.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    try {
                        ps.setInt(7, aTrans.getTransactionReasonId());
                    } catch (NullPointerException npe) {
                        ps.setInt(7, 0);
                    }
                    try {
                        ps.setInt(8, aTrans.getAddUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(8, 0);
                    }
                    try {
                        ps.setTimestamp(9, new java.sql.Timestamp(aTrans.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(9, null);
                    }
                    try {
                        ps.setTimestamp(10, new java.sql.Timestamp(aTrans.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(10, null);
                    }
                    try {
                        ps.setInt(11, aTrans.getEditUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(11, 0);
                    }
                    try {
                        ps.setTimestamp(12, new java.sql.Timestamp(aTrans.getEditDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(12, null);
                    }
                    try {
                        ps.setTimestamp(13, new java.sql.Timestamp(aTrans.getEditDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(13, null);
                    }
                    try {
                        ps.setString(14, aTransSummary.getFieldName());
                    } catch (NullPointerException npe) {
                        ps.setString(14, "");
                    }
                    try {
                        ps.setInt(15, aTrans.getTransactionUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(15, 0);
                    }
                    try {
                        ps.setLong(16, aTrans.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(16, 0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ReportTransSummary.add(this.getTransSummaryFromResultset(rs));
                    }
                    this.ActionMessage = ((""));
                } catch (SQLException se) {
                    System.err.println(se.getMessage());
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        return this.ReportTransSummary;
    }

    public List<TransSummary> getReportTransEarnUserSummary(Trans aTrans) {
        String sql;
        sql = "{call sp_report_transaction_user_earn_summary(?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransSummary.clear();
        if (aTrans != null) {
            if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() == null) {
                //this.ActionMessage=(("Atleast one date range(TransactionDate,AddDate,EditDate) is needed..."));
            } else if (aTrans.getTransactionDate() == null && aTrans.getTransactionDate2() != null) {
                //this.ActionMessage=(("Trans Date(From) is needed..."));
            } else if (aTrans.getTransactionDate() != null && aTrans.getTransactionDate2() == null) {
                //this.ActionMessage=(("Trans Date(T0) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aTrans.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTrans.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aTrans.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setInt(4, aTrans.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(4, 0);
                    }
                    try {
                        ps.setInt(5, aTrans.getTransactionReasonId());
                    } catch (NullPointerException npe) {
                        ps.setInt(5, 0);
                    }
                    try {
                        ps.setInt(6, aTrans.getTransactionUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        TransSummary ts = new TransSummary();
                        try {
                            ts.setEarnUserId(rs.getInt("EarnUserId"));
                        } catch (Exception ex) {
                            ts.setEarnUserId(0);
                        }
                        try {
                            ts.setTotalEarnAmount(rs.getFloat("TotalEarnAmount"));
                        } catch (Exception ex) {
                            ts.setTotalEarnAmount(0);
                        }
                        this.ReportTransSummary.add(ts);
                    }
                    this.ActionMessage = ((""));
                } catch (SQLException se) {
                    System.err.println(se.getMessage());
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        return this.ReportTransSummary;
    }

    public String getFieldName(String aFieldId, String aFieldName) {
        UserDetailBean udb = new UserDetailBean();
        TransactorBean tb = new TransactorBean();
        TransactionReasonBean trb = new TransactionReasonBean();
        String ReturnField = "";
        try {
            if ((aFieldName.equals("add_user_detail_id") || aFieldName.equals("edit_user_detail_id") || aFieldName.equals("transaction_user_detail_id")) && !aFieldId.equals("")) {
                ReturnField = udb.getUserDetail(Integer.parseInt(aFieldId)).getFirstName() + " " + udb.getUserDetail(Integer.parseInt(aFieldId)).getSecondName();
            } else if (aFieldName.equals("transactor_id") && !aFieldId.equals("")) {
                ReturnField = tb.getTransactor(Long.parseLong(aFieldId)).getTransactorNames();
            } else if (aFieldName.equals("bill_transactor_id") && !aFieldId.equals("")) {
                ReturnField = tb.getTransactor(Long.parseLong(aFieldId)).getTransactorNames();
            } else if (aFieldName.equals("transaction_reason_id") && !aFieldId.equals("")) {
                ReturnField = trb.getTransactionReason(Integer.parseInt(aFieldId)).getTransactionReasonName();
            } else {
                ReturnField = "Summary";
            }
            return ReturnField;
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public boolean isApproveNeeded(Trans aTrans) {
        if (aTrans != null) {
            if ((aTrans.getCashDiscount() > 0 && new GeneralUserSetting().getIsApproveDiscountNeeded() == 1) || (aTrans.getSpendPointsAmount() > 0 && new GeneralUserSetting().getIsApprovePointsNeeded() == 1)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isDisableOverridePrices(UserDetail aUserDetail, List<GroupRight> aGroupRights) {
        GroupRightBean grb = new GroupRightBean();
        if (grb.IsUserGroupsFunctionAccessAllowed(aUserDetail, aGroupRights, "ITEM", "Edit") == 1 && grb.IsUserGroupsFunctionAccessAllowed(aUserDetail, aGroupRights, "ITEM", "Add") == 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isCreateFolio(UserDetail aUserDetail, List<GroupRight> aGroupRights) {
        GroupRightBean grb = new GroupRightBean();
        if (grb.IsUserGroupsFunctionAccessAllowed(aUserDetail, aGroupRights, "GUESTFOLIO", "Edit") == 1 && grb.IsUserGroupsFunctionAccessAllowed(aUserDetail, aGroupRights, "GUESTFOLIO", "Add") == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setDateToToday(Trans aTrans) {
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aTrans.setAddDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aTrans.getAddDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setAddDate(cal.getTime());

        aTrans.setAddDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aTrans.getAddDate2());
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setAddDate2(cal2.getTime());
    }

    public void setTransDateToToday(Trans aTrans) {
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aTrans.setTransactionDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aTrans.getTransactionDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setTransactionDate(cal.getTime());

        aTrans.setTransactionDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aTrans.getTransactionDate2());
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setTransactionDate2(cal2.getTime());
    }

    public void setDateToYesturday(Trans aTrans) {
        //Date CurrentServerDate=CompanySetting.getCURRENT_SERVER_DATE();
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aTrans.setAddDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aTrans.getAddDate());
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setAddDate(cal.getTime());

        aTrans.setAddDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aTrans.getAddDate2());
        cal2.add(Calendar.DATE, -1);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setAddDate2(cal2.getTime());
    }

    public void setTransDateToYesturday(Trans aTrans) {
        //Date CurrentServerDate=CompanySetting.getCURRENT_SERVER_DATE();
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aTrans.setTransactionDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aTrans.getTransactionDate());
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setTransactionDate(cal.getTime());

        aTrans.setTransactionDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aTrans.getTransactionDate2());
        cal2.add(Calendar.DATE, -1);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aTrans.setTransactionDate2(cal2.getTime());
    }

    /**
     * @param Transs the Transs to set
     */
    public void setTranss(List<Trans> Transs) {
        this.Transs = Transs;
    }

    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param ActionMessage the ActionMessage to set
     */
    public void setActionMessage(String ActionMessage) {
        this.ActionMessage = ActionMessage;
    }

    /**
     * @return the SelectedTrans
     */
    public Trans getSelectedTrans() {
        return SelectedTrans;
    }

    /**
     * @param SelectedTrans the SelectedTrans to set
     */
    public void setSelectedTrans(Trans SelectedTrans) {
        this.SelectedTrans = SelectedTrans;
    }

    /**
     * @return the SelectedTransactionId
     */
    public long getSelectedTransactionId() {
        return SelectedTransactionId;
    }

    /**
     * @param SelectedTransactionId the SelectedTransactionId to set
     */
    public void setSelectedTransactionId(long SelectedTransactionId) {
        this.SelectedTransactionId = SelectedTransactionId;
    }

    /**
     * @return the SearchTrans
     */
    public String getSearchTrans() {
        return SearchTrans;
    }

    /**
     * @param SearchTrans the SearchTrans to set
     */
    public void setSearchTrans(String SearchTrans) {
        this.SearchTrans = SearchTrans;
    }

    /**
     * @return the TypedTransactorName
     */
    public String getTypedTransactorName() {
        return TypedTransactorName;
    }

    /**
     * @param TypedTransactorName the TypedTransactorName to set
     */
    public void setTypedTransactorName(String TypedTransactorName) {
        this.TypedTransactorName = TypedTransactorName;
    }

    public void insertApproveTrans(long aTransactionId, String aFunctionName, int aUserDetailId) {
        String sql = "{call sp_insert_transaction_approve(?,?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setLong("in_transaction_id", aTransactionId);
            cs.setString("in_function_name", aFunctionName);
            cs.setInt("in_user_detail_id", aUserDetailId);
            cs.executeUpdate();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
    }

    public long getTransViewAbsoluteTransactionId(long aTransactionId, long aTransactionId2) {
        long AbsoluteTransactionId = 0;
        if (aTransactionId != 0 || aTransactionId2 != 0) {
            if (aTransactionId != 0) {
                AbsoluteTransactionId = aTransactionId;
            } else {
                AbsoluteTransactionId = aTransactionId2;
            }
        }
        return AbsoluteTransactionId;
    }

    public String getTransViewAbsoluteTransactionNo(String aTransactionNo2, String aTransactionNo3) {
        String AbsoluteTransactionNo = "";
        if (aTransactionNo2.length() > 0 || aTransactionNo3.length() > 0) {
            if (aTransactionNo2.length() > 0) {
                AbsoluteTransactionNo = aTransactionNo2;
            } else {
                AbsoluteTransactionNo = aTransactionNo3;
            }
        }
        return AbsoluteTransactionNo;
    }

    public void ViewTransByTransIdType(long aTransactionId, int aTransactionTypeId, int aOverride) {
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
        httpSession.setAttribute("CURRENT_PAY_ID", 0);
        try {
            if (aTransactionId > 0 && aTransactionTypeId > 0) {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                try {
                    httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId).getPayId());
                } catch (NullPointerException npe) {
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
                //open the view in a dialog
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", true);
                options.put("contentWidth", 1000);
                options.put("contentHeight", 600);
                options.put("scrollable", true);
                httpSession.setAttribute("CURRENT_PRINT_OUT_JSF_FILE", this.getPrintoutJsfFile(aTransactionTypeId, aOverride));
                org.primefaces.context.RequestContext.getCurrentInstance().openDialog("TransactionViewALL.xhtml", options, null);
            }
        } catch (NullPointerException npe) {
        }
    }

    public void SaleView(long aTransactionId, long aTransactionId2) {
        Trans aTrans = null;
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        try {
            if (aTransactionId != 0 || aTransactionId2 != 0) {
                if (aTransactionId != 0) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                    httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId).getPayId());
                } else {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId2);
                    httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId2).getPayId());
                }
                aTrans = this.getTrans(new GeneralUserSetting().getCurrentTransactionId());
                if (!"SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || aTrans.getTransactionTypeId() != 2 || aTrans.getStoreId() != new GeneralUserSetting().getCurrentStore().getStoreId()) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
            } else {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);
            }
            //---SalesInvoiceBean.initSalesInvoiceBean();
        } catch (NullPointerException npe) {
            httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
            httpSession.setAttribute("CURRENT_PAY_ID", 0);
            //---SalesInvoiceBean.initSalesInvoiceBean();
        }
    }

    public void ViewTrans(long aTransactionId) {
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
        httpSession.setAttribute("CURRENT_PAY_ID", 0);
        try {
            if (aTransactionId != 0) {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                try {
                    httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId).getPayId());
                } catch (NullPointerException npe) {
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
                //open the view in a dialog
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", true);
                options.put("contentWidth", 1000);
                options.put("contentHeight", 650);
                options.put("scrollable", true);
                //org.primefaces.context.RequestContext.getCurrentInstance().openDialog("TransactionView.xhtml", options, null);
                org.primefaces.context.RequestContext.getCurrentInstance().openDialog("TransViewStatic.xhtml", options, null);
            }
        } catch (NullPointerException npe) {
        }
    }

    public void ViewSalesTrans(long aTransactionId) {
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
        httpSession.setAttribute("CURRENT_PAY_ID", 0);
        try {
            if (aTransactionId != 0) {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                try {
                    httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId).getPayId());
                } catch (NullPointerException npe) {
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
                //open the view in a dialog
                Map<String, Object> options = new HashMap<String, Object>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", true);
                options.put("contentWidth", 1000);
                options.put("contentHeight", 500);
                options.put("scrollable", true);
//                if (CompanySetting.getSalesReceiptVersion() == 1) {
//                    org.primefaces.context.RequestContext.getCurrentInstance().openDialog("SaleTransInvoice.xhtml", options, null);
//                } else if (CompanySetting.getSalesReceiptVersion() == 2) {
//                    org.primefaces.context.RequestContext.getCurrentInstance().openDialog("SaleTransInvoice2.xhtml", null, null);
//                }
                org.primefaces.context.RequestContext.getCurrentInstance().openDialog(this.getSRCInvoice(), options, null);
            }
        } catch (NullPointerException npe) {
        }
    }

    public void PurchaseView(long aTransactionId, long aTransactionId2) {
        Trans aTrans = null;
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        try {
            if (aTransactionId != 0 || aTransactionId2 != 0) {
                if (aTransactionId != 0) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                    try {
                        httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId).getPayId());
                    } catch (NullPointerException npe) {
                        httpSession.setAttribute("CURRENT_PAY_ID", 0);
                    }
                } else {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId2);
                    try {
                        httpSession.setAttribute("CURRENT_PAY_ID", PayBean.getTransactionFirstPay(aTransactionId2).getPayId());
                    } catch (NullPointerException npe) {
                        httpSession.setAttribute("CURRENT_PAY_ID", 0);
                    }
                }
                aTrans = this.getTrans(new GeneralUserSetting().getCurrentTransactionId());
                if (!"PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || aTrans.getTransactionTypeId() != 1 || aTrans.getStoreId() != new GeneralUserSetting().getCurrentStore().getStoreId()) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
            } else {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);
            }

            //---SalesInvoiceBean.initSalesInvoiceBean();
        } catch (NullPointerException npe) {
            httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
            httpSession.setAttribute("CURRENT_PAY_ID", 0);
            //---SalesInvoiceBean.initSalesInvoiceBean();
        }
    }

    public void DisposeView(long aTransactionId, long aTransactionId2) {
        Trans aTrans = null;
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        try {
            if (aTransactionId != 0 || aTransactionId2 != 0) {
                if (aTransactionId != 0) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                } else {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId2);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
                aTrans = this.getTrans(new GeneralUserSetting().getCurrentTransactionId());
                if (!"DISPOSE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || aTrans.getTransactionTypeId() != 3 || aTrans.getStoreId() != new GeneralUserSetting().getCurrentStore().getStoreId()) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
            } else {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);
            }

            //---SalesInvoiceBean.initSalesInvoiceBean();
        } catch (NullPointerException npe) {
            httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
            httpSession.setAttribute("CURRENT_PAY_ID", 0);
            //---SalesInvoiceBean.initSalesInvoiceBean();
        }
    }

    public void TransferView(long aTransactionId, long aTransactionId2) {
        Trans aTrans = null;
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        try {
            if (aTransactionId != 0 || aTransactionId2 != 0) {
                if (aTransactionId != 0) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                } else {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransactionId2);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
                aTrans = this.getTrans(new GeneralUserSetting().getCurrentTransactionId());
                if (!"TRANSFER".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || aTrans.getTransactionTypeId() != 4 || aTrans.getStoreId() != new GeneralUserSetting().getCurrentStore().getStoreId()) {
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                    httpSession.setAttribute("CURRENT_PAY_ID", 0);
                }
            } else {
                httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
                httpSession.setAttribute("CURRENT_PAY_ID", 0);
            }

            //---SalesInvoiceBean.initSalesInvoiceBean();
        } catch (NullPointerException npe) {
            httpSession.setAttribute("CURRENT_TRANSACTION_ID", 0);
            httpSession.setAttribute("CURRENT_PAY_ID", 0);
            //---SalesInvoiceBean.initSalesInvoiceBean();
        }
    }

    public void showSalesInvoice() {
        options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("contentHeight", 420);
        org.primefaces.context.RequestContext.getCurrentInstance().openDialog("SalesInvoice.xhtml", options, null);
    }

    public void printSalesInvoice() {
        options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("contentHeight", 420);
        org.primefaces.context.RequestContext.getCurrentInstance().openDialog("SalesInvoice.xhtml", options, null);
    }

    public void callAnotherButton() {
        org.primefaces.context.RequestContext.getCurrentInstance().execute("doHiddenClick()");
    }

    public void autoPrint() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot root = facesContext.getViewRoot();
        //ActionEvent actionEvent = new ActionEvent(root.findComponent("TransFormSales:cmdbPrint"));
        ActionEvent actionEvent = new ActionEvent(root.findComponent("TransFormSales:cmdbPrint"));
        actionEvent.queue();
        System.out.println("AvtionEvent To String=" + actionEvent.toString());
        System.out.println("Component Id==" + root.findComponent(":TransFormSales:cmdbPrint").getId());

    }

    public void dummyAction() {
        //does nothing
    }

    public boolean isTransDeleted(long aTransactionId) {
        String sql = "{call sp_search_transaction_deleted_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;//deleted
            } else {
                return false;//not deleted
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return true;//deleted
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }

    public void initClearActionStatus() {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            this.setActionMessage("");
        }
    }

    /**
     * @return the SelectedTransactionId2
     */
    public long getSelectedTransactionId2() {
        return SelectedTransactionId2;
    }

    /**
     * @param SelectedTransactionId2 the SelectedTransactionId2 to set
     */
    public void setSelectedTransactionId2(long SelectedTransactionId2) {
        this.SelectedTransactionId2 = SelectedTransactionId2;
    }

    /**
     * @return the AutoPrintAfterSave
     */
    public boolean isAutoPrintAfterSave() {
        return AutoPrintAfterSave;
    }

    /**
     * @param AutoPrintAfterSave the AutoPrintAfterSave to set
     */
    public void setAutoPrintAfterSave(boolean AutoPrintAfterSave) {
        this.AutoPrintAfterSave = AutoPrintAfterSave;
    }

    public void setTransTotalsAndUpdate(Trans aTrans, List<TransItem> aActiveTransItems) {
        aTrans.setTotalTradeDiscount(this.getTotalTradeDiscount(aActiveTransItems));
        aTrans.setTotalVat(this.getTotalVat(aActiveTransItems));
        aTrans.setSubTotal(this.getSubTotal(aActiveTransItems));
        aTrans.setGrandTotal(this.getGrandTotal(aTrans, aActiveTransItems));
        aTrans.setTotalStdVatableAmount(this.getTotalStdVatableAmount(aActiveTransItems));
        aTrans.setTotalZeroVatableAmount(this.getTotalZeroVatableAmount(aActiveTransItems));
        aTrans.setTotalExemptVatableAmount(this.getTotalExemptVatableAmount(aActiveTransItems));
        aTrans.setChangeAmount(this.getChangeAmount(aTrans));
        aTrans.setPointsAwarded(this.getPointsAwarded(aTrans));
        aTrans.setSpendPoints(this.getSpendPoints(aTrans));
        aTrans.setTotalProfitMargin(this.getTotalProfitMargin(aActiveTransItems));
    }

    public float getTotalProfitMargin(List<TransItem> aActiveTransItems) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float TotProfitMargin = 0;
        while (ListItemIndex < ListItemNo) {
            TotProfitMargin = TotProfitMargin + (ati.get(ListItemIndex).getUnitProfitMargin() * ati.get(ListItemIndex).getItemQty());
            ListItemIndex = ListItemIndex + 1;
        }
        return TotProfitMargin;
    }

    public float getTotalTradeDiscount(List<TransItem> aActiveTransItems) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float TotTradeDisc = 0;
        while (ListItemIndex < ListItemNo) {
            TotTradeDisc = TotTradeDisc + (ati.get(ListItemIndex).getUnitTradeDiscount() * ati.get(ListItemIndex).getItemQty());
            ListItemIndex = ListItemIndex + 1;
        }
        return TotTradeDisc;
    }

    public float getTotalVat(List<TransItem> aActiveTransItems) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float TVat = 0;
        while (ListItemIndex < ListItemNo) {
            TVat = TVat + (ati.get(ListItemIndex).getUnitVat() * ati.get(ListItemIndex).getItemQty());
            ListItemIndex = ListItemIndex + 1;
        }
        return TVat;
    }

    public float getSubTotal(List<TransItem> aActiveTransItems) {
        List<TransItem> ati = aActiveTransItems;
        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        float SubT = 0;
        while (ListItemIndex < ListItemNo) {
            SubT = SubT + (ati.get(ListItemIndex).getUnitPriceExcVat() * ati.get(ListItemIndex).getItemQty());
            ListItemIndex = ListItemIndex + 1;
        }
        return SubT;
    }

    public float getGrandTotal(Trans aTrans, List<TransItem> aActiveTransItems) {
        float GTotal = 0;
        if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            GTotal = (aTrans.getSubTotal() + aTrans.getTotalVat()) - (aTrans.getTotalTradeDiscount() + aTrans.getCashDiscount());
        } else if ("SALE QUOTATION".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            GTotal = (aTrans.getSubTotal() + aTrans.getTotalVat()) - (aTrans.getTotalTradeDiscount() + aTrans.getCashDiscount());
        } else if ("RESERVATION".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            GTotal = (aTrans.getSubTotal() + aTrans.getTotalVat()) - (aTrans.getTotalTradeDiscount() + aTrans.getCashDiscount());
        } else if ("SALE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            GTotal = (aTrans.getSubTotal() + aTrans.getTotalVat()) - (aTrans.getTotalTradeDiscount() + aTrans.getCashDiscount());
        } else if ("PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            List<TransItem> ati = aActiveTransItems;
            int ListItemIndex = 0;
            int ListItemNo = ati.size();
            GTotal = 0;
            while (ListItemIndex < ListItemNo) {
                GTotal = GTotal + (ati.get(ListItemIndex).getAmount());
                ListItemIndex = ListItemIndex + 1;
            }
            GTotal = GTotal - aTrans.getCashDiscount();
        } else if ("PURCHASE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            List<TransItem> ati = aActiveTransItems;
            int ListItemIndex = 0;
            int ListItemNo = ati.size();
            GTotal = 0;
            while (ListItemIndex < ListItemNo) {
                GTotal = GTotal + (ati.get(ListItemIndex).getAmount());
                ListItemIndex = ListItemIndex + 1;
            }
            GTotal = GTotal - aTrans.getCashDiscount();
        } else if ("DISPOSE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            List<TransItem> ati = aActiveTransItems;

            int ListItemIndex = 0;
            int ListItemNo = ati.size();
            GTotal = 0;
            while (ListItemIndex < ListItemNo) {
                GTotal = GTotal + (ati.get(ListItemIndex).getItemQty() * ati.get(ListItemIndex).getUnitPriceExcVat());
                ListItemIndex = ListItemIndex + 1;
            }
        }
        return GTotal;
    }

    public float getPointsAwarded(Trans aTrans) {
        float PtsAwarded = 0;
        try {
            if (aTrans.getPointsCardId() > 0 && aTrans.getCardNumber().length() > 0) {
                PtsAwarded = aTrans.getGrandTotal() / CompanySetting.getAwardAmountPerPoint();
            }
            return PtsAwarded;
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public float getSpendPoints(Trans aTrans) {
        float SpendPts = 0;
        try {
            if (aTrans.getPointsCardId() > 0 && aTrans.getCardNumber().length() > 0) {
                if ("SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                    SpendPts = aTrans.getSpendPointsAmount() / CompanySetting.getSpendAmountPerPoint();
                } else if ("PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
                    SpendPts = 0;
                }
            } else {
                SpendPts = 0;
            }
            return SpendPts;
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public void updatePointsCard(Trans aTrans) {
        if (new DBConnection().isINTER_BRANCH_MySQLConnectionAvailable().equals("ON")) {
            if (new BranchBean().IsCompanyBranchInvalid()) {
                this.clearTransPointsDetails(aTrans);
            } else {
                PointsCard pc = new PointsCard();
                pc = new PointsCardBean().getPointsCardByCardNumber(aTrans.getCardNumber());
                if (pc != null) {
                    aTrans.setPointsCardId(pc.getPointsCardId());
                    aTrans.setCardHolder(pc.getCardHolder());
                    aTrans.setBalancePoints(pc.getPointsBalance());
                    aTrans.setBalancePointsAmount(aTrans.getBalancePoints() * CompanySetting.getSpendAmountPerPoint());
                    aTrans.setSpendPointsAmount(0);
                    aTrans.setSpendPoints(0);
                } else {
                    this.clearTransPointsDetails(aTrans);
                }
            }
        } else {
            this.clearTransPointsDetails(aTrans);
        }
    }

    public void clearTransPointsDetails(Trans aTrans) {
        aTrans.setPointsCardId(0);
        aTrans.setCardNumber("");
        aTrans.setCardHolder("");
        aTrans.setBalancePoints(0);
        aTrans.setBalancePointsAmount(0);
        aTrans.setSpendPointsAmount(0);
        aTrans.setSpendPoints(0);
    }

    public float getTotalStdVatableAmount(List<TransItem> aActiveTransItems) {
        float GTotalStdVatableAmount = 0;
        List<TransItem> ati = aActiveTransItems;

        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        GTotalStdVatableAmount = 0;
        if ("SALE QUOTATION".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            while (ListItemIndex < ListItemNo) {
                if ("STANDARD".equals(ati.get(ListItemIndex).getVatRated())) {
                    if ("Yes".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                        GTotalStdVatableAmount = GTotalStdVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - 0) * ati.get(ListItemIndex).getItemQty());
                    } else {
                        GTotalStdVatableAmount = GTotalStdVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - ati.get(ListItemIndex).getUnitTradeDiscount()) * ati.get(ListItemIndex).getItemQty());
                    }
                }
                ListItemIndex = ListItemIndex + 1;
            }
        }
        return GTotalStdVatableAmount;
    }

    public float getTotalZeroVatableAmount(List<TransItem> aActiveTransItems) {
        float GTotalZeroVatableAmount = 0;
        List<TransItem> ati = aActiveTransItems;

        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        GTotalZeroVatableAmount = 0;
        if ("SALE QUOTATION".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            while (ListItemIndex < ListItemNo) {
                if ("ZERO".equals(ati.get(ListItemIndex).getVatRated())) {
                    if ("Yes".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                        GTotalZeroVatableAmount = GTotalZeroVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - 0) * ati.get(ListItemIndex).getItemQty());
                    } else {
                        GTotalZeroVatableAmount = GTotalZeroVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - ati.get(ListItemIndex).getUnitTradeDiscount()) * ati.get(ListItemIndex).getItemQty());
                    }
                }
                ListItemIndex = ListItemIndex + 1;
            }
        }
        return GTotalZeroVatableAmount;
    }

    public float getTotalExemptVatableAmount(List<TransItem> aActiveTransItems) {
        float GTotalExemptVatableAmount = 0;
        List<TransItem> ati = aActiveTransItems;

        int ListItemIndex = 0;
        int ListItemNo = ati.size();
        GTotalExemptVatableAmount = 0;
        if ("SALE QUOTATION".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "SALE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE INVOICE".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) || "PURCHASE ORDER".equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            while (ListItemIndex < ListItemNo) {
                if ("EXEMPT SALE INVOICE".equals(ati.get(ListItemIndex).getVatRated())) {
                    if ("Yes".equals(CompanySetting.getIsTradeDiscountVatLiable())) {
                        GTotalExemptVatableAmount = GTotalExemptVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - 0) * ati.get(ListItemIndex).getItemQty());
                    } else {
                        GTotalExemptVatableAmount = GTotalExemptVatableAmount + ((ati.get(ListItemIndex).getUnitPriceExcVat() - ati.get(ListItemIndex).getUnitTradeDiscount()) * ati.get(ListItemIndex).getItemQty());
                    }
                }
                ListItemIndex = ListItemIndex + 1;
            }
        }
        return GTotalExemptVatableAmount;
    }

    public float getChangeAmount(Trans aTrans) {
        float ChangeAmt = 0;
        ChangeAmt = (aTrans.getAmountTendered() + aTrans.getSpendPointsAmount()) - aTrans.getGrandTotal();
        return ChangeAmt;
    }

    public String getPriceConflictMsg(float aCurrentCostPrice, float aNewCostPrice) {
        String PriceConflictMsg;
        if (aNewCostPrice <= 0) {
            PriceConflictMsg = "";
        } else if (aNewCostPrice > aCurrentCostPrice) {
            PriceConflictMsg = "HIGH Cost Price";
        } else if (aNewCostPrice < aCurrentCostPrice) {
            PriceConflictMsg = "LOW Cost Price";
        } else {
            PriceConflictMsg = "";
        }
        return PriceConflictMsg;
    }

    /**
     * @return the SRCInvoice
     */
    public String getSRCInvoice() {
        if (CompanySetting.getSalesReceiptVersion() == 1) {//1-Small Width
            SRCInvoice = "TransactionViewSI1.xhtml";
        } else if (CompanySetting.getSalesReceiptVersion() == 2) {//2-A4 Size
            SRCInvoice = "TransactionViewSI2.xhtml";
        } else if (CompanySetting.getSalesReceiptVersion() == 3) {//3-Very Small Width
            SRCInvoice = "TransactionViewSI3.xhtml";
        }
        return SRCInvoice;
    }

    public String getPrintoutJsfFile(int aTranstypeId, int aOverrideVersion) {
        /*
         1	PURCHASE INVOICE
         2	SALE INVOICE
         3	DISPOSE
         4	TRANSFER
         5	ITEM
         6	PAYMENT
         7	UNPACK
         8	PURCHASE ORDER
         9	GOODS RECEIVED
         10	SALE QUOTATION
         11	SALE ORDER
         12	GOODS DELIVERY
         13	TRANSFER REQUEST
         */
        String the_file = "";
        switch (aTranstypeId) {
            case 1:
                the_file = "TransactionViewPI.xhtml";
                break;
            case 2:
                if (aOverrideVersion == 0) {
                    if (CompanySetting.getSalesReceiptVersion() == 1) {//1-Small Width
                        the_file = "TransactionViewSI1.xhtml";
                    } else if (CompanySetting.getSalesReceiptVersion() == 2) {//2-A4 Size
                        the_file = "TransactionViewSI2.xhtml";
                    } else if (CompanySetting.getSalesReceiptVersion() == 3) {//3-Very Small Width
                        the_file = "TransactionViewSI3.xhtml";
                    }
                } else if (aOverrideVersion > 0) {
                    if (aOverrideVersion == 1) {//1-Small Width
                        the_file = "TransactionViewSI1.xhtml";
                    } else if (aOverrideVersion == 2) {//2-A4 Size
                        the_file = "TransactionViewSI2.xhtml";
                    } else if (aOverrideVersion == 3) {//3-Very Small Width
                        the_file = "TransactionViewSI3.xhtml";
                    }
                }
                break;
            case 3:
                the_file = "TransactionViewDS.xhtml";
                break;
            case 4:
                the_file = "TransactionViewST.xhtml";
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                the_file = "TransactionViewPO.xhtml";
                break;
            case 9:
                the_file = "TransactionViewGRN.xhtml";
                break;
            case 10:
                the_file = "TransactionViewSQ.xhtml";
                break;
            case 11:
                the_file = "TransactionViewSO.xhtml";
                break;
            case 12:
                the_file = "TransactionViewGDN.xhtml";
                break;
            case 13:
                the_file = "TransactionViewSTR.xhtml";
                break;
            case 14:
                the_file = "TransactionViewR.xhtml";
                break;
        }

        return the_file;
    }

    /**
     * @param SRCInvoice the SRCInvoice to set
     */
    public void setSRCInvoice(String SRCInvoice) {
        this.SRCInvoice = SRCInvoice;
    }

    /**
     * @return the ReportGrandTotal
     */
    public float getReportGrandTotal() {
        return ReportGrandTotal;
    }

    /**
     * @param ReportGrandTotal the ReportGrandTotal to set
     */
    public void setReportGrandTotal(float ReportGrandTotal) {
        this.ReportGrandTotal = ReportGrandTotal;
    }

    /**
     * @return the AuthorisedByUserDetail
     */
    public UserDetail getAuthorisedByUserDetail() {
        return AuthorisedByUserDetail;
    }

    /**
     * @param AuthorisedByUserDetail the AuthorisedByUserDetail to set
     */
    public void setAuthorisedByUserDetail(UserDetail AuthorisedByUserDetail) {
        this.AuthorisedByUserDetail = AuthorisedByUserDetail;
    }

    /**
     * @return the TransUserDetail
     */
    public UserDetail getTransUserDetail() {
        return TransUserDetail;
    }

    /**
     * @param TransUserDetail the TransUserDetail to set
     */
    public void setTransUserDetail(UserDetail TransUserDetail) {
        this.TransUserDetail = TransUserDetail;
    }

    /**
     * @return the TransactorTranss
     */
    public List<Trans> getTransactorTranss() {
        return TransactorTranss;
    }

    /**
     * @param TransactorTranss the TransactorTranss to set
     */
    public void setTransactorTranss(List<Trans> TransactorTranss) {
        this.TransactorTranss = TransactorTranss;
    }

    /**
     * @return the OverridePrintVersion
     */
    public int getOverridePrintVersion() {
        return OverridePrintVersion;
    }

    /**
     * @param OverridePrintVersion the OverridePrintVersion to set
     */
    public void setOverridePrintVersion(int OverridePrintVersion) {
        this.OverridePrintVersion = OverridePrintVersion;
    }

}
