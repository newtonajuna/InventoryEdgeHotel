
import java.io.Serializable;
import java.util.Date;
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
public class TransactorLedgerRoomDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String TransactorName;
    private String RoomNumber;
    private Date StartDate;
    private Date EndDate;
    private Date ActualExitDate;
    private String CheckedOutBy;

    public String getTransactorName() {
        return TransactorName;
    }

    public void setTransactorName(String TransactorName) {
        this.TransactorName = TransactorName;
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

    public Date getActualExitDate() {
        return ActualExitDate;
    }

    public void setActualExitDate(Date ActualExitDate) {
        this.ActualExitDate = ActualExitDate;
    }

    public String getCheckedOutBy() {
        return CheckedOutBy;
    }

    public void setCheckedOutBy(String CheckedOutBy) {
        this.CheckedOutBy = CheckedOutBy;
    }

}
