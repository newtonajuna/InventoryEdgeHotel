
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

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
public class TransactorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Transactor> Transactors;
    private String ActionMessage = null;
    private String SearchTransactorNames = "";
    private Transactor SelectedTransactor;
    private Transactor SelectedBillTransactor;
    private Transactor SelectedSchemeTransactor;
    private List<Transactor> TransactorObjectList;
    private List<Transactor> ReportTransactors = new ArrayList<Transactor>();

    public Transactor findTransactor(Long TransactorId) {
        String sql = "{call sp_search_transactor_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransactorId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactorFromResultSet(rs);
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

    public void saveTransactor(Transactor transactor) {
        String sql = null;
        String msg = "";
        String sql2 = null;
        String sql3 = null;
        sql2 = "SELECT * FROM transactor WHERE transactor_names='" + transactor.getTransactorNames() + "'";
        sql3 = "SELECT * FROM transactor WHERE transactor_names='" + transactor.getTransactorNames() + "' AND transactor_id!=" + transactor.getTransactorId();
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if ("TRANSACTOR".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && transactor.getTransactorId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "TRANSACTOR", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("TRANSACTOR".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && transactor.getTransactorId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "TRANSACTOR", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getTransactorType(), 1, 20).equals("FAIL")) {
            msg = "Transactor Type MUST be specified and cannot exceed 20 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getCategory(), 1, 20).equals("FAIL")) {
            msg = "Transactor Category MUST be specified and cannot exceed 20 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getTransactorNames(), 3, 100).equals("FAIL")) {
            msg = "Names MUST be between 3-to-100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getPhone(), 0, 100).equals("FAIL")) {
            msg = "Transactor's Phone cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getEmail(), 0, 100).equals("FAIL")) {
            msg = "Transactor's email cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getWebsite(), 0, 100).equals("FAIL")) {
            msg = "Transactor's website cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getCpName(), 0, 100).equals("FAIL")) {
            msg = "Contact person's name cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getCpTitle(), 0, 100).equals("FAIL")) {
            msg = "Contact person's title cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getCpPhone(), 0, 100).equals("FAIL")) {
            msg = "Contact person's phone cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getCpEmail(), 0, 100).equals("FAIL")) {
            msg = "Contact person's email cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getPhysicalAddress(), 0, 255).equals("FAIL")) {
            msg = "Physical address cannot exceed 255 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getTaxIdentity(), 0, 100).equals("FAIL")) {
            msg = "Tax Identity cannot exceed 100 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (new CustomValidator().TextSize(transactor.getAccountDetails(), 0, 255).equals("FAIL")) {
            msg = "Account details cannot exceed 255 characters";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ((new CustomValidator().CheckRecords(sql2) > 0 && transactor.getTransactorId() == 0) || (new CustomValidator().CheckRecords(sql3) > 0 && transactor.getTransactorId() > 0)) {
            msg = "Transactor Name(s) already exists, please enter different name(s) !";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            if (transactor.getTransactorId() == 0) {
                sql = "{call sp_insert_transactor(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            } else if (transactor.getTransactorId() > 0) {
                sql = "{call sp_update_transactor(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            }
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (transactor.getTransactorId() == 0) {
                    cs.setString("in_transactor_type", transactor.getTransactorType());
                    cs.setString("in_transactor_names", transactor.getTransactorNames());
                    cs.setString("in_phone", transactor.getPhone());
                    cs.setString("in_email", transactor.getEmail());
                    cs.setString("in_website", transactor.getWebsite());
                    cs.setString("in_cpname", transactor.getCpName());
                    cs.setString("in_cptitle", transactor.getCpTitle());
                    cs.setString("in_cpphone", transactor.getCpPhone());
                    cs.setString("in_cpemail", transactor.getCpEmail());
                    cs.setString("in_physical_address", transactor.getPhysicalAddress());
                    cs.setString("in_tax_identity", transactor.getTaxIdentity());
                    cs.setString("in_account_details", transactor.getAccountDetails());
                    cs.setString("in_card_number", transactor.getCardNumber());
                    try {
                        cs.setDate("in_dob", new java.sql.Date(transactor.getDOB().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_dob", null);
                    }
                    cs.setString("in_is_suspended", transactor.getIsSuspended());
                    cs.setString("in_suspended_reason", transactor.getSuspendedReason());
                    cs.setString("in_category", transactor.getCategory());
                    cs.setString("in_sex", transactor.getSex());
                    cs.setString("in_occupation", transactor.getOccupation());
                    cs.setString("in_loc_country", transactor.getLocCountry());
                    cs.setString("in_loc_district", transactor.getLocDistrict());
                    cs.setString("in_loc_town", transactor.getLocTown());
                    try {
                        cs.setDate("in_first_date", new java.sql.Date(transactor.getFirstDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_first_date", null);
                    }
                    cs.setString("in_file_reference", transactor.getFileReference());
                    cs.setString("in_id_type", transactor.getIdType());
                    cs.setString("in_id_number", transactor.getIdNumber());
                    try {
                        cs.setDate("in_id_expiry_date", new java.sql.Date(transactor.getIdExpiryDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_id_expiry_date", null);
                    }
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearTransactor(transactor);

                } else if (transactor.getTransactorId() > 0) {
                    cs.setLong("in_transactor_id", transactor.getTransactorId());
                    cs.setString("in_transactor_type", transactor.getTransactorType());
                    cs.setString("in_transactor_names", transactor.getTransactorNames());
                    cs.setString("in_phone", transactor.getPhone());
                    cs.setString("in_email", transactor.getEmail());
                    cs.setString("in_website", transactor.getWebsite());
                    cs.setString("in_cpname", transactor.getCpName());
                    cs.setString("in_cptitle", transactor.getCpTitle());
                    cs.setString("in_cpphone", transactor.getCpPhone());
                    cs.setString("in_cpemail", transactor.getCpEmail());
                    cs.setString("in_physical_address", transactor.getPhysicalAddress());
                    cs.setString("in_tax_identity", transactor.getTaxIdentity());
                    cs.setString("in_account_details", transactor.getAccountDetails());
                    cs.setString("in_card_number", transactor.getCardNumber());
                    try {
                        cs.setDate("in_dob", new java.sql.Date(transactor.getDOB().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_dob", null);
                    }
                    cs.setString("in_is_suspended", transactor.getIsSuspended());
                    cs.setString("in_suspended_reason", transactor.getSuspendedReason());
                    cs.setString("in_category", transactor.getCategory());
                    cs.setString("in_sex", transactor.getSex());
                    cs.setString("in_occupation", transactor.getOccupation());
                    cs.setString("in_loc_country", transactor.getLocCountry());
                    cs.setString("in_loc_district", transactor.getLocDistrict());
                    cs.setString("in_loc_town", transactor.getLocTown());
                    try {
                        cs.setDate("in_first_date", new java.sql.Date(transactor.getFirstDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_first_date", null);
                    }
                    cs.setString("in_file_reference", transactor.getFileReference());
                    cs.setString("in_id_type", transactor.getIdType());
                    cs.setString("in_id_number", transactor.getIdNumber());
                    try {
                        cs.setDate("in_id_expiry_date", new java.sql.Date(transactor.getIdExpiryDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_id_expiry_date", null);
                    }
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearTransactor(transactor);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Transactor NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Transactor NOT saved!"));
            }
        }
    }

    public Transactor getTransactorFromResultSet(ResultSet rs) {
        try {
            Transactor transactor = new Transactor();
            try {
                transactor.setTransactorId(rs.getLong("transactor_id"));
            } catch (NullPointerException npe) {
                transactor.setTransactorId(0);
            }
            try {
                transactor.setTransactorType(rs.getString("transactor_type"));
            } catch (NullPointerException npe) {
                transactor.setTransactorType("");
            }
            try {
                transactor.setTransactorNames(rs.getString("transactor_names"));
            } catch (NullPointerException npe) {
                transactor.setTransactorNames("");
            }
            try {
                transactor.setPhone(rs.getString("phone"));
            } catch (NullPointerException npe) {
                transactor.setPhone("");
            }
            try {
                transactor.setEmail(rs.getString("email"));
            } catch (NullPointerException npe) {
                transactor.setEmail("");
            }
            try {
                transactor.setWebsite(rs.getString("website"));
            } catch (NullPointerException npe) {
                transactor.setWebsite("");
            }
            try {
                transactor.setCpName(rs.getString("cpname"));
            } catch (NullPointerException npe) {
                transactor.setCpName("");
            }
            try {
                transactor.setCpTitle(rs.getString("cptitle"));
            } catch (NullPointerException npe) {
                transactor.setCpTitle("");
            }
            try {
                transactor.setCpPhone(rs.getString("cpphone"));
            } catch (NullPointerException npe) {
                transactor.setCpPhone("");
            }
            try {
                transactor.setCpEmail(rs.getString("cpemail"));
            } catch (NullPointerException npe) {
                transactor.setCpEmail("");
            }
            try {
                transactor.setPhysicalAddress(rs.getString("physical_address"));
            } catch (NullPointerException npe) {
                transactor.setPhysicalAddress("");
            }
            try {
                transactor.setTaxIdentity(rs.getString("tax_identity"));
            } catch (NullPointerException npe) {
                transactor.setTaxIdentity("");
            }
            try {
                transactor.setAccountDetails(rs.getString("account_details"));
            } catch (NullPointerException npe) {
                transactor.setAccountDetails("");
            }
            try {
                transactor.setCardNumber(rs.getString("card_number"));
            } catch (NullPointerException npe) {
                transactor.setCardNumber("");
            }
            try {
                transactor.setDOB(new Date(rs.getDate("dob").getTime()));
            } catch (NullPointerException npe) {
                transactor.setDOB(null);
            }
            try {
                transactor.setIsSuspended(rs.getString("is_suspended"));
            } catch (NullPointerException npe) {
                transactor.setIsSuspended("");
            }
            try {
                transactor.setSuspendedReason(rs.getString("suspended_reason"));
            } catch (NullPointerException npe) {
                transactor.setSuspendedReason("");
            }
            try {
                transactor.setCategory(rs.getString("category"));
            } catch (NullPointerException npe) {
                transactor.setCategory("");
            }
            try {
                transactor.setSex(rs.getString("sex"));
            } catch (NullPointerException npe) {
                transactor.setSex("");
            }
            try {
                transactor.setOccupation(rs.getString("occupation"));
            } catch (NullPointerException npe) {
                transactor.setOccupation("");
            }
            try {
                transactor.setLocCountry(rs.getString("loc_country"));
            } catch (NullPointerException npe) {
                transactor.setLocCountry("");
            }
            try {
                transactor.setLocDistrict(rs.getString("loc_district"));
            } catch (NullPointerException npe) {
                transactor.setLocDistrict("");
            }
            try {
                transactor.setLocTown(rs.getString("loc_town"));
            } catch (NullPointerException npe) {
                transactor.setLocTown("");
            }
            try {
                transactor.setFirstDate(new Date(rs.getDate("first_date").getTime()));
            } catch (NullPointerException npe) {
                transactor.setFirstDate(null);
            }

            try {
                transactor.setFileReference(rs.getString("file_reference"));
            } catch (NullPointerException npe) {
                transactor.setFileReference("");
            }
            try {
                transactor.setIdType(rs.getString("id_type"));
            } catch (NullPointerException npe) {
                transactor.setIdType("");
            }
            try {
                transactor.setIdNumber(rs.getString("id_number"));
            } catch (NullPointerException npe) {
                transactor.setIdNumber("");
            }
            try {
                transactor.setIdExpiryDate(new Date(rs.getDate("id_expiry_date").getTime()));
            } catch (NullPointerException npe) {
                transactor.setIdExpiryDate(null);
            }
            return transactor;
        } catch (SQLException se) {
            return null;
        }
    }

    public Transactor getTransactor(long TransactorId) {
        String sql = "{call sp_search_transactor_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, TransactorId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getTransactorFromResultSet(rs);
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

    public void deleteTransactor(Transactor transactor) {
        String sql = "DELETE FROM transactor WHERE transactor_id=?";
        String msg = "";
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if ("TRANSACTOR".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && "CUSTOMER".equals(new GeneralUserSetting().getCurrentTransactorType()) && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "CUSTOMER", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("TRANSACTOR".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && "SUPPLIER".equals(new GeneralUserSetting().getCurrentTransactorType()) && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SUPPLIER", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, transactor.getTransactorId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearTransactor(transactor);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Transactor NOT deleted");
            }
        }
    }

    public void displayTransactor(Transactor TransactorFrom, Transactor TransactorTo) {
        TransactorTo.setTransactorId(TransactorFrom.getTransactorId());
        TransactorTo.setTransactorType(TransactorFrom.getTransactorType());
        TransactorTo.setTransactorNames(TransactorFrom.getTransactorNames());
        TransactorTo.setPhone(TransactorFrom.getPhone());
        TransactorTo.setEmail(TransactorFrom.getEmail());
        TransactorTo.setWebsite(TransactorFrom.getWebsite());
        TransactorTo.setCpName(TransactorFrom.getCpName());
        TransactorTo.setCpTitle(TransactorFrom.getCpTitle());
        TransactorTo.setCpEmail(TransactorFrom.getCpEmail());
        TransactorTo.setCpPhone(TransactorFrom.getCpPhone());
        TransactorTo.setPhysicalAddress(TransactorFrom.getPhysicalAddress());
        TransactorTo.setTaxIdentity(TransactorFrom.getTaxIdentity());
        TransactorTo.setAccountDetails(TransactorFrom.getAccountDetails());
        TransactorTo.setCardNumber(TransactorFrom.getCardNumber());
        TransactorTo.setDOB(TransactorFrom.getDOB());
        TransactorTo.setIsSuspended(TransactorFrom.getIsSuspended());
        TransactorTo.setSuspendedReason(TransactorFrom.getSuspendedReason());
        TransactorTo.setCategory(TransactorFrom.getCategory());
        TransactorTo.setSex(TransactorFrom.getSex());
        TransactorTo.setOccupation(TransactorFrom.getOccupation());
        TransactorTo.setLocCountry(TransactorFrom.getLocCountry());
        TransactorTo.setLocDistrict(TransactorFrom.getLocDistrict());
        TransactorTo.setLocTown(TransactorFrom.getLocTown());
        TransactorTo.setFirstDate(TransactorFrom.getFirstDate());
        TransactorTo.setFileReference(TransactorFrom.getFileReference());
        TransactorTo.setIdType(TransactorFrom.getIdType());
        TransactorTo.setIdNumber(TransactorFrom.getIdNumber());
        TransactorTo.setIdExpiryDate(TransactorFrom.getIdExpiryDate());
    }

    public void clearTransactor(Transactor transactor) {
        if (transactor != null) {
            transactor.setTransactorId(0);
            transactor.setTransactorType("");
            transactor.setTransactorNames("");
            transactor.setPhone("");
            transactor.setEmail("");
            transactor.setWebsite("");
            transactor.setCpName("");
            transactor.setCpTitle("");
            transactor.setCpEmail("");
            transactor.setCpPhone("");
            transactor.setPhysicalAddress("");
            transactor.setTaxIdentity("");
            transactor.setAccountDetails("");
            transactor.setCardNumber("");
            transactor.setDOB(null);
            transactor.setIsSuspended("");
            transactor.setSuspendedReason("");
            transactor.setCategory("");
            transactor.setSex("");
            transactor.setOccupation("");
            transactor.setLocCountry("");
            transactor.setLocDistrict("");
            transactor.setLocTown("");
            transactor.setFirstDate(null);
            transactor.setFileReference("");
            transactor.setIdType("");
            transactor.setIdNumber("");
            transactor.setIdExpiryDate(null);
        }
    }

    public void initClearTransactor(Transactor transactor) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else if (transactor != null) {
            transactor.setTransactorId(0);
            transactor.setTransactorType("");
            transactor.setTransactorNames("");
            transactor.setPhone("");
            transactor.setEmail("");
            transactor.setWebsite("");
            transactor.setCpName("");
            transactor.setCpTitle("");
            transactor.setCpEmail("");
            transactor.setCpPhone("");
            transactor.setPhysicalAddress("");
            transactor.setTaxIdentity("");
            transactor.setAccountDetails("");
            transactor.setCardNumber("");
            transactor.setDOB(null);
            transactor.setIsSuspended("");
            transactor.setSuspendedReason("");
            transactor.setCategory("");
            transactor.setSex("");
            transactor.setOccupation("");
            transactor.setLocCountry("");
            transactor.setLocDistrict("");
            transactor.setLocTown("");
            transactor.setFirstDate(null);
            transactor.setFileReference("");
            transactor.setIdType("");
            transactor.setIdNumber("");
            transactor.setIdExpiryDate(null);
        }
    }

    public void clearSelectedTransactor() {
        this.clearTransactor(this.getSelectedTransactor());
    }

    public void clearSelectedBillTransactor() {
        this.clearTransactor(this.getSelectedBillTransactor());
    }

    public List<Transactor> getTransactors() {
        String sql;
        sql = "{call sp_search_transactor_by_name(?)}";
        ResultSet rs = null;
        Transactors = new ArrayList<Transactor>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, this.getSearchTransactorNames());
            rs = ps.executeQuery();
            while (rs.next()) {
                Transactors.add(this.getTransactorFromResultSet(rs));
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
        return Transactors;
    }

    public List<Transactor> getTransactorsByNameType(String aName, String aType) {
        String sql;
        sql = "{call sp_search_transactor_by_name_type(?,?)}";
        ResultSet rs = null;
        List<Transactor> NewTransactors = new ArrayList<Transactor>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aName);
            ps.setString(2, aType);
            rs = ps.executeQuery();
            while (rs.next()) {
                NewTransactors.add(this.getTransactorFromResultSet(rs));
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
        return NewTransactors;
    }

    public void setTransactors(List<Transactor> Transactors) {
        this.Transactors = Transactors;
    }

    /**
     * @param Query
     * @return the TransactorStringList
     */
    public List<String> getTransactorStringList(String Query) {
        String sql;
        sql = "{call sp_search_transactor_by_name(?)}";
        ResultSet rs = null;
        List<String> TransactorStringList = new ArrayList<String>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactorStringList.add(rs.getString("transactor_names"));
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
        return TransactorStringList;
    }

    /**
     * @param Query
     * @return the TransactorObjectList
     */
    public List<Transactor> getTransactorObjectList(String Query) {
        String sql;
        sql = "{call sp_search_transactor_by_name(?)}";
        ResultSet rs = null;
        TransactorObjectList = new ArrayList<Transactor>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransactorObjectList.add(this.getTransactorFromResultSet(rs));
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
        return TransactorObjectList;
    }

    public List<Transactor> getReportTransactors(Transactor aTransactor, boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_transactor(?)}";
        ResultSet rs = null;
        this.ReportTransactors.clear();
        if (aTransactor != null && RETRIEVE_REPORT == true) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, aTransactor.getTransactorType());
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.ReportTransactors.add(this.getTransactorFromResultSet(rs));
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
            this.ReportTransactors.clear();
        }
        return this.ReportTransactors;
    }

    public long getReportTransactorsCount() {
        return this.ReportTransactors.size();
    }

    /**
     * @param TransactorObjectList the TransactorObjectList to set
     */
    public void setTransactorObjectList(List<Transactor> TransactorObjectList) {
        this.TransactorObjectList = TransactorObjectList;
    }

    /**
     * @return the SearchTransactorNames
     */
    public String getSearchTransactorNames() {
        return SearchTransactorNames;
    }

    /**
     * @param SearchTransactorNames the SearchTransactorNames to set
     */
    public void setSearchTransactorNames(String SearchTransactorNames) {
        this.SearchTransactorNames = SearchTransactorNames;
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
     * @return the SelectedSchemeTransactor
     */
    public Transactor getSelectedSchemeTransactor() {
        return SelectedSchemeTransactor;
    }

    /**
     * @param SelectedSchemeTransactor the SelectedSchemeTransactor to set
     */
    public void setSelectedSchemeTransactor(Transactor SelectedSchemeTransactor) {
        this.SelectedSchemeTransactor = SelectedSchemeTransactor;
    }

    /**
     * @return the GuestFolios
     */
    public List<GuestFolio> getGuestFolios() {
        String sql;
        sql = "{call sp_search_guest_folio_by_transaction_id(?)}";
        ResultSet rs = null;
        List<GuestFolio> guestFolios = new ArrayList<>();
        if (SelectedBillTransactor != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, SelectedBillTransactor.getTransactorId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    GuestFolio guestFolio = new GuestFolio();
                    guestFolio.setGuestFolioId(rs.getInt("guest_folio_id"));
                    guestFolio.setTransactorName(rs.getString("transactor_names"));
                    guestFolio.setStartDate(rs.getDate("start_date"));
                    guestFolio.setEndDate(rs.getDate("end_date"));
                    guestFolio.setStatus(rs.getString("status"));
                    guestFolio.setIsCurrent(rs.getString("is_current"));
                    guestFolios.add(guestFolio);
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
        return guestFolios;
    }

}
