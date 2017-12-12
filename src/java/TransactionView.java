
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name = "transactionView")
@SessionScoped
public class TransactionView implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long TransactionId;
    private long PayId;
    private Trans CurrentTransaction;
    private Pay CurrentPay;
    private PointsCard CurrentPointscard;
    private UserDetail CurrentUserDetail;
    private Transactor CurrentTransactor;
    private UserDetail CurrentEditUserDetail;
    private List<TransItem> CurrentTransactionItems=new ArrayList<TransItem>();

    public TransactionView() {
    }
    
    public TransactionView(long TransactionId,long PayId) {
        this.TransactionId = TransactionId;
        this.PayId=PayId;
    }

    /**
     * @return the CurrentTransaction
     */
    public Trans getCurrentTransaction() {
        try {
            if(this.getTransactionId()!=0){
                this.CurrentTransaction = new TransBean().getTrans(this.TransactionId);
            }else{
                this.CurrentTransaction = null;
            }
        } catch (Exception e) {
            this.CurrentTransaction = null;
        }
        return CurrentTransaction;
    }

    /**
     * @param CurrentTransaction the CurrentTransaction to set
     */
    public void setCurrentTransaction(Trans CurrentTransaction) {
        this.CurrentTransaction = CurrentTransaction;
    }

    /**
     * @return the CurrentPay
     */
    public Pay getCurrentPay() {
        try {
            if (this.getPayId()!=0) {
                this.CurrentPay = new PayBean().getPay(this.PayId);
            } else {
                this.CurrentPay = null;
            }
        } catch (Exception e) {
            this.CurrentPay = null;
        }
        return CurrentPay;
    }

    /**
     * @param CurrentPay the CurrentPay to set
     */
    public void setCurrentPay(Pay CurrentPay) {
        this.CurrentPay = CurrentPay;
    }

    /**
     * @return the CurrentPointscard
     */
    public PointsCard getCurrentPointscard() {
        try {
            this.CurrentPointscard = new PointsCardBean().getPointsCardByCardNumber(this.CurrentTransaction.getCardNumber());
        } catch (Exception e) {
            this.CurrentPointscard = null;
        }
        return CurrentPointscard;
    }

    /**
     * @param CurrentPointscard the CurrentPointscard to set
     */
    public void setCurrentPointscard(PointsCard CurrentPointscard) {
        this.CurrentPointscard = CurrentPointscard;
    }

    /**
     * @return the CurrentUserDetail
     */
    public UserDetail getCurrentUserDetail() {
        try {
            this.CurrentUserDetail = new UserDetailBean().getUserDetail(this.CurrentTransaction.getAddUserDetailId());
        } catch (Exception e) {
            this.CurrentUserDetail = null;
        }
        return CurrentUserDetail;
    }

    /**
     * @param CurrentUserDetail the CurrentUserDetail to set
     */
    public void setCurrentUserDetail(UserDetail CurrentUserDetail) {
        this.CurrentUserDetail = CurrentUserDetail;
    }

    /**
     * @return the CurrentTransactor
     */
    public Transactor getCurrentTransactor() {
        try {
            this.CurrentTransactor = new TransactorBean().getTransactor(this.CurrentTransaction.getTransactorId());
        } catch (Exception e) {
            this.CurrentTransactor = null;
        }
        return CurrentTransactor;
    }

    /**
     * @param CurrentTransactor the CurrentTransactor to set
     */
    public void setCurrentTransactor(Transactor CurrentTransactor) {
        this.CurrentTransactor = CurrentTransactor;
    }

    /**
     * @return the CurrentEditUserDetail
     */
    public UserDetail getCurrentEditUserDetail() {
        try {
            this.CurrentEditUserDetail = new UserDetailBean().getUserDetail(this.CurrentTransaction.getEditUserDetailId());
        } catch (Exception e) {
            this.CurrentEditUserDetail = null;
        }
        return CurrentEditUserDetail;
    }

    /**
     * @param CurrentEditUserDetail the CurrentEditUserDetail to set
     */
    public void setCurrentEditUserDetail(UserDetail CurrentEditUserDetail) {
        this.CurrentEditUserDetail = CurrentEditUserDetail;
    }

    /**
     * @return the CurrentTransactionItems
     */
    public List<TransItem> getCurrentTransactionItems() {
        try {
            if(this.getTransactionId()!=0){
                this.CurrentTransactionItems = new TransItemBean().getTransItemsByTransactionId(this.TransactionId);
            }else{
                this.CurrentTransactionItems.clear();
            }
        } catch (Exception e) {
            this.CurrentTransactionItems.clear();
        }
        return CurrentTransactionItems;
    }

    /**
     * @param CurrentTransactionItems the CurrentTransactionItems to set
     */
    public void setCurrentTransactionItems(List<TransItem> CurrentTransactionItems) {
        this.CurrentTransactionItems = CurrentTransactionItems;
    }

    /**
     * @return the TransactionId
     */
    public long getTransactionId() {
        return TransactionId;
    }

    /**
     * @param TransactionId the TransactionId to set
     */
    public void setTransactionId(long TransactionId) {
        this.TransactionId = TransactionId;
    }
    
    public String getVatRatedCode(String Vatrated){
        switch (Vatrated) {
            case "STANDARD":
                return "S";
            case "ZERO":
                return "Z";
            case "EXEMPT":
                return "E";
            default:
                return "";
        }
    }

    /**
     * @return the PayId
     */
    public long getPayId() {
        return PayId;
    }

    /**
     * @param PayId the PayId to set
     */
    public void setPayId(long PayId) {
        this.PayId = PayId;
    }
    
}
