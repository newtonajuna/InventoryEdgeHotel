
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bajuna
 */
@ManagedBean
@SessionScoped
public class GuestFolioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<GuestFolio> GuestFolios;
    private String ActionMessage = null;
    private String SearchGuestFolioNames = "";
    private GuestFolio SelectedGuestFolio;
    private GuestFolio SelectedBillGuestFolio;
    private GuestFolio SelectedSchemeGuestFolio;
    private List<GuestFolio> GuestFolioObjectList;
    private List<GuestFolio> ReportGuestFolios = new ArrayList<>();

    List<GuestFolio> ReportGuestFolioSummary = new ArrayList<>();

    public List<GuestFolio> getReportGuestFolios() {
        return ReportGuestFolios;
    }

    public void setReportGuestFolios(List<GuestFolio> ReportGuestFolios) {
        this.ReportGuestFolios = ReportGuestFolios;
    }

    public List<GuestFolio> getReportGuestFolioSummary() {
        return ReportGuestFolioSummary;
    }

    public void setReportGuestFolioSummary(List<GuestFolio> ReportGuestFolioSummary) {
        this.ReportGuestFolioSummary = ReportGuestFolioSummary;
    }

    public GuestFolio findGuestFolio(Long GuestFolioId) {
        String sql = "{call sp_search_guest_folio_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, GuestFolioId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getGuestFolioFromResultSet(rs);
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

    public void saveGuestFolio(GuestFolio guest_folio) {
        String sql = null;
        String msg = "";
        String sql2 = null;
        if (guest_folio.getTransactor() != null) {
            sql2 = "SELECT * FROM guest_folio WHERE transactor_id=" + guest_folio.getTransactor().getTransactorId() + " and is_current='Y'";
        } else {
            sql2 = "SELECT * FROM guest_folio WHERE transactor_id=0 and is_current='Y'";
        }
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (guest_folio.getTransactor() == null) {
            msg = "GUEST/COMPANY NOT SELECTED";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (guest_folio.getStartDate() == null) {
            msg = "START DATE IS A REQUIRED FIELD";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (guest_folio.getStartDate() == null) {
            msg = "END DATE IS A REQUIRED FIELD";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && guest_folio.getGuestFolioId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "GUESTFOLIO", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && guest_folio.getGuestFolioId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "GUESTFOLIO", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if ((new CustomValidator().CheckRecords(sql2) > 0 && guest_folio.getGuestFolioId() == 0)) {
            msg = "An active Guest Folio for (" + guest_folio.getTransactor().getTransactorNames() + ") already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            if (guest_folio.getGuestFolioId() == 0) {
                sql = "{call sp_insert_guest_folio(?,?,?,?,?,?)}";
            } else if (guest_folio.getGuestFolioId() > 0) {
                sql = "{call sp_update_guest_folio(?,?,?,?,?,?,?,?,?)}";
            }
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (guest_folio.getGuestFolioId() == 0) {

                    //IN in_transactor_id bigint,IN in_start_date date,IN in_end_date date,IN in_add_user_detail_id int
                    cs.setLong("in_transactor_id", guest_folio.getTransactor().getTransactorId());
                    try {
                        cs.setDate("in_start_date", new java.sql.Date(guest_folio.getStartDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_start_date", null);
                    }
                    try {
                        cs.setDate("in_end_date", new java.sql.Date(guest_folio.getEndDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_end_date", null);
                    }
                    try {
                        cs.setInt("in_children", guest_folio.getChildren());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_children", null);
                    }
                    try {
                        cs.setInt("in_adults", guest_folio.getAdults());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_adults", null);
                    }

                    cs.setInt("in_add_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearGuestFolio(guest_folio);

                } else if (guest_folio.getGuestFolioId() > 0) {
                    //IN in_guest_folio_id bigint,IN in_is_current varchar(1),IN in_status varchar(20) ,IN in_transactor_id bigint,IN in_start_date date,IN in_end_date date,IN in_edit_user_detail_id int
                    cs.setLong("in_guest_folio_id", guest_folio.getGuestFolioId());
                    cs.setString("in_is_current", guest_folio.getIsCurrent());
                    cs.setString("in_status", guest_folio.getStatus());
                    cs.setLong("in_transactor_id", guest_folio.getTransactorId());
                    try {
                        cs.setDate("in_start_date", new java.sql.Date(guest_folio.getStartDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_start_date", null);
                    }
                    try {
                        cs.setDate("in_end_date", new java.sql.Date(guest_folio.getEndDate().getTime()));
                    } catch (NullPointerException npe) {
                        cs.setDate("in_end_date", null);
                    }

                    try {
                        cs.setInt("in_children", guest_folio.getChildren());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_children", null);
                    }
                    try {
                        cs.setInt("in_adults", guest_folio.getAdults());
                    } catch (NullPointerException npe) {
                        cs.setDate("in_adults", null);
                    }

                    cs.setInt("in_edit_user_detail_id", new GeneralUserSetting().getCurrentUser().getUserDetailId());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearGuestFolio(guest_folio);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("GuestFolio NOT saved");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("GuestFolio NOT saved!"));
            }
        }
    }

    public GuestFolio getGuestFolioFromResultSet(ResultSet rs) {
        GuestFolio guest_folio = new GuestFolio();
        try {
            guest_folio.setGuestFolioId(rs.getLong("guest_folio_id"));
        } catch (Exception ex) {
            guest_folio.setGuestFolioId(0);
        }
        try {
            guest_folio.setTransactorId(rs.getLong("transactor_id"));
        } catch (Exception ex) {
            guest_folio.setIsCurrent("");
        }
        try {
            guest_folio.setIsCurrent(rs.getString("is_current"));
        } catch (Exception ex) {
            guest_folio.setIsCurrent("");
        }
        try {
            guest_folio.setTransactorName(rs.getString("transactor_names"));
        } catch (Exception ex) {
            guest_folio.setTransactorName("");
        }
        try {
            guest_folio.setStatus(rs.getString("status"));
        } catch (Exception ex) {
            guest_folio.setStatus("");
        }
        try {
            guest_folio.setStartDate(rs.getDate("start_date"));
        } catch (Exception ex) {
            guest_folio.setStatus(null);
        }
        try {
            guest_folio.setEndDate(rs.getDate("end_date"));
        } catch (Exception ex) {
            guest_folio.setStatus(null);
        }
        try {
            guest_folio.setTotal_amount_credit(rs.getInt("amount_credit"));
        } catch (Exception ex) {
            guest_folio.setTotal_amount_credit(0);
        }
        try {
            guest_folio.setTotal_amount_debit(rs.getInt("amount_debit"));
        } catch (Exception ex) {
            guest_folio.setTotal_amount_debit(0);
        }
        try {
            guest_folio.setChildren(rs.getInt("children"));
        } catch (Exception ex) {
            guest_folio.setChildren(0);
        }
        try {
            guest_folio.setAdults(rs.getInt("adults"));
        } catch (Exception ex) {
            guest_folio.setAdults(0);
        }
        return guest_folio;
    }

    private GuestFolio guestFolio;

    public GuestFolio getGuestFolio() {
        return guestFolio;
    }

    public void setGuestFolio(GuestFolio guestFolio) {
        this.guestFolio = guestFolio;
    }

    public void set_guest_folio(long GuestFolioId) {
        this.guestFolio = getGuestFolio(GuestFolioId);
    }

    public void set_guest_folio_null() {
        GuestFolio gf = new GuestFolio();
        gf.setGuestFolioId(0);
        this.guestFolio = gf;
    }

    public GuestFolio getGuestFolio(long GuestFolioId) {
        String sql = "{call sp_search_guest_folio_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, GuestFolioId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return this.getGuestFolioFromResultSet(rs);
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

    public void deleteGuestFolio(GuestFolio guest_folio) {
        String sql = "DELETE FROM guest_folio WHERE guest_folio_id=?";
        String msg = "";
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if ("GUESTFOLIO".equals(new GeneralUserSetting().getCurrentTransactionTypeName()) && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "CUSTOMER", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, guest_folio.getGuestFolioId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                this.clearGuestFolio(guest_folio);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("GuestFolio NOT deleted");
            }
        }
    }

    public void displayGuestFolio(GuestFolio GuestFolioFrom, GuestFolio GuestFolioTo) {
        GuestFolioTo.setGuestFolioId(GuestFolioFrom.getGuestFolioId());
        GuestFolioTo.setTransactorId(GuestFolioFrom.getTransactorId());
        GuestFolioTo.setStartDate(GuestFolioFrom.getStartDate());
        GuestFolioTo.setEndDate(GuestFolioFrom.getEndDate());
        GuestFolioTo.setIsCurrent(GuestFolioFrom.getIsCurrent());
        GuestFolioTo.setStatus(GuestFolioFrom.getStatus());
        GuestFolioTo.setAdults(GuestFolioFrom.getAdults());
        GuestFolioTo.setChildren(GuestFolioFrom.getChildren());
        GuestFolioTo.setTransactor(getTransactor(GuestFolioFrom.getTransactorId()));
    }

    public void closeGuestFolio(GuestFolio GuestFolioFrom, GuestFolio GuestFolioTo) {
//        GuestFolioTo.setGuestFolioId(GuestFolioFrom.getGuestFolioId());
//        GuestFolioTo.setTransactorId(GuestFolioFrom.getTransactorId());
//        GuestFolioTo.setStartDate(GuestFolioFrom.getStartDate());
//        GuestFolioTo.setEndDate(GuestFolioFrom.getEndDate());
//        GuestFolioTo.setIsCurrent(GuestFolioFrom.getIsCurrent());
//        GuestFolioTo.setStatus(GuestFolioFrom.getStatus());

        String sql = "{call sp_update_close_guest_folio(?,?)}";

        try (Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setLong(1, GuestFolioFrom.getGuestFolioId());
            cs.setLong(2, new GeneralUserSetting().getCurrentUser().getUserDetailId());
            cs.executeUpdate();
        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }

    }

    public void clearGuestFolio(GuestFolio guest_folio) {
        if (guest_folio != null) {
            guest_folio.setGuestFolioId(0);
            guest_folio.setIsCurrent("");
            guest_folio.setStatus("");
            guest_folio.setStartDate(null);
            guest_folio.setEndDate(null);
            guest_folio.setChildren(null);
            guest_folio.setAdults(null);
            guest_folio.setTransactor(null);
        }
        //guest_folio = new GuestFolio();
    }

    public void initClearGuestFolio(GuestFolio guest_folio) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else if (guest_folio != null) {
            guest_folio.setGuestFolioId(0);
            guest_folio.setIsCurrent("");
            guest_folio.setStatus("");
            guest_folio.setEndDate(null);
            guest_folio.setEndDate(null);
        }
    }

    public void clearSelectedGuestFolio() {
        this.clearGuestFolio(this.getSelectedGuestFolio());
    }

    public void clearSelectedBillGuestFolio() {
        this.clearGuestFolio(this.getSelectedBillGuestFolio());
    }

    public List<GuestFolio> getGuestFolios() {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        GuestFolios = new ArrayList<GuestFolio>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, this.getSearchGuestFolioNames());
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
        return GuestFolios;
    }

    public List<GuestFolio> getGuestFoliosByTransactor_Amounts_History(String room_number) {
        String sql;
        sql = "{call sp_report_guest_folios_summary_by_room_number(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (room_number != null) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, room_number);
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
        return NewGuestFolios;
    }

    public List<GuestFolio> getGuestFoliosByTransactor_Amounts_History_Bill_Transactor(long bill_transactor_id) {
        String sql;
        sql = "{call sp_report_guest_folios_summary_by_transactor(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (bill_transactor_id != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, bill_transactor_id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
        return NewGuestFolios;
    }

    public List<GuestFolio> getGuestFoliosByTransactor_Amounts(long transactor, long billOther) {
        String sql;
        sql = "{call sp_report_current_guest_folio_summary_by_transactor(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (transactor != 0 || billOther != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                if (billOther != 0) {
                    ps.setLong(1, billOther);
                } else if (transactor != 0 && billOther == 0) {
                    ps.setLong(1, transactor);
                } else {
                    ps.setLong(1, 0);
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
        return NewGuestFolios;
    }

    public List<GuestFolio> getGuestFoliosByTransactor(long transactor, long billOther) {
        String sql;
        sql = "{call sp_search_guest_folio_by_transactor(?)}";
        ResultSet rs = null;
        List<GuestFolio> NewGuestFolios = new ArrayList<GuestFolio>();
        if (transactor != 0 || billOther != 0) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                if (billOther != 0) {
                    if (billOther != 0) {
                        ps.setLong(1, billOther);
                    } else if (transactor != 0) {
                        ps.setLong(1, transactor);
                    } else {
                        ps.setLong(1, 0);
                    }
                } else {
                    if (transactor != 0) {
                        ps.setLong(1, transactor);
                    } else {
                        ps.setLong(1, 0);
                    }
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    NewGuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
        return NewGuestFolios;
    }

    public void setGuestFolios(List<GuestFolio> GuestFolios) {
        this.GuestFolios = GuestFolios;
    }

    /**
     * @param Query
     * @return the GuestFolioStringList
     */
    public List<String> getGuestFolioStringList(String Query) {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        List<String> GuestFolioStringList = new ArrayList<String>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolioStringList.add(rs.getString("guest_folio_names"));
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
        return GuestFolioStringList;
    }

    /**
     * @param Query
     * @return the GuestFolioObjectList
     */
    public List<GuestFolio> getGuestFolioObjectList(String Query) {
        String sql;
        sql = "{call sp_search_guest_folio_by_name(?)}";
        ResultSet rs = null;
        GuestFolioObjectList = new ArrayList<GuestFolio>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                GuestFolioObjectList.add(this.getGuestFolioFromResultSet(rs));
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
        return GuestFolioObjectList;
    }

    public List<GuestFolio> getReportGuestFolios(GuestFolio aGuestFolio, boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_guest_folio(?)}";
        ResultSet rs = null;
        this.ReportGuestFolios.clear();
        if (aGuestFolio != null && RETRIEVE_REPORT == true) {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, aGuestFolio.getTransactorId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.ReportGuestFolios.add(this.getGuestFolioFromResultSet(rs));
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
            this.ReportGuestFolios.clear();
        }
        return this.ReportGuestFolios;
    }

    public long getReportGuestFoliosCount() {
        return this.ReportGuestFolios.size();
    }

    /**
     * @param GuestFolioObjectList the GuestFolioObjectList to set
     */
    public void setGuestFolioObjectList(List<GuestFolio> GuestFolioObjectList) {
        this.GuestFolioObjectList = GuestFolioObjectList;
    }

    /**
     * @return the SearchGuestFolioNames
     */
    public String getSearchGuestFolioNames() {
        return SearchGuestFolioNames;
    }

    /**
     * @param SearchGuestFolioNames the SearchGuestFolioNames to set
     */
    public void setSearchGuestFolioNames(String SearchGuestFolioNames) {
        this.SearchGuestFolioNames = SearchGuestFolioNames;
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
     * @return the SelectedGuestFolio
     */
    public GuestFolio getSelectedGuestFolio() {
        return SelectedGuestFolio;
    }

    /**
     * @param SelectedGuestFolio the SelectedGuestFolio to set
     */
    public void setSelectedGuestFolio(GuestFolio SelectedGuestFolio) {
        this.SelectedGuestFolio = SelectedGuestFolio;
    }

    /**
     * @return the SelectedBillGuestFolio
     */
    public GuestFolio getSelectedBillGuestFolio() {
        return SelectedBillGuestFolio;
    }

    /**
     * @param SelectedBillGuestFolio the SelectedBillGuestFolio to set
     */
    public void setSelectedBillGuestFolio(GuestFolio SelectedBillGuestFolio) {
        this.SelectedBillGuestFolio = SelectedBillGuestFolio;
    }

    /**
     * @return the SelectedSchemeGuestFolio
     */
    public GuestFolio getSelectedSchemeGuestFolio() {
        return SelectedSchemeGuestFolio;
    }

    /**
     * @param SelectedSchemeGuestFolio the SelectedSchemeGuestFolio to set
     */
    public void setSelectedSchemeGuestFolio(GuestFolio SelectedSchemeGuestFolio) {
        this.SelectedSchemeGuestFolio = SelectedSchemeGuestFolio;
    }

    public List<GuestFolio> getReportCurrentGuestFolioSummaryByTransactor(long aBillTransactorId) {
        String sql;
        sql = "{call sp_report_current_guest_folio_summary_by_transactor(?)}";
        ResultSet rs = null;
        this.ReportGuestFolioSummary.clear();
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
                GuestFolio TmpGuestFolio;
                while (rs.next()) {
                    TmpGuestFolio = new GuestFolio();
                    try {
                        TmpGuestFolio.setTotal_amount_credit(rs.getInt("amount_credit"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setTotal_amount_credit(0);
                    }
                    try {
                        TmpGuestFolio.setTotal_amount_debit(rs.getInt("amount_debit"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setTotal_amount_debit(0);
                    }
                    try {
                        TmpGuestFolio.setStartDate(rs.getDate("start_date"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setStartDate(null);
                    }
                    try {
                        TmpGuestFolio.setEndDate(rs.getDate("end_date"));
                    } catch (Exception ex) {
                        TmpGuestFolio.setEndDate(null);
                    }
                    this.ReportGuestFolioSummary.add(TmpGuestFolio);
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
        return this.ReportGuestFolioSummary;
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
}
