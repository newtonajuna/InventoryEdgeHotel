
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
public class ItemLocationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ItemLocation> ItemLocations;
    private String ActionMessage=null;
    private ItemLocation SelectedItemLocation=null;
    private int SelectedItemLocationId;
    private String SearchItemLocationName="";
    private int SelectedItemId;
    private int SelectedLocationId;
    
    public void saveItemLocation(ItemLocation aItemLocation) {
        String sql = null;
        String sql2 = null;
        String msg=null;
        
        sql2="SELECT * FROM item_location WHERE item_id=" + aItemLocation.getItemId() + " AND location_id=" + aItemLocation.getLocationId();
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (aItemLocation.getItemLocationId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (aItemLocation.getItemLocationId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(aItemLocation.getItemId()==0 || aItemLocation.getLocationId()==0){
            this.setActionMessage("Select ITEM and LOCATION please...!");
        }else if(new CustomValidator().CheckRecords(sql2)>0){
            this.setActionMessage("ITEM already EXISTS at the LOCATION...!");
        }else if (aItemLocation.getItemLocationId() != 0) {
          //do nothing
      } else{
        sql = "{call sp_insert_item_location(?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setLong(1, aItemLocation.getItemId());
                cs.setLong(2, aItemLocation.getLocationId());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("NOT saved");
        }
      }
      
    }
    
    public ItemLocation getItemLocation(ItemLocation aItemLocation) {
        String sql = "{call sp_search_item_location(?,?,?,?,?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, aItemLocation.getItemLocationId());//item_loc_id
            ps.setInt(2, 0);//store_id
            ps.setLong(3, 0);//item_id
            ps.setLong(4, 0);//loc_id
            ps.setInt(5, 0);//limit
            rs = ps.executeQuery();
            if (rs.next()) {
                ItemLocation itemLocation = new ItemLocation();
                itemLocation.setItemLocationId(rs.getLong("item_location_id"));
                itemLocation.setItemId(rs.getLong("item_id"));
                itemLocation.setLocationId(rs.getLong("location_id"));
                //for report only
                itemLocation.setStoreName(rs.getString("store_name"));
                itemLocation.setDescription(rs.getString("description"));
                return itemLocation;
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
    
    public void deleteItemLocation() {
         this.deleteItemLocationById(this.SelectedItemLocationId);
    }
    
    public void deleteItemLocationByObject(ItemLocation aItemLocation) {
         this.deleteItemLocationById(aItemLocation.getItemLocationId());
    }

    public void deleteItemLocationById(long itemLocationId) {
        String sql = "DELETE FROM item_location WHERE item_location_id=?";
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, itemLocationId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("NOT deleted");
        }
        }
    }
    
    public void displayItemLocation(ItemLocation aItemLocationFrom, ItemLocation aItemLocationTo) {
        aItemLocationTo.setItemLocationId(aItemLocationFrom.getItemLocationId());
        aItemLocationTo.setItemId(aItemLocationFrom.getItemId());
        aItemLocationTo.setLocationId(aItemLocationFrom.getLocationId());
    }

    public void clearItemLocation(ItemLocation aItemLocation) {
        aItemLocation.setItemLocationId(0);
        aItemLocation.setItemId(0);
        //aItemLocation.setLocationId(0);
        this.SelectedLocationId=0;
    }

    public List<ItemLocation> getItemLocations(Location aLocation,ItemLocation aItemLocation) {
        String sql = "{call sp_search_item_location(?,?,?,?,?)}";
        ResultSet rs = null;
        ItemLocations = new ArrayList<ItemLocation>();
        ItemLocations.clear();
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setLong(1, aItemLocation.getItemLocationId());//item_loc_id
                    }catch(NullPointerException | SQLException nse){
                        ps.setLong(1, 0);//item_loc_id
                    }
                    try{
                        ps.setInt(2, aLocation.getStoreId());//store_id
                    }catch(NullPointerException | SQLException nse){
                        ps.setInt(2, 0);//store_id
                    }
                    try{
                        ps.setLong(3, aItemLocation.getItemId());//item_id
                    }catch(NullPointerException | SQLException nse){
                        ps.setLong(3, 0);//item_id
                    }
                    try{
                        ps.setLong(4, aItemLocation.getLocationId());//loc_id
                    }catch(NullPointerException | SQLException nse){
                        ps.setLong(4, 0);//loc_id
                    }
                    ps.setInt(5, 0);//limit
                    rs = ps.executeQuery();
                    //System.out.println(rs.getStatement());
                    while (rs.next()) {
                        ItemLocation itemLocation = new ItemLocation();
                        itemLocation.setItemLocationId(rs.getLong("item_location_id"));
                        itemLocation.setItemId(rs.getLong("item_id"));
                        itemLocation.setLocationId(rs.getLong("location_id"));
                        //for report only
                        itemLocation.setStoreName(rs.getString("store_name"));
                        itemLocation.setDescription(rs.getString("description"));
                        itemLocation.setLocationName(rs.getString("location_name"));
                        ItemLocations.add(itemLocation);
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
        return ItemLocations;
    }
    
    public List<ItemLocation> getReportItemLocations(int aStoreId,long aLocationId,Item aItem,boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_item_location(?,?,?)}";
        ResultSet rs = null;
        List<ItemLocation> aItemLocations=new ArrayList<ItemLocation>();
            if(RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    try{
                        ps.setInt(1, aStoreId);
                    }catch(NullPointerException npe){
                        ps.setInt(1, 0);
                    }
                    try{
                        ps.setLong(2, aLocationId);
                    }catch(NullPointerException npe){
                        ps.setLong(2, 0);
                    }
                    try{
                        ps.setLong(3, aItem.getItemId());
                    }catch(NullPointerException npe){
                        ps.setLong(3, 0);
                    }
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        ItemLocation itemLocation = new ItemLocation();
                        itemLocation.setItemLocationId(rs.getLong("item_location_id"));
                        itemLocation.setItemId(rs.getLong("item_id"));
                        itemLocation.setLocationId(rs.getLong("location_id"));
                        //for report only
                        itemLocation.setStoreName(rs.getString("store_name"));
                        itemLocation.setDescription(rs.getString("description"));
                        itemLocation.setLocationName(rs.getString("location_name"));
                        itemLocation.setUnitSymbol(rs.getString("unit_symbol"));
                        aItemLocations.add(itemLocation);
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
        return aItemLocations;
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
     * @return the ItemLocations
     */

    /**
     * @param ItemLocations the ItemLocations to set
     */
    public void setItemLocations(List<ItemLocation> ItemLocations) {
        this.ItemLocations = ItemLocations;
    }

    /**
     * @return the SelectedItemLocation
     */
    public ItemLocation getSelectedItemLocation() {
        return SelectedItemLocation;
    }

    /**
     * @param SelectedItemLocation the SelectedItemLocation to set
     */
    public void setSelectedItemLocation(ItemLocation SelectedItemLocation) {
        this.SelectedItemLocation = SelectedItemLocation;
    }

    /**
     * @return the SelectedItemLocationId
     */
    public int getSelectedItemLocationId() {
        return SelectedItemLocationId;
    }

    /**
     * @param SelectedItemLocationId the SelectedItemLocationId to set
     */
    public void setSelectedItemLocationId(int SelectedItemLocationId) {
        this.SelectedItemLocationId = SelectedItemLocationId;
    }

    /**
     * @return the SearchItemLocationName
     */
    public String getSearchItemLocationName() {
        return SearchItemLocationName;
    }

    /**
     * @param SearchItemLocationName the SearchItemLocationName to set
     */
    public void setSearchItemLocationName(String SearchItemLocationName) {
        this.SearchItemLocationName = SearchItemLocationName;
    }

    /**
     * @return the SelectedItemId
     */
    public int getSelectedItemId() {
        return SelectedItemId;
    }

    /**
     * @param SelectedItemId the SelectedItemId to set
     */
    public void setSelectedItemId(int SelectedItemId) {
        this.SelectedItemId = SelectedItemId;
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
}
