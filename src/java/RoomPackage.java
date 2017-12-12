
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
public class RoomPackage implements Serializable {

    private static final long serialVersionUID = 1L;
    private int RoomPackageId;
    private String RoomPackageName;

    /**
     * @return the RoomPackageId
     */
    public int getRoomPackageId() {
        return RoomPackageId;
    }

    /**
     * @param RoomPackageId the RoomPackageId to set
     */
    public void setRoomPackageId(int RoomPackageId) {
        this.RoomPackageId = RoomPackageId;
    }

    /**
     * @return the RoomPackageName
     */
    public String getRoomPackageName() {
        return RoomPackageName;
    }

    /**
     * @param RoomPackageName the RoomPackageName to set
     */
    public void setRoomPackageName(String RoomPackageName) {
        this.RoomPackageName = RoomPackageName;
    }
}
