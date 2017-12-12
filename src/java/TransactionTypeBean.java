import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class TransactionTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<TransactionType> TransactionTypes;
    private String ActionMessage;
    private TransactionType SelectedTransactionType = null;
    private int SelectedTransactionTypeId;
    private String SearchTransactionTypeName = "";
    private List<TransactionType> TransactionTypesForEdit;
    private TransactionType TransactionTypeForEdit;

    public int transNeeds(int aTransId, String aNeeded) {
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
        int x = 0;
        switch (aNeeded) {
            case "total":
                //1,2,8,10,11
                if (aTransId == 1 || aTransId == 2 || aTransId == 8 || aTransId == 10 || aTransId == 11) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "payment":
                //2
                if (aTransId == 2) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "loyalty":
                //2
                if (aTransId == 2) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "delivery":
                //8,10,11,12
                if (aTransId == 12 || aTransId == 8 || aTransId == 10 || aTransId == 11) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "authorise":
                //4,13,8
                if (aTransId == 4 || aTransId == 13 || aTransId == 8) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "gen_terms":
                //1,10,11
                if (aTransId == 1 || aTransId == 10 || aTransId == 11) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "pay_terms":
                //1,10,11
                if (aTransId == 1 || aTransId == 10 || aTransId == 11) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
            case "cash_discount":
                //1,2,8,10,11
                if (aTransId == 1 || aTransId == 2 || aTransId == 8 || aTransId == 10 || aTransId == 11) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
        }
        return x;
    }

    public void updateTransactionTypes(List<TransactionType> aTransactionTypes) {
        String sql = null;
        String msg = null;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            List<TransactionType> att = aTransactionTypes;
            int ListItemIndex = 0;
            int ListItemNo = att.size();
            while (ListItemIndex < ListItemNo) {
                this.updateTransactionType(att.get(ListItemIndex));
                ListItemIndex = ListItemIndex + 1;
            }
        }
    }

    public void updateTransactionTypeFromResultset(TransactionType tt,ResultSet rs) {
        try {
            tt.setTransactionTypeId(rs.getInt("transaction_type_id"));
        } catch (Exception e) {
            tt.setTransactionTypeId(0);
        }
        try {
            tt.setTransactionTypeName(rs.getString("transaction_type_name"));
        } catch (Exception e) {
            tt.setTransactionTypeName("");
        }
        try {
            tt.setTransactorLabel(rs.getString("transactor_label"));
        } catch (Exception e) {
            tt.setTransactorLabel("");
        }
        try {
            tt.setTransactionNumberLabel(rs.getString("transaction_number_label"));
        } catch (Exception e) {
            tt.setTransactionNumberLabel("");
        }
        try {
            tt.setTransactionOutputLabel(rs.getString("transaction_output_label"));
        } catch (Exception e) {
            tt.setTransactionOutputLabel("");
        }
        try {
            tt.setBillTransactorLabel(rs.getString("bill_transactor_label"));
        } catch (Exception e) {
            tt.setBillTransactorLabel("");
        }
        try {
            tt.setTransactionRefLabel(rs.getString("transaction_ref_label"));
        } catch (Exception e) {
            tt.setTransactionRefLabel("");
        }
        try {
            tt.setTransactionDateLabel(rs.getString("transaction_date_label"));
        } catch (Exception e) {
            tt.setTransactionDateLabel("");
        }
        try {
            tt.setTransactionUserLabel(rs.getString("transaction_user_label"));
        } catch (Exception e) {
            tt.setTransactionUserLabel("");
        }
        try {
            tt.setIsTransactorMandatory(rs.getString("is_transactor_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactorMandatory("");
        }
        try {
            tt.setIsTransactionUserMandatory(rs.getString("is_transaction_user_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactionUserMandatory("");
        }
        try {
            tt.setIsTransactionRefMandatory(rs.getString("is_transaction_ref_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactionRefMandatory("");
        }
        try {
            tt.setIsAuthoriseUserMandatory(rs.getString("is_authorise_user_mandatory"));
        } catch (Exception e) {
            tt.setIsAuthoriseUserMandatory("");
        }
        try {
            tt.setIsAuthoriseDateMandatory(rs.getString("is_authorise_date_mandatory"));
        } catch (Exception e) {
            tt.setIsAuthoriseDateMandatory("");
        }
        try {
            tt.setIsDeliveryAddressMandatory(rs.getString("is_delivery_address_mandatory"));
        } catch (Exception e) {
            tt.setIsDeliveryAddressMandatory("");
        }
        try {
            tt.setIsDeliveryDateMandatory(rs.getString("is_delivery_date_mandatory"));
        } catch (Exception e) {
            tt.setIsDeliveryDateMandatory("");
        }
        try {
            tt.setIsPayDueDateMandatory(rs.getString("is_pay_due_date_mandatory"));
        } catch (Exception e) {
            tt.setIsPayDueDateMandatory("");
        }
        try {
            tt.setIsExpiryDateMandatory(rs.getString("is_expiry_date_mandatory"));
        } catch (Exception e) {
            tt.setIsExpiryDateMandatory("");
        }
    }
    
     public TransactionType getTransactionTypeFromResultset(ResultSet rs) {
        TransactionType tt = new TransactionType();
        try {
            tt.setTransactionTypeId(rs.getInt("transaction_type_id"));
        } catch (Exception e) {
            tt.setTransactionTypeId(0);
        }
        try {
            tt.setTransactionTypeName(rs.getString("transaction_type_name"));
        } catch (Exception e) {
            tt.setTransactionTypeName("");
        }
        try {
            tt.setTransactorLabel(rs.getString("transactor_label"));
        } catch (Exception e) {
            tt.setTransactorLabel("");
        }
        try {
            tt.setTransactionNumberLabel(rs.getString("transaction_number_label"));
        } catch (Exception e) {
            tt.setTransactionNumberLabel("");
        }
        try {
            tt.setTransactionOutputLabel(rs.getString("transaction_output_label"));
        } catch (Exception e) {
            tt.setTransactionOutputLabel("");
        }
        try {
            tt.setBillTransactorLabel(rs.getString("bill_transactor_label"));
        } catch (Exception e) {
            tt.setBillTransactorLabel("");
        }
        try {
            tt.setTransactionRefLabel(rs.getString("transaction_ref_label"));
        } catch (Exception e) {
            tt.setTransactionRefLabel("");
        }
        try {
            tt.setTransactionDateLabel(rs.getString("transaction_date_label"));
        } catch (Exception e) {
            tt.setTransactionDateLabel("");
        }
        try {
            tt.setTransactionUserLabel(rs.getString("transaction_user_label"));
        } catch (Exception e) {
            tt.setTransactionUserLabel("");
        }
        try {
            tt.setIsTransactorMandatory(rs.getString("is_transactor_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactorMandatory("");
        }
        try {
            tt.setIsTransactionUserMandatory(rs.getString("is_transaction_user_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactionUserMandatory("");
        }
        try {
            tt.setIsTransactionRefMandatory(rs.getString("is_transaction_ref_mandatory"));
        } catch (Exception e) {
            tt.setIsTransactionRefMandatory("");
        }
        try {
            tt.setIsAuthoriseUserMandatory(rs.getString("is_authorise_user_mandatory"));
        } catch (Exception e) {
            tt.setIsAuthoriseUserMandatory("");
        }
        try {
            tt.setIsAuthoriseDateMandatory(rs.getString("is_authorise_date_mandatory"));
        } catch (Exception e) {
            tt.setIsAuthoriseDateMandatory("");
        }
        try {
            tt.setIsDeliveryAddressMandatory(rs.getString("is_delivery_address_mandatory"));
        } catch (Exception e) {
            tt.setIsDeliveryAddressMandatory("");
        }
        try {
            tt.setIsDeliveryDateMandatory(rs.getString("is_delivery_date_mandatory"));
        } catch (Exception e) {
            tt.setIsDeliveryDateMandatory("");
        }
        try {
            tt.setIsPayDueDateMandatory(rs.getString("is_pay_due_date_mandatory"));
        } catch (Exception e) {
            tt.setIsPayDueDateMandatory("");
        }
        try {
            tt.setIsExpiryDateMandatory(rs.getString("is_expiry_date_mandatory"));
        } catch (Exception e) {
            tt.setIsExpiryDateMandatory("");
        }
        return tt;
    }

    public void updateTransactionType(TransactionType tt) {
        String sql = null;
        this.setActionMessage("");
        if (tt.getTransactionTypeId() > 0) {
            sql = "{call sp_update_transaction_type(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (tt.getTransactionTypeId() > 0) {
                    cs.setInt("in_transaction_type_id", tt.getTransactionTypeId());
                    cs.setString("in_transaction_type_name", tt.getTransactionTypeName());
                    cs.setString("in_transactor_label", tt.getTransactorLabel());
                    cs.setString("in_transaction_number_label", tt.getTransactionNumberLabel());
                    cs.setString("in_transaction_output_label", tt.getTransactionOutputLabel());
                    cs.setString("in_bill_transactor_label", tt.getBillTransactorLabel());
                    cs.setString("in_transaction_ref_label", tt.getTransactionRefLabel());
                    cs.setString("in_transaction_date_label", tt.getTransactionDateLabel());
                    cs.setString("in_transaction_user_label", tt.getTransactionUserLabel());
                    cs.setString("in_is_transactor_mandatory", tt.getIsTransactorMandatory());
                    cs.setString("in_is_transaction_user_mandatory", tt.getIsTransactionUserMandatory());
                    cs.setString("in_is_transaction_ref_mandatory", tt.getIsTransactionRefMandatory());
                    cs.setString("in_is_authorise_user_mandatory", tt.getIsAuthoriseUserMandatory());
                    cs.setString("in_is_authorise_date_mandatory", tt.getIsAuthoriseDateMandatory());
                    cs.setString("in_is_delivery_address_mandatory", tt.getIsDeliveryAddressMandatory());
                    cs.setString("in_is_delivery_date_mandatory", tt.getIsDeliveryDateMandatory());
                    cs.setString("in_is_pay_due_date_mandatory", tt.getIsPayDueDateMandatory());
                    cs.setString("in_is_expiry_date_mandatory", tt.getIsExpiryDateMandatory());
                    cs.executeUpdate();
                    this.setActionMessage("Transaction Settings Updated Successfully");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public TransactionType getTransactionType(int TtId) {
        String sql = "SELECT * FROM transaction_type WHERE transaction_type_id=?";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, TtId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactionTypeFromResultset(rs);
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

    public void setTransactionTypeGivenId(TransactionType aTransactionType, int TtId) {
        String sql = "SELECT * FROM transaction_type WHERE transaction_type_id=?";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, TtId);
            rs = ps.executeQuery();
            if (rs.next()) {
                this.updateTransactionTypeFromResultset(aTransactionType, rs);
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

    public void deleteTransactionType() {
        this.deleteTransactionTypeById(this.SelectedTransactionTypeId);
    }

    public void deleteTransactionTypeByObject(TransactionType tt) {
        this.deleteTransactionTypeById(tt.getTransactionTypeId());
    }

    public void deleteTransactionTypeById(int TtId) {
        String sql = "DELETE FROM transaction_type WHERE transaction_type_id=?";
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, TtId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("TransactionType NOT deleted");
            }
        }
    }

    public void displayTransactionType(TransactionType PmFrom, TransactionType PmTo) {
        PmTo.setTransactionTypeId(PmFrom.getTransactionTypeId());
        PmTo.setTransactionTypeName(PmFrom.getTransactionTypeName());
        PmTo.setTransactorLabel(PmFrom.getTransactorLabel());
        PmTo.setTransactionNumberLabel(PmFrom.getTransactionNumberLabel());
        PmTo.setTransactionOutputLabel(PmFrom.getTransactionOutputLabel());
    }

    public void initRefreshTransactionTypes() {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            refreshTransactionTypesForEdit();
        }
    }

    public void refreshTransactionTypesForEdit() {
        String sql;
        sql = "SELECT * FROM transaction_type ORDER BY transaction_type_name ASC";
        ResultSet rs = null;
        TransactionTypesForEdit = new ArrayList<TransactionType>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactionTypesForEdit.add(this.getTransactionTypeFromResultset(rs));
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

    public void clearTransactionType(TransactionType tt) {
        tt.setTransactionTypeId(0);
        tt.setTransactionTypeName("");
        tt.setTransactorLabel("");
        tt.setTransactionNumberLabel("");
        tt.setTransactionOutputLabel("");
        tt.setBillTransactorLabel("");
        tt.setTransactionRefLabel("");
        tt.setTransactionDateLabel("");
        tt.setTransactionUserLabel("");
        tt.setIsTransactorMandatory("");
        tt.setIsTransactionUserMandatory("");
        tt.setIsTransactionRefMandatory("");
    }

    /**
     * @return the TransactionTypes
     */
    public List<TransactionType> getTransactionTypes() {
        String sql;
        sql = "SELECT * FROM transaction_type ORDER BY transaction_type_name ASC";
        ResultSet rs = null;
        TransactionTypes = new ArrayList<TransactionType>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactionTypes.add(this.getTransactionTypeFromResultset(rs));
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
        return TransactionTypes;
    }

    public List<TransactionType> getTransTypesByTcTypeTransId(long aTransactionId, String aTransactorType) {
        String sql;
        int aTransactionTypeId = 0;
        List<TransactionType> tts;
        Trans t;

        if (aTransactionId > 0) {
            t = new TransBean().getTrans(aTransactionId);
            aTransactionTypeId = t.getTransactionTypeId();
        } else {
            if (aTransactorType.equals("CUSTOMER") || aTransactorType.equals("SCHEME")) {
                aTransactionTypeId = 2;//SALE
            } else if (aTransactorType.equals("SUPPLIER")) {
                aTransactionTypeId = 1;//SUPPLIER
            }
        }
        sql = "SELECT * FROM transaction_type WHERE transaction_type_id=" + aTransactionTypeId;
        ResultSet rs = null;
        tts = new ArrayList<TransactionType>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                tts.add(this.getTransactionTypeFromResultset(rs));
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
        return tts;
    }

    /**
     * @param TransactionTypes the TransactionTypes to set
     */
    public void setTransactionTypes(List<TransactionType> TransactionTypes) {
        this.TransactionTypes = TransactionTypes;
    }

    /**
     * @return the SelectedTransactionType
     */
    public TransactionType getSelectedTransactionType() {
        return SelectedTransactionType;
    }

    /**
     * @param SelectedTransactionType the SelectedTransactionType to set
     */
    public void setSelectedTransactionType(TransactionType SelectedTransactionType) {
        this.SelectedTransactionType = SelectedTransactionType;
    }

    /**
     * @return the SelectedTransactionTypeId
     */
    public int getSelectedTransactionTypeId() {
        return SelectedTransactionTypeId;
    }

    /**
     * @param SelectedTransactionTypeId the SelectedTransactionTypeId to set
     */
    public void setSelectedTransactionTypeId(int SelectedTransactionTypeId) {
        this.SelectedTransactionTypeId = SelectedTransactionTypeId;
    }

    /**
     * @return the SearchTransactionTypeName
     */
    public String getSearchTransactionTypeName() {
        return SearchTransactionTypeName;
    }

    /**
     * @param SearchTransactionTypeName the SearchTransactionTypeName to set
     */
    public void setSearchTransactionTypeName(String SearchTransactionTypeName) {
        this.SearchTransactionTypeName = SearchTransactionTypeName;
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
     * @return the TransactionTypesForEdit
     */
    public List<TransactionType> getTransactionTypesForEdit() {
        return TransactionTypesForEdit;
    }

    /**
     * @param TransactionTypesForEdit the TransactionTypesForEdit to set
     */
    public void setTransactionTypesForEdit(List<TransactionType> TransactionTypesForEdit) {
        this.TransactionTypesForEdit = TransactionTypesForEdit;
    }

    /**
     * @return the TransactionTypeForEdit
     */
    public TransactionType getTransactionTypeForEdit() {
        return TransactionTypeForEdit;
    }

    /**
     * @param TransactionTypeForEdit the TransactionTypeForEdit to set
     */
    public void setTransactionTypeForEdit(TransactionType TransactionTypeForEdit) {
        this.TransactionTypeForEdit = TransactionTypeForEdit;
    }

}
