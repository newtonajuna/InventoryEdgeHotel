import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;
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
public class ItemMapBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ItemMap> ItemMaps;
    private String ActionMessage = null;
    private ItemMap SelectedItemMap = null;
    private int SelectedItemMapId;
    private String SearchItemMap = "";
    private String ItemAddedStatus = "Item Added";
    private String ItemNotAddedStatus = "Item Not Added";
    private int ShowItemAddedStatus = 0;
    private int ShowItemNotAddedStatus = 0;
    private long SelectedMapGroupId;
    long NewId = 0;

    public void saveItemMap(ItemMap itemmap) {
        String sql = null;
        String sql2 = null;
        String msg = "";
        if (itemmap != null) {
            if (new ItemBean().getItem(itemmap.getBigItemId()).getItemType().equals("SERVICE") || new ItemBean().getItem(itemmap.getSmallItemId()).getItemType().equals("SERVICE")) {
                msg = "SERVICE Item cannot be Mapped...";
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
            } else {
                UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
                List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
                GroupRightBean grb = new GroupRightBean();

                if (itemmap.getItemMapId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
                    msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if (itemmap.getItemMapId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
                    msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if (itemmap.getBigItemId() == 0 || itemmap.getSmallItemId() == 0) {
                    msg = "Either BigItem or SmallItem is not selected";
                    this.setActionMessage("ItemMap not saved");
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if (itemmap.getBigItemId() == itemmap.getSmallItemId()) {
                    msg = "BigItem and SmallItem cannot be identical, you cannot map an item to itself!";
                    this.setActionMessage("ItemMap not saved");
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if (itemmap.getFractionQty() == 0) {
                    msg = "Specifiy how many times is the SmallItem to the BigItem";
                    this.setActionMessage("ItemMap not saved");
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if (itemmap.getPosition() == 0) {
                    msg = "Specifiy the position of this mapping in the group";
                    this.setActionMessage("ItemMap not saved");
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if ((this.itemCountInMap("BIG", itemmap.getBigItemId()) > 0 && itemmap.getItemMapId() == 0) || (this.itemCountInMap("BIG", itemmap.getBigItemId()) > 0 && this.itemCountInMap("BIG", itemmap.getBigItemId()) != 1 && itemmap.getItemMapId() > 0)) {
                    msg = "The selected BigItem already exists and cannot be a BigItem more than once!";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if ((this.itemCountInMap("SMALL", itemmap.getSmallItemId()) > 0 && itemmap.getItemMapId() == 0) || (this.itemCountInMap("SMALL", itemmap.getSmallItemId()) > 0 && this.itemCountInMap("SMALL", itemmap.getSmallItemId()) != 1 && itemmap.getItemMapId() > 0)) {
                    msg = "The selected SmallItem already exists and cannot be a SmallItem more than once!";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else if ((this.groupCountInMap(this.SelectedMapGroupId) >= 2 && itemmap.getItemMapId() == 0) || (this.groupCountInMap(this.SelectedMapGroupId) >= 2 && this.groupCountInMap(this.SelectedMapGroupId) != 2 && itemmap.getItemMapId() > 0)) {
                    msg = "You cannot map beyong 3 levels(Item1, Item2, Item3)";
                    FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
                } else {

                    if (itemmap.getItemMapId() == 0) {
                        sql = "{call sp_insert_item_map(?,?,?,?,?)}";
                    } else if (itemmap.getItemMapId() > 0) {
                        sql = "{call sp_update_item_map(?,?,?,?,?,?)}";
                    }
                    if (this.SelectedMapGroupId == 0) {
                        this.SelectedMapGroupId = this.getNewMapGroupId();
                    }
                    try (
                            Connection conn = DBConnection.getMySQLConnection();
                            CallableStatement cs = conn.prepareCall(sql);) {
                        if (itemmap.getItemMapId() == 0) {
                            cs.setLong("in_big_item_id", itemmap.getBigItemId());
                            cs.setLong("in_small_item_id", itemmap.getSmallItemId());
                            cs.setFloat("in_fraction_qty", itemmap.getFractionQty());
                            cs.setInt("in_position", itemmap.getPosition());
                            cs.setLong("in_map_group_id", this.SelectedMapGroupId);
                            cs.executeUpdate();
                            this.clearItemMap(itemmap);
                            this.setActionMessage("Saved Successfully");
                        } else if (itemmap.getItemMapId() > 0) {
                            cs.setLong("in_item_map_id", itemmap.getItemMapId());
                            cs.setLong("in_big_item_id", itemmap.getBigItemId());
                            cs.setLong("in_small_item_id", itemmap.getSmallItemId());
                            cs.setFloat("in_fraction_qty", itemmap.getFractionQty());
                            cs.setInt("in_position", itemmap.getPosition());
                            cs.setLong("in_map_group_id", this.SelectedMapGroupId);
                            cs.executeUpdate();
                            this.clearItemMap(itemmap);
                            this.setActionMessage("Saved Successfully");
                        }
                    } catch (SQLException se) {
                        System.err.println(se.getMessage());
                        this.setActionMessage("ItemMap NOT saved");
                        FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("ItemMap NOT saved!"));
                    }
                }
            }
        }
    }

    public ItemMap getItemMap(long ItemMapId) {
        //revise this fully
        String sql = "{call sp_search_item_map_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, ItemMapId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                return itemmap;
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

    public List<ItemMap> getItemMapsByBigItemId(long BigItemId) {
        String sql = "{call sp_search_item_map_by_big_item_id(?)}";
        ResultSet rs = null;
        List<ItemMap> ItemMaps2 = new ArrayList<ItemMap>();
        ItemMaps2.clear();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, BigItemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                ItemMaps2.add(itemmap);
            }
            return ItemMaps2;
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

    public ItemMap getItemMapByBigItemId(long BigItemId) {
        String sql = "{call sp_search_item_map_by_big_item_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, BigItemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                return itemmap;
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

    public ItemMap getItemMapBySmallItemId(long SmallItemId) {
        String sql = "{call sp_search_item_map_by_small_item_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, SmallItemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                return itemmap;
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

    public void deleteItemMap(ItemMap itemmap) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM item_map WHERE item_map_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, itemmap.getItemMapId());
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
                //this.clearItemMap(itemmap);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("ItemMap NOT deleted");
            }
        }
    }

    public void displayItemMap(ItemMap ItemMapFrom, ItemMap ItemMapTo) {
        ItemMapTo.setItemMapId(ItemMapFrom.getItemMapId());
        ItemMapTo.setBigItemId(ItemMapFrom.getBigItemId());
        ItemMapTo.setSmallItemId(ItemMapFrom.getSmallItemId());
        ItemMapTo.setFractionQty(ItemMapFrom.getFractionQty());
        ItemMapTo.setPosition(ItemMapFrom.getPosition());
        ItemMapTo.setMapGroupId(ItemMapFrom.getMapGroupId());
    }

    /**
     * @param BigItmId
     * @param SmallItmId
     * @return the ItemMaps
     */
    public List<ItemMap> getItemMaps(long BigItmId, long SmallItmId) {
        String sql = "{call sp_search_item_map_by_map_group_id(?)}";
        ResultSet rs = null;
        ItemMaps = new ArrayList<ItemMap>();
        this.SelectedMapGroupId = 0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, this.getItemMapGroupId(BigItmId, SmallItmId));
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                this.SelectedMapGroupId = rs.getLong("map_group_id");
                ItemMaps.add(itemmap);
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
        return ItemMaps;
    }

    /**
     * @param BigItmId
     * @param SmallItmId
     * @return the ItemMaps
     */
    public long getItemMapGroupId(long BigItmId, long SmallItmId) {
        String sql = "{call sp_search_item_map_by_big_small_item_id(?,?)}";
        ResultSet rs = null;
        ItemMaps = new ArrayList<ItemMap>();
        this.SelectedMapGroupId = 0;

        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, BigItmId);
            ps.setLong(2, SmallItmId);
            rs = ps.executeQuery();
            if (rs.next()) {
                this.SelectedMapGroupId = rs.getLong("map_group_id");
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
        return this.SelectedMapGroupId;
    }

    public List<ItemMap> getItemMaps() {
        String sql = "{call sp_search_item_map_by_map_group_id(?)}";
        ResultSet rs = null;
        ItemMaps = new ArrayList<ItemMap>();
        this.SelectedMapGroupId = 0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, this.SelectedMapGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemMap itemmap = new ItemMap();
                itemmap.setItemMapId(rs.getLong("item_map_id"));
                itemmap.setBigItemId(rs.getLong("big_item_id"));
                itemmap.setSmallItemId(rs.getLong("small_item_id"));
                itemmap.setFractionQty(rs.getFloat("fraction_qty"));
                itemmap.setPosition(rs.getInt("position"));
                itemmap.setMapGroupId(rs.getLong("map_group_id"));
                this.SelectedMapGroupId = rs.getLong("map_group_id");
                ItemMaps.add(itemmap);
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
        return ItemMaps;
    }

    public void clearItemMap(ItemMap im) {
        if (im != null) {
            im.setItemMapId(0);
            //im.setBigItemId(0);
            //im.setSmallItemId(0);
            im.setFractionQty(0);
            im.setPosition(0);
            //im.setMapGroupId(0);
        }
    }

    public long getNewMapGroupId() {
        String sql = "{call sp_get_new_id(?,?,?)}";
        NewId = 0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setString(1, "item_map");
            cs.setString(2, "map_group_id");
            cs.registerOutParameter("out_new_id", VARCHAR);
            cs.executeUpdate();
            NewId = cs.getLong(3);
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            NewId = 0;
        }
        return NewId;
    }

    public int itemCountInMap(String BigSmall, long ItmId) {
        String sql = "";
        if ("BIG".equals(BigSmall)) {
            sql = "{call sp_search_item_map_by_big_item_id(?)}";
        } else if ("SMALL".equals(BigSmall)) {
            sql = "{call sp_search_item_map_by_small_item_id(?)}";
        }
        ResultSet rs = null;
        int records = 0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, ItmId);
            rs = ps.executeQuery();
            while (rs.next()) {
                records = records + 1;
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
        return records;
    }

    public int groupCountInMap(long GrpId) {
        String sql = "{call sp_search_item_map_get_count_map_group_id(?)}";
        ResultSet rs = null;
        int records = 0;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, GrpId);
            rs = ps.executeQuery();
            while (rs.next()) {
                records = records + 1;
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
        return records;
    }

    /**
     * @param ItemMaps the ItemMaps to set
     */
    public void setItemMaps(List<ItemMap> ItemMaps) {
        this.ItemMaps = ItemMaps;
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
     * @return the SelectedItemMap
     */
    public ItemMap getSelectedItemMap() {
        return SelectedItemMap;
    }

    /**
     * @param SelectedItemMap the SelectedItemMap to set
     */
    public void setSelectedItemMap(ItemMap SelectedItemMap) {
        this.SelectedItemMap = SelectedItemMap;
    }

    /**
     * @return the SelectedItemMapId
     */
    public int getSelectedItemMapId() {
        return SelectedItemMapId;
    }

    /**
     * @param SelectedItemMapId the SelectedItemMapId to set
     */
    public void setSelectedItemMapId(int SelectedItemMapId) {
        this.SelectedItemMapId = SelectedItemMapId;
    }

    /**
     * @return the SearchItemMap
     */
    public String getSearchItemMap() {
        return SearchItemMap;
    }

    /**
     * @param SearchItemMap the SearchItemMap to set
     */
    public void setSearchItemMap(String SearchItemMap) {
        this.SearchItemMap = SearchItemMap;
    }

    /**
     * @return the SelectedMapGroupId
     */
    public long getSelectedMapGroupId() {
        return SelectedMapGroupId;
    }

    /**
     * @param SelectedMapGroupId the SelectedMapGroupId to set
     */
    public void setSelectedMapGroupId(long SelectedMapGroupId) {
        this.SelectedMapGroupId = SelectedMapGroupId;
    }

    /**
     * @return the ItemAddedStatus
     */
    public String getItemAddedStatus() {
        return ItemAddedStatus;
    }

    /**
     * @param ItemAddedStatus the ItemAddedStatus to set
     */
    public void setItemAddedStatus(String ItemAddedStatus) {
        this.ItemAddedStatus = ItemAddedStatus;
    }

    /**
     * @return the ItemNotAddedStatus
     */
    public String getItemNotAddedStatus() {
        return ItemNotAddedStatus;
    }

    /**
     * @param ItemNotAddedStatus the ItemNotAddedStatus to set
     */
    public void setItemNotAddedStatus(String ItemNotAddedStatus) {
        this.ItemNotAddedStatus = ItemNotAddedStatus;
    }

    /**
     * @return the ShowItemAddedStatus
     */
    public int getShowItemAddedStatus() {
        return ShowItemAddedStatus;
    }

    /**
     * @param ShowItemAddedStatus the ShowItemAddedStatus to set
     */
    public void setShowItemAddedStatus(int ShowItemAddedStatus) {
        this.ShowItemAddedStatus = ShowItemAddedStatus;
    }

    /**
     * @return the ShowItemNotAddedStatus
     */
    public int getShowItemNotAddedStatus() {
        return ShowItemNotAddedStatus;
    }

    /**
     * @param ShowItemNotAddedStatus the ShowItemNotAddedStatus to set
     */
    public void setShowItemNotAddedStatus(int ShowItemNotAddedStatus) {
        this.ShowItemNotAddedStatus = ShowItemNotAddedStatus;
    }

}
