
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class UnitBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Unit> Units;
    private String ActionMessage=null;
    private Unit SelectedUnit=null;
    private int SelectedUnitId;
    private String SearchUnitName="";
    
    public void saveUnit(Unit unit) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

     if (unit.getUnitId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
     }else if (unit.getUnitId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
     }else if (unit.getUnitName().length()<=0 || unit.getUnitSymbol().length()<=0) {
            msg="Unit Name and Symbol Cannot be empty...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
     }else{
        if (unit.getUnitId() == 0) {
            sql = "{call sp_insert_unit(?,?)}";
        } else if (unit.getUnitId() > 0) {
            sql = "{call sp_update_unit(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (unit.getUnitId() == 0) {
                cs.setString(1, unit.getUnitName());
                cs.setString(2, unit.getUnitSymbol());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUnit(unit);
            } else if (unit.getUnitId() > 0) {
                cs.setInt(1, unit.getUnitId());
                cs.setString(2, unit.getUnitName());
                cs.setString(3, unit.getUnitSymbol());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearUnit(unit);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Unit NOT saved");
        }
     } 
        
    }
    
    public Unit getUnit(int aUnitId) {
        String sql = "{call sp_search_unit_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUnitId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Unit unit = new Unit();
                unit.setUnitId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setUnitSymbol(rs.getString("unit_symbol"));
                return unit;
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
    
    public void deleteUnit() {
         this.deleteUnitById(this.SelectedUnitId);
    }
    public void deleteUnitByObject(Unit unit) {
         this.deleteUnitById(unit.getUnitId());
    }

    public void deleteUnitById(int UnitId) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        String sql = "DELETE FROM unit WHERE unit_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, UnitId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Unit NOT deleted");
        }
    }
    }

    public void displayUnit(Unit UnitFrom, Unit UnitTo) {
        UnitTo.setUnitId(UnitFrom.getUnitId());
        UnitTo.setUnitName(UnitFrom.getUnitName());
        UnitTo.setUnitSymbol(UnitFrom.getUnitSymbol());
    }

    public void clearUnit(Unit unit) {
        unit.setUnitId(0);
        unit.setUnitName("");
        unit.setUnitSymbol("");
    }
    
    public List<Unit> getUnits() {
        String sql;
        sql="{call sp_search_unit_by_none()}";
        ResultSet rs = null;
        Units = new ArrayList<Unit>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setUnitId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setUnitSymbol(rs.getString("unit_symbol"));
                Units.add(unit);
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
        return Units;
    }

    /**
     * @param aUnitName
     * @return the Units
     */
    public List<Unit> getUnitsByUnitName(String aUnitName) {
        String sql;
        sql="{call sp_search_unit_by_name(?)}";
        ResultSet rs = null;
        Units = new ArrayList<Unit>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aUnitName);
            rs = ps.executeQuery();
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setUnitId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setUnitSymbol(rs.getString("unit_symbol"));
                Units.add(unit);
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
        return Units;
    }

    /**
     * @param Units the Units to set
     */
    public void setUnits(List<Unit> Units) {
        this.Units = Units;
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
     * @return the SelectedUnit
     */
    public Unit getSelectedUnit() {
        return SelectedUnit;
    }

    /**
     * @param SelectedUnit the SelectedUnit to set
     */
    public void setSelectedUnit(Unit SelectedUnit) {
        this.SelectedUnit = SelectedUnit;
    }

    /**
     * @return the SelectedUnitId
     */
    public int getSelectedUnitId() {
        return SelectedUnitId;
    }

    /**
     * @param SelectedUnitId the SelectedUnitId to set
     */
    public void setSelectedUnitId(int SelectedUnitId) {
        this.SelectedUnitId = SelectedUnitId;
    }

    /**
     * @return the SearchUnitName
     */
    public String getSearchUnitName() {
        return SearchUnitName;
    }

    /**
     * @param SearchUnitName the SearchUnitName to set
     */
    public void setSearchUnitName(String SearchUnitName) {
        this.SearchUnitName = SearchUnitName;
    }
    
}
