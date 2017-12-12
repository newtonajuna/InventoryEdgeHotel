import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
public class ReportPayBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ActionMessage = null;
    List<ReportPay> ActiveReportPay = new ArrayList<ReportPay>();
    List<ReportPaySummary> ActiveReportPaySummary = new ArrayList<ReportPaySummary>();

    public List<ReportPay> getActiveReportPay(ReportPay aReportPay) {
        String sql;
        sql = "{call sp_report_pay(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportPay.clear();
        if (aReportPay != null) {
            if (aReportPay.getPayDate() == null && aReportPay.getPayDate2() == null
                    && aReportPay.getAddDate() == null && aReportPay.getAddDate2() == null
                    && aReportPay.getEditDate() == null && aReportPay.getEditDate2() == null
                    && (aReportPay.getTransactionId() == 0 && aReportPay.getPayId() == 0)) {
                this.setActionMessage("Atleast one date range(PayDate,AddDate,EditDate) is needed...");
            } else if (aReportPay.getPayDate() == null && aReportPay.getPayDate2() != null) {
                this.setActionMessage("Pay Date(From) is needed...");
            } else if (aReportPay.getPayDate() != null && aReportPay.getPayDate2() == null) {
                this.setActionMessage("Pay Date(T0) is needed...");
            } else if (aReportPay.getAddDate() == null && aReportPay.getAddDate2() != null) {
                this.setActionMessage("Add Date(From) is needed...");
            } else if (aReportPay.getAddDate() != null && aReportPay.getAddDate2() == null) {
                this.setActionMessage("Add Date(To) is needed...");
            } else if (aReportPay.getEditDate() == null && aReportPay.getEditDate2() != null) {
                this.setActionMessage("Edit Date(From) is needed...");
            } else if (aReportPay.getEditDate() != null && aReportPay.getEditDate2() == null) {
                this.setActionMessage("Edit Date(To) is needed...");
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aReportPay.getPayDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aReportPay.getPayDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aReportPay.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setLong(4, aReportPay.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(4, 0);
                    }
                    try {
                        ps.setInt(5, aReportPay.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(5, 0);
                    }
                    try {
                        ps.setInt(6, aReportPay.getAddUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    try {
                        ps.setTimestamp(7, new java.sql.Timestamp(aReportPay.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(7, null);
                    }
                    try {
                        ps.setTimestamp(8, new java.sql.Timestamp(aReportPay.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(8, null);
                    }
                    try {
                        ps.setInt(9, aReportPay.getEditUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(9, 0);
                    }
                    try {
                        ps.setTimestamp(10, new java.sql.Timestamp(aReportPay.getEditDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(10, null);
                    }
                    try {
                        ps.setTimestamp(11, new java.sql.Timestamp(aReportPay.getEditDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(11, null);
                    }
                    try {
                        ps.setLong(12, aReportPay.getTransactionId());
                    } catch (NullPointerException npe) {
                        ps.setLong(12, 0);
                    }
                    try {
                        ps.setInt(13, aReportPay.getPayMethodId());
                    } catch (NullPointerException npe) {
                        ps.setInt(13, 0);
                    }
                    try {
                        ps.setLong(14, aReportPay.getPayId());
                    } catch (NullPointerException npe) {
                        ps.setLong(14, 0);
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportPay.add(this.getReportPayFromResultset(rs));
                    }
                    this.setActionMessage("");
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
        return this.ActiveReportPay;
    }

    public long getActiveReportPayCount() {
        return this.ActiveReportPay.size();
    }

    public List<ReportPaySummary> getActiveReportPaySummary(ReportPay aReportPay, ReportPaySummary aReportPaySummary) {
        String sql;
        sql = "{call sp_report_pay_summary(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ActiveReportPaySummary.clear();
        if (aReportPay != null) {
            if (aReportPay.getPayDate() == null && aReportPay.getPayDate2() == null
                    && aReportPay.getAddDate() == null && aReportPay.getAddDate2() == null
                    && aReportPay.getEditDate() == null && aReportPay.getEditDate2() == null
                    && (aReportPay.getTransactionId() == 0 && aReportPay.getPayId() == 0)) {
                this.setActionMessage("Atleast one date range(PayDate,AddDate,EditDate) is needed...");
            } else if (aReportPay.getPayDate() == null && aReportPay.getPayDate2() != null) {
                this.setActionMessage("Pay Date(From) is needed...");
            } else if (aReportPay.getPayDate() != null && aReportPay.getPayDate2() == null) {
                this.setActionMessage("Pay Date(T0) is needed...");
            } else if (aReportPay.getAddDate() == null && aReportPay.getAddDate2() != null) {
                this.setActionMessage("Add Date(From) is needed...");
            } else if (aReportPay.getAddDate() != null && aReportPay.getAddDate2() == null) {
                this.setActionMessage("Add Date(To) is needed...");
            } else if (aReportPay.getEditDate() == null && aReportPay.getEditDate2() != null) {
                this.setActionMessage("Edit Date(From) is needed...");
            } else if (aReportPay.getEditDate() != null && aReportPay.getEditDate2() == null) {
                this.setActionMessage("Edit Date(To) is needed...");
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setDate(1, new java.sql.Date(aReportPay.getPayDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(1, null);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aReportPay.getPayDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setInt(3, aReportPay.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(3, 0);
                    }
                    try {
                        ps.setLong(4, aReportPay.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(4, 0);
                    }
                    try {
                        ps.setInt(5, aReportPay.getTransactionTypeId());
                    } catch (NullPointerException npe) {
                        ps.setInt(5, 0);
                    }
                    try {
                        ps.setInt(6, aReportPay.getAddUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(6, 0);
                    }
                    try {
                        ps.setTimestamp(7, new java.sql.Timestamp(aReportPay.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(7, null);
                    }
                    try {
                        ps.setTimestamp(8, new java.sql.Timestamp(aReportPay.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(8, null);
                    }
                    try {
                        ps.setInt(9, aReportPay.getEditUserDetailId());
                    } catch (NullPointerException npe) {
                        ps.setInt(9, 0);
                    }
                    try {
                        ps.setTimestamp(10, new java.sql.Timestamp(aReportPay.getEditDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(10, null);
                    }
                    try {
                        ps.setTimestamp(11, new java.sql.Timestamp(aReportPay.getEditDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(11, null);
                    }
                    try {
                        ps.setLong(12, aReportPay.getTransactionId());
                    } catch (NullPointerException npe) {
                        ps.setLong(12, 0);
                    }
                    try {
                        ps.setInt(13, aReportPay.getPayMethodId());
                    } catch (NullPointerException npe) {
                        ps.setInt(13, 0);
                    }
                    try {
                        ps.setLong(14, aReportPay.getPayId());
                    } catch (NullPointerException npe) {
                        ps.setLong(14, 0);
                    }
                    try {
                        ps.setString(15, aReportPaySummary.getFieldName());
                    } catch (NullPointerException npe) {
                        ps.setString(15, "");
                    }

                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ActiveReportPaySummary.add(this.getReportPaySummaryFromResultset(rs, aReportPaySummary.getFieldName()));
                    }
                    //refresh cash in out
                    this.refreshCashInOut(this.ActiveReportPaySummary, aReportPaySummary);
                    this.setActionMessage("");
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
        return this.ActiveReportPaySummary;
    }

    public long getActiveReportPaySummaryCount() {
        return this.ActiveReportPaySummary.size();
    }

    public ReportPay getReportPayFromResultset(ResultSet aResultSet) {
        try {
            ReportPay reportPay = new ReportPay();
            reportPay.setPayId(aResultSet.getLong("pay_id"));
            reportPay.setTransactionId(aResultSet.getLong("transaction_id"));
            reportPay.setPayDate(new Date(aResultSet.getDate("pay_date").getTime()));
            reportPay.setPaidAmount(aResultSet.getFloat("paid_amount"));
            reportPay.setPayMethodId(aResultSet.getInt("pay_method_id"));
            reportPay.setAddUserDetailId(aResultSet.getInt("add_user_detail_id"));
            reportPay.setEditUserDetailId(aResultSet.getInt("edit_user_detail_id"));
            reportPay.setAddDate(new Date(aResultSet.getTimestamp("add_date").getTime()));
            try {
                reportPay.setEditDate(new Date(aResultSet.getTimestamp("edit_date").getTime()));
            } catch (NullPointerException npe) {
                reportPay.setEditDate(null);
            }
            reportPay.setPointsSpent(aResultSet.getFloat("points_spent"));
            reportPay.setPointsSpentAmount(aResultSet.getFloat("points_spent_amount"));

            reportPay.setAddUserNames(aResultSet.getString("add_user_names"));
            reportPay.setEditUserNames(aResultSet.getString("edit_user_names"));
            try {
                reportPay.setBillTransactorId(aResultSet.getLong("bill_transactor_id"));
            } catch (NullPointerException npe) {
                reportPay.setBillTransactorId(0);
            }
            reportPay.setBillTransactorNames(aResultSet.getString("bill_transactor_names"));
            reportPay.setStoreId(aResultSet.getInt("store_id"));
            reportPay.setStoreName(aResultSet.getString("store_name"));
            reportPay.setTransactionTypeId(aResultSet.getInt("transaction_type_id"));
            reportPay.setTransactionTypeName(aResultSet.getString("transaction_type_name"));
            reportPay.setPayMethodName(aResultSet.getString("pay_method_name"));

            return reportPay;

        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }

    public ReportPaySummary getReportPaySummaryFromResultset(ResultSet aResultSet, String aFieldName) {
        try {
            if (aResultSet != null) {
                ReportPaySummary reportPaySummary = new ReportPaySummary();
                try {
                    if (aResultSet.getString("field_name") != null) {
                        if (aFieldName.equals("add_user_detail_id")) {
                            reportPaySummary.setFieldName(aResultSet.getString("add_user_names"));
                        } else if (aFieldName.equals("edit_user_detail_id")) {
                            reportPaySummary.setFieldName(aResultSet.getString("edit_user_names"));
                        } else if (aFieldName.equals("bill_transactor_id")) {
                            reportPaySummary.setFieldName(aResultSet.getString("bill_transactor_names"));
                        } else if (aFieldName.equals("pay_method_id")) {
                            reportPaySummary.setFieldName(aResultSet.getString("pay_method_name"));
                        } else if (aFieldName.equals("store_id")) {
                            reportPaySummary.setFieldName(aResultSet.getString("store_name"));
                        } else {
                            reportPaySummary.setFieldName("");
                        }
                    } else {
                        reportPaySummary.setFieldName("");
                    }
                } catch (NullPointerException | SQLException npe) {
                    reportPaySummary.setFieldName("");
                }
                reportPaySummary.setStoreId(aResultSet.getInt("store_id"));
                reportPaySummary.setTransactionTypeId(aResultSet.getInt("transaction_type_id"));
                reportPaySummary.setSumPaidAmount(aResultSet.getFloat("sum_paid_amount"));
                reportPaySummary.setSumPointsSpentAmount(aResultSet.getFloat("sum_points_spent_amount"));
                return reportPaySummary;
            } else {
                return null;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }
    }

    public void refreshCashInOut(List<ReportPaySummary> aReportPaySummaryList, ReportPaySummary aReportPaySummary) {
        List<ReportPaySummary> psl = aReportPaySummaryList;
        int ListItemIndex = 0;
        int ListItemNo = psl.size();
        float CashI = 0;
        float CashO = 0;
        float CashN = 0;
        while (ListItemIndex < ListItemNo) {
            if (psl.get(ListItemIndex).getTransactionTypeId() == 2) {//sales
                CashI = CashI + psl.get(ListItemIndex).getSumPaidAmount();
            } else if (psl.get(ListItemIndex).getTransactionTypeId() == 1) {//purchase
                CashO = CashO + psl.get(ListItemIndex).getSumPaidAmount();
            }
            ListItemIndex = ListItemIndex + 1;
        }
        CashN = CashI - CashO;
        aReportPaySummary.setCashIn(CashI);
        aReportPaySummary.setCashOut(CashO);
        aReportPaySummary.setCashNet(CashN);
    }

    public void setDateToToday(ReportPay aReportPay) {
        //Date CurrentServerDate=CompanySetting.getCURRENT_SERVER_DATE();
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aReportPay.setAddDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aReportPay.getAddDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aReportPay.setAddDate(cal.getTime());

        aReportPay.setAddDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aReportPay.getAddDate2());
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aReportPay.setAddDate2(cal2.getTime());
    }

    public void setDateToYesturday(ReportPay aReportPay) {
        //Date CurrentServerDate=CompanySetting.getCURRENT_SERVER_DATE();
        Date CurrentServerDate = CompanySetting.getCURRENT_SERVER_DATE();

        aReportPay.setAddDate(CurrentServerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aReportPay.getAddDate());
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aReportPay.setAddDate(cal.getTime());

        aReportPay.setAddDate2(CurrentServerDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(aReportPay.getAddDate2());
        cal2.add(Calendar.DATE, -1);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        // Put it back in the Date object  
        aReportPay.setAddDate2(cal2.getTime());
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
}
