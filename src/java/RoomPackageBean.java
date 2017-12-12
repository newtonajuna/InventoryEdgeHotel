
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
public class RoomPackageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<RoomPackage> RoomPackages;
    private String ActionMessage;
    private RoomPackage SelectedRoomPackage = null;
    private int SelectedRoomPackageId;
    private String SearchRoomPackageName = "";
    private int TempId1;
    private String TempString1;
    private int TempId2;
    private String TempString2;

    public void saveRoomPackage(RoomPackage room_package) {
        String sql = null;
        String msg = null;

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (room_package.getRoomPackageId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room_package.getRoomPackageId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room_package.getRoomPackageName().length() <= 0) {
            msg = "RoomPackage Name Needed...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (room_package.getRoomPackageId() == 0) {
                sql = "{call sp_insert_room_package(?)}";
            } else if (room_package.getRoomPackageId() > 0) {
                sql = "{call sp_update_room_package(?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (room_package.getRoomPackageId() == 0) {
                    cs.setString(1, room_package.getRoomPackageName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoomPackage(room_package);
                } else if (room_package.getRoomPackageId() > 0) {
                    cs.setInt(1, room_package.getRoomPackageId());
                    cs.setString(2, room_package.getRoomPackageName());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoomPackage(room_package);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("RoomPackage NOT saved");
            }
        }

    }

    public void saveRoomPackage1() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_room_package(?,?)}";

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
            this.setActionMessage("RoomPackage 1 NOT saved");
        }
    }

    public void saveRoomPackage2() {
        String sql = null;
        String msg = null;

        sql = "{call sp_update_room_package(?,?)}";

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
            this.setActionMessage("RoomPackage 2 NOT saved");
        }
    }

    public RoomPackage getRoomPackage(int CatId) {
        String sql = "{call sp_search_room_package_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, CatId);
            rs = ps.executeQuery();
            if (rs.next()) {
                RoomPackage room_package = new RoomPackage();
                room_package.setRoomPackageId(rs.getInt("room_package_id"));
                room_package.setRoomPackageName(rs.getString("room_package_name"));
                return room_package;
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

    public void deleteRoomPackage() {
        this.deleteRoomPackageById(this.SelectedRoomPackageId);
    }

    public void deleteRoomPackageByObject(RoomPackage Cat) {
        this.deleteRoomPackageById(Cat.getRoomPackageId());
    }

    public void deleteRoomPackageById(int CatId) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM room_package WHERE room_package_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, CatId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("RoomPackage NOT deleted");
            }
        }
    }

    public void displayRoomPackage(RoomPackage CatFrom, RoomPackage CatTo) {
        CatTo.setRoomPackageId(CatFrom.getRoomPackageId());
        CatTo.setRoomPackageName(CatFrom.getRoomPackageName());
    }

    public void clearRoomPackage(RoomPackage Cat) {
        Cat.setRoomPackageId(0);
        Cat.setRoomPackageName("");
    }

    public List<RoomPackage> getRoomPackages() {
        String sql;
        sql = "{call sp_search_room_package_by_none()}";
        ResultSet rs = null;
        RoomPackages = new ArrayList<RoomPackage>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomPackage room_package = new RoomPackage();
                room_package.setRoomPackageId(rs.getInt("room_package_id"));
                room_package.setRoomPackageName(rs.getString("room_package_name"));
                RoomPackages.add(room_package);
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
        return RoomPackages;
    }

    /**
     * @param aRoomPackageName
     * @return the RoomPackages
     */
    public List<RoomPackage> getRoomPackagesByRoomPackageName(String aRoomPackageName) {
        String sql;
        sql = "{call sp_search_room_package_by_name(?)}";
        ResultSet rs = null;
        RoomPackages = new ArrayList<RoomPackage>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aRoomPackageName);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomPackage room_package = new RoomPackage();
                room_package.setRoomPackageId(rs.getInt("room_package_id"));
                room_package.setRoomPackageName(rs.getString("room_package_name"));
                RoomPackages.add(room_package);
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
        return RoomPackages;
    }

    /**
     * @param RoomPackages the RoomPackages to set
     */
    public void setRoomPackages(List<RoomPackage> RoomPackages) {
        this.RoomPackages = RoomPackages;
    }

    /**
     * @return the SelectedRoomPackage
     */
    public RoomPackage getSelectedRoomPackage() {
        return SelectedRoomPackage;
    }

    /**
     * @param SelectedRoomPackage the SelectedRoomPackage to set
     */
    public void setSelectedRoomPackage(RoomPackage SelectedRoomPackage) {
        this.SelectedRoomPackage = SelectedRoomPackage;
    }

    /**
     * @return the SelectedRoomPackageId
     */
    public int getSelectedRoomPackageId() {
        return SelectedRoomPackageId;
    }

    /**
     * @param SelectedRoomPackageId the SelectedRoomPackageId to set
     */
    public void setSelectedRoomPackageId(int SelectedRoomPackageId) {
        this.SelectedRoomPackageId = SelectedRoomPackageId;
    }

    /**
     * @return the SearchRoomPackageName
     */
    public String getSearchRoomPackageName() {
        return SearchRoomPackageName;
    }

    /**
     * @param SearchRoomPackageName the SearchRoomPackageName to set
     */
    public void setSearchRoomPackageName(String SearchRoomPackageName) {
        this.SearchRoomPackageName = SearchRoomPackageName;
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
