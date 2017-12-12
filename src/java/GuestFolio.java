
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
public class GuestFolio implements Serializable {

    private static final long serialVersionUID = 1L;
    private long GuestFolioId;
    private long TransactorId;
    private Date StartDate;
    private Date EndDate;
    private String IsCurrent;
    private String Status;
    private Transactor transactor;
    private String TransactorName;

    private long total_amount_credit;
    private long total_amount_debit;
    private Integer adults;
    private Integer children;

    public Integer getAdults() {
        return adults;
    }

    public void setAdults(Integer adults) {
        this.adults = adults;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public long getTotal_amount_credit() {
        return total_amount_credit;
    }

    public void setTotal_amount_credit(long total_amount_credit) {
        this.total_amount_credit = total_amount_credit;
    }

    public long getTotal_amount_debit() {
        return total_amount_debit;
    }

    public void setTotal_amount_debit(long total_amount_debit) {
        this.total_amount_debit = total_amount_debit;
    }

    public long getTransactorId() {
        return TransactorId;
    }

    public void setTransactorId(long TransactorId) {
        this.TransactorId = TransactorId;
    }

    public long getGuestFolioId() {
        return GuestFolioId;
    }

    public void setGuestFolioId(long GuestFolioId) {
        this.GuestFolioId = GuestFolioId;
    }

    public String getTransactorName() {
        return TransactorName;
    }

    public void setTransactorName(String TransactorName) {
        this.TransactorName = TransactorName;
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

    public String getIsCurrent() {
        return IsCurrent;
    }

    public void setIsCurrent(String IsCurrent) {
        this.IsCurrent = IsCurrent;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public Transactor getTransactor() {
        return transactor;
    }

    public void setTransactor(Transactor transactor) {
        this.transactor = transactor;
    }

    public void clearTransactor(GuestFolio guest_folio) {
        transactor = null;
        if (guest_folio != null) {
            guest_folio.setGuestFolioId(0);
            guest_folio.setIsCurrent("");
            guest_folio.setStatus("");
            guest_folio.setStartDate(null);
            guest_folio.setEndDate(null);
            guest_folio.setChildren(0);
            guest_folio.setAdults(0);
        }
    }
}
