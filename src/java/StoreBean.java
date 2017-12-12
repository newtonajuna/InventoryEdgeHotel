
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
public class StoreBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Store> Stores;
    private String ActionMessage=null;
    private Store SelectedStore=null;
    private int SelectedStoreId;
    private String SearchStoreName="";
    
    public void saveStore(Store store) {
        String sql = null;
        String msg=null;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

      if (store.getStoreId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else if (store.getStoreId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
      }else{
        if (store.getStoreId() == 0) {
            sql = "{call sp_insert_store(?)}";
        } else if (store.getStoreId() > 0) {
            sql = "{call sp_update_store(?,?)}";
        }

        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (store.getStoreId() == 0) {
                cs.setString(1, store.getStoreName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            } else if (store.getStoreId() > 0) {
                cs.setInt(1, store.getStoreId());
                cs.setString(2, store.getStoreName());
                cs.executeUpdate();
                this.setActionMessage("Saved Successfully");
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Store NOT saved");
        }
      }
    }
    
    public Store getStore(int aStoreId) {
        String sql = "{call sp_search_store_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aStoreId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                return store;
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
    
    public Store getStoreByNameEqual(String aStoreName) {
        String sql = "{call sp_search_store_by_name_equal(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aStoreName);
            rs = ps.executeQuery();
            if (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                return store;
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
    
    public void deleteStore() {
         this.deleteStoreById(this.SelectedStoreId);
    }
    public void deleteStoreByObject(Store store) {
         this.deleteStoreById(store.getStoreId());
    }

    public void deleteStoreById(int aStoreId) {
        String sql = "DELETE FROM store WHERE store_id=?";
        String msg;
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Delete")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else
        {
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aStoreId);
            ps.executeUpdate();
            this.setActionMessage("Deleted Successfully!");
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("Store NOT deleted");
        }
        }
    }

    public void displayStore(Store StoreFrom, Store StoreTo) {
        StoreTo.setStoreId(StoreFrom.getStoreId());
        StoreTo.setStoreName(StoreFrom.getStoreName());
    }

    public void clearStore(Store store) {
        store.setStoreId(0);
        store.setStoreName("");
    }

    /**
     * @return the Stores
     */
    public List<Store> getStores() {
        String sql;
        sql = "{call sp_search_store_by_none()}";
        ResultSet rs = null;
        Stores = new ArrayList<Store>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                Stores.add(store);
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
        return Stores;
    }
    public List<Store> getStoresByName(String aStoreName) {
        String sql;
        sql = "{call sp_search_store_by_name(?)}";
        ResultSet rs = null;
        Stores = new ArrayList<Store>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1,aStoreName);
            rs = ps.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                Stores.add(store);
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
        return Stores;
    }

    /**
     * @param Stores the Stores to set
     */
    public void setStores(List<Store> Stores) {
        this.Stores = Stores;
    }
    
    public List<Store> getStoresByUser(int aUserDetId) {
        String sql;
        sql = "{call sp_search_store_by_user_detail(?)}";
        ResultSet rs = null;
        Stores = new ArrayList<Store>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1,aUserDetId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                Stores.add(store);
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
        return Stores;
    }
    
    public List<Store> getStoresAll() {
        String sql;
        sql = "{call sp_search_store_by_none()}";
        ResultSet rs = null;
        Stores = new ArrayList<Store>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                Stores.add(store);
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
        return Stores;
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
     * @return the SelectedStore
     */
    public Store getSelectedStore() {
        return SelectedStore;
    }

    /**
     * @param SelectedStore the SelectedStore to set
     */
    public void setSelectedStore(Store SelectedStore) {
        this.SelectedStore = SelectedStore;
    }

    /**
     * @return the SelectedStoreId
     */
    public int getSelectedStoreId() {
        return SelectedStoreId;
    }

    /**
     * @param SelectedStoreId the SelectedStoreId to set
     */
    public void setSelectedStoreId(int SelectedStoreId) {
        this.SelectedStoreId = SelectedStoreId;
    }

    /**
     * @return the SearchStoreName
     */
    public String getSearchStoreName() {
        return SearchStoreName;
    }

    /**
     * @param SearchStoreName the SearchStoreName to set
     */
    public void setSearchStoreName(String SearchStoreName) {
        this.SearchStoreName = SearchStoreName;
    }
    
}
