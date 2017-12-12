
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
public class LocationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Location> Locations;
    private String ActionMessage=null;
    private Location SelectedLocation=null;
    private int SelectedLocationId;
    private String SearchLocationName="";
    
    public void saveLocation(Location loc) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

      if (loc.getLocationId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (loc.getLocationId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (loc.getStoreId()==0) {
            msg="Select Store for this location...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if ("".equals(loc.getLocationName()) || loc.getLocationName().length()>20) {
            msg="Please enter a location name, it must not exceed 20characters as well...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else{
        if (loc.getLocationId() == 0) {
            sql = "{call sp_insert_location(?,?)}";
        } else if (loc.getLocationId() > 0) {
            sql = "{call sp_update_location(?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (loc.getLocationId() == 0) {
                cs.setInt("in_store_id", loc.getStoreId());
                cs.setString("in_location_name", loc.getLocationName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            } else if (loc.getLocationId() > 0) {
                cs.setLong("in_location_id", loc.getLocationId());
                cs.setInt("in_store_id", loc.getStoreId());
                cs.setString("in_location_name", loc.getLocationName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Location NOT saved");
        }
     }   
        
    }
    
    public Location getLocation(long LocId) {
        String sql = "SELECT * FROM location WHERE location_id=?";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, LocId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Location loc = new Location();
                loc.setLocationId(rs.getLong("location_id"));
                loc.setStoreId(rs.getInt("store_id"));
                loc.setLocationName(rs.getString("location_name"));
                return loc;
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
    
    public void deleteLocation() {
         this.deleteLocationById(this.SelectedLocationId);
    }
    
    public void deleteLocationByObject(Location Loc) {
         this.deleteLocationById(Loc.getLocationId());
    }

    public void deleteLocationById(long LocId) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        String sql = "DELETE FROM location WHERE location_id=?";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, LocId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("NOT deleted");
        }
    }
    }
    
    public void displayLocation(Location LocFrom, Location LocTo) {
        LocTo.setLocationId(LocFrom.getLocationId());
        LocTo.setStoreId(LocFrom.getStoreId());
        LocTo.setLocationName(LocFrom.getLocationName());
    }

    public void clearLocation(Location Loc) {
        Loc.setLocationId(0);
        Loc.setStoreId(0);
        Loc.setLocationName("");
    }

    public List<Location> getLocations() {
        String sql;

        sql="{call sp_search_location(?,?,?)}";
        ResultSet rs = null;
        Locations = new ArrayList<Location>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1,0);
            ps.setString(2,this.SearchLocationName);
            ps.setInt(3, 0);

            rs = ps.executeQuery();
            while (rs.next()) {
                Location loc = new Location();
                loc.setLocationId(rs.getInt("location_id"));
                loc.setStoreId(rs.getInt("store_id"));
                loc.setLocationName(rs.getString("location_name"));
                Locations.add(loc);
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
        return Locations;
    }
    
    public List<Location> getLocations(int aStoreId) {
        String sql;
        
        sql="{call sp_search_location(?,?,?)}";
        ResultSet rs = null;
        Locations = new ArrayList<Location>();
        Locations.clear();
        if(aStoreId==0){
            //do nothing
        }else{
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1,aStoreId);
                ps.setString(2,"");
                ps.setInt(3, 0);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Location loc = new Location();
                    loc.setLocationId(rs.getInt("location_id"));
                    loc.setStoreId(rs.getInt("store_id"));
                    loc.setLocationName(rs.getString("location_name"));
                    Locations.add(loc);
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
        return Locations;
    }
    
    public List<Location> getLocationsByItem(long aItemId) {
        String sql;
        
        sql="{call sp_search_location_by_item_id(?)}";
        ResultSet rs = null;
        Locations = new ArrayList<Location>();
        Locations.clear();
        if(aItemId==0){
            //do nothing
        }else{
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1,aItemId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Location loc = new Location();
                    loc.setLocationId(rs.getInt("location_id"));
                    loc.setStoreId(rs.getInt("store_id"));
                    loc.setLocationName(rs.getString("location_name"));
                    Locations.add(loc);
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
        return Locations;
    }
        
    /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param aActionMessage the ActionMessage to set
     */
    public void setActionMessage(String aActionMessage) {
        this.ActionMessage = aActionMessage;
    }
    
    /**
     * @return the Locations
     */

    /**
     * @param Locations the Locations to set
     */
    public void setLocations(List<Location> Locations) {
        this.Locations = Locations;
    }

    /**
     * @return the SelectedLocation
     */
    public Location getSelectedLocation() {
        return SelectedLocation;
    }

    /**
     * @param SelectedLocation the SelectedLocation to set
     */
    public void setSelectedLocation(Location SelectedLocation) {
        this.SelectedLocation = SelectedLocation;
    }

    /**
     * @return the SelectedLocationId
     */
    public int getSelectedLocationId() {
        return SelectedLocationId;
    }

    /**
     * @param SelectedLocationId the SelectedLocationId to set
     */
    public void setSelectedLocationId(int SelectedLocationId) {
        this.SelectedLocationId = SelectedLocationId;
    }

    /**
     * @return the SearchLocationName
     */
    public String getSearchLocationName() {
        return SearchLocationName;
    }

    /**
     * @param SearchLocationName the SearchLocationName to set
     */
    public void setSearchLocationName(String SearchLocationName) {
        this.SearchLocationName = SearchLocationName;
    }
}
