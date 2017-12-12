
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class PayBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pay> Pays;
    private String ActionMessage = null;
    private Pay SelectedPay = null;
    private long SelectedPayId;
    private String SearchPay = "";
    private String TypedTransactorName;
    private Transactor SelectedTransactor;
    private Transactor SelectedBillTransactor;
    private String SearchBy;

    public void savePay(Pay pay) {
        String sql = null;
        String sql2 = null;
        String msg = "";
        TransactorLedger NewTransactorLedger;
        TransactorLedgerBean NewTransactorLedgerBean;
        Trans NewTrans;
        TransBean NewTransBean;
        Transactor NewTransactor;
        Transactor NewBillTransactor;
        TransactorBean NewTransactorBean;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (pay.getPayId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "PAYMENT", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getPayId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "PAYMENT", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getPayDate() == null) {
            msg = "Select Pay Date";
            this.setActionMessage("Payment NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getBillTransactorId() == 0) {
            msg = "Specify the Bill Transactor...";
            this.setActionMessage("Payment NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
//        } else if (pay.getTransactionId() == 0 && (pay.getTransactionTypeId() == 0 || pay.getTransactionReasonId() == 0)) {
//            msg = "Select OR Specify a valid transaction...";
//            this.setActionMessage("Payment NOT saved");
//            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getTransactionTypeId() != 0 && pay.getTransactionReasonId() == 0) {
            msg = "Specify transaction reason...";
            this.setActionMessage("Payment NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getPayMethodId() == 0) {
            msg = "Invalid payment method...";
            this.setActionMessage("Payment NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pay.getPaidAmount() <= 0) {
            msg = "Invalid amount paid...";
            this.setActionMessage("Payment NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (pay.getPayId() == 0) {
                sql = "{call sp_insert_pay(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (pay.getPayId() > 0) {
                sql = "{call sp_update_pay(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            }
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (pay.getPayId() == 0) {
                    cs.setLong("in_transaction_id", pay.getTransactionId());
                    cs.setDate("in_pay_date", new java.sql.Date(pay.getPayDate().getTime()));
                    cs.setFloat("in_paid_amount", pay.getPaidAmount());
                    cs.setInt("in_pay_method_id", pay.getPayMethodId());
                    cs.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());//will be made null by the SP
                    cs.setFloat("in_points_spent", pay.getPointsSpent());
                    cs.setFloat("in_points_spent_amount", pay.getPointsSpentAmount());
                    cs.setTimestamp("in_add_date", new java.sql.Timestamp(new java.util.Date().getTime()));
                    cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));//will be made null by the SP
                    cs.setString("in_pay_ref_no", pay.getPayRefNo());
                    cs.setString("in_pay_category", new GeneralUserSetting().getCurrentPayCategory());
                    cs.setLong("in_bill_transactor_id", pay.getBillTransactorId());
                    if (pay.getTransactionId() > 0) {
                        pay.setTransactionTypeId(new TransBean().getTrans(pay.getTransactionId()).getTransactionTypeId());
                        pay.setTransactionReasonId(new TransBean().getTrans(pay.getTransactionId()).getTransactionReasonId());
                    } else {
                        //take the ones from the interface
                    }
                    cs.setInt("in_transaction_type_id", pay.getTransactionTypeId());
                    cs.setInt("in_transaction_reason_id", pay.getTransactionReasonId());
                    cs.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
                    cs.setInt("in_currency_type_id", pay.getCurrencyTypeId());
                    //define output
                    cs.setLong("in_delete_pay_id", 0);
                    cs.registerOutParameter("out_pay_id", VARCHAR);
                    cs.executeUpdate();
                    //manage session variable
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                    HttpSession httpSession = request.getSession(true);
                    httpSession.setAttribute("CURRENT_PAY_ID", cs.getLong("out_pay_id"));
                    httpSession.setAttribute("CURRENT_TRANSACTION_ID", pay.getTransactionId());

                    //insert TransactorLedger
                    NewTransBean = new TransBean();
                    NewTransactorBean = new TransactorBean();
                    NewTransactor = new Transactor();
                    NewBillTransactor = new Transactor();
                    NewTrans = new Trans();
                    try {
                        NewTrans = NewTransBean.getTrans(pay.getTransactionId());
                        NewTransactor = NewTransactorBean.getTransactor(NewTrans.getTransactorId());
                    } catch (NullPointerException npe) {
                        NewTrans = null;
                        NewTransactor = null;
                    }

                    try {
                        NewBillTransactor = NewTransactorBean.getTransactor(pay.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        NewBillTransactor = null;
                    }

                    if (NewBillTransactor != null) {
                        NewTransactorLedger = new TransactorLedger();
                        NewTransactorLedgerBean = new TransactorLedgerBean();

                        NewTransactorLedger.setStoreId(new GeneralUserSetting().getCurrentStore().getStoreId());
                        NewTransactorLedger.setStoreName(new StoreBean().getStore(new GeneralUserSetting().getCurrentStore().getStoreId()).getStoreName());
                        NewTransactorLedger.setTransactionId(pay.getTransactionId());
                        NewTransactorLedger.setPayId(new GeneralUserSetting().getCurrentPayId());
                        NewTransactorLedger.setTransactionDate(pay.getPayDate());
                        if (NewTransactor != null) {
                            NewTransactorLedger.setTransactorId(NewTransactor.getTransactorId());
                            NewTransactorLedger.setTransactorNames(NewTransactor.getTransactorNames());
                        } else {
                            NewTransactorLedger.setTransactorId(0);
                            NewTransactorLedger.setTransactorNames("");
                        }

                        NewTransactorLedger.setBillTransactorId(NewBillTransactor.getTransactorId());
                        NewTransactorLedger.setBillTransactorNames(NewBillTransactor.getTransactorNames());

                        if (pay.getTransactionTypeId() == 2 && new GeneralUserSetting().getCurrentPayCategory().equals("IN")) {//sale
                            NewTransactorLedger.setTransactionTypeName("SALE");
                            NewTransactorLedger.setDescription("Payment Made for Bought Item(s)");
                            NewTransactorLedger.setLedgerEntryType("C");
                            NewTransactorLedger.setAmountDebit(0);
                            NewTransactorLedger.setAmountCredit(pay.getPaidAmount());
                        } else if (pay.getTransactionTypeId() == 1 && new GeneralUserSetting().getCurrentPayCategory().equals("OUT")) {//purchase
                            NewTransactorLedger.setTransactionTypeName("PURCHASE");
                            NewTransactorLedger.setDescription("Payment Received for Supplied Item(s)");
                            NewTransactorLedger.setLedgerEntryType("D");
                            NewTransactorLedger.setAmountDebit(pay.getPaidAmount());
                            NewTransactorLedger.setAmountCredit(0);
                        } else {
                            NewTransactorLedger.setTransactionTypeName("SALE");
                            NewTransactorLedger.setDescription("Payment Made for Bought Item(s)");
                            NewTransactorLedger.setLedgerEntryType("C");
                            NewTransactorLedger.setAmountDebit(0);
                            NewTransactorLedger.setAmountCredit(pay.getPaidAmount());
                        }
                        NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);

                        NewTransactorLedger = null;
                        NewTransactorLedgerBean = null;
                        NewTransBean = null;
                        NewTransactor = null;
                        NewBillTransactor = null;
                        NewTransactorBean = null;
                        NewTrans = null;
                    }
                    this.setActionMessage("Saved Successfully");
                    this.clearPay(pay);

                } else if (pay.getPayId() > 0) {
                    //for edit if any
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Payment NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Payment NOT saved!"));
            }
        }

    }

    public boolean updatePay(Pay pay) {
        String sql = "{call sp_update_pay(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setLong("in_pay_id", pay.getPayId());
            cs.setLong("in_transaction_id", pay.getTransactionId());
            cs.setDate("in_pay_date", new java.sql.Date(pay.getPayDate().getTime()));
            cs.setFloat("in_paid_amount", pay.getPaidAmount());
            cs.setInt("in_pay_method_id", pay.getPayMethodId());
            cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
            cs.setFloat("in_points_spent_amount", pay.getPointsSpentAmount());
            //pay.setPointsSpent(pay.getPointsSpentAmount()/CompanySetting.getSpendAmountPerPoint());
            cs.setFloat("in_points_spent", pay.getPointsSpent());
            cs.setTimestamp("in_edit_date", new java.sql.Timestamp(new java.util.Date().getTime()));
            cs.setLong("in_delete_pay_id", pay.getDeletePayId());
            cs.setString("in_pay_ref_no", pay.getPayRefNo());
            cs.setString("in_pay_category", pay.getPayCategory());
            cs.setLong("in_bill_transactor_id", pay.getBillTransactorId());
            cs.setInt("in_transaction_type_id", pay.getTransactionTypeId());
            cs.setInt("in_transaction_reason_id", pay.getTransactionReasonId());
            cs.setInt("in_store_id", pay.getStoreId());
            cs.executeUpdate();
            return true;
        } catch (SQLException se) {
            System.err.println("UpdatePay: " + se.getMessage());
            return false;
        }
    }

    public Pay getPay(long PayId) {
        String sql = "{call sp_search_pay_by_pay_id(?)}";
        ResultSet rs = null;
        Pay pay = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, PayId);
            rs = ps.executeQuery();
            if (rs.next()) {
                pay = this.getPayFromResultset(rs);
                return pay;
                //return this.getPayFromResultset(rs);
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

    public Pay getPayFromResultset(ResultSet rs) {
        try {

            Pay pay = new Pay();
            try {
                pay.setPayId(rs.getLong("pay_id"));
            } catch (NullPointerException npe) {
                pay.setPayId(0);
            }
            try {
                pay.setTransactionId(rs.getLong("transaction_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionId(0);
            }
            try {
                pay.setPayDate(new Date(rs.getDate("pay_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setPayDate(null);
            }
            try {
                pay.setPaidAmount(rs.getFloat("paid_amount"));
            } catch (NullPointerException npe) {
                pay.setPaidAmount(0);
            }
            try {
                pay.setPayMethodId(rs.getInt("pay_method_id"));
            } catch (NullPointerException npe) {
                pay.setPayMethodId(0);
            }
            try {
                pay.setAddUserDetailId(rs.getInt("add_user_detail_id"));
            } catch (NullPointerException npe) {
                pay.setAddUserDetailId(0);
            }
            try {
                pay.setEditUserDetailId(rs.getInt("edit_user_detail_id"));
            } catch (NullPointerException npe) {
                pay.setEditUserDetailId(0);
            }
            try {
                pay.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setAddDate(null);
            }
            try {
                pay.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setEditDate(null);
            }
            try {
                pay.setPointsSpent(rs.getFloat("points_spent"));
            } catch (NullPointerException npe) {
                pay.setPointsSpent(0);
            }
            try {
                pay.setPointsSpentAmount(rs.getFloat("points_spent_amount"));
            } catch (NullPointerException npe) {
                pay.setPointsSpentAmount(0);
            }
            try {
                pay.setDeletePayId(rs.getLong("delete_pay_id"));
            } catch (NullPointerException npe) {
                pay.setDeletePayId(0);
            }
            try {
                pay.setPayRefNo(rs.getString("pay_ref_no"));
            } catch (NullPointerException npe) {
                pay.setPayRefNo("");
            }
            try {
                pay.setPayCategory(rs.getString("pay_category"));
            } catch (NullPointerException npe) {
                pay.setPayCategory("");
            }
            try {
                pay.setBillTransactorId(rs.getLong("bill_transactor_id"));
            } catch (NullPointerException npe) {
                pay.setBillTransactorId(0);
            }
            try {
                pay.setTransactionTypeId(rs.getInt("transaction_type_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionTypeId(0);
            }
            try {
                pay.setTransactionReasonId(rs.getInt("transaction_reason_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionReasonId(0);
            }
            try {
                pay.setStoreId(rs.getInt("store_id"));
            } catch (NullPointerException npe) {
                pay.setStoreId(0);
            }
            try {
                pay.setCurrencyTypeName(rs.getString("currency_type_name"));
            } catch (NullPointerException npe) {
                pay.setCurrencyTypeName("");
            }

            try {
                pay.setExchangeRate(rs.getFloat("exchange_rate"));
            } catch (NullPointerException npe) {
                pay.setExchangeRate(0);
            }
            return pay;
        } catch (SQLException se) {
            return null;
        }
    }

    public static Pay getPayFromResultset2(ResultSet rs) {
        try {

            Pay pay = new Pay();
            try {
                pay.setPayId(rs.getLong("pay_id"));
            } catch (NullPointerException npe) {
                pay.setPayId(0);
            }
            try {
                pay.setTransactionId(rs.getLong("transaction_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionId(0);
            }
            try {
                pay.setPayDate(new Date(rs.getDate("pay_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setPayDate(null);
            }
            try {
                pay.setPaidAmount(rs.getFloat("paid_amount"));
            } catch (NullPointerException npe) {
                pay.setPaidAmount(0);
            }
            try {
                pay.setPayMethodId(rs.getInt("pay_method_id"));
            } catch (NullPointerException npe) {
                pay.setPayMethodId(0);
            }
            try {
                pay.setAddUserDetailId(rs.getInt("add_user_detail_id"));
            } catch (NullPointerException npe) {
                pay.setAddUserDetailId(0);
            }
            try {
                pay.setEditUserDetailId(rs.getInt("edit_user_detail_id"));
            } catch (NullPointerException npe) {
                pay.setEditUserDetailId(0);
            }
            try {
                pay.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setAddDate(null);
            }
            try {
                pay.setEditDate(new Date(rs.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException npe) {
                pay.setEditDate(null);
            }
            try {
                pay.setPointsSpent(rs.getFloat("points_spent"));
            } catch (NullPointerException npe) {
                pay.setPointsSpent(0);
            }
            try {
                pay.setPointsSpentAmount(rs.getFloat("points_spent_amount"));
            } catch (NullPointerException npe) {
                pay.setPointsSpentAmount(0);
            }
            try {
                pay.setDeletePayId(rs.getLong("delete_pay_id"));
            } catch (NullPointerException npe) {
                pay.setDeletePayId(0);
            }
            try {
                pay.setPayRefNo(rs.getString("pay_ref_no"));
            } catch (NullPointerException npe) {
                pay.setPayRefNo("");
            }
            try {
                pay.setPayCategory(rs.getString("pay_category"));
            } catch (NullPointerException npe) {
                pay.setPayCategory("");
            }
            try {
                pay.setBillTransactorId(rs.getLong("bill_transactor_id"));
            } catch (NullPointerException npe) {
                pay.setBillTransactorId(0);
            }
            try {
                pay.setTransactionTypeId(rs.getInt("transaction_type_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionTypeId(0);
            }
            try {
                pay.setTransactionReasonId(rs.getInt("transaction_reason_id"));
            } catch (NullPointerException npe) {
                pay.setTransactionReasonId(0);
            }
            try {
                pay.setStoreId(rs.getInt("store_id"));
            } catch (NullPointerException npe) {
                pay.setStoreId(0);
            }
            return pay;
        } catch (SQLException se) {
            return null;
        }
    }

    public void setPayById(long PayId, Pay pay) {
        String sql = "{call sp_search_pay_by_pay_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, PayId);
            rs = ps.executeQuery();
            if (rs.next()) {
                pay = this.getPayFromResultset(rs);
            } else {
                this.clearPay(pay);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.clearPay(pay);
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

    public int getCountPayByDeletePayId(long aDeletePayId) {
        String sql = "{call sp_search_pay_count_by_delete_pay_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aDeletePayId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count_delete_pay");
            } else {
                return 0;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return 0;
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

    public boolean isAllowCancelPay(Pay aPay) {
        if (aPay.getBillTransactorId() == 0 || (aPay.getPaidAmount() <= 0 && aPay.getPointsSpentAmount() <= 0)) {
            return false;
        } else {
            return true;
        }
    }

    public void deletePay(Pay pay) {
        String sql = "DELETE FROM pay WHERE pay_id=?";
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "PAYMENT", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Delete", new FacesMessage(msg));
        } else {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, pay.getPayId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearPay(pay);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Pay NOT deleted");
            }
        }
    }

    public void cancelPay(Pay pay) {
        String msg = "";
        String sql = "";
        TransactorLedger NewTransactorLedger;
        TransactorLedgerBean NewTransactorLedgerBean;
        Trans NewTrans;
        TransBean NewTransBean;
        Transactor NewTransactor;
        Transactor NewBillTransactor;
        TransactorBean NewTransactorBean;
        PointsCard NewPointsCard;
        PointsCardBean NewPointsCardBean;
        PointsTransaction NewPointsTransaction;
        PointsTransactionBean NewPointsTransactionBean;

        long PayHistId = 0;
        boolean isPayCopySuccess = false;
        boolean isPayUpdateSuccess = false;
        boolean isLedgerUpdateSuccess = false;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "PAYMENT", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Delete", new FacesMessage(msg));
        } else if (pay != null & (pay.getPaidAmount() > 0 || pay.getPointsSpentAmount() > 0)) {
            //first make a copy into the history table
            sql = "{call sp_copy_pay(?,?,?,?)}";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong("in_pay_id", pay.getPayId());
                cs.setString("in_hist_flag", "Edit");
                cs.registerOutParameter("out_pay_hist_id", VARCHAR);
                cs.setInt("in_edit_user_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                cs.executeUpdate();
                PayHistId = cs.getLong("out_pay_hist_id");
                isPayCopySuccess = true;
            } catch (SQLException se) {
                isPayCopySuccess = false;
                System.err.println("CopyPay:" + se.getMessage());
            }

            //update the payment by setting paid amounts and points to 0-zero
            if (isPayCopySuccess) {
                Pay newPay = new Pay();
                newPay = this.getPay(pay.getPayId());
                newPay.setPaidAmount(0);
                newPay.setEditUserDetailId(new GeneralUserSetting().getCurrentUser().getUserDetailId());
                newPay.setPointsSpent(0);
                newPay.setPointsSpentAmount(0);
                newPay.setEditDate(new java.util.Date());
                newPay.setDeletePayId(PayHistId);
                isPayUpdateSuccess = this.updatePay(newPay);
            }

            //insert TransactorLedger
            if (isPayUpdateSuccess) {
                try {
                    NewTransBean = new TransBean();
                    NewTransactorBean = new TransactorBean();
                    NewTransactor = new Transactor();
                    NewBillTransactor = new Transactor();
                    NewTrans = new Trans();
                    NewPointsCard = new PointsCard();
                    NewPointsCardBean = new PointsCardBean();
                    NewPointsTransaction = new PointsTransaction();
                    NewPointsTransactionBean = new PointsTransactionBean();

                    try {
                        NewTrans = NewTransBean.getTrans(pay.getTransactionId());
                    } catch (NullPointerException npe) {
                        NewTrans = null;
                    }
                    try {
                        NewTransactor = NewTransactorBean.getTransactor(NewTrans.getTransactorId());
                    } catch (NullPointerException npe) {
                        NewTransactor = null;
                    }
                    try {
                        NewBillTransactor = NewTransactorBean.getTransactor(pay.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        NewBillTransactor = null;
                    }
                    if (NewBillTransactor != null) {
                        NewTransactorLedger = new TransactorLedger();
                        NewTransactorLedgerBean = new TransactorLedgerBean();

                        NewTransactorLedger.setStoreId(new GeneralUserSetting().getCurrentStore().getStoreId());
                        NewTransactorLedger.setStoreName(new GeneralUserSetting().getCurrentStore().getStoreName());
                        try {
                            NewTransactorLedger.setTransactionId(pay.getTransactionId());
                        } catch (NullPointerException npe) {
                            NewTransactorLedger.setTransactionId(0);
                        }
                        //NewTransactorLedger.setPayId(new GeneralUserSetting().getCurrentPayId());
                        NewTransactorLedger.setPayId(pay.getPayId());
                        try {
                            NewTransactorLedger.setTransactorId(NewTransactor.getTransactorId());
                        } catch (NullPointerException npe) {
                            NewTransactorLedger.setTransactorId(0);
                        }
                        //NewTransactorLedger.setTransactorNames(NewTransactor.getTransactorNames());
                        NewTransactorLedger.setTransactionDate(pay.getPayDate());
                        if (NewTransactor != null) {
                            NewTransactorLedger.setTransactorId(NewTransactor.getTransactorId());
                            NewTransactorLedger.setTransactorNames(NewTransactor.getTransactorNames());
                        } else {
                            NewTransactorLedger.setTransactorId(0);
                            NewTransactorLedger.setTransactorNames("");
                        }

                        NewTransactorLedger.setBillTransactorId(NewBillTransactor.getTransactorId());
                        NewTransactorLedger.setBillTransactorNames(NewBillTransactor.getTransactorNames());

                        if (pay.getPaidAmount() > 0) {//for cash payments
                            if (pay.getTransactionTypeId() == 2 && new GeneralUserSetting().getCurrentPayCategory().equals("IN")) {//sale
                                NewTransactorLedger.setTransactionTypeName("SALE");
                                NewTransactorLedger.setDescription("Cancelled - Payment Made for Bought Item(s)");
                                NewTransactorLedger.setLedgerEntryType("D");
                                NewTransactorLedger.setAmountDebit(pay.getPaidAmount());
                                NewTransactorLedger.setAmountCredit(0);
                            } else if (pay.getTransactionTypeId() == 1 && new GeneralUserSetting().getCurrentPayCategory().equals("OUT")) {//purchase
                                NewTransactorLedger.setTransactionTypeName("PURCHASE");
                                NewTransactorLedger.setDescription("Cancelled - Payment Received for Supplied Item(s)");
                                NewTransactorLedger.setLedgerEntryType("C");
                                NewTransactorLedger.setAmountDebit(0);
                                NewTransactorLedger.setAmountCredit(pay.getPaidAmount());
                            }
                            NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);
                        }
                        if (pay.getPointsSpentAmount() > 0) {//for cash payments
                            if (pay.getTransactionTypeId() == 2 && new GeneralUserSetting().getCurrentPayCategory().equals("IN")) {//sale
                                NewTransactorLedger.setTransactionTypeName("SALE");
                                NewTransactorLedger.setDescription("Cancelled - Points Spent Amount for Bought Item(s)");
                                NewTransactorLedger.setLedgerEntryType("D");
                                NewTransactorLedger.setAmountDebit(pay.getPointsSpentAmount());
                                NewTransactorLedger.setAmountCredit(0);
                                NewTransactorLedgerBean.saveTransactorLedger(NewTransactorLedger);

                                //Update customer point card balance
                                //insert PointsTransaction for both the awarded and spent points to the stage area
                                if (!NewTrans.getCardNumber().equals("")) {
                                    //1. insert PointsTransaction for both the awarded and spent points to the stage area
                                    NewPointsTransaction.setPointsCardId(NewPointsCardBean.getPointsCardByCardNumber(NewTrans.getCardNumber()).getPointsCardId());
                                    NewPointsTransaction.setTransactionDate(new java.sql.Date(pay.getPayDate().getTime()));
                                    NewPointsTransaction.setPointsAwarded(0 - NewTrans.getPointsAwarded());
                                    NewPointsTransaction.setPointsSpent(0 - pay.getPointsSpent());
                                    NewPointsTransaction.setTransactionId(NewTrans.getTransactionId());
                                    NewPointsTransaction.setTransBranchId(CompanySetting.getBranchId());
                                    NewPointsTransaction.setPointsSpentAmount(0 - pay.getPointsSpentAmount());
                                    NewPointsTransactionBean.addPointsTransactionToStage(NewPointsTransaction);
                                }
                                //Move all, if any PointsTransactions in the stage area to the live server
                                //The move function after deletes all if any in the stage area, so only the stage area will have those records not committed due to failure to connect to the server
                                //Transactions will be moved if connectivity to the InterBranch DB is ON
                                if (new DBConnection().isINTER_BRANCH_MySQLConnectionAvailable().equals("ON")) {
                                    NewPointsTransactionBean.movePointsTransactionsFromStageToLive();
                                }
                            }
                        }
                        NewTransactorLedger = null;
                        NewTransactorLedgerBean = null;
                        NewTransBean = null;
                        NewTransactor = null;
                        NewTransactorBean = null;
                        NewTrans = null;
                        NewPointsCard = null;
                        NewPointsCardBean = null;
                        NewPointsTransaction = null;
                        NewPointsTransactionBean = null;
                    }
                    isLedgerUpdateSuccess = true;
                } catch (Exception e) {
                    isLedgerUpdateSuccess = false;
                }
            }

            if (isPayCopySuccess && isPayUpdateSuccess && isLedgerUpdateSuccess) {
                //manage session variable
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("CURRENT_PAY_ID", pay.getPayId());

                this.setActionMessage("Cancelled Successfully");
            } else {
                this.setActionMessage("Payment NOT Cancelled");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Payment NOT Cancelled!"));
            }
        } else {
            this.setActionMessage("Payment CANNOT be Cancelled");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Payment CANNOT be Cancelled!"));
        }
    }

    public void displayPay(Pay PayFrom, Pay PayTo) {
        PayTo.setPayId(PayFrom.getPayId());
        PayTo.setTransactionId(PayFrom.getTransactionId());
        PayTo.setPayDate(PayFrom.getPayDate());
        PayTo.setPaidAmount(PayFrom.getPaidAmount());
        PayTo.setPayMethodId(PayFrom.getPayMethodId());
        PayTo.setAddUserDetailId(PayFrom.getAddUserDetailId());
        PayTo.setEditUserDetailId(PayFrom.getEditUserDetailId());
        PayTo.setAddDate(PayFrom.getAddDate());
        PayTo.setEditDate(PayFrom.getEditDate());
        PayTo.setPointsSpent(PayFrom.getPointsSpent());
        PayTo.setPointsSpentAmount(PayFrom.getPointsSpentAmount());
        PayTo.setDeletePayId(PayFrom.getDeletePayId());
    }

    public void clearPay(Pay pay) {
        pay.setPayId(0);
        pay.setTransactionId(0);
        pay.setPayDate(null);
        pay.setPaidAmount(0);
        pay.setPayMethodId(0);
        pay.setAddUserDetailId(0);
        pay.setEditUserDetailId(0);
        pay.setAddDate(null);
        pay.setEditDate(null);
        pay.setPointsSpent(0);
        pay.setPointsSpentAmount(0);
        pay.setDeletePayId(0);
        pay.setPayRefNo("");
        pay.setPayCategory("");
        pay.setBillTransactorId(0);
        pay.setTransactionTypeId(0);
        pay.setTransactionReasonId(0);
        pay.setStoreId(0);
        pay.setCurrencyTypeId(0);
        pay.setSurcharge(0);
        pay.setTotal_amount(0);
    }

    public void initClearPay(Pay pay, Transactor transactor) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            try {
                pay.setPayId(0);
                pay.setTransactionId(0);
                pay.setPayDate(null);
                pay.setPaidAmount(0);
                pay.setPayMethodId(0);
                pay.setAddUserDetailId(0);
                pay.setEditUserDetailId(0);
                pay.setAddDate(null);
                pay.setEditDate(null);
                pay.setPointsSpent(0);
                pay.setPointsSpentAmount(0);
                pay.setDeletePayId(0);
                pay.setPayRefNo("");
                pay.setPayCategory("");
                pay.setBillTransactorId(0);
                pay.setTransactionTypeId(0);
                pay.setTransactionReasonId(0);
                pay.setStoreId(0);
                new TransactorBean().clearTransactor(transactor);
            } catch (Exception e) {

            }
        }
    }

    /**
     * @return the Pays
     */
    public List<Pay> getPays() {
        String sql;
        if (this.SearchPay.length() > 0) {
            sql = "SELECT * FROM pay WHERE pay_id=" + this.SearchPay + " ORDER BY pay_id DESC LIMIT 5";
        } else {
            sql = "SELECT * FROM pay ORDER BY pay_id DESC LIMIT 5";
        }
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByTransaction(long TransId) {
        String sql = "{call sp_search_pay_by_transaction_id(?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByTransIdTransType(long TransId, int aTransTypeId) {
        String sql = "{call sp_search_pay_by_transaction_id_type(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransId);
            ps.setInt(2, aTransTypeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByTransIdPayCat(long TransId, String aPayCat) {
        String sql = "{call sp_search_pay_by_transaction_id_paycat(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransId);
            ps.setString(2, aPayCat);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByTransNoPayCat(String TransNo, String aPayCat) {
        String sql = "{call sp_search_pay_by_transaction_number_paycat(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, TransNo);
            ps.setString(2, aPayCat);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public static Pay getTransactionFirstPay(long aTransactionId) {
        String sql = "{call sp_search_pay_first_by_transaction_id(?)}";
        ResultSet rs = null;
        Pay pay = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                pay = new Pay();
                pay = PayBean.getPayFromResultset2(rs);
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
        return pay;
    }

    public static Pay getTransactionFirstPayByTransNo(String aTransactionNumber) {
        String sql = "{call sp_search_pay_first_by_transaction_number(?)}";
        ResultSet rs = null;
        Pay pay = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aTransactionNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                pay = new Pay();
                pay = PayBean.getPayFromResultset2(rs);
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
        return pay;
    }

    public List<Pay> getPaysByTransactor(long TracId) {
        String sql = "{call sp_search_pay_by_transactor_id(?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TracId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByTrTcTt(long aTransactionId, long aTransactorId, String aTransactorType) {
        List<Pay> aPays = new ArrayList<Pay>();
        aPays.clear();
        if (aTransactorId != 0) {
            aPays = this.getPaysByTransactorTransType(aTransactorId, aTransactorType);
        } else if (aTransactionId != 0) {
            if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                aPays = new PayBean().getPaysByTransIdTransType(aTransactionId, 2);//SALE
            } else if (aTransactorType.equals("SUPPLIER")) {
                aPays = new PayBean().getPaysByTransIdTransType(aTransactionId, 1);//PURCHASE
            }
        }
        return aPays;
    }

    public List<Pay> getPaysByBillTrTcTt(long aTransactionId, long aBillTransactorId, String aTransactorType) {
        List<Pay> aPays = new ArrayList<Pay>();
        aPays.clear();
        if (aBillTransactorId != 0) {
            aPays = this.getPaysByBillTransactorTransType(aBillTransactorId, aTransactorType);
        } else if (aTransactionId != 0) {
            if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                aPays = new PayBean().getPaysByTransIdTransType(aTransactionId, 2);//SALE
            } else if (aTransactorType.equals("SUPPLIER")) {
                aPays = new PayBean().getPaysByTransIdTransType(aTransactionId, 1);//PURCHASE
            }
        }
        return aPays;
    }

    public List<Pay> getPaysByBillTrPaCa(long aTransactionId, long aBillTransactorId, String aPayCat) {
        List<Pay> aPays = new ArrayList<Pay>();
        aPays.clear();
        if (aBillTransactorId != 0) {
            aPays = this.getPaysByBillTransactorPayCat(aBillTransactorId, aPayCat);
        } else if (aTransactionId != 0) {
            aPays = new PayBean().getPaysByTransIdPayCat(aTransactionId, aPayCat);
        }
        return aPays;
    }

    public List<Pay> getPaysByBillTrPaCaTransNo(String aTransactionNumber, long aBillTransactorId, String aPayCat) {
        List<Pay> aPays = new ArrayList<Pay>();
        aPays.clear();
        if (aBillTransactorId != 0) {
            aPays = this.getPaysByBillTransactorPayCat(aBillTransactorId, aPayCat);
        } else if (aTransactionNumber.length() > 0) {
            aPays = new PayBean().getPaysByTransNoPayCat(aTransactionNumber, aPayCat);
        }
        return aPays;
    }

    public List<Pay> getPaysByTransactorTransType(long TracId, String aTransactorType) {
        String sql = "{call sp_search_pay_by_transactor_transtype(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TracId);
            if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                ps.setInt(2, 2);
            } else if (aTransactorType.equals("SUPPLIER")) {
                ps.setInt(2, 1);
            } else {
                ps.setInt(2, 33);//nothing
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByBillTransactorTransType(long TracId, String aTransactorType) {
        String sql = "{call sp_search_pay_by_bill_transactor_transtype(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TracId);
            if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                ps.setInt(2, 2);
            } else if (aTransactorType.equals("SUPPLIER")) {
                ps.setInt(2, 1);
            } else {
                ps.setInt(2, 33);//nothing
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Pays.add(this.getPayFromResultset(rs));
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
        return Pays;
    }

    public List<Pay> getPaysByBillTransactorPayCat(long aBillTransactorId, String aPayCategory) {
        String sql = "{call sp_search_pay_by_bill_transactor_paycat(?,?)}";
        ResultSet rs = null;
        Pays = new ArrayList<Pay>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aBillTransactorId);
            ps.setString(2, aPayCategory);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pay pay = this.getPayFromResultset(rs);
                Pays.add(pay);
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
        return Pays;
    }

    public void ViewReceiptVoucher(long aPayId) {
        //manage session variables
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_PAY_ID", aPayId);
        org.primefaces.context.RequestContext.getCurrentInstance().openDialog("PayReceipt.xhtml", null, null);
    }

    public int getTransTypeIdByPayCat(String aPayCat) {
        int x = 0;
        if (aPayCat.equals("IN")) {//SALES INVOICE
            x = 2;
        } else if (aPayCat.equals("OUT")) {//PURCHASE INVOICE
            x = 1;
        }
        return x;
    }

    /**
     * @param Pays the Pays to set
     */
    public void setPays(List<Pay> Pays) {
        this.Pays = Pays;
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
     * @return the SelectedPay
     */
    public Pay getSelectedPay() {
        return SelectedPay;
    }

    /**
     * @param SelectedPay the SelectedPay to set
     */
    public void setSelectedPay(Pay SelectedPay) {
        this.SelectedPay = SelectedPay;
    }

    /**
     * @return the SelectedPayId
     */
    public long getSelectedPayId() {
        return SelectedPayId;
    }

    /**
     * @param SelectedPayId the SelectedPayId to set
     */
    public void setSelectedPayId(long SelectedPayId) {
        this.SelectedPayId = SelectedPayId;
    }

    /**
     * @return the SearchPay
     */
    public String getSearchPay() {
        return SearchPay;
    }

    /**
     * @param SearchPay the SearchPay to set
     */
    public void setSearchPay(String SearchPay) {
        this.SearchPay = SearchPay;
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

    /**
     * @return the SelectedTransactor
     */
    public Transactor getSelectedTransactor() {
        return SelectedTransactor;
    }

    /**
     * @param SelectedTransactor the SelectedTransactor to set
     */
    public void setSelectedTransactor(Transactor SelectedTransactor) {
        this.SelectedTransactor = SelectedTransactor;
    }

    /**
     * @return the SelectedBillTransactor
     */
    public Transactor getSelectedBillTransactor() {
        return SelectedBillTransactor;
    }

    /**
     * @param SelectedBillTransactor the SelectedBillTransactor to set
     */
    public void setSelectedBillTransactor(Transactor SelectedBillTransactor) {
        this.SelectedBillTransactor = SelectedBillTransactor;
    }

    /**
     * @return the SearchBy
     */
    public String getSearchBy() {
        return SearchBy;
    }

    /**
     * @param SearchBy the SearchBy to set
     */
    public void setSearchBy(String SearchBy) {
        this.SearchBy = SearchBy;
    }

    public void compute_surcharge_total_amount(long pay_method_id, Pay pay) {
        String sql;
        sql = "SELECT * FROM pay_method WHERE pay_method_id=" + pay_method_id + " limit 1";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            if (rs.next()) {
                pay.setSurcharge((rs.getFloat("surcharge") * pay.getPaidAmount()) / 100);
                pay.setTotal_amount(((rs.getFloat("surcharge") * pay.getPaidAmount()) / 100) + pay.getPaidAmount());
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
