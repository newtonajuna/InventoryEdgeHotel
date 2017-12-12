
import java.io.Serializable;
import java.sql.*;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class CompanySettingBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ActionMessage;

    
    public void saveCompanySetting(CompanySetting company) {
        String sql = null;
        String msg=null;
        
        UserDetail aCurrentUserDetail=new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights=new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb=new GroupRightBean();
        
        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Add")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail,aCurrentGroupRights,"SETTING", "Edit")==0) {
            msg="YOU ARE NOT ALLOWED TO USE THIS FUNCTION, CONTACT SYSTEM ADMINISTRATOR...";
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(msg));
        }else{
            sql = "{call sp_update_company_setting(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        
        try (
                Connection conn = DBConnection.getMySQLConnection();
                CallableStatement cs = conn.prepareCall(sql);) {
                cs.setString("in_ECompanyName",new CompanySetting().getLICENSE_CLIENT_NAME());
                cs.setString("in_EPhysicalAddress",company.getEPhysicalAddress());
                cs.setString("in_EPhone",company.getEPhone());
                cs.setString("in_EFax",company.getEFax());
                cs.setString("in_EEmail",company.getEEmail());
                cs.setString("in_EWebsite",company.getEWebsite());
                cs.setString("in_ELogoUrl",company.getELogoUrl());
                cs.setString("in_ESloghan",company.getESloghan());
                cs.setString("in_ECurrencyUsed",company.getECurrencyUsed());
                cs.setFloat("in_EVatPerc",company.getEVatPerc());
                cs.setString("in_EIsAllowDiscount",company.getEIsAllowDiscount());
                cs.setString("in_EIsAllowDebt",company.getEIsAllowDebt());
                cs.setString("in_EIsCustomerMandatory",company.getEIsCustomerMandatory());
                cs.setString("in_EIsSupplierMandatory",company.getEIsSupplierMandatory());
                cs.setString("in_EIsVatInclusive",company.getEIsVatInclusive());
                cs.setString("in_EIsTradeDiscountVatLiable",company.getEIsTradeDiscountVatLiable());
                cs.setString("in_EIsCashDiscountVatLiable",company.getEIsCashDiscountVatLiable());
                cs.setString("in_EIsMapItemsActive",company.getEIsMapItemsActive());
                cs.setString("in_EBranchCode",company.getEBranchCode());
                cs.setInt("in_EBranchId",company.getEBranchId());
                cs.setFloat("in_EAwardAmountPerPoint",company.getEAwardAmountPerPoint());
                cs.setFloat("in_ESpendAmountPerPoint",company.getESpendAmountPerPoint());
                cs.setString("in_ETaxIdentity",company.getETaxIdentity());
                cs.setString("in_ESalesReceiptName", company.getESalesReceiptName());
                cs.setString("in_EIsShowDeveloper", company.getEIsShowDeveloper());
                cs.setString("in_EDeveloperEmail", company.getEDeveloperEmail());
                cs.setString("in_EDeveloperPhone", company.getEDeveloperPhone());
                cs.setString("in_EShowLogoInvoice", company.getEShowLogoInvoice());
                cs.setString("in_EShowBranchInvoice", company.getEShowBranchInvoice());
                cs.setString("in_EShowStoreInvoice", company.getEShowStoreInvoice());
                cs.setString("in_EIsAllowAutoUnpack", company.getEIsAllowAutoUnpack());
                cs.setString("in_ETimeZone", company.getETimeZone());
                cs.setString("in_EDateFormat", company.getEDateFormat());
                cs.setString("in_ELicenseKey", company.getELicenseKey());
                cs.setInt("in_ESalesReceiptVersion", company.getESalesReceiptVersion());
                cs.setString("in_EEnforceTransUserSelect", company.getEEnforceTransUserSelect());
                cs.setString("in_EShowVatAnalysisInvoice", company.getEShowVatAnalysisInvoice());
                cs.setString("in_EStoreEquivName", company.getEStoreEquivName());
                cs.executeUpdate();
                this.setActionMessage("Updated Successfully"); 
                CompanySetting.RefreshStaticCompanySettings();
        }catch ( SQLException | NullPointerException se) {
            System.err.println(se.getMessage());
            this.setActionMessage("CompanySetting NOT updated");
        }
     }  
    } 
        /**
     * @return the ActionMessage
     */
    public String getActionMessage() {
        return ActionMessage;
    }

    /**
     * @param aActionMessage the ActionMessage to set
     */
    public void setActionMessage(String aActionMessage) {
        ActionMessage = aActionMessage;
    }
    
}
