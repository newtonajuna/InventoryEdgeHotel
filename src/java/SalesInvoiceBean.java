
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name = "salesInvoiceBean")
@SessionScoped
public class SalesInvoiceBean implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Trans CurrentTransaction;
    private Pay CurrentPay;
    private PointsCard CurrentPointscard;
    private UserDetail CurrentUserDetail;
    private UserDetail CurrentTransUserDetail;
    private Transactor CurrentTransactor;
    private Transactor CurrentBillTransactor;
    private UserDetail CurrentEditUserDetail;
    private List<TransItem> CurrentTransactionItems=new ArrayList<TransItem>();

    public SalesInvoiceBean() {
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
     * @return the CurrentTransaction
     */
    public Trans getCurrentTransaction() {
        try {
            //System.out.println("T");
            this.CurrentTransaction = new TransBean().getTrans(new GeneralUserSetting().getCurrentTransactionId());
        } catch (Exception e) {
            //System.out.println("Te");
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
            if (new GeneralUserSetting().getCurrentPayId() != 0) {
                //System.out.println("P");
                Pay pay=new PayBean().getPay(new GeneralUserSetting().getCurrentPayId());
                this.CurrentPay = pay;//new PayBean().getPay(new GeneralUserSetting().getCurrentPayId());
            } else {
                //System.out.println("Pe");
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
            //System.out.println("C");
            this.CurrentPointscard = new PointsCardBean().getPointsCardByCardNumber(this.CurrentTransaction.getCardNumber());
        } catch (Exception e) {
            //System.out.println("Ce");
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
            //System.out.println("TC");
            this.CurrentTransactor = new TransactorBean().getTransactor(this.CurrentTransaction.getTransactorId());
        } catch (Exception e) {
            //System.out.println("TCe");
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
            this.CurrentTransactionItems = new TransItemBean().getTransItemsByTransactionId(new GeneralUserSetting().getCurrentTransactionId());
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
     * @return the CurrentBillTransactor
     */
    public Transactor getCurrentBillTransactor() {
        try {
            this.CurrentBillTransactor = new TransactorBean().getTransactor(this.CurrentTransaction.getBillTransactorId());
        } catch (Exception e) {
            this.CurrentBillTransactor = null;
        }
        return CurrentBillTransactor;
    }

    /**
     * @param CurrentBillTransactor the CurrentBillTransactor to set
     */
    public void setCurrentBillTransactor(Transactor CurrentBillTransactor) {
        this.CurrentBillTransactor = CurrentBillTransactor;
    }

    /**
     * @return the CurrentTransUserDetail
     */
    public UserDetail getCurrentTransUserDetail() {
        try {
            this.CurrentTransUserDetail = new UserDetailBean().getUserDetail(this.CurrentTransaction.getTransactionUserDetailId());
        } catch (Exception e) {
            this.CurrentTransUserDetail = null;
        }
        return CurrentTransUserDetail;
    }

    /**
     * @param CurrentTransUserDetail the CurrentTransUserDetail to set
     */
    public void setCurrentTransUserDetail(UserDetail CurrentTransUserDetail) {
        this.CurrentTransUserDetail = CurrentTransUserDetail;
    }
    
}
