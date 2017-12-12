
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class CurrencyTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<CurrencyType> CurrencyTypes;
    private String ActionMessage;
    private CurrencyType SelectedCurrencyType = null;
    private int SelectedCurrencyTypeId;
    private String SearchCurrencyTypeName = "";
    private int TempId1;
    private String TempString1;
    private float TempFloat1;
    private int TempId2;
    private String TempString2;
    private float TempFloat2;

    public void saveCurrencyType(CurrencyType cur) {
        String sql = null;
        String msg = null;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (cur.getCurrencyTypeId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (cur.getCurrencyTypeId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (cur.getCurrencyTypeName().length() <= 0) {
            msg = "CurrencyType Name Needed...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (cur.getExchangeRate() == 0) {
            msg = "Exchange Rate Needed...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (cur.getCurrencyTypeId() == 0) {
                sql = "{call sp_insert_currency_type(?,?)}";
            } else if (cur.getCurrencyTypeId() > 0) {
                sql = "{call sp_update_currency_type(?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (cur.getCurrencyTypeId() == 0) {
                    cs.setString(1, cur.getCurrencyTypeName());
                    cs.setFloat(2, cur.getExchangeRate());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearCurrencyType(cur);
                } else if (cur.getCurrencyTypeId() > 0) {
                    cs.setInt(1, cur.getCurrencyTypeId());
                    cs.setString(2, cur.getCurrencyTypeName());
                    cs.setFloat(3, cur.getExchangeRate());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearCurrencyType(cur);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("CurrencyType NOT saved");
            }
        }

    }

    public void saveCurrencyType1() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_currency_type(?,?,?)}";

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            conn.setAutoCommit(false);
            cs.setInt(1, this.TempId1);
            cs.setString(2, this.TempString1);
            int x = cs.executeUpdate();
            System.out.println("X=" + x);
            //---
            cs.setInt(1, this.TempId2);
            cs.setString(2, this.TempString2);
            int y = cs.executeUpdate();
            System.out.println("Y=" + y);
            //---
            conn.commit();
            this.setActionMessage("Saved 1 Successfully");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("CurrencyType 1 NOT saved");
        }
    }

    public void saveCurrencyType2() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_currency_type(?,?,?)}";

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            conn.setAutoCommit(false);
            cs.setInt(1, this.TempId2);
            cs.setString(2, this.TempString2);
            cs.executeUpdate();
            conn.commit();
            this.setActionMessage("Saved 2 Successfully");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("CurrencyType 2 NOT saved");
        }
    }

    public CurrencyType getCurrencyType(int CurId) {
        String sql = "{call sp_search_currency_type_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, CurId);
            rs = ps.executeQuery();
            if (rs.next()) {
                CurrencyType cur = new CurrencyType();
                cur.setCurrencyTypeId(rs.getInt("currency_type_id"));
                cur.setCurrencyTypeName(rs.getString("currency_type_name"));
                cur.setExchangeRate(rs.getFloat("exchange_rate"));
                return cur;
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

    public void deleteCurrencyType() {
        this.deleteCurrencyTypeById(this.SelectedCurrencyTypeId);
    }

    public void deleteCurrencyTypeByObject(CurrencyType Cat) {
        this.deleteCurrencyTypeById(Cat.getCurrencyTypeId());
    }

    public void deleteCurrencyTypeById(int CurId) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM currency_type WHERE currency_type_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, CurId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("CurrencyType NOT deleted");
            }
        }
    }

    public void displayCurrencyType(CurrencyType CatFrom, CurrencyType CatTo) {
        CatTo.setCurrencyTypeId(CatFrom.getCurrencyTypeId());
        CatTo.setCurrencyTypeName(CatFrom.getCurrencyTypeName());
    }

    public void clearCurrencyType(CurrencyType Cat) {
        Cat.setCurrencyTypeId(0);
        Cat.setCurrencyTypeName("");
        Cat.setExchangeRate(0);
    }

    public List<CurrencyType> getCurrencyTypes() {
        String sql;
        sql = "{call sp_search_currency_type_by_none()}";
        ResultSet rs = null;
        CurrencyTypes = new ArrayList<CurrencyType>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                CurrencyType cur = new CurrencyType();
                cur.setCurrencyTypeId(rs.getInt("currency_type_id"));
                cur.setCurrencyTypeName(rs.getString("currency_type_name"));
                cur.setExchangeRate(rs.getFloat("exchange_rate"));
                CurrencyTypes.add(cur);
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
        return CurrencyTypes;
    }

    /**
     * @param aCurrencyTypeName
     * @return the CurrencyTypes
     */
    public List<CurrencyType> getCurrencyTypesByCurrencyTypeName(String aCurrencyTypeName) {
        String sql;
        sql = "{call sp_search_currency_type_by_name(?)}";
        ResultSet rs = null;
        CurrencyTypes = new ArrayList<CurrencyType>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aCurrencyTypeName);
            rs = ps.executeQuery();
            while (rs.next()) {
                CurrencyType cur = new CurrencyType();
                cur.setCurrencyTypeId(rs.getInt("currency_type_id"));
                cur.setCurrencyTypeName(rs.getString("currency_type_name"));
                cur.setExchangeRate(rs.getFloat("exchange_rate"));
                CurrencyTypes.add(cur);
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
        return CurrencyTypes;
    }

    /**
     * @param CurrencyTypes the CurrencyTypes to set
     */
    public void setCurrencyTypes(List<CurrencyType> CurrencyTypes) {
        this.CurrencyTypes = CurrencyTypes;
    }

    /**
     * @return the SelectedCurrencyType
     */
    public CurrencyType getSelectedCurrencyType() {
        return SelectedCurrencyType;
    }

    /**
     * @param SelectedCurrencyType the SelectedCurrencyType to set
     */
    public void setSelectedCurrencyType(CurrencyType SelectedCurrencyType) {
        this.SelectedCurrencyType = SelectedCurrencyType;
    }

    /**
     * @return the SelectedCurrencyTypeId
     */
    public int getSelectedCurrencyTypeId() {
        return SelectedCurrencyTypeId;
    }

    /**
     * @param SelectedCurrencyTypeId the SelectedCurrencyTypeId to set
     */
    public void setSelectedCurrencyTypeId(int SelectedCurrencyTypeId) {
        this.SelectedCurrencyTypeId = SelectedCurrencyTypeId;
    }

    /**
     * @return the SearchCurrencyTypeName
     */
    public String getSearchCurrencyTypeName() {
        return SearchCurrencyTypeName;
    }

    /**
     * @param SearchCurrencyTypeName the SearchCurrencyTypeName to set
     */
    public void setSearchCurrencyTypeName(String SearchCurrencyTypeName) {
        this.SearchCurrencyTypeName = SearchCurrencyTypeName;
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
     * @return the TempId1
     */
    public int getTempId1() {
        return TempId1;
    }

    /**
     * @param TempId1 the TempId1 to set
     */
    public void setTempId1(int TempId1) {
        this.TempId1 = TempId1;
    }

    /**
     * @return the TempString1
     */
    public String getTempString1() {
        return TempString1;
    }

    /**
     * @param TempString1 the TempString1 to set
     */
    public void setTempString1(String TempString1) {
        this.TempString1 = TempString1;
    }

    /**
     * @return the TempId2
     */
    public int getTempId2() {
        return TempId2;
    }

    /**
     * @param TempId2 the TempId2 to set
     */
    public void setTempId2(int TempId2) {
        this.TempId2 = TempId2;
    }

    /**
     * @return the TempString2
     */
    public String getTempString2() {
        return TempString2;
    }

    /**
     * @param TempString2 the TempString2 to set
     */
    public void setTempString2(String TempString2) {
        this.TempString2 = TempString2;
    }

    public float getTempFloat1() {
        return TempFloat1;
    }

    public void setTempFloat1(float TempFloat1) {
        this.TempFloat1 = TempFloat1;
    }

    public float getTempFloat2() {
        return TempFloat2;
    }

    public void setTempFloat2(float TempFloat2) {
        this.TempFloat2 = TempFloat2;
    }
    

}
