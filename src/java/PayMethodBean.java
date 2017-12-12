
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class PayMethodBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<PayMethod> PayMethods;
    private String ActionMessage;
    private PayMethod SelectedPayMethod = null;
    private int SelectedPayMethodId;
    private String SearchPayMethodName = "";

    public void savePayMethod(PayMethod pm) {
        String sql = null;
        String msg = null;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (pm.getPayMethodId() == 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Add") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else if (pm.getPayMethodId() > 0 && grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Edit") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            if (pm.getPayMethodId() == 0) {
                sql = "{call sp_insert_pay_method(?,?)}";
            } else if (pm.getPayMethodId() > 0) {
                sql = "{call sp_update_pay_method(?,?,?)}";
            }

            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    CallableStatement cs = conn.prepareCall(sql);) {
                if (pm.getPayMethodId() == 0) {
                    cs.setString(1, pm.getPayMethodName());
                    cs.setFloat(2, pm.getSurchage());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                } else if (pm.getPayMethodId() > 0) {
                    cs.setInt(1, pm.getPayMethodId());
                    cs.setString(2, pm.getPayMethodName());
                    cs.setFloat(3, pm.getSurchage());
                    cs.executeUpdate();
                    this.setActionMessage("Saved Successfully");
                }
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("PayMethod NOT saved");
            }
        }
    }

    public PayMethod getPayMethod(int aPayMethodId) {
        String sql = "{call sp_search_pay_method_by_id(?)}";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, aPayMethodId);
            rs = ps.executeQuery();
            if (rs.next()) {
                PayMethod pm = new PayMethod();
                pm.setPayMethodId(rs.getInt("pay_method_id"));
                pm.setPayMethodName(rs.getString("pay_method_name"));
                return pm;
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

    public void deletePayMethod() {
        this.deletePayMethodById(this.SelectedPayMethodId);
    }

    public void deletePayMethodByObject(PayMethod pm) {
        this.deletePayMethodById(pm.getPayMethodId());
    }

    public void deletePayMethodById(int PMId) {
        String sql = "DELETE FROM pay_method WHERE pay_method_id=?";
        String msg;
        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, "SETTING", "Delete") == 0) {
            msg = "YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        } else {
            try (
                    Connection conn = DBConnection.getMySQLConnection();
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setInt(1, PMId);
                ps.executeUpdate();
                this.setActionMessage("Deleted Successfully!");
            } catch (SQLException se) {
                System.err.println(se.getMessage());
                this.setActionMessage("PayMethod NOT deleted");
            }
        }
    }

    public void displayPayMethod(PayMethod PmFrom, PayMethod PmTo) {
        PmTo.setPayMethodId(PmFrom.getPayMethodId());
        PmTo.setPayMethodName(PmFrom.getPayMethodName());
    }

    public void clearPayMethod(PayMethod Cat) {
        Cat.setPayMethodId(0);
        Cat.setPayMethodName("");
    }

    public List<PayMethod> getPayMethods() {
        String sql;
        sql = "{call sp_search_pay_method_by_none()}";
        ResultSet rs = null;
        PayMethods = new ArrayList<PayMethod>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            rs = ps.executeQuery();
            while (rs.next()) {
                PayMethod pm = new PayMethod();
                pm.setPayMethodId(rs.getInt("pay_method_id"));
                pm.setPayMethodName(rs.getString("pay_method_name"));
                PayMethods.add(pm);
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
        return PayMethods;
    }

    /**
     * @param aPayMethodName
     * @return the PayMethods
     */
    public List<PayMethod> getPayMethodsByPayMethodName(String aPayMethodName) {
        String sql;
        sql = "{call sp_search_pay_method_by_name(?)}";
        ResultSet rs = null;
        PayMethods = new ArrayList<PayMethod>();
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, aPayMethodName);
            rs = ps.executeQuery();
            while (rs.next()) {
                PayMethod pm = new PayMethod();
                pm.setPayMethodId(rs.getInt("pay_method_id"));
                pm.setPayMethodName(rs.getString("pay_method_name"));
                PayMethods.add(pm);
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
        return PayMethods;
    }

    /**
     * @param PayMethods the PayMethods to set
     */
    public void setPayMethods(List<PayMethod> PayMethods) {
        this.PayMethods = PayMethods;
    }

    /**
     * @return the SelectedPayMethod
     */
    public PayMethod getSelectedPayMethod() {
        return SelectedPayMethod;
    }

    /**
     * @param SelectedPayMethod the SelectedPayMethod to set
     */
    public void setSelectedPayMethod(PayMethod SelectedPayMethod) {
        this.SelectedPayMethod = SelectedPayMethod;
    }

    /**
     * @return the SelectedPayMethodId
     */
    public int getSelectedPayMethodId() {
        return SelectedPayMethodId;
    }

    /**
     * @param SelectedPayMethodId the SelectedPayMethodId to set
     */
    public void setSelectedPayMethodId(int SelectedPayMethodId) {
        this.SelectedPayMethodId = SelectedPayMethodId;
    }

    /**
     * @return the SearchPayMethodName
     */
    public String getSearchPayMethodName() {
        return SearchPayMethodName;
    }

    /**
     * @param SearchPayMethodName the SearchPayMethodName to set
     */
    public void setSearchPayMethodName(String SearchPayMethodName) {
        this.SearchPayMethodName = SearchPayMethodName;
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

}
