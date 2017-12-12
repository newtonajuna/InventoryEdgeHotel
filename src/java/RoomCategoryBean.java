
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
public class RoomCategoryBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<RoomCategory> RoomCategories;
    private String ActionMessage;
    private RoomCategory SelectedRoomCategory = null;
    private int SelectedRoomCategoryId;
    private String SearchRoomCategoryName = "";
    private int TempId1;
    private String TempString1;
    private int TempId2;
    private String TempString2;

    public void saveRoomCategory(RoomCategory room_cat) {
        String sql = null;
        String msg = null;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (room_cat.getRoomCategoryId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room_cat.getRoomCategoryId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room_cat.getRoomCategoryName().length() <= 0) {
            msg = "RoomCategory Name Needed...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (room_cat.getRoomCategoryId() == 0) {
                sql = "{call sp_insert_room_category(?,?)}";
            } else if (room_cat.getRoomCategoryId() > 0) {
                sql = "{call sp_update_room_category(?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (room_cat.getRoomCategoryId() == 0) {
                    cs.setString(1, room_cat.getRoomCategoryName());
                    cs.setString(2, room_cat.getShortName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoomCategory(room_cat);
                } else if (room_cat.getRoomCategoryId() > 0) {
                    cs.setInt(1, room_cat.getRoomCategoryId());
                    cs.setString(2, room_cat.getRoomCategoryName());
                    cs.setString(3, room_cat.getShortName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoomCategory(room_cat);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("RoomCategory NOT saved");
            }
        }

    }

    public void saveRoomCategory1() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_room_category(?,?)}";

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
            this.setActionMessage("RoomCategory 1 NOT saved");
        }
    }

    public void saveRoomCategory2() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_room_category(?,?)}";

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
            this.setActionMessage("RoomCategory 2 NOT saved");
        }
    }

    public RoomCategory getRoomCategory(int CatId) {
        String sql = "{call sp_search_room_category_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, CatId);
            rs = ps.executeQuery();
            if (rs.next()) {
                RoomCategory room_cat = new RoomCategory();
                room_cat.setRoomCategoryId(rs.getInt("room_category_id"));
                room_cat.setRoomCategoryName(rs.getString("room_category_name"));
                return room_cat;
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

    public void deleteRoomCategory() {
        this.deleteRoomCategoryById(this.SelectedRoomCategoryId);
    }

    public void deleteRoomCategoryByObject(RoomCategory Cat) {
        this.deleteRoomCategoryById(Cat.getRoomCategoryId());
    }

    public void deleteRoomCategoryById(int CatId) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM room_category WHERE room_category_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, CatId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("RoomCategory NOT deleted");
            }
        }
    }

    public void displayRoomCategory(RoomCategory CatFrom, RoomCategory CatTo) {
        CatTo.setRoomCategoryId(CatFrom.getRoomCategoryId());
        CatTo.setRoomCategoryName(CatFrom.getRoomCategoryName());
        CatTo.setShortName(CatFrom.getShortName());
    }

    public void clearRoomCategory(RoomCategory Cat) {
        Cat.setRoomCategoryId(0);
        Cat.setRoomCategoryName("");
        Cat.setShortName("");
    }

    public List<RoomCategory> getRoomCategories() {
        String sql;
        sql = "{call sp_search_room_category_by_none()}";
        ResultSet rs = null;
        RoomCategories = new ArrayList<RoomCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomCategory room_cat = new RoomCategory();
                room_cat.setRoomCategoryId(rs.getInt("room_category_id"));
                room_cat.setRoomCategoryName(rs.getString("room_category_name"));
                RoomCategories.add(room_cat);
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
        return RoomCategories;
    }

    /**
     * @param aRoomCategoryName
     * @return the RoomCategories
     */
    public List<RoomCategory> getRoomCategoriesByRoomCategoryName(String aRoomCategoryName) {
        String sql;
        sql = "{call sp_search_room_category_by_name(?)}";
        ResultSet rs = null;
        RoomCategories = new ArrayList<RoomCategory>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aRoomCategoryName);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomCategory room_cat = new RoomCategory();
                room_cat.setRoomCategoryId(rs.getInt("room_category_id"));
                room_cat.setRoomCategoryName(rs.getString("room_category_name"));
                room_cat.setShortName(rs.getString("short_name"));
                RoomCategories.add(room_cat);
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
        return RoomCategories;
    }

    /**
     * @param RoomCategories the RoomCategories to set
     */
    public void setRoomCategories(List<RoomCategory> RoomCategories) {
        this.RoomCategories = RoomCategories;
    }

    /**
     * @return the SelectedRoomCategory
     */
    public RoomCategory getSelectedRoomCategory() {
        return SelectedRoomCategory;
    }

    /**
     * @param SelectedRoomCategory the SelectedRoomCategory to set
     */
    public void setSelectedRoomCategory(RoomCategory SelectedRoomCategory) {
        this.SelectedRoomCategory = SelectedRoomCategory;
    }

    /**
     * @return the SelectedRoomCategoryId
     */
    public int getSelectedRoomCategoryId() {
        return SelectedRoomCategoryId;
    }

    /**
     * @param SelectedRoomCategoryId the SelectedRoomCategoryId to set
     */
    public void setSelectedRoomCategoryId(int SelectedRoomCategoryId) {
        this.SelectedRoomCategoryId = SelectedRoomCategoryId;
    }

    /**
     * @return the SearchRoomCategoryName
     */
    public String getSearchRoomCategoryName() {
        return SearchRoomCategoryName;
    }

    /**
     * @param SearchRoomCategoryName the SearchRoomCategoryName to set
     */
    public void setSearchRoomCategoryName(String SearchRoomCategoryName) {
        this.SearchRoomCategoryName = SearchRoomCategoryName;
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
}
