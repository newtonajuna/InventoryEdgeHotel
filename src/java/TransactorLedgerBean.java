
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
@ManagedBean(name = "transactorLedgerBean")
@SessionScoped
public class TransactorLedgerBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ActionMessage = null;
    List<TransactorLedger> ReportTransactorLedger = new ArrayList<TransactorLedger>();
    List<TransactorLedger> ReportTransactorLedgerSummary = new ArrayList<TransactorLedger>();
    List<TransactorLedgerRoomDetails> ReportTransactorLedgerRoomDetails = new ArrayList<>();
    List<TransactorLedger> ReportTransactorLedgerSummaryAll = new ArrayList<TransactorLedger>();
    List<TransactorLedger> ReportTransactorLedgerSummaryAllIndividual = new ArrayList<TransactorLedger>();
    private TransactorLedger CurrentTransactorLedger = new TransactorLedger();

    public void saveTransactorLedger(TransactorLedger aTransactorLedger) {
        String sql = null;
        if (aTransactorLedger.getTransactorLedgerId() == 0) {
            sql = "{call sp_insert_transactor_ledger(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        } else {
            sql = "{call sp_update_transactor_ledger(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        }
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setInt("in_store_id", aTransactorLedger.getStoreId());
            cs.setString("in_store_name", aTransactorLedger.getStoreName());
            cs.setLong("in_transaction_id", aTransactorLedger.getTransactionId());
            cs.setLong("in_pay_id", aTransactorLedger.getPayId());
            cs.setString("in_transaction_type_name", aTransactorLedger.getTransactionTypeName());
            cs.setString("in_description", aTransactorLedger.getDescription());
            cs.setDate("in_transaction_date", new java.sql.Date(aTransactorLedger.getTransactionDate().getTime()));
            cs.setLong("in_transactor_id", aTransactorLedger.getTransactorId());
            cs.setString("in_transactor_names", aTransactorLedger.getTransactorNames());
            cs.setLong("in_bill_transactor_id", aTransactorLedger.getBillTransactorId());
            cs.setString("in_bill_transactor_names", aTransactorLedger.getBillTransactorNames());
            cs.setString("in_ledger_entry_type", aTransactorLedger.getLedgerEntryType());
            cs.setFloat("in_amount_debit", aTransactorLedger.getAmountDebit());
            cs.setFloat("in_amount_credit", aTransactorLedger.getAmountCredit());
            if (aTransactorLedger.getTransactorLedgerId() > 0) {
                cs.setLong("in_transactor_ledger_id", aTransactorLedger.getTransactorLedgerId());
                cs.setTimestamp("in_add_date", new java.sql.Timestamp(aTransactorLedger.getAddDate().getTime()));
            }
            cs.executeUpdate();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
    }

    public TransactorLedger getTransactorLedger(long aTransactorLedgerId) {
        String sql = "{call sp_search_transactor_ledger_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactorLedgerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactorLedgerFromResultset(rs);
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

    public TransactorLedger getTransactorLedgerByTransIdTransType(long aTransactionId, String aTransactionTypeName) {
        String sql = "{call sp_search_transactor_ledger_by_transid_transtype(?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            ps.setString(2, aTransactionTypeName);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactorLedgerFromResultset(rs);
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

    public TransactorLedger getTransactorLedgerByMinTransIdTransTypeDesc(long aTransactionId, String aTransactionTypeName, String aDescription) {
        String sql = "{call sp_search_transactor_ledger_by_min_transid_type_desc(?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aTransactionId);
            ps.setString(2, aTransactionTypeName);
            ps.setString(3, aDescription);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactorLedgerFromResultset(rs);
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

    public TransactorLedger getTransactorLedgerFromResultset(ResultSet aResultSet) {
        try {
            TransactorLedger transactorLedger = new TransactorLedger();
            transactorLedger.setTransactorLedgerId(aResultSet.getLong("transactor_ledger_id"));
            try {
                transactorLedger.setStoreId(aResultSet.getInt("store_id"));
            } catch (NullPointerException npe) {
                transactorLedger.setStoreId(0);
            }
            try {
                transactorLedger.setStoreName(aResultSet.getString("store_name"));
            } catch (NullPointerException npe) {
                transactorLedger.setStoreName("");
            }
            try {
                transactorLedger.setTransactionId(aResultSet.getLong("transaction_id"));
            } catch (NullPointerException npe) {
                transactorLedger.setTransactionId(0);
            }
            try {
                transactorLedger.setPayId(aResultSet.getLong("pay_id"));
            } catch (NullPointerException npe) {
                transactorLedger.setPayId(0);
            }
            try {
                transactorLedger.setTransactionTypeName(aResultSet.getString("transaction_type_name"));
            } catch (NullPointerException npe) {
                transactorLedger.setTransactionTypeName("");
            }
            try {
                transactorLedger.setDescription(aResultSet.getString("description"));
            } catch (NullPointerException npe) {
                transactorLedger.setDescription("");
            }
            try {
                transactorLedger.setItemDescription(aResultSet.getString("item_description"));
            } catch (NullPointerException npe) {
                transactorLedger.setItemDescription("");
            }
            try {
                transactorLedger.setTransactionDate(new Date(aResultSet.getDate("transaction_date").getTime()));
            } catch (NullPointerException npe) {
                transactorLedger.setTransactionDate(null);
            }
            try {
                transactorLedger.setTransactorId(aResultSet.getLong("transactor_id"));
            } catch (NullPointerException npe) {
                transactorLedger.setTransactorId(0);
            }
            try {
                transactorLedger.setQuantity(aResultSet.getFloat("item_qty"));
            } catch (NullPointerException npe) {
                transactorLedger.setQuantity(0);
            }
            try {
                transactorLedger.setUnitPrice(aResultSet.getFloat("unit_price"));
            } catch (NullPointerException npe) {
                transactorLedger.setUnitPrice(0);
            }
            try {
                transactorLedger.setUnitTradeDiscount(aResultSet.getFloat("unit_trade_discount"));
            } catch (NullPointerException npe) {
                transactorLedger.setUnitTradeDiscount(0);
            }
            try {
                transactorLedger.setTransactorNames(aResultSet.getString("transactor_names"));
            } catch (NullPointerException npe) {
                transactorLedger.setTransactorNames("");
            }
            try {
                transactorLedger.setBillTransactorId(aResultSet.getLong("bill_transactor_id"));
            } catch (NullPointerException npe) {
                transactorLedger.setBillTransactorId(0);
            }
            try {
                transactorLedger.setBillTransactorNames(aResultSet.getString("bill_transactor_names"));
            } catch (NullPointerException npe) {
                transactorLedger.setBillTransactorNames("");
            }
            try {
                transactorLedger.setLedgerEntryType(aResultSet.getString("ledger_entry_type"));
            } catch (NullPointerException npe) {
                transactorLedger.setLedgerEntryType("");
            }
            try {
                transactorLedger.setAddDate(new Date(aResultSet.getTimestamp("add_date").getTime()));
            } catch (NullPointerException npe) {
                transactorLedger.setAddDate(null);
            }
            try {
                transactorLedger.setAmountDebit(aResultSet.getFloat("amount"));
            } catch (NullPointerException npe) {
                transactorLedger.setAmountDebit(0);
            }
            try {
                transactorLedger.setAmountCredit(aResultSet.getFloat("amount_credit"));
            } catch (NullPointerException npe) {
                transactorLedger.setAmountCredit(0);
            }
            try {
                transactorLedger.setPostedBy(aResultSet.getString("PostedBy"));
            } catch (NullPointerException npe) {
                transactorLedger.setPostedBy("");
            }
            try {
                transactorLedger.setCurrencyTypeName(aResultSet.getString("PostedBy"));
            } catch (NullPointerException npe) {
                transactorLedger.setPostedBy("");
            }
            try {
                transactorLedger.setCurrencyTypeName(aResultSet.getString("currency_type_name"));
            } catch (NullPointerException npe) {
                transactorLedger.setCurrencyTypeName("");
            }
            try {
                transactorLedger.setExchangeRate(aResultSet.getFloat("exchange_rate"));
            } catch (NullPointerException npe) {
                transactorLedger.setExchangeRate(0);
            }
            try {
                transactorLedger.setRemarks(aResultSet.getString("remarks"));
            } catch (NullPointerException npe) {
                transactorLedger.setRemarks("");
            }
            try {
                transactorLedger.setNumberOfDays(aResultSet.getInt("days"));
            } catch (NullPointerException npe) {
                transactorLedger.setNumberOfDays(0);
            }
            try {
                transactorLedger.setNumberOfPeople(aResultSet.getInt("number_of_persons"));
            } catch (NullPointerException npe) {
                transactorLedger.setNumberOfPeople(0);
            }
            return transactorLedger;

        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return null;
        }

    }

    public float get_ledger_balance(int transactor_ledger_id, int transactor_id, Date transaction_date1, Date transaction_date2, String CurrencyTypeName) {
        float balance = 0;
        String sql;
        sql = "{call sp_report_transactor_ledger_balance(?,?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            try {
                ps.setInt(1, transactor_id);
            } catch (NullPointerException npe) {
                ps.setInt(1, 0);
            }
            try {
                ps.setInt(2, transactor_ledger_id);
            } catch (NullPointerException npe) {
                ps.setInt(2, 0);
            }
            try {
                ps.setDate(3, new java.sql.Date(transaction_date1.getTime()));
            } catch (NullPointerException npe) {
                ps.setDate(3, null);
            }
            try {
                ps.setDate(4, new java.sql.Date(transaction_date2.getTime()));
            } catch (NullPointerException npe) {
                ps.setDate(4, null);
            }
            rs = ps.executeQuery();
            //System.out.println(rs.getStatement());
            while (rs.next()) {
                balance = rs.getFloat("balance");
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
        return balance;
    }

    public List<TransactorLedger> getReportTransactorLedger(TransactorLedger aTransactorLedger) {
        //Test
        String sql;
        sql = "{call sp_report_transactor_ledger_detail(?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransactorLedger.clear();
        if (aTransactorLedger != null) {
            if (aTransactorLedger.getTransactionDate() == null && aTransactorLedger.getTransactionDate2() == null
                    && aTransactorLedger.getAddDate() == null && aTransactorLedger.getAddDate2() == null) {
                this.ActionMessage = (("Atleast one date range(TransactionDate or AddDate) is needed..."));
            } else if (aTransactorLedger.getTransactionDate() == null && aTransactorLedger.getTransactionDate2() != null) {
                this.ActionMessage = (("Transaction Date(From) is needed..."));
            } else if (aTransactorLedger.getTransactionDate() != null && aTransactorLedger.getTransactionDate2() == null) {
                this.ActionMessage = (("Transaction Date(T0) is needed..."));
            } else if (aTransactorLedger.getAddDate() == null && aTransactorLedger.getAddDate2() != null) {
                this.ActionMessage = (("Add Date(From) is needed..."));
            } else if (aTransactorLedger.getAddDate() != null && aTransactorLedger.getAddDate2() == null) {
                this.ActionMessage = (("Add Date(To) is needed..."));
            } else {
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try {
                        ps.setInt(1, aTransactorLedger.getStoreId());
                    } catch (NullPointerException npe) {
                        ps.setInt(1, 0);
                    }
                    try {
                        ps.setDate(2, new java.sql.Date(aTransactorLedger.getTransactionDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(2, null);
                    }
                    try {
                        ps.setDate(3, new java.sql.Date(aTransactorLedger.getTransactionDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setDate(3, null);
                    }
                    try {
                        ps.setTimestamp(4, new java.sql.Timestamp(aTransactorLedger.getAddDate().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(4, null);
                    }
                    try {
                        ps.setTimestamp(5, new java.sql.Timestamp(aTransactorLedger.getAddDate2().getTime()));
                    } catch (NullPointerException npe) {
                        ps.setTimestamp(5, null);
                    }
                    try {
                        ps.setLong(6, aTransactorLedger.getBillTransactorId());
                    } catch (NullPointerException npe) {
                        ps.setLong(6, 0);
                    }
                    try {
                        ps.setString(7, aTransactorLedger.getTransactionTypeName());
                    } catch (NullPointerException npe) {
                        ps.setString(7, "");
                    }
                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        this.ReportTransactorLedger.add(this.getTransactorLedgerFromResultset(rs));
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
        return this.ReportTransactorLedger;
    }

    public List<TransactorLedger> getReportTransactorLedger_Guest_Folio(TransactorLedger aTransactorLedger) {
        //Test
        String sql;
        sql = "{call sp_report_transactor_ledger_detail_guest_folio(?,?,?,?,?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransactorLedger.clear();
        if (aTransactorLedger != null) {
//            if (aTransactorLedger.getTransactionDate() == null && aTransactorLedger.getTransactionDate2() == null
//                    && aTransactorLedger.getAddDate() == null && aTransactorLedger.getAddDate2() == null) {
//                this.ActionMessage = (("Atleast one date range(TransactionDate or AddDate) is needed..."));
//            } else if (aTransactorLedger.getTransactionDate() == null && aTransactorLedger.getTransactionDate2() != null) {
//                this.ActionMessage = (("Transaction Date(From) is needed..."));
//            } else if (aTransactorLedger.getTransactionDate() != null && aTransactorLedger.getTransactionDate2() == null) {
//                this.ActionMessage = (("Transaction Date(T0) is needed..."));
//            } else if (aTransactorLedger.getAddDate() == null && aTransactorLedger.getAddDate2() != null) {
//                this.ActionMessage = (("Add Date(From) is needed..."));
//            } else if (aTransactorLedger.getAddDate() != null && aTransactorLedger.getAddDate2() == null) {
//                this.ActionMessage = (("Add Date(To) is needed..."));
//            } else {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setInt(1, aTransactorLedger.getStoreId());
                } catch (NullPointerException npe) {
                    ps.setInt(1, 0);
                }
                try {
                    ps.setDate(2, new java.sql.Date(aTransactorLedger.getTransactionDate().getTime()));
                } catch (NullPointerException npe) {
                    ps.setDate(2, null);
                }
                try {
                    ps.setDate(3, new java.sql.Date(aTransactorLedger.getTransactionDate2().getTime()));
                } catch (NullPointerException npe) {
                    ps.setDate(3, null);
                }
                try {
                    ps.setTimestamp(4, new java.sql.Timestamp(aTransactorLedger.getAddDate().getTime()));
                } catch (NullPointerException npe) {
                    ps.setTimestamp(4, null);
                }
                try {
                    ps.setTimestamp(5, new java.sql.Timestamp(aTransactorLedger.getAddDate2().getTime()));
                } catch (NullPointerException npe) {
                    ps.setTimestamp(5, null);
                }
                try {
                    ps.setLong(6, aTransactorLedger.getBillTransactorId());
                } catch (NullPointerException npe) {
                    ps.setLong(6, 0);
                }
                try {
                    ps.setString(7, aTransactorLedger.getTransactionTypeName());
                } catch (NullPointerException npe) {
                    ps.setString(7, "");
                }
                try {
                    ps.setInt(8, aTransactorLedger.getGuestFolioId());
                } catch (NullPointerException npe) {
                    ps.setInt(8, 0);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                while (rs.next()) {
                    this.ReportTransactorLedger.add(this.getTransactorLedgerFromResultset(rs));
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
        //}
        return this.ReportTransactorLedger;
    }

    public long getReportTransactionCount() {
        return this.ReportTransactorLedger.size();
    }

    public List<TransactorLedger> getReportTransactorLedgerSummarySingleIndividual(long aBillTransactorId) {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_single_individual(?)}";
        ResultSet rs = null;
        this.ReportTransactorLedgerSummary.clear();
        if (aBillTransactorId != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aBillTransactorId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedger TmpTransactorLedger;
                while (rs.next()) {
                    TmpTransactorLedger = new TransactorLedger();
                    TmpTransactorLedger.setSumAmountDebit(rs.getFloat("sum_amount_debit"));
                    TmpTransactorLedger.setSumAmountCredit(rs.getFloat("sum_amount_credit"));
                    TmpTransactorLedger.setCurrencyTypeName(rs.getString("currency_type_name"));
                    if ((rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit")) < 0) {
                        TmpTransactorLedger.setNetDebt(0);
                    } else {
                        TmpTransactorLedger.setNetDebt(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                    }
                    if ((rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit")) < 0) {
                        TmpTransactorLedger.setNetCredit(0);
                    } else {
                        TmpTransactorLedger.setNetCredit(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                    }
                    this.ReportTransactorLedgerSummary.add(TmpTransactorLedger);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerSummary;
    }

    public List<TransactorLedger> getReportTransactorLedgerSummarySingleIndividual_Guest_Folio(long aBillTransactorId, long GuestFolioId) {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_single_individual_folio(?,?)}";
        ResultSet rs = null;
        this.ReportTransactorLedgerSummary.clear();
        if (aBillTransactorId != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aBillTransactorId);
                    ps.setLong(2, GuestFolioId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                    ps.setLong(2, 0);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedger TmpTransactorLedger;
                while (rs.next()) {
                    TmpTransactorLedger = new TransactorLedger();
                    TmpTransactorLedger.setSumAmountDebit(rs.getFloat("sum_amount_debit"));
                    TmpTransactorLedger.setSumAmountCredit(rs.getFloat("sum_amount_credit"));
                    TmpTransactorLedger.setCurrencyTypeName(rs.getString("currency_type_name"));
                    if ((rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit")) < 0) {
                        TmpTransactorLedger.setNetDebt(0);
                    } else {
                        TmpTransactorLedger.setNetDebt(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                    }
                    if ((rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit")) < 0) {
                        TmpTransactorLedger.setNetCredit(0);
                    } else {
                        TmpTransactorLedger.setNetCredit(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                    }
                    this.ReportTransactorLedgerSummary.add(TmpTransactorLedger);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerSummary;
    }

    public List<TransactorLedgerRoomDetails> getReportTransactorLedgerRoomDetails(TransactorLedger aTransactorLedger) {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_room_details(?,?,?)}";
        ResultSet rs = null;
        this.ReportTransactorLedgerRoomDetails.clear();
        if (aTransactorLedger != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aTransactorLedger.getBillTransactorId());
                    ps.setDate(2, new java.sql.Date(aTransactorLedger.getTransactionDate().getTime()));
                    ps.setDate(3, new java.sql.Date(aTransactorLedger.getTransactionDate2().getTime()));
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                    ps.setDate(2, null);
                    ps.setDate(3, null);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedgerRoomDetails TmpTransactorLedgerRoomDetails;
                while (rs.next()) {
                    TmpTransactorLedgerRoomDetails = new TransactorLedgerRoomDetails();
                    try {
                        TmpTransactorLedgerRoomDetails.setTransactorName(rs.getString("transactor_names"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setTransactorName("");
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setRoomNumber(rs.getString("room_number"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setRoomNumber("");
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setStartDate(rs.getDate("start_date"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setStartDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setEndDate(rs.getDate("end_date"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setEndDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setActualExitDate(new Date(rs.getTimestamp("actual_exit_date").getTime()));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setActualExitDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setCheckedOutBy(rs.getString("checked_out_by"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setCheckedOutBy("");
                    }
                    this.ReportTransactorLedgerRoomDetails.add(TmpTransactorLedgerRoomDetails);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerRoomDetails;
    }

    public List<TransactorLedgerRoomDetails> getReportTransactorLedgerRoomDetails_Guest_Folio(TransactorLedger aTransactorLedger, long GuestFolioId) {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_room_details_guest_folio(?,?,?,?)}";
        ResultSet rs = null;
        this.ReportTransactorLedgerRoomDetails.clear();
        if (aTransactorLedger != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aTransactorLedger.getBillTransactorId());
                    ps.setDate(2, null);
                    ps.setDate(3, null);
                    ps.setLong(4, GuestFolioId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                    ps.setDate(2, null);
                    ps.setDate(3, null);
                    ps.setLong(4, 0);
                }
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedgerRoomDetails TmpTransactorLedgerRoomDetails;
                while (rs.next()) {
                    TmpTransactorLedgerRoomDetails = new TransactorLedgerRoomDetails();
                    try {
                        TmpTransactorLedgerRoomDetails.setTransactorName(rs.getString("transactor_names"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setTransactorName("");
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setRoomNumber(rs.getString("room_number"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setRoomNumber("");
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setStartDate(rs.getDate("start_date"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setStartDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setEndDate(rs.getDate("end_date"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setEndDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setActualExitDate(new Date(rs.getTimestamp("actual_exit_date").getTime()));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setActualExitDate(null);
                    }
                    try {
                        TmpTransactorLedgerRoomDetails.setCheckedOutBy(rs.getString("checked_out_by"));
                    } catch (NullPointerException ex) {
                        TmpTransactorLedgerRoomDetails.setCheckedOutBy("");
                    }
                    this.ReportTransactorLedgerRoomDetails.add(TmpTransactorLedgerRoomDetails);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerRoomDetails;
    }

    public List<TransactorLedgerRoomDetails> get_report_transactor_ledger_room_details_guest_folio_recent_check_outs() {
        String sql;
        sql = "SELECT t.transactor_names, r.room_number,ro.start_date,ro.end_date,ro.check_out_date AS actual_exit_date,CONCAT(ud.second_name,' ',ud.first_name) AS checked_out_by FROM room_occupancy ro \n"
                + "INNER JOIN transactor t on t.transactor_id=ro.transactor_id\n"
                + " LEFT JOIN user_detail ud on ro.check_out_user_detail_id=ud.user_detail_id\n"
                + "LEFT JOIN room r on r.room_id=ro.room_id \n"
                + "INNER JOIN `transaction` tr on tr.transaction_id=ro.transaction_id\n"
                + "where DATEDIFF(now(), actual_exit_date)<=3\n"
                + " order by ro.start_date desc;";
        ResultSet rs = null;
        this.ReportTransactorLedgerRoomDetails.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            //System.out.println(rs.getStatement());
            TransactorLedgerRoomDetails TmpTransactorLedgerRoomDetails;
            while (rs.next()) {
                TmpTransactorLedgerRoomDetails = new TransactorLedgerRoomDetails();
                try {
                    TmpTransactorLedgerRoomDetails.setTransactorName(rs.getString("transactor_names"));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setTransactorName("");
                }
                try {
                    TmpTransactorLedgerRoomDetails.setRoomNumber(rs.getString("room_number"));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setRoomNumber("");
                }
                try {
                    TmpTransactorLedgerRoomDetails.setStartDate(rs.getDate("start_date"));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setStartDate(null);
                }
                try {
                    TmpTransactorLedgerRoomDetails.setEndDate(rs.getDate("end_date"));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setEndDate(null);
                }
                try {
                    TmpTransactorLedgerRoomDetails.setActualExitDate(new Date(rs.getTimestamp("actual_exit_date").getTime()));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setActualExitDate(null);
                }
                try {
                    TmpTransactorLedgerRoomDetails.setCheckedOutBy(rs.getString("checked_out_by"));
                } catch (NullPointerException ex) {
                    TmpTransactorLedgerRoomDetails.setCheckedOutBy("");
                }
                this.ReportTransactorLedgerRoomDetails.add(TmpTransactorLedgerRoomDetails);
            }
            //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerRoomDetails;
    }

    public TransactorLedger getCurrentTransactorLedgerByIndividual(long aBillTransactorId) {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_single_individual(?)}";
        ResultSet rs = null;
        TransactorLedger tl = new TransactorLedger();
        //initialise
        tl.setSumAmountDebit(0);
        tl.setSumAmountCredit(0);
        tl.setNetDebt(0);
        tl.setNetDebt(0);
        //for output only
        tl.setNetDebtCreditAmount(0);
        tl.setNetDebtCreditLabel("BAL");

        if (aBillTransactorId == 0) {
            //do nothing
        } else {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try {
                    ps.setLong(1, aBillTransactorId);
                } catch (NullPointerException npe) {
                    ps.setLong(1, 0);
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    tl.setSumAmountDebit(rs.getFloat("sum_amount_debit"));
                    tl.setSumAmountCredit(rs.getFloat("sum_amount_credit"));
                    if (rs.getFloat("sum_amount_debit") > rs.getFloat("sum_amount_credit")) {
                        tl.setNetDebt(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                        //for output only
                        tl.setNetDebtCreditAmount(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                        tl.setNetDebtCreditLabel("DEBT");
                    } else if (rs.getFloat("sum_amount_credit") > rs.getFloat("sum_amount_debit")) {
                        tl.setNetCredit(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                        //output only
                        tl.setNetDebtCreditAmount(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                        tl.setNetDebtCreditLabel("CREDIT");
                    }
                }
            } catch (SQLException se) {
                tl.setSumAmountDebit(0);
                tl.setSumAmountCredit(0);
                tl.setNetDebt(0);
                tl.setNetDebt(0);
                //for output only
                tl.setNetDebtCreditAmount(0);
                tl.setNetDebtCreditLabel("BAL");
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
        return tl;
    }

    public List<TransactorLedger> getReportTransactorLedgerSummaryAllIndividual() {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_all_individual()}";
        ResultSet rs = null;
        this.ReportTransactorLedgerSummaryAllIndividual.clear();
        if (1 == 1) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedger TmpTransactorLedger;
                while (rs.next()) {
                    TmpTransactorLedger = new TransactorLedger();
                    TmpTransactorLedger.setBillTransactorNames(rs.getString("bill_transactor_names"));
                    TmpTransactorLedger.setSumAmountDebit(rs.getFloat("sum_amount_debit"));
                    TmpTransactorLedger.setSumAmountCredit(rs.getFloat("sum_amount_credit"));
                    if ((rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit")) < 0) {
                        TmpTransactorLedger.setNetDebt(0);
                    } else {
                        TmpTransactorLedger.setNetDebt(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                    }
                    if ((rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit")) < 0) {
                        TmpTransactorLedger.setNetCredit(0);
                    } else {
                        TmpTransactorLedger.setNetCredit(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                    }
                    this.ReportTransactorLedgerSummaryAllIndividual.add(TmpTransactorLedger);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerSummaryAllIndividual;
    }

    public List<TransactorLedger> getReportTransactorLedgerSummaryAll() {
        String sql;
        sql = "{call sp_report_transactor_ledger_summary_all()}";
        ResultSet rs = null;
        this.ReportTransactorLedgerSummaryAll.clear();
        if (1 == 1) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                rs = ps.executeQuery();
                //System.out.println(rs.getStatement());
                TransactorLedger TmpTransactorLedger;
                while (rs.next()) {
                    TmpTransactorLedger = new TransactorLedger();
                    TmpTransactorLedger.setSumAmountDebit(rs.getFloat("sum_amount_debit"));
                    TmpTransactorLedger.setSumAmountCredit(rs.getFloat("sum_amount_credit"));
                    if ((rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit")) < 0) {
                        TmpTransactorLedger.setNetDebt(0);
                    } else {
                        TmpTransactorLedger.setNetDebt(rs.getFloat("sum_amount_debit") - rs.getFloat("sum_amount_credit"));
                    }
                    if ((rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit")) < 0) {
                        TmpTransactorLedger.setNetCredit(0);
                    } else {
                        TmpTransactorLedger.setNetCredit(rs.getFloat("sum_amount_credit") - rs.getFloat("sum_amount_debit"));
                    }
                    this.ReportTransactorLedgerSummaryAll.add(TmpTransactorLedger);
                }
                //this.ActionMessage=((""));
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
        return this.ReportTransactorLedgerSummaryAll;
    }

    public boolean IsAmountNegative(float aAmount) {
        if (aAmount >= 0) {
            return false;
        } else {
            return true;
        }
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
     * @return the CurrentTransactorLedger
     */
    public TransactorLedger getCurrentTransactorLedger() {
        return CurrentTransactorLedger;
    }

    /**
     * @param CurrentTransactorLedger the CurrentTransactorLedger to set
     */
    public void setCurrentTransactorLedger(TransactorLedger CurrentTransactorLedger) {
        this.CurrentTransactorLedger = CurrentTransactorLedger;
    }

}
