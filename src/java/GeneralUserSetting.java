/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class GeneralUserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean ReportRetrievalOption;

    /**
     * Creates a new instance of GeneralUserSetting
     */
    public GeneralUserSetting() {
    }

    public void setSessionCurrentPayId(long aPayId) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_PAY_ID", aPayId);
    }

    public void setSessionCurrentTransId(long aTransId) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("CURRENT_TRANSACTION_ID", aTransId);
    }

    public UserDetail getCurrentUser() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            return (UserDetail) httpSession.getAttribute("CURRENT_USER");
        } catch (NullPointerException | ClassCastException npe) {
            return null;
        }
    }

    public String getCurrentUserNames() {
        try {
            UserDetail ud = new UserDetail();
            ud = this.getCurrentUser();
            return ud.getFirstName() + " " + ud.getSecondName() + " " + ud.getThirdName();
        } catch (NullPointerException | ClassCastException npe) {
            return "";
        }
    }

    public Store getCurrentStore() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            return (Store) httpSession.getAttribute("CURRENT_STORE");
        } catch (NullPointerException | ClassCastException npe) {
            return null;
        }
    }

    public int getCurrentStore2Id() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("CURRENT_STORE2_ID");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public List<GroupRight> getCurrentGroupRights() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            return (List<GroupRight>) httpSession.getAttribute("CURRENT_GROUP_RIGHTS");
        } catch (NullPointerException | ClassCastException npe) {
            return null;
        }
    }

    public int getIsApprovePointsNeeded() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("IS_APPROVE_POINTS_NEEDED");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public int getIsApproveDiscountNeeded() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("IS_APPROVE_DISCOUNT_NEEDED");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public int getCurrentTransactionTypeId() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("TRANSACTION_TYPE_ID");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public int getCurrentTransactionReasonId() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("TRANSACTION_REASON_ID");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public String getCurrentTransactionTypeName() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTION_TYPE_NAME");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentTransactorType() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTOR_TYPE");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }
    public String getCurrentGuestFolioType() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTOR_TYPE");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }
    
    public String getCurrentInvokeMode() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("INVOKE_MODE");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentTransactorLabel() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTOR_LABEL");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentTransactionNumberLabel() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTION_NUMBER_LABEL");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentTransactionOutputLabel() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("TRANSACTION_OUTPUT_LABEL");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentPayCategory() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("PAY_CATEGORY");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentSaleType() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("SALE_TYPE");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public int getCurrentApproveUserId() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("APPROVE_USER_ID");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public String getCurrentApproveDiscountStatus() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("APPROVE_DISCOUNT_STATUS");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public String getCurrentApprovePointsStatus() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("APPROVE_POINTS_STATUS");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }
    
    public String getCurrentPrintoutJsfFile() {
        String StrResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            StrResponse = (String) httpSession.getAttribute("CURRENT_PRINT_OUT_JSF_FILE");
        } catch (NullPointerException npe) {
            StrResponse = "";
        }
        return StrResponse;
    }

    public int getChangePasswordAllowed() {
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("CHANGE_PASSWORD_ALLOWED");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public long getCurrentTransactionId() {
        long LongResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            LongResponse = (long) httpSession.getAttribute("CURRENT_TRANSACTION_ID");
        } catch (NullPointerException | ClassCastException npe) {
            LongResponse = 0;
        }
        return LongResponse;
    }

    public long getCurrentPayId() {
        long LongResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            LongResponse = (long) httpSession.getAttribute("CURRENT_PAY_ID");
        } catch (NullPointerException | ClassCastException npe) {
            LongResponse = 0;
        }
        return LongResponse;
    }

    public long getCurrentItemId() {
        long LongResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            LongResponse = (long) httpSession.getAttribute("ITEM_ID");
        } catch (NullPointerException | ClassCastException npe) {
            LongResponse = 0;
        }
        return LongResponse;
    }

    /**
     * @return the ReportRetrievalOption
     */
    public boolean isReportRetrievalOption() {
        return ReportRetrievalOption;
    }

    /**
     * @param ReportRetrievalOption the ReportRetrievalOption to set
     */
    public void setReportRetrievalOption(boolean ReportRetrievalOption) {
        this.ReportRetrievalOption = ReportRetrievalOption;
    }

    public void initReportRetrievalOption(boolean ReportRetrievalOption) {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            // Skip ajax requests.
        } else {
            this.ReportRetrievalOption = ReportRetrievalOption;
        }
    }

    public int getLoginType() {//1=Branch, 2=InterBranch
        int IntResponse;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            HttpSession httpSession = request.getSession(false);
            IntResponse = (int) httpSession.getAttribute("LOGIN_TYPE");
        } catch (NullPointerException npe) {
            IntResponse = 0;
        }
        return IntResponse;
    }

    public long getDaysFromDateToLicenseExpiryDate(Date aFromDate) {
        Date d1 = null;
        Date d2 = null;
        long diffDays = 0;
        try {
            d2 = aFromDate;
            d1 = CompanySetting.getLICENSE_EXPIRY_DATE();
            //in milliseconds
            long diff = d1.getTime() - d2.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
            diffDays = diffDays + 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return diffDays;
    }

}
