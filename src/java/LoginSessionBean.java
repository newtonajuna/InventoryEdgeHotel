
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class LoginSessionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ActionMessage=null;
    
    public int saveLoginSession(LoginSession loginsession) {
        String sql = null;
        sql = "{call sp_insert_login_session(?,?,?,?,?,?)}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            if (loginsession.getLoginSessionId() == 0) {
                cs.setInt("in_user_detail_id", loginsession.getUserDetailId());
                cs.setInt("in_store_id", loginsession.getStoreId());
                cs.setString("in_session_id", loginsession.getSessionId());
                cs.setString("in_remote_ip", loginsession.getRemoteIp());
                cs.setString("in_remote_host", loginsession.getRemoteHost());
                cs.setString("in_remote_user", loginsession.getRemoteUser());
                cs.executeUpdate();
                return 1;
            }else{
                return 0;
            }
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return 0;
        }
    }
    
    public LoginSession getLoginSession(int aLoginSessionId) {
        String sql = "{call sp_search_login_session_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aLoginSessionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                LoginSession loginsession = new LoginSession();
                loginsession.setLoginSessionId(rs.getInt("login_session_id"));
                loginsession.setUserDetailId(rs.getInt("user_detail_id"));
                loginsession.setStoreId(rs.getInt("store_id"));
                loginsession.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                loginsession.setSessionId(rs.getString("session_id"));
                loginsession.setRemoteIp(rs.getString("remote_ip"));
                loginsession.setRemoteHost(rs.getString("remote_host"));
                loginsession.setRemoteUser(rs.getString("remote_user"));
                return loginsession;
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
    
    public LoginSession getLoginSessionBySession(String aSessionId) {
        String sql = "{call sp_search_login_session_by_session(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aSessionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                LoginSession loginsession = new LoginSession();
                loginsession.setLoginSessionId(rs.getInt("login_session_id"));
                loginsession.setUserDetailId(rs.getInt("user_detail_id"));
                loginsession.setStoreId(rs.getInt("store_id"));
                loginsession.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                loginsession.setSessionId(rs.getString("session_id"));
                loginsession.setRemoteIp(rs.getString("remote_ip"));
                loginsession.setRemoteHost(rs.getString("remote_host"));
                loginsession.setRemoteUser(rs.getString("remote_user"));
                return loginsession;
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

    public int deleteLoginSessionBySession(String aSessionId) {
        if (!aSessionId.isEmpty()) {
            String sql = "{call sp_delete_login_session_by_session(?)}";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, aSessionId);
                ps.executeUpdate();
                return 1;
            } catch (SQLException se) {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public int deleteLoginSessionByUser(int aUserDetailId) {
        if (aUserDetailId!=0) {
            String sql = "{call sp_delete_login_session_by_user(?)}";
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, aUserDetailId);
                ps.executeUpdate();
                return 1;
            } catch (SQLException se) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void clearLoginSession(LoginSession loginsession) {
        loginsession.setLoginSessionId(0);
        loginsession.setUserDetailId(0);
        loginsession.setStoreId(0);
        loginsession.setSessionId("");
        loginsession.setAddDate(null);
        loginsession.setRemoteIp("");
        loginsession.setRemoteHost("");
        loginsession.setRemoteUser("");
    }

    public List<LoginSession> getLoginSessions() {
        String sql;
        sql = "{call sp_search_login_session()}";
        ResultSet rs = null;
        List<LoginSession> aLoginSessions = new ArrayList<LoginSession>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                LoginSession loginsession = new LoginSession();
                loginsession.setLoginSessionId(rs.getInt("login_session_id"));
                loginsession.setUserDetailId(rs.getInt("user_detail_id"));
                loginsession.setStoreId(rs.getInt("store_id"));
                loginsession.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                loginsession.setSessionId(rs.getString("session_id"));
                loginsession.setRemoteIp(rs.getString("remote_ip"));
                loginsession.setRemoteHost(rs.getString("remote_host"));
                loginsession.setRemoteUser(rs.getString("remote_user"));
                aLoginSessions.add(loginsession);
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
        return aLoginSessions;
    }
    
    public List<LoginSession> getLoginSessionsByUser(int aUserDetailId) {
        String sql;
        sql = "{call sp_search_login_session_by_user(?)}";
        ResultSet rs = null;
        List<LoginSession> aLoginSessions = new ArrayList<LoginSession>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aUserDetailId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LoginSession loginsession = new LoginSession();
                loginsession.setLoginSessionId(rs.getInt("login_session_id"));
                loginsession.setUserDetailId(rs.getInt("user_detail_id"));
                loginsession.setStoreId(rs.getInt("store_id"));
                loginsession.setAddDate(new Date(rs.getTimestamp("add_date").getTime()));
                loginsession.setSessionId(rs.getString("session_id"));
                loginsession.setRemoteIp(rs.getString("remote_ip"));
                loginsession.setRemoteHost(rs.getString("remote_host"));
                loginsession.setRemoteUser(rs.getString("remote_user"));
                aLoginSessions.add(loginsession);
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
        return aLoginSessions;
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
    
    public void deleteOldUnloggedOutSessions() {
        String sql = "{call sp_delete_login_session_unlogged_out()}";
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.executeUpdate();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
}
