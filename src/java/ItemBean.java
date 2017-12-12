
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
import javax.faces.event.AjaxBehaviorEvent;

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
public class ItemBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Item> Items;
    private String ActionMessage=null;
    private Item SelectedItem=null;
    private Item SelectedItemX=null;
    private long SelectedItemId;
    private String SearchItemDesc="";
    private List<Item> ItemObjectList;
    private String TypedItemCode;
    List<Item> ReportItems= new ArrayList<Item>();
    List<Item> ReportItemsSummary= new ArrayList<Item>();
    
    public void ItemBarCodeListener(AjaxBehaviorEvent event) {
        System.out.println("OkaY");
    }   
    
    public void saveItem(Item item) {
        String sql = null;
        String msg="";
        String sql2 = null;
        sql2="SELECT * FROM item WHERE (item_code!='' AND item_code='" + item.getItemCode() + "') or description='" + item.getDescription() + "'";
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (item.getItemId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (item.getItemId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(item.getCategoryId()==0){
            msg="Select a valid Category please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(item.getItemType().length()<=0){
            msg="Select a valid Item Type please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(item.getUnitId()==0){
            msg="Select a valid Unit please";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(item.getDescription(), 1, 100).equals("FAIL")){
            msg="Enter Item Description!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(item.getIsSuspended(), 2, 3).equals("FAIL")){
            msg="Select value for Is Suspended!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if(new CustomValidator().TextSize(item.getVatRated(), 2, 10).equals("FAIL")){
            msg="Select value for Vat Rated!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if((new CustomValidator().CheckRecords(sql2)>0 && item.getItemId()==0) || (new CustomValidator().CheckRecords(sql2)>0 && new CustomValidator().CheckRecords(sql2)!=1 && item.getItemId()>0)){
            msg="Item Code OR Description already exists!";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            
        if (item.getItemId() == 0) {
            sql = "{call sp_insert_item(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        } else if (item.getItemId() > 0) {
            sql = "{call sp_update_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (item.getItemId() == 0) {
                cs.setString("in_item_code", item.getItemCode());
                cs.setString("in_description", item.getDescription());
                cs.setInt("in_category_id", item.getCategoryId());
                cs.setInt("in_sub_category_id", item.getSubCategoryId());
                cs.setInt("in_unit_id", item.getUnitId());
                cs.setInt("in_reorder_level", item.getReorderLevel());
                cs.setFloat("in_unit_cost_price", item.getUnitCostPrice());
                cs.setFloat("in_unit_retailsale_price", item.getUnitRetailsalePrice());
                cs.setFloat("in_unit_wholesale_price", item.getUnitWholesalePrice());
                cs.setString("in_is_suspended", item.getIsSuspended());
                cs.setString("in_vat_rated", item.getVatRated());
                cs.setString("in_item_img_url", item.getItemImgUrl());
                cs.setString("in_item_type", item.getItemType());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearItem(item);
            } else if (item.getItemId() > 0) {
                cs.setLong("in_item_id", item.getItemId());
                cs.setString("in_item_code", item.getItemCode());
                cs.setString("in_description", item.getDescription());
                cs.setInt("in_category_id", item.getCategoryId());
                cs.setInt("in_sub_category_id", item.getSubCategoryId());
                cs.setInt("in_unit_id", item.getUnitId());
                cs.setInt("in_reorder_level", item.getReorderLevel());
                cs.setFloat("in_unit_cost_price", item.getUnitCostPrice());
                cs.setFloat("in_unit_retailsale_price", item.getUnitRetailsalePrice());
                cs.setFloat("in_unit_wholesale_price", item.getUnitWholesalePrice());
                cs.setString("in_is_suspended", item.getIsSuspended());
                cs.setString("in_vat_rated", item.getVatRated());
                cs.setString("in_item_img_url", item.getItemImgUrl());
                cs.setString("in_item_type", item.getItemType());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
                this.clearItem(item);
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Item NOT saved");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage("Item NOT saved!"));
        }
    }
    }
    
    public Item getItem(long ItemId) {
        String sql = "{call sp_search_item_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, ItemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                return item;
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
    
    public Item findItem(long ItemId) {
        String sql = "{call sp_search_item_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, ItemId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                return item;
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
    
    public Item findItemByCode(String ItemCode) {
        String sql = "{call sp_search_item_by_code(?)}";  
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, ItemCode);
            rs = ps.executeQuery();
            if (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                //item.setBatchno(rs.getString("batchno"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                return item;
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
    
    public Item findItemByCodeActive(String ItemCode) {
        if(ItemCode.trim().isEmpty()){
            return  null;
        }
        String sql = "{call sp_search_item_active_by_code(?)}";  
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, ItemCode);
            rs = ps.executeQuery();
            if (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                //item.setBatchno(rs.getString("batchno"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                return item;
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

    public void deleteItem(Item item) {
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"ITEM", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
            String sql = "DELETE FROM item WHERE item_id=?";
            try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, item.getItemId());
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
            this.clearItem(item);
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Item NOT deleted");
            }
       }
    }

    public void displayItem(Item ItemFrom, Item ItemTo) {
                ItemTo.setItemId(ItemFrom.getItemId());
                ItemTo.setItemCode(ItemFrom.getItemCode());
                ItemTo.setDescription(ItemFrom.getDescription());
                //ItemTo.setBatchno(ItemFrom.getBatchno());
                ItemTo.setCategoryId(ItemFrom.getCategoryId());
                ItemTo.setSubCategoryId(ItemFrom.getSubCategoryId());
                ItemTo.setUnitId(ItemFrom.getUnitId());
                ItemTo.setReorderLevel(ItemFrom.getReorderLevel());
                ItemTo.setUnitCostPrice(ItemFrom.getUnitCostPrice());
                ItemTo.setUnitRetailsalePrice(ItemFrom.getUnitRetailsalePrice());
                ItemTo.setUnitWholesalePrice(ItemFrom.getUnitWholesalePrice());
                ItemTo.setIsSuspended(ItemFrom.getIsSuspended());
                ItemTo.setVatRated(ItemFrom.getVatRated());
                ItemTo.setItemImgUrl(ItemFrom.getItemImgUrl());
                ItemTo.setItemType(ItemFrom.getItemType());
    }

    public void clearItem(Item item) {
        if (item != null) {
            item.setItemId(0);
            item.setItemCode("");
            item.setDescription("");
            //item.setBatchno("");
            item.setCategoryId(0);
            item.setSubCategoryId(0);
            item.setUnitId(0);
            item.setReorderLevel(0);
            item.setUnitCostPrice(0);
            item.setUnitRetailsalePrice(0);
            item.setUnitWholesalePrice(0);
            item.setIsSuspended("");
            item.setVatRated("");
            item.setItemImgUrl("");
            item.setItemType("");
        }
    }
    
    public void initClearItem(Item item) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            if (item != null) {
                item.setItemId(0);
                item.setItemCode("");
                item.setDescription("");
                //item.setBatchno("");
                item.setCategoryId(0);
                item.setSubCategoryId(0);
                item.setUnitId(0);
                item.setReorderLevel(0);
                item.setUnitCostPrice(0);
                item.setUnitRetailsalePrice(0);
                item.setUnitWholesalePrice(0);
                item.setIsSuspended("");
                item.setVatRated("");
                item.setItemImgUrl("");
                item.setItemType("");
            }
        }
    }
    
    public void clearSelectedItem(){
        this.clearItem(this.getSelectedItem());
    }
    
    public List<Item> getItemObjectList(String Query) {
        this.setTypedItemCode(Query);
        String sql;
        sql = "{call sp_search_item_by_code_desc(?)}";    
        ResultSet rs = null;
        this.ItemObjectList = new ArrayList<Item>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                this.ItemObjectList.add(item);
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
        return ItemObjectList;
    }
    
    public List<Item> getItemObjectListActive(String Query) {
        this.setTypedItemCode(Query);
        String sql;
        sql = "{call sp_search_item_active_by_code_desc(?)}";    
        ResultSet rs = null;
        this.ItemObjectList = new ArrayList<Item>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                this.ItemObjectList.add(item);
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
        return ItemObjectList;
    }
    public List<Item> getItemObjectListActive_Accomodation(String Query) {
        this.setTypedItemCode(Query);
        String sql;
        sql = "{call sp_search_item_active_by_code_desc_accomodation(?)}";    
        ResultSet rs = null;
        this.ItemObjectList = new ArrayList<Item>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                item.setUnitId(rs.getInt("unit_id"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                this.ItemObjectList.add(item);
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
        return ItemObjectList;
    }
    
     /**
     * @param ItemObjectList the ItemObjectList to set
     */
    public void setItemObjectList(List<Item> ItemObjectList) {
        this.ItemObjectList = ItemObjectList;
    }

    /**
     * @param aNameOrCode
     * @return the Items
     */
    public List<Item> getItems(String aNameOrCode) {
        String sql = "{call sp_search_item_by_code_desc(?)}";
        ResultSet rs = null;
        Items = new ArrayList<Item>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aNameOrCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getLong("item_id"));
                item.setItemCode(rs.getString("item_code"));
                item.setDescription(rs.getString("description"));
                //item.setBatchno(rs.getString("batchno"));
                item.setCategoryId(rs.getInt("category_id"));
                    item.setCategoryName(rs.getString("category_name"));
                item.setSubCategoryId(rs.getInt("sub_category_id"));
                    item.setSubCategoryName(rs.getString("sub_category_name"));
                item.setUnitId(rs.getInt("unit_id"));
                    item.setUnitSymbol(rs.getString("unit_symbol"));
                item.setReorderLevel(rs.getInt("reorder_level"));
                item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                item.setIsSuspended(rs.getString("is_suspended"));
                item.setVatRated(rs.getString("vat_rated"));
                item.setItemImgUrl(rs.getString("item_img_url"));
                item.setItemType(rs.getString("item_type"));
                Items.add(item);
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
        return Items;
    }
    
    public List<Item> getReportItems(Item aItem,boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_item(?,?,?)}";
        ResultSet rs = null;
        this.ReportItems.clear();
            if(aItem!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    ps.setInt(1, aItem.getCategoryId());
                    try{
                        ps.setInt(2, aItem.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2, 0);
                    }
                    ps.setString(3, aItem.getIsSuspended());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        Item item = new Item();
                        item.setItemId(rs.getLong("item_id"));
                        item.setItemCode(rs.getString("item_code"));
                        item.setDescription(rs.getString("description"));
                        item.setCategoryId(rs.getInt("category_id"));
                        item.setCategoryName(rs.getString("category_name"));
                        try{
                            item.setSubCategoryId(rs.getInt("sub_category_id"));
                            item.setSubCategoryName(rs.getString("sub_category_name"));
                        }catch(NullPointerException npe){
                            item.setSubCategoryId(0);
                            item.setSubCategoryName("");
                        }
                        item.setUnitId(rs.getInt("unit_id"));
                        item.setUnitSymbol(rs.getString("unit_symbol"));
                        item.setReorderLevel(rs.getInt("reorder_level"));
                        item.setUnitCostPrice(rs.getFloat("unit_cost_price"));
                        item.setUnitRetailsalePrice(rs.getFloat("unit_retailsale_price"));
                        item.setUnitWholesalePrice(rs.getFloat("unit_wholesale_price"));
                        item.setIsSuspended(rs.getString("is_suspended"));
                        item.setVatRated(rs.getString("vat_rated"));
                        try{
                            item.setItemImgUrl(rs.getString("item_img_url"));
                        }catch(NullPointerException npe){
                            item.setItemImgUrl("");
                        }
                        item.setItemType(rs.getString("item_type"));
                        this.ReportItems.add(item);
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
            }else{
                this.ReportItems.clear();
            }
        return this.ReportItems;
    }
    
    public long getReportItemsCount() {
         return this.ReportItems.size();
    }
    
    public List<Item> getReportItemsSummary(Item aItem,boolean RETRIEVE_REPORT) {
        String sql = "{call sp_report_item_summary(?,?,?)}";
        ResultSet rs = null;
        this.ReportItemsSummary.clear();
            if(aItem!=null && RETRIEVE_REPORT==true){
                try (
                        Connection conn = DBConnection.getMySQLConnection();
                        PreparedStatement ps = conn.prepareStatement(sql);) {
                    ps.setInt(1, aItem.getCategoryId());
                    try{
                        ps.setInt(2, aItem.getSubCategoryId());
                    }catch(NullPointerException npe){
                        ps.setInt(2, 0);
                    }
                    ps.setString(3, aItem.getIsSuspended());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        Item item = new Item();
                        item.setCategoryId(rs.getInt("category_id"));
                        item.setCategoryName(rs.getString("category_name"));
                        try{
                            item.setSubCategoryId(rs.getInt("sub_category_id"));
                            item.setSubCategoryName(rs.getString("sub_category_name"));
                        }catch(NullPointerException npe){
                            item.setSubCategoryId(0);
                            item.setSubCategoryName("");
                        }
                        try{
                            item.setCountItems(rs.getInt("count_items"));
                        }catch(NullPointerException npe){
                            item.setCountItems(0);
                        }

                        this.ReportItemsSummary.add(item);
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
            }else{
                this.ReportItemsSummary.clear();
            }
        return this.ReportItemsSummary;
    }
    
    public long getReportItemsSummaryCount() {
         return this.ReportItemsSummary.size();
    }
    
    /**
     * @param Items the Items to set
     */
    public void setItems(List<Item> Items) {
        this.Items = Items;
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
     * @return the SelectedItemId
     */
    public long getSelectedItemId() {
        return SelectedItemId;
    }

    /**
     * @param SelectedItemId the SelectedItemId to set
     */
    public void setSelectedItemId(long SelectedItemId) {
        this.SelectedItemId = SelectedItemId;
    }

    /**
     * @return the SearchItemDesc
     */
    public String getSearchItemDesc() {
        return SearchItemDesc;
    }

    /**
     * @param SearchItemDesc the SearchItemDesc to set
     */
    public void setSearchItemDesc(String SearchItemDesc) {
        this.SearchItemDesc = SearchItemDesc;
    }

    /**
     * @return the SelectedItem
     */
    public Item getSelectedItem() {
        return SelectedItem;
    }

    /**
     * @param SelectedItem the SelectedItem to set
     */
    public void setSelectedItem(Item SelectedItem) {
        this.SelectedItem = SelectedItem;
    }

    /**
     * @return the SelectedItemX
     */
    public Item getSelectedItemX() {
        return SelectedItemX;
    }

    /**
     * @param SelectedItemX the SelectedItemX to set
     */
    public void setSelectedItemX(Item SelectedItemX) {
        this.SelectedItemX = SelectedItemX;
    }

    /**
     * @return the TypedItemCode
     */
    public String getTypedItemCode() {
        return TypedItemCode;
    }

    /**
     * @param TypedItemCode the TypedItemCode to set
     */
    public void setTypedItemCode(String TypedItemCode) {
        this.TypedItemCode = TypedItemCode;
    }

}
