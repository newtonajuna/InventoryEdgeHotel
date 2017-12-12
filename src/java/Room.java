
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private int RoomId;
    private String RoomNumber;
    private int RoomCategoryId;
    private String RoomCategoryName;

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String RoomNumber) {
        this.RoomNumber = RoomNumber;
    }

    public int getRoomCategoryId() {
        return RoomCategoryId;
    }

    public void setRoomCategoryId(int RoomCategoryId) {
        this.RoomCategoryId = RoomCategoryId;
    }

    public String getRoomCategoryName() {
        return RoomCategoryName;
    }

    public void setRoomCategoryName(String RoomCategoryName) {
        this.RoomCategoryName = RoomCategoryName;
    }

}
