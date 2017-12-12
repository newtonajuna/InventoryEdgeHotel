
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.primefaces.context.RequestContext;

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
public class TransExtendStay implements Serializable {

    private Date StartDate;
    private Date Prev_EndDate;
    private Date EndDate;

    private String RoomNumber;
    private float Rate;
    private int ExtraDays;

    private String Guest;

    private int transaction_id;
    private int item_id;

    private Date TransactionDate;

    public Date getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(Date TransactionDate) {
        this.TransactionDate = TransactionDate;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getGuest() {
        return Guest;
    }

    public void setGuest(String Guest) {
        this.Guest = Guest;
    }

    public int getExtraDays() {
        return ExtraDays;
    }

    public void setExtraDays(int ExtraDays) {
        this.ExtraDays = ExtraDays;
    }

    public Date getPrev_EndDate() {
        return Prev_EndDate;
    }

    public void setPrev_EndDate(Date Prev_EndDate) {
        this.Prev_EndDate = Prev_EndDate;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float Rate) {
        this.Rate = Rate;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String RoomNumber) {
        this.RoomNumber = RoomNumber;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date EndDate) {
        this.EndDate = EndDate;
    }

    public void compute_extra_days() {
        if (getEndDate() != null && getPrev_EndDate() != null) {
            setExtraDays(Days.daysBetween(new LocalDate(getPrev_EndDate()), new LocalDate(getEndDate())).getDays());
        }
    }

    public void reset() {
        EndDate = null;
        ExtraDays = 0;
    }

    public void extend_stay() {
        if (ExtraDays < 1) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Please set the new Departure Date!", "Please set the new Departure Date!"));
            return;
        }
        int SystemUserId = 0;
        SystemUserId = UserDetailBean.getSystemUserDetailId();
        if (SystemUserId == 0) {
            SystemUserId = new GeneralUserSetting().getCurrentUser().getUserDetailId();
        }
        //IN in_transaction_id int, IN in_days int, IN in_new_date date, IN in_rate float,IN in_store_id int,IN in_add_user_detail_id int,IN in_start_date date,IN in_end_date date,IN in_transaction_date date
        String sql = "{call sp_insert_extend_stay(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
            cs.setInt("in_transaction_id", transaction_id);
            cs.setInt("in_days", ExtraDays);
            cs.setDate("in_new_date", new java.sql.Date(EndDate.getTime()));
            cs.setFloat("in_rate", Rate);
            cs.setInt("in_store_id", new GeneralUserSetting().getCurrentStore().getStoreId());
            cs.setInt("in_add_user_detail_id", SystemUserId);
            cs.setDate("in_start_date", new java.sql.Date(Prev_EndDate.getTime()));
            cs.setDate("in_end_date", new java.sql.Date(EndDate.getTime()));
            cs.setDate("in_end_date", new java.sql.Date(EndDate.getTime()));
            cs.setDate("in_transaction_date", new java.sql.Date(TransactionDate.getTime()));

            cs.executeUpdate();

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('SaleInvoiceTransRoomDialog').hide();");
        } catch (SQLException se) {
            System.err.println("Extend stay:" + se.getMessage());
        }
    }

    public Date get_current_date() {
        return new Date();
    }
}
