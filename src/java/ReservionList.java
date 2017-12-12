
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
public class ReservionList {
    private String TransactorName;
    private Date StartDate;
    private Date EndDate;
    private int NumberOfPersons;
    private String ReservedBy;
    private String Terms;
    private String TransactionReasonName;
    private String TransactionStatus;
    private int TransactionId;

    public int getTransactionId() {
        return TransactionId;
    }
    public void setTransactionId(int TransactionId) {
        this.TransactionId = TransactionId;
    }   

    public String getTransactionReasonName() {
        return TransactionReasonName;
    }

    public void setTransactionReasonName(String TransactionReasonName) {
        this.TransactionReasonName = TransactionReasonName;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setTransactionStatus(String TransactionStatus) {
        this.TransactionStatus = TransactionStatus;
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

    public int getNumberOfPersons() {
        return NumberOfPersons;
    }

    public void setNumberOfPersons(int NumberOfPersons) {
        this.NumberOfPersons = NumberOfPersons;
    }

    public String getReservedBy() {
        return ReservedBy;
    }

    public void setReservedBy(String ReservedBy) {
        this.ReservedBy = ReservedBy;
    }

    public String getTerms() {
        return Terms;
    }

    public void setTerms(String Terms) {
        this.Terms = Terms;
    }
    
}
