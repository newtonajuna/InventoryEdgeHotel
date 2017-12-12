
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
public class LoginSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private int LoginSessionId;
    private int UserDetailId;
    private int StoreId;
    private String SessionId;
    private Date AddDate;
    private String RemoteIp;
    private String RemoteHost;
    private String RemoteUser;

    /**
     * @return the LoginSessionId
     */
    public int getLoginSessionId() {
        return LoginSessionId;
    }

    /**
     * @param LoginSessionId the LoginSessionId to set
     */
    public void setLoginSessionId(int LoginSessionId) {
        this.LoginSessionId = LoginSessionId;
    }

    /**
     * @return the UserDetailId
     */
    public int getUserDetailId() {
        return UserDetailId;
    }

    /**
     * @param UserDetailId the UserDetailId to set
     */
    public void setUserDetailId(int UserDetailId) {
        this.UserDetailId = UserDetailId;
    }

    /**
     * @return the StoreId
     */
    public int getStoreId() {
        return StoreId;
    }

    /**
     * @param StoreId the StoreId to set
     */
    public void setStoreId(int StoreId) {
        this.StoreId = StoreId;
    }

    /**
     * @return the SessionId
     */
    public String getSessionId() {
        return SessionId;
    }

    /**
     * @param SessionId the SessionId to set
     */
    public void setSessionId(String SessionId) {
        this.SessionId = SessionId;
    }

    /**
     * @return the AddDate
     */
    public Date getAddDate() {
        return AddDate;
    }

    /**
     * @param AddDate the AddDate to set
     */
    public void setAddDate(Date AddDate) {
        this.AddDate = AddDate;
    }

    /**
     * @return the RemoteIp
     */
    public String getRemoteIp() {
        return RemoteIp;
    }

    /**
     * @param RemoteIp the RemoteIp to set
     */
    public void setRemoteIp(String RemoteIp) {
        this.RemoteIp = RemoteIp;
    }

    /**
     * @return the RemoteHost
     */
    public String getRemoteHost() {
        return RemoteHost;
    }

    /**
     * @param RemoteHost the RemoteHost to set
     */
    public void setRemoteHost(String RemoteHost) {
        this.RemoteHost = RemoteHost;
    }

    /**
     * @return the RemoteUser
     */
    public String getRemoteUser() {
        return RemoteUser;
    }

    /**
     * @param RemoteUser the RemoteUser to set
     */
    public void setRemoteUser(String RemoteUser) {
        this.RemoteUser = RemoteUser;
    }
}
