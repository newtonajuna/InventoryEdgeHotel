
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
public class RoomCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    private int RoomCategoryId;
    private String RoomCategoryName;
    private String ShortName;

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    /**
     * @return the RoomCategoryId
     */
    public int getRoomCategoryId() {
        return RoomCategoryId;
    }

    /**
     * @param RoomCategoryId the RoomCategoryId to set
     */
    public void setRoomCategoryId(int RoomCategoryId) {
        this.RoomCategoryId = RoomCategoryId;
    }

    /**
     * @return the RoomCategoryName
     */
    public String getRoomCategoryName() {
        return RoomCategoryName;
    }

    /**
     * @param RoomCategoryName the RoomCategoryName to set
     */
    public void setRoomCategoryName(String RoomCategoryName) {
        this.RoomCategoryName = RoomCategoryName;
    }
}
