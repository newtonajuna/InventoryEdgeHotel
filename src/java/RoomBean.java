
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
public class RoomBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Room> Rooms;
    private String ActionMessage = null;
    private Room SelectedRoom = null;
    private int SelectedRoomId;
    private String SearchRoomNumber = "";

    public void saveRoom(Room room) {
        String sql = null;
        String msg = null;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (room.getRoomId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room.getRoomId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (room.getRoomCategoryId() == 0 || room.getRoomNumber().length() <= 0) {
            msg = "Room Category and Room cannot be empty...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (room.getRoomId() == 0) {
                sql = "{call sp_insert_room(?,?)}";
            } else if (room.getRoomId() > 0) {
                sql = "{call sp_update_room(?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (room.getRoomId() == 0) {
                    cs.setInt(1, room.getRoomCategoryId());
                    cs.setString(2, room.getRoomNumber());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoom(room);
                } else if (room.getRoomId() > 0) {
                    cs.setInt(1, room.getRoomId());
                    cs.setInt(2, room.getRoomCategoryId());
                    cs.setString(3, room.getRoomNumber());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                    this.clearRoom(room);
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Room NOT saved");
            }
        }

    }

    public Room getRoom(int aRoomId) {
        String sql = "{call sp_search_room_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aRoomId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomNumber(rs.getString("room_number"));
                return room;
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

    public void deleteRoom() {
        this.deleteRoomById(this.SelectedRoomId);
    }

    public void deleteRoomByObject(Room SubCat) {
        this.deleteRoomById(SubCat.getRoomId());
    }

    public void deleteRoomById(int aRoomId) {
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "ITEM", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            String sql = "DELETE FROM room WHERE room_id=?";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, aRoomId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("Category NOT deleted");
            }
        }
    }

    public void displayRoom(Room SubCatFrom, Room SubCatTo) {
        SubCatTo.setRoomId(SubCatFrom.getRoomId());
        SubCatTo.setRoomCategoryId(SubCatFrom.getRoomCategoryId());
        SubCatTo.setRoomNumber(SubCatFrom.getRoomNumber());
    }

    public void clearRoom(Room SubCat) {
        SubCat.setRoomId(0);
        SubCat.setRoomCategoryId(0);
        SubCat.setRoomCategoryName("");
        SubCat.setRoomNumber("");
    }

    public List<Room> getRooms() {
        String sql;
        sql = "{call sp_search_room_by_none()}";
        ResultSet rs = null;
        Rooms = new ArrayList<Room>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomCategoryName(rs.getString("category_name"));
                room.setRoomNumber(rs.getString("room_number"));
                Rooms.add(room);
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
        return Rooms;
    }

    public List<Room> getRoomsByCatSubcatName(String aCategorySubcategoryName) {
        String sql;
        sql = "{call sp_search_room_by_name(?)}";
        ResultSet rs = null;
        Rooms = new ArrayList<Room>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aCategorySubcategoryName);
            rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomCategoryName(rs.getString("room_category_name"));
                room.setRoomNumber(rs.getString("room_number"));
                Rooms.add(room);
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
        return Rooms;
    }

    public Room findRoom(long RoomId) {
        String sql = "{call sp_search_room_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setLong(1, RoomId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomCategoryName(rs.getString("room_category_name"));
                room.setRoomNumber(rs.getString("room_number"));
                return room;
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

    public List<Room> getRoomsByCatSubcatName_Empty(String Query) {
        String sql;
        sql = "{call sp_search_room_by_name_empty(?)}";
        ResultSet rs = null;
        Rooms = new ArrayList<Room>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, Query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomCategoryName(rs.getString("room_category_name"));
                room.setRoomNumber(rs.getString("room_number"));
                Rooms.add(room);
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
        return Rooms;
    }

    public List<Room> getRoomsByCategoryId(int aCategoryId) {
        String sql;
        sql = "{call sp_search_room_by_room_category_id(?)}";
        ResultSet rs = null;
        Rooms = new ArrayList<Room>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aCategoryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomCategoryId(rs.getInt("room_category_id"));
                room.setRoomNumber(rs.getString("room_number"));
                Rooms.add(room);
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
        return Rooms;
    }

    public void setRooms(List<Room> Rooms) {
        this.Rooms = Rooms;
    }

    public String getActionMessage() {
        return ActionMessage;
    }

    public void setActionMessage(String ActionMessage) {
        this.ActionMessage = ActionMessage;
    }

    public Room getSelectedRoom() {
        return SelectedRoom;
    }

    public void setSelectedRoom(Room SelectedRoom) {
        this.SelectedRoom = SelectedRoom;
    }

    public int getSelectedRoomId() {
        return SelectedRoomId;
    }

    public void setSelectedRoomId(int SelectedRoomId) {
        this.SelectedRoomId = SelectedRoomId;
    }

    public String getSearchRoomNumber() {
        return SearchRoomNumber;
    }

    public void setSearchRoomNumber(String SearchRoomNumber) {
        this.SearchRoomNumber = SearchRoomNumber;
    }

}
