import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
@ManagedBean
@SessionScoped
public class CompanySetting implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static int CompanySettingId;
    
    //license information
    private static String LICENSE_KEY;
    private static String LICENSE_EXPIRY_DAY;
    private static String LICENSE_EXPIRY_MONTH;
    private static String LICENSE_EXPIRY_YEAR;
    private static String LICENSE_CLIENT_ID;
    private static String LICENSE_CLIENT_NAME;
    private static Date LICENSE_EXPIRY_DATE;
    private static Date CURRENT_SERVER_DATE;
    private static String PACKAGE_NAME;
    
    private static String LicenseKey;
    private static String CompanyName;
    private static String PhysicalAddress;
    private static String Phone;
    private static String Fax;
    private static String Email;
    private static String Website;
    private static String LogoUrl;
    private static String Sloghan;
    private static String CurrencyUsed;
    private static float VatPerc;
    private static String IsAllowDiscount;
    private static String IsAllowDebt;
    private static String IsCustomerMandatory;
    private static String IsSupplierMandatory;
    private static String IsVatInclusive;
    private static String IsTradeDiscountVatLiable;
    private static String IsCashDiscountVatLiable;
    private static String IsMapItemsActive;
    private static String BranchCode;
    private static int BranchId;
    private static float AwardAmountPerPoint;
    private static float SpendAmountPerPoint;
    private static String TaxIdentity;
    private static String SalesReceiptName;
    private static String IsShowDeveloper;
    private static String DeveloperEmail;
    private static String DeveloperPhone;
    private static String ShowLogoInvoice;
    private static String IsAllowAutoUnpack;
    private static String TimeZone;
    private static String DateFormat;
    private static String EnforceTransUserSelect;
    private static int SalesReceiptVersion;
    private static String ShowBranchInvoice;
    private static String ShowStoreInvoice;
    private static String ShowVatAnalysisInvoice;
    private static String StoreEquivName;

    /**
     * @return the TimeZone
     */
    public static String getTimeZone() {
        return TimeZone;
    }

    /**
     * @param aTimeZone the TimeZone to set
     */
    public static void setTimeZone(String aTimeZone) {
        TimeZone = aTimeZone;
    }

    /**
     * @return the DateFormat
     */
    public static String getDateFormat() {
        return DateFormat;
    }

    /**
     * @param aDateFormat the DateFormat to set
     */
    public static void setDateFormat(String aDateFormat) {
        DateFormat = aDateFormat;
    }

    /**
     * @return the LicenseKey
     */
    public static String getLicenseKey() {
        return LicenseKey;
    }

    /**
     * @param aLicenseKey the LicenseKey to set
     */
    public static void setLicenseKey(String aLicenseKey) {
        LicenseKey = aLicenseKey;
    }

    /**
     * @return the EnforceTransUserSelect
     */
    public static String getEnforceTransUserSelect() {
        return EnforceTransUserSelect;
    }

    /**
     * @param aEnforceTransUserSelect the EnforceTransUserSelect to set
     */
    public static void setEnforceTransUserSelect(String aEnforceTransUserSelect) {
        EnforceTransUserSelect = aEnforceTransUserSelect;
    }

    /**
     * @return the SalesReceiptVersion
     */
    public static int getSalesReceiptVersion() {
        return SalesReceiptVersion;
    }

    /**
     * @param aSalesReceiptVersion the SalesReceiptVersion to set
     */
    public static void setSalesReceiptVersion(int aSalesReceiptVersion) {
        SalesReceiptVersion = aSalesReceiptVersion;
    }

    /**
     * @return the ShowBranchInvoice
     */
    public static String getShowBranchInvoice() {
        return ShowBranchInvoice;
    }

    /**
     * @param aShowBranchInvoice the ShowBranchInvoice to set
     */
    public static void setShowBranchInvoice(String aShowBranchInvoice) {
        ShowBranchInvoice = aShowBranchInvoice;
    }

    /**
     * @return the ShowStoreInvoice
     */
    public static String getShowStoreInvoice() {
        return ShowStoreInvoice;
    }

    /**
     * @param aShowStoreInvoice the ShowStoreInvoice to set
     */
    public static void setShowStoreInvoice(String aShowStoreInvoice) {
        ShowStoreInvoice = aShowStoreInvoice;
    }

    /**
     * @return the ShowVatAnalysisInvoice
     */
    public static String getShowVatAnalysisInvoice() {
        return ShowVatAnalysisInvoice;
    }

    /**
     * @param aShowVatAnalysisInvoice the ShowVatAnalysisInvoice to set
     */
    public static void setShowVatAnalysisInvoice(String aShowVatAnalysisInvoice) {
        ShowVatAnalysisInvoice = aShowVatAnalysisInvoice;
    }

    /**
     * @return the StoreEquivName
     */
    public static String getStoreEquivName() {
        return StoreEquivName;
    }

    /**
     * @param aStoreEquivName the StoreEquivName to set
     */
    public static void setStoreEquivName(String aStoreEquivName) {
        StoreEquivName = aStoreEquivName;
    }
    
    private String ELicenseKey;
    private int ECompanySettingId;
    private String ECompanyName;
    private String EPhysicalAddress;
    private String EPhone;
    private String EFax;
    private String EEmail;
    private String EWebsite;
    private String ELogoUrl;
    private String ESloghan;
    private String ECurrencyUsed;
    private float EVatPerc;
    private String EIsAllowDiscount;
    private String EIsAllowDebt;
    private String EIsCustomerMandatory;
    private String EIsSupplierMandatory;
    private String EIsVatInclusive;
    private String EIsTradeDiscountVatLiable;
    private String EIsCashDiscountVatLiable;
    private String EIsMapItemsActive;
    private String EBranchCode;
    private int EBranchId;
    private float EAwardAmountPerPoint;
    private float ESpendAmountPerPoint;
    private String ETaxIdentity;
    private String ESalesReceiptName;
    private String EIsShowDeveloper;
    private String EDeveloperEmail;
    private String EDeveloperPhone;
    private String EShowLogoInvoice;
    private String EIsAllowAutoUnpack;
    private String ETimeZone;
    private String EDateFormat;
    private String EEnforceTransUserSelect;
    private int ESalesReceiptVersion;
    private String EShowBranchInvoice;
    private String EShowStoreInvoice;
    private String EShowVatAnalysisInvoice;
    private String EStoreEquivName;
    
    static {
        String sql = "call sp_search_company_setting_by_id(?)";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                CompanySetting.setCompanySettingId(rs.getInt("company_setting_id"));
                CompanySetting.setCompanyName(rs.getString("company_name"));
                CompanySetting.setPhysicalAddress(rs.getString("physical_address"));
                CompanySetting.setPhone(rs.getString("phone"));
                CompanySetting.setFax(rs.getString("fax"));
                CompanySetting.setEmail(rs.getString("email"));
                CompanySetting.setWebsite(rs.getString("website"));
                CompanySetting.setLogoUrl(rs.getString("logo_url"));
                CompanySetting.setSloghan(rs.getString("sloghan"));
                CompanySetting.setCurrencyUsed(rs.getString("currency_used"));
                CompanySetting.setVatPerc(rs.getFloat("vat_perc"));
                CompanySetting.setIsAllowDiscount(rs.getString("is_allow_discount"));
                CompanySetting.setIsAllowDebt(rs.getString("is_allow_debt"));
                CompanySetting.setIsCustomerMandatory(rs.getString("is_customer_mandatory"));
                CompanySetting.setIsSupplierMandatory(rs.getString("is_supplier_mandatory"));
                CompanySetting.setIsVatInclusive(rs.getString("is_vat_inclusive"));
                CompanySetting.setIsTradeDiscountVatLiable(rs.getString("is_trade_discount_vat_liable"));
                CompanySetting.setIsCashDiscountVatLiable(rs.getString("is_cash_discount_vat_liable"));
                CompanySetting.setIsMapItemsActive(rs.getString("is_map_items_active"));
                CompanySetting.setBranchCode(rs.getString("branch_code"));
                CompanySetting.setBranchId(rs.getInt("branch_id"));
                CompanySetting.setAwardAmountPerPoint(rs.getFloat("award_amount_per_point"));
                CompanySetting.setSpendAmountPerPoint(rs.getFloat("spend_amount_per_point"));
                CompanySetting.setTaxIdentity(rs.getString("tax_identity"));
                CompanySetting.setSalesReceiptName(rs.getString("sales_receipt_name"));
                CompanySetting.setIsShowDeveloper(rs.getString("is_show_developer"));
                CompanySetting.setDeveloperEmail(rs.getString("developer_email"));
                CompanySetting.setDeveloperPhone(rs.getString("developer_phone"));
                CompanySetting.setShowLogoInvoice(rs.getString("show_logo_invoice"));
                CompanySetting.setShowBranchInvoice(rs.getString("show_branch_invoice"));
                CompanySetting.setShowStoreInvoice(rs.getString("show_store_invoice"));
                CompanySetting.setIsAllowAutoUnpack(rs.getString("is_allow_auto_unpack"));
                CompanySetting.setTimeZone(rs.getString("time_zone"));
                CompanySetting.setDateFormat(rs.getString("date_format"));
                CompanySetting.setDateFormat(rs.getString("license_key"));
                CompanySetting.setSalesReceiptVersion(rs.getInt("sales_receipt_version"));
                CompanySetting.setEnforceTransUserSelect(rs.getString("enforce_trans_user_select"));
                CompanySetting.setShowVatAnalysisInvoice(rs.getString("show_vat_analysis_invoice"));
                CompanySetting.setStoreEquivName(rs.getString("store_equiv_name"));
            } else {

            }
        } catch (SQLException | NullPointerException se) {
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
    }
    
    {
        String sql = "call sp_search_company_setting_by_id(?)";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                this.ECompanySettingId=rs.getInt("company_setting_id");
                this.ECompanyName=rs.getString("company_name");
                this.EPhysicalAddress=rs.getString("physical_address");
                this.EPhone=rs.getString("phone");
                this.EFax=rs.getString("fax");
                this.EEmail=rs.getString("email");
                this.EWebsite=rs.getString("website");
                this.ELogoUrl=rs.getString("logo_url");
                this.ESloghan=rs.getString("sloghan");
                this.ECurrencyUsed=rs.getString("currency_used");
                this.EVatPerc=rs.getFloat("vat_perc");
                this.EIsAllowDiscount=rs.getString("is_allow_discount");
                this.EIsAllowDebt=rs.getString("is_allow_debt");
                this.EIsCustomerMandatory=rs.getString("is_customer_mandatory");
                this.EIsSupplierMandatory=rs.getString("is_supplier_mandatory");
                this.EIsVatInclusive=rs.getString("is_vat_inclusive");
                this.EIsTradeDiscountVatLiable=rs.getString("is_trade_discount_vat_liable");
                this.EIsCashDiscountVatLiable=rs.getString("is_cash_discount_vat_liable");
                this.EIsMapItemsActive=rs.getString("is_map_items_active");
                this.EBranchCode=rs.getString("branch_code");
                this.EBranchId=rs.getInt("branch_id");
                this.EAwardAmountPerPoint=rs.getFloat("award_amount_per_point");
                this.ESpendAmountPerPoint=rs.getFloat("spend_amount_per_point");
                this.ETaxIdentity=rs.getString("tax_identity");
                this.ESalesReceiptName=rs.getString("sales_receipt_name");
                this.EIsShowDeveloper=rs.getString("is_show_developer");
                this.EDeveloperEmail=rs.getString("developer_email");
                this.EDeveloperPhone=rs.getString("developer_phone");
                this.EShowLogoInvoice=rs.getString("show_logo_invoice");
                this.EShowBranchInvoice=rs.getString("show_branch_invoice");
                this.EShowStoreInvoice=rs.getString("show_store_invoice");
                this.EIsAllowAutoUnpack=rs.getString("is_allow_auto_unpack");
                this.setETimeZone(rs.getString("time_zone"));
                this.setEDateFormat(rs.getString("date_format"));
                this.setELicenseKey(rs.getString("license_key"));
                this.setESalesReceiptVersion(rs.getInt("sales_receipt_version"));
                this.setEEnforceTransUserSelect(rs.getString("enforce_trans_user_select"));
                this.setEShowVatAnalysisInvoice(rs.getString("show_vat_analysis_invoice"));
                this.setEStoreEquivName(rs.getString("store_equiv_name"));
            } else {

            }
        } catch (SQLException | NullPointerException se) {
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
    }
    
    public static void RefreshStaticCompanySettings(){
        String sql = "call sp_search_company_setting_by_id(?)";
        ResultSet rs = null;
        try (
                Connection conn = DBConnection.getMySQLConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                CompanySetting.setCompanySettingId(rs.getInt("company_setting_id"));
                CompanySetting.setCompanyName(rs.getString("company_name"));
                CompanySetting.setPhysicalAddress(rs.getString("physical_address"));
                CompanySetting.setPhone(rs.getString("phone"));
                CompanySetting.setFax(rs.getString("fax"));
                CompanySetting.setEmail(rs.getString("email"));
                CompanySetting.setWebsite(rs.getString("website"));
                CompanySetting.setLogoUrl(rs.getString("logo_url"));
                CompanySetting.setSloghan(rs.getString("sloghan"));
                CompanySetting.setCurrencyUsed(rs.getString("currency_used"));
                CompanySetting.setVatPerc(rs.getFloat("vat_perc"));
                CompanySetting.setIsAllowDiscount(rs.getString("is_allow_discount"));
                CompanySetting.setIsAllowDebt(rs.getString("is_allow_debt"));
                CompanySetting.setIsCustomerMandatory(rs.getString("is_customer_mandatory"));
                CompanySetting.setIsSupplierMandatory(rs.getString("is_supplier_mandatory"));
                CompanySetting.setIsVatInclusive(rs.getString("is_vat_inclusive"));
                CompanySetting.setIsTradeDiscountVatLiable(rs.getString("is_trade_discount_vat_liable"));
                CompanySetting.setIsCashDiscountVatLiable(rs.getString("is_cash_discount_vat_liable"));
                CompanySetting.setIsMapItemsActive(rs.getString("is_map_items_active"));
                CompanySetting.setBranchCode(rs.getString("branch_code"));
                CompanySetting.setBranchId(rs.getInt("branch_id"));
                CompanySetting.setAwardAmountPerPoint(rs.getFloat("award_amount_per_point"));
                CompanySetting.setSpendAmountPerPoint(rs.getFloat("spend_amount_per_point"));
                CompanySetting.setTaxIdentity(rs.getString("tax_identity"));
                CompanySetting.setSalesReceiptName(rs.getString("sales_receipt_name"));
                CompanySetting.setIsShowDeveloper(rs.getString("is_show_developer"));
                CompanySetting.setDeveloperEmail(rs.getString("developer_email"));
                CompanySetting.setDeveloperPhone(rs.getString("developer_phone"));
                CompanySetting.setShowLogoInvoice(rs.getString("show_logo_invoice"));
                CompanySetting.setShowBranchInvoice(rs.getString("show_branch_invoice"));
                CompanySetting.setShowStoreInvoice(rs.getString("show_store_invoice"));
                CompanySetting.setIsAllowAutoUnpack(rs.getString("is_allow_auto_unpack"));
                CompanySetting.setTimeZone(rs.getString("time_zone"));
                CompanySetting.setDateFormat(rs.getString("date_format"));
                CompanySetting.setLicenseKey(rs.getString("license_key"));
                CompanySetting.setSalesReceiptVersion(rs.getInt("sales_receipt_version"));
                CompanySetting.setEnforceTransUserSelect(rs.getString("enforce_trans_user_select"));
                CompanySetting.setShowVatAnalysisInvoice(rs.getString("show_vat_analysis_invoice"));
                CompanySetting.setStoreEquivName(rs.getString("store_equiv_name"));
            } else {

            }
        } catch (SQLException | NullPointerException se) {
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

    }
    
    
    /**
     * @return the IsAllowAutoUnpack
     */
    public static String getIsAllowAutoUnpack() {
        return IsAllowAutoUnpack;
    }

    /**
     * @param aIsAllowAutoUnpack the IsAllowAutoUnpack to set
     */
    public static void setIsAllowAutoUnpack(String aIsAllowAutoUnpack) {
        IsAllowAutoUnpack = aIsAllowAutoUnpack;
    }
    
    /**
     * @return the ShowLogoInvoice
     */
    public static String getShowLogoInvoice() {
        return ShowLogoInvoice;
    }

    /**
     * @param aShowLogoInvoice the ShowLogoInvoice to set
     */
    public static void setShowLogoInvoice(String aShowLogoInvoice) {
        ShowLogoInvoice = aShowLogoInvoice;
    }

    /**
     * @return the SalesReceiptName
     */
    public static String getSalesReceiptName() {
        return SalesReceiptName;
    }

    /**
     * @param aSalesReceiptName the SalesReceiptName to set
     */
    public static void setSalesReceiptName(String aSalesReceiptName) {
        SalesReceiptName = aSalesReceiptName;
    }

    /**
     * @return the IsShowDeveloper
     */
    public static String getIsShowDeveloper() {
        return IsShowDeveloper;
    }

    /**
     * @param aIsShowDeveloper the IsShowDeveloper to set
     */
    public static void setIsShowDeveloper(String aIsShowDeveloper) {
        IsShowDeveloper = aIsShowDeveloper;
    }

    /**
     * @return the DeveloperEmail
     */
    public static String getDeveloperEmail() {
        return DeveloperEmail;
    }

    /**
     * @param aDeveloperEmail the DeveloperEmail to set
     */
    public static void setDeveloperEmail(String aDeveloperEmail) {
        DeveloperEmail = aDeveloperEmail;
    }

    /**
     * @return the DeveloperPhone
     */
    public static String getDeveloperPhone() {
        return DeveloperPhone;
    }

    /**
     * @param aDeveloperPhone the DeveloperPhone to set
     */
    public static void setDeveloperPhone(String aDeveloperPhone) {
        DeveloperPhone = aDeveloperPhone;
    }
    
    /**
     * @return the CompanySettingId
     */
    public static int getCompanySettingId() {
        return CompanySettingId;
    }

    /**
     * @param aCompanySettingId the CompanySettingId to set
     */
    public static void setCompanySettingId(int aCompanySettingId) {
        CompanySettingId = aCompanySettingId;
    }

    /**
     * @return the CompanyName
     */
    public static String getCompanyName() {
        return CompanyName;
    }

    /**
     * @param aCompanyName the CompanyName to set
     */
    public static void setCompanyName(String aCompanyName) {
        CompanyName = aCompanyName;
    }

    /**
     * @return the PhysicalAddress
     */
    public static String getPhysicalAddress() {
        return PhysicalAddress;
    }

    /**
     * @param aPhysicalAddress the PhysicalAddress to set
     */
    public static void setPhysicalAddress(String aPhysicalAddress) {
        PhysicalAddress = aPhysicalAddress;
    }

    /**
     * @return the Phone
     */
    public static String getPhone() {
        return Phone;
    }

    /**
     * @param aPhone the Phone to set
     */
    public static void setPhone(String aPhone) {
        Phone = aPhone;
    }

    /**
     * @return the Fax
     */
    public static String getFax() {
        return Fax;
    }

    /**
     * @param aFax the Fax to set
     */
    public static void setFax(String aFax) {
        Fax = aFax;
    }

    /**
     * @return the Email
     */
    public static String getEmail() {
        return Email;
    }

    /**
     * @param aEmail the Email to set
     */
    public static void setEmail(String aEmail) {
        Email = aEmail;
    }

    /**
     * @return the Website
     */
    public static String getWebsite() {
        return Website;
    }

    /**
     * @param aWebsite the Website to set
     */
    public static void setWebsite(String aWebsite) {
        Website = aWebsite;
    }

    /**
     * @return the LogoUrl
     */
    public static String getLogoUrl() {
        return LogoUrl;
    }

    /**
     * @param aLogoUrl the LogoUrl to set
     */
    public static void setLogoUrl(String aLogoUrl) {
        LogoUrl = aLogoUrl;
    }

    /**
     * @return the Sloghan
     */
    public static String getSloghan() {
        return Sloghan;
    }

    /**
     * @param aSloghan the Sloghan to set
     */
    public static void setSloghan(String aSloghan) {
        Sloghan = aSloghan;
    }

    /**
     * @return the CurrencyUsed
     */
    public static String getCurrencyUsed() {
        return CurrencyUsed;
    }

    /**
     * @param aCurrencyUsed the CurrencyUsed to set
     */
    public static void setCurrencyUsed(String aCurrencyUsed) {
        CurrencyUsed = aCurrencyUsed;
    }

    /**
     * @return the VatPerc
     */
    public static float getVatPerc() {
        return VatPerc;
    }

    /**
     * @param aVatPerc the VatPerc to set
     */
    public static void setVatPerc(float aVatPerc) {
        VatPerc = aVatPerc;
    }

    /**
     * @return the IsAllowDiscount
     */
    public static String getIsAllowDiscount() {
        return IsAllowDiscount;
    }

    /**
     * @param aIsAllowDiscount the IsAllowDiscount to set
     */
    public static void setIsAllowDiscount(String aIsAllowDiscount) {
        IsAllowDiscount = aIsAllowDiscount;
    }

    /**
     * @return the IsAllowDebt
     */
    public static String getIsAllowDebt() {
        return IsAllowDebt;
    }

    /**
     * @param aIsAllowDebt the IsAllowDebt to set
     */
    public static void setIsAllowDebt(String aIsAllowDebt) {
        IsAllowDebt = aIsAllowDebt;
    }

    /**
     * @return the IsCustomerMandatory
     */
    public static String getIsCustomerMandatory() {
        return IsCustomerMandatory;
    }

    /**
     * @param aIsCustomerMandatory the IsCustomerMandatory to set
     */
    public static void setIsCustomerMandatory(String aIsCustomerMandatory) {
        IsCustomerMandatory = aIsCustomerMandatory;
    }

    /**
     * @return the IsSupplierMandatory
     */
    public static String getIsSupplierMandatory() {
        return IsSupplierMandatory;
    }

    /**
     * @param aIsSupplierMandatory the IsSupplierMandatory to set
     */
    public static void setIsSupplierMandatory(String aIsSupplierMandatory) {
        IsSupplierMandatory = aIsSupplierMandatory;
    }

    /**
     * @return the IsVatInclusive
     */
    public static String getIsVatInclusive() {
        return IsVatInclusive;
    }

    /**
     * @param aIsVatInclusive the IsVatInclusive to set
     */
    public static void setIsVatInclusive(String aIsVatInclusive) {
        IsVatInclusive = aIsVatInclusive;
    }

    /**
     * @return the IsTradeDiscountVatLiable
     */
    public static String getIsTradeDiscountVatLiable() {
        return IsTradeDiscountVatLiable;
    }

    /**
     * @param aIsTradeDiscountVatLiable the IsTradeDiscountVatLiable to set
     */
    public static void setIsTradeDiscountVatLiable(String aIsTradeDiscountVatLiable) {
        IsTradeDiscountVatLiable = aIsTradeDiscountVatLiable;
    }

    /**
     * @return the IsCashDiscountVatLiable
     */
    public static String getIsCashDiscountVatLiable() {
        return IsCashDiscountVatLiable;
    }

    /**
     * @param aIsCashDiscountVatLiable the IsCashDiscountVatLiable to set
     */
    public static void setIsCashDiscountVatLiable(String aIsCashDiscountVatLiable) {
        IsCashDiscountVatLiable = aIsCashDiscountVatLiable;
    }

    /**
     * @return the IsMapItemsActive
     */
    public static String getIsMapItemsActive() {
        return IsMapItemsActive;
    }

    /**
     * @param aIsMapItemsActive the IsMapItemsActive to set
     */
    public static void setIsMapItemsActive(String aIsMapItemsActive) {
        IsMapItemsActive = aIsMapItemsActive;
    }

    /**
     * @return the BranchCode
     */
    public static String getBranchCode() {
        return BranchCode;
    }

    /**
     * @param aBranchCode the BranchCode to set
     */
    public static void setBranchCode(String aBranchCode) {
        BranchCode = aBranchCode;
    }

    /**
     * @return the BranchId
     */
    public static int getBranchId() {
        return BranchId;
    }

    /**
     * @param aBranchId the BranchId to set
     */
    public static void setBranchId(int aBranchId) {
        BranchId = aBranchId;
    }

    /**
     * @return the ECompanySettingId
     */
    public int getECompanySettingId() {
        return ECompanySettingId;
    }

    /**
     * @param ECompanySettingId the ECompanySettingId to set
     */
    public void setECompanySettingId(int ECompanySettingId) {
        this.ECompanySettingId = ECompanySettingId;
    }

    /**
     * @return the ECompanyName
     */
    public String getECompanyName() {
        return ECompanyName;
    }

    /**
     * @param ECompanyName the ECompanyName to set
     */
    public void setECompanyName(String ECompanyName) {
        this.ECompanyName = ECompanyName;
    }

    /**
     * @return the EPhysicalAddress
     */
    public String getEPhysicalAddress() {
        return EPhysicalAddress;
    }

    /**
     * @param EPhysicalAddress the EPhysicalAddress to set
     */
    public void setEPhysicalAddress(String EPhysicalAddress) {
        this.EPhysicalAddress = EPhysicalAddress;
    }

    /**
     * @return the EPhone
     */
    public String getEPhone() {
        return EPhone;
    }

    /**
     * @param EPhone the EPhone to set
     */
    public void setEPhone(String EPhone) {
        this.EPhone = EPhone;
    }

    /**
     * @return the EFax
     */
    public String getEFax() {
        return EFax;
    }

    /**
     * @param EFax the EFax to set
     */
    public void setEFax(String EFax) {
        this.EFax = EFax;
    }

    /**
     * @return the EEmail
     */
    public String getEEmail() {
        return EEmail;
    }

    /**
     * @param EEmail the EEmail to set
     */
    public void setEEmail(String EEmail) {
        this.EEmail = EEmail;
    }

    /**
     * @return the EWebsite
     */
    public String getEWebsite() {
        return EWebsite;
    }

    /**
     * @param EWebsite the EWebsite to set
     */
    public void setEWebsite(String EWebsite) {
        this.EWebsite = EWebsite;
    }

    /**
     * @return the ELogoUrl
     */
    public String getELogoUrl() {
        return ELogoUrl;
    }

    /**
     * @param ELogoUrl the ELogoUrl to set
     */
    public void setELogoUrl(String ELogoUrl) {
        this.ELogoUrl = ELogoUrl;
    }

    /**
     * @return the ESloghan
     */
    public String getESloghan() {
        return ESloghan;
    }

    /**
     * @param ESloghan the ESloghan to set
     */
    public void setESloghan(String ESloghan) {
        this.ESloghan = ESloghan;
    }

    /**
     * @return the ECurrencyUsed
     */
    public String getECurrencyUsed() {
        return ECurrencyUsed;
    }

    /**
     * @param ECurrencyUsed the ECurrencyUsed to set
     */
    public void setECurrencyUsed(String ECurrencyUsed) {
        this.ECurrencyUsed = ECurrencyUsed;
    }

    /**
     * @return the EVatPerc
     */
    public float getEVatPerc() {
        return EVatPerc;
    }

    /**
     * @param EVatPerc the EVatPerc to set
     */
    public void setEVatPerc(float EVatPerc) {
        this.EVatPerc = EVatPerc;
    }

    /**
     * @return the EIsAllowDiscount
     */
    public String getEIsAllowDiscount() {
        return EIsAllowDiscount;
    }

    /**
     * @param EIsAllowDiscount the EIsAllowDiscount to set
     */
    public void setEIsAllowDiscount(String EIsAllowDiscount) {
        this.EIsAllowDiscount = EIsAllowDiscount;
    }

    /**
     * @return the EIsAllowDebt
     */
    public String getEIsAllowDebt() {
        return EIsAllowDebt;
    }

    /**
     * @param EIsAllowDebt the EIsAllowDebt to set
     */
    public void setEIsAllowDebt(String EIsAllowDebt) {
        this.EIsAllowDebt = EIsAllowDebt;
    }

    /**
     * @return the EIsCustomerMandatory
     */
    public String getEIsCustomerMandatory() {
        return EIsCustomerMandatory;
    }

    /**
     * @param EIsCustomerMandatory the EIsCustomerMandatory to set
     */
    public void setEIsCustomerMandatory(String EIsCustomerMandatory) {
        this.EIsCustomerMandatory = EIsCustomerMandatory;
    }

    /**
     * @return the EIsSupplierMandatory
     */
    public String getEIsSupplierMandatory() {
        return EIsSupplierMandatory;
    }

    /**
     * @param EIsSupplierMandatory the EIsSupplierMandatory to set
     */
    public void setEIsSupplierMandatory(String EIsSupplierMandatory) {
        this.EIsSupplierMandatory = EIsSupplierMandatory;
    }

    /**
     * @return the EIsVatInclusive
     */
    public String getEIsVatInclusive() {
        return EIsVatInclusive;
    }

    /**
     * @param EIsVatInclusive the EIsVatInclusive to set
     */
    public void setEIsVatInclusive(String EIsVatInclusive) {
        this.EIsVatInclusive = EIsVatInclusive;
    }

    /**
     * @return the EIsTradeDiscountVatLiable
     */
    public String getEIsTradeDiscountVatLiable() {
        return EIsTradeDiscountVatLiable;
    }

    /**
     * @param EIsTradeDiscountVatLiable the EIsTradeDiscountVatLiable to set
     */
    public void setEIsTradeDiscountVatLiable(String EIsTradeDiscountVatLiable) {
        this.EIsTradeDiscountVatLiable = EIsTradeDiscountVatLiable;
    }

    /**
     * @return the EIsCashDiscountVatLiable
     */
    public String getEIsCashDiscountVatLiable() {
        return EIsCashDiscountVatLiable;
    }

    /**
     * @param EIsCashDiscountVatLiable the EIsCashDiscountVatLiable to set
     */
    public void setEIsCashDiscountVatLiable(String EIsCashDiscountVatLiable) {
        this.EIsCashDiscountVatLiable = EIsCashDiscountVatLiable;
    }

    /**
     * @return the EIsMapItemsActive
     */
    public String getEIsMapItemsActive() {
        return EIsMapItemsActive;
    }

    /**
     * @param EIsMapItemsActive the EIsMapItemsActive to set
     */
    public void setEIsMapItemsActive(String EIsMapItemsActive) {
        this.EIsMapItemsActive = EIsMapItemsActive;
    }

    /**
     * @return the EBranchCode
     */
    public String getEBranchCode() {
        return EBranchCode;
    }

    /**
     * @param EBranchCode the EBranchCode to set
     */
    public void setEBranchCode(String EBranchCode) {
        this.EBranchCode = EBranchCode;
    }

    /**
     * @return the EBranchId
     */
    public int getEBranchId() {
        return EBranchId;
    }

    /**
     * @param EBranchId the EBranchId to set
     */
    public void setEBranchId(int EBranchId) {
        this.EBranchId = EBranchId;
    }

    /**
     * @return the EAwardAmountPerPoint
     */
    public float getEAwardAmountPerPoint() {
        return EAwardAmountPerPoint;
    }

    /**
     * @param EAwardAmountPerPoint the EAwardAmountPerPoint to set
     */
    public void setEAwardAmountPerPoint(float EAwardAmountPerPoint) {
        this.EAwardAmountPerPoint = EAwardAmountPerPoint;
    }

    /**
     * @return the ESpendAmountPerPoint
     */
    public float getESpendAmountPerPoint() {
        return ESpendAmountPerPoint;
    }

    /**
     * @param ESpendAmountPerPoint the ESpendAmountPerPoint to set
     */
    public void setESpendAmountPerPoint(float ESpendAmountPerPoint) {
        this.ESpendAmountPerPoint = ESpendAmountPerPoint;
    }
    
        /**
     * @return the AwardAmountPerPoint
     */
    public static float getAwardAmountPerPoint() {
        return AwardAmountPerPoint;
    }

    /**
     * @param aAwardAmountPerPoint the AwardAmountPerPoint to set
     */
    public static void setAwardAmountPerPoint(float aAwardAmountPerPoint) {
        AwardAmountPerPoint = aAwardAmountPerPoint;
    }

    /**
     * @return the SpendAmountPerPoint
     */
    public static float getSpendAmountPerPoint() {
        return SpendAmountPerPoint;
    }

    /**
     * @param aSpendAmountPerPoint the SpendAmountPerPoint to set
     */
    public static void setSpendAmountPerPoint(float aSpendAmountPerPoint) {
        SpendAmountPerPoint = aSpendAmountPerPoint;
    }

    /**
     * @return the ETaxIdentity
     */
    public String getETaxIdentity() {
        return ETaxIdentity;
    }

    /**
     * @param ETaxIdentity the ETaxIdentity to set
     */
    public void setETaxIdentity(String ETaxIdentity) {
        this.ETaxIdentity = ETaxIdentity;
    }
    
    
    /**
     * @return the TaxIdentity
     */
    public static String getTaxIdentity() {
        return TaxIdentity;
    }

    /**
     * @param aTaxIdentity the TaxIdentity to set
     */
    public static void setTaxIdentity(String aTaxIdentity) {
        TaxIdentity = aTaxIdentity;
    }
    
    public boolean getBoolIsAllowDiscount(){
        if("Yes".equals(CompanySetting.IsAllowDiscount)){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @return the ESalesReceiptName
     */
    public String getESalesReceiptName() {
        return ESalesReceiptName;
    }

    /**
     * @param ESalesReceiptName the ESalesReceiptName to set
     */
    public void setESalesReceiptName(String ESalesReceiptName) {
        this.ESalesReceiptName = ESalesReceiptName;
    }

    /**
     * @return the EIsShowDeveloper
     */
    public String getEIsShowDeveloper() {
        return EIsShowDeveloper;
    }

    /**
     * @param EIsShowDeveloper the EIsShowDeveloper to set
     */
    public void setEIsShowDeveloper(String EIsShowDeveloper) {
        this.EIsShowDeveloper = EIsShowDeveloper;
    }

    /**
     * @return the EDeveloperEmail
     */
    public String getEDeveloperEmail() {
        return EDeveloperEmail;
    }

    /**
     * @param EDeveloperEmail the EDeveloperEmail to set
     */
    public void setEDeveloperEmail(String EDeveloperEmail) {
        this.EDeveloperEmail = EDeveloperEmail;
    }

    /**
     * @return the EDeveloperPhone
     */
    public String getEDeveloperPhone() {
        return EDeveloperPhone;
    }

    /**
     * @param EDeveloperPhone the EDeveloperPhone to set
     */
    public void setEDeveloperPhone(String EDeveloperPhone) {
        this.EDeveloperPhone = EDeveloperPhone;
    }

    /**
     * @return the EShowLogoInvoice
     */
    public String getEShowLogoInvoice() {
        return EShowLogoInvoice;
    }

    /**
     * @param EShowLogoInvoice the EShowLogoInvoice to set
     */
    public void setEShowLogoInvoice(String EShowLogoInvoice) {
        this.EShowLogoInvoice = EShowLogoInvoice;
    }
    
       /**
     * @return the LICENSE_KEY
     */
    public static String getLICENSE_KEY() {
        LICENSE_KEY=Security.Decrypt(LicenseKey);
        return LICENSE_KEY;
    }

    /**
     * @param aLICENSE_KEY the LICENSE_KEY to set
     */
    public static void setLICENSE_KEY(String aLICENSE_KEY) {
        LICENSE_KEY = aLICENSE_KEY;
    }

    /**
     * @return the LICENSE_EXPIRY_DAY
     */
    public static String getLICENSE_EXPIRY_DAY() {
        CompanySetting.LICENSE_EXPIRY_DAY=CompanySetting.getLICENSE_KEY().substring(0, 2);
        return LICENSE_EXPIRY_DAY;
    }

    /**
     * @param aLICENSE_EXPIRY_DAY the LICENSE_EXPIRY_DAY to set
     */
    public static void setLICENSE_EXPIRY_DAY(String aLICENSE_EXPIRY_DAY) {
        LICENSE_EXPIRY_DAY = aLICENSE_EXPIRY_DAY;
    }

    /**
     * @return the LICENSE_EXPIRY_MONTH
     */
    public static String getLICENSE_EXPIRY_MONTH() {
        CompanySetting.LICENSE_EXPIRY_MONTH=CompanySetting.getLICENSE_KEY().substring(2, 4);
        return LICENSE_EXPIRY_MONTH;
    }

    /**
     * @param aLICENSE_EXPIRY_MONTH the LICENSE_EXPIRY_MONTH to set
     */
    public static void setLICENSE_EXPIRY_MONTH(String aLICENSE_EXPIRY_MONTH) {
        LICENSE_EXPIRY_MONTH = aLICENSE_EXPIRY_MONTH;
    }

    /**
     * @return the LICENSE_EXPIRY_YEAR
     */
    public static String getLICENSE_EXPIRY_YEAR() {
        CompanySetting.LICENSE_EXPIRY_YEAR=CompanySetting.getLICENSE_KEY().substring(4, 8);
        return LICENSE_EXPIRY_YEAR;
    }

    /**
     * @param aLICENSE_EXPIRY_YEAR the LICENSE_EXPIRY_YEAR to set
     */
    public static void setLICENSE_EXPIRY_YEAR(String aLICENSE_EXPIRY_YEAR) {
        LICENSE_EXPIRY_YEAR = aLICENSE_EXPIRY_YEAR;
    }

    /**
     * @return the LICENSE_CLIENT_ID
     */
    public static String getLICENSE_CLIENT_ID() {
        CompanySetting.LICENSE_CLIENT_ID=CompanySetting.getLICENSE_KEY().substring(8, 16);
        return LICENSE_CLIENT_ID;
    }

    /**
     * @param aLICENSE_CLIENT_ID the LICENSE_CLIENT_ID to set
     */
    public static void setLICENSE_CLIENT_ID(String aLICENSE_CLIENT_ID) {
        LICENSE_CLIENT_ID = aLICENSE_CLIENT_ID;
    }

    /**
     * @return the LICENSE_CLIENT_NAME
     */
    public static String getLICENSE_CLIENT_NAME() {
        CompanySetting.LICENSE_CLIENT_NAME=CompanySetting.getLICENSE_KEY().substring(24);
        return LICENSE_CLIENT_NAME;
    }

    /**
     * @param aLICENSE_CLIENT_NAME the LICENSE_CLIENT_NAME to set
     */
    public static void setLICENSE_CLIENT_NAME(String aLICENSE_CLIENT_NAME) {
        LICENSE_CLIENT_NAME = aLICENSE_CLIENT_NAME;
    }
    
        /**
     * @return the LICENSE_EXPIRY_DATE
     */
    public static Date getLICENSE_EXPIRY_DATE() {
        DateFormat formatter = null;
        formatter = new SimpleDateFormat("ddMMyyyy");
        String sExpiryDate=CompanySetting.getLICENSE_EXPIRY_DAY() + "" +  CompanySetting.getLICENSE_EXPIRY_MONTH() + "" +  CompanySetting.getLICENSE_EXPIRY_YEAR();
        try {
            CompanySetting.LICENSE_EXPIRY_DATE=(Date) formatter.parse(sExpiryDate);
        } catch (ParseException ex) {
            CompanySetting.LICENSE_EXPIRY_DATE=null;
        }
        return LICENSE_EXPIRY_DATE;
    }

    /**
     * @param aLICENSE_EXPIRY_DATE the LICENSE_EXPIRY_DATE to set
     */
    public static void setLICENSE_EXPIRY_DATE(Date aLICENSE_EXPIRY_DATE) {
        LICENSE_EXPIRY_DATE = aLICENSE_EXPIRY_DATE;
    }
    public static Date getDateString(Date aDate) {
        Date aDate2=null;
        DateFormat formatter = null;
        formatter = new SimpleDateFormat("ddMMyyyy");
        String sExpiryDate="";
        try {
            sExpiryDate=aDate.toString();
            aDate2=(Date) formatter.parse(sExpiryDate);
        } catch (ParseException ex) {
            aDate2=null;
        }catch(NullPointerException npe){
            aDate2=null;
        }
        return aDate2;
    }
    
    /**
     * @return the CURRENT_SERVER_DATE
     */
    public static Date getCURRENT_SERVER_DATE() {
                    String sql22="{call sp_get_current_system_datetime(?)}";
                    try(
                            Connection conn22 = DBConnection.getMySQLConnection();
                            CallableStatement cs22 = conn22.prepareCall(sql22);
                       )
                    {  
                        cs22.registerOutParameter("out_current_system_datetime",VARCHAR);
                        cs22.executeQuery();
                        CompanySetting.CURRENT_SERVER_DATE=new Date(cs22.getTimestamp("out_current_system_datetime").getTime());
                    }catch(SQLException sqe){
                        CompanySetting.CURRENT_SERVER_DATE=null;
                    }
        return CURRENT_SERVER_DATE;
    }

    /**
     * @param aCURRENT_SERVER_DATE the CURRENT_SERVER_DATE to set
     */
    public static void setCURRENT_SERVER_DATE(Date aCURRENT_SERVER_DATE) {
        CURRENT_SERVER_DATE = aCURRENT_SERVER_DATE;
    }
    
    public static long getLicenseDaysLeft() {
		Date d1 = null;
		Date d2 = null;
                long diffDays=0;
		try {
			//d1 = format.parse(dateStart);
			//d2 = format.parse(dateStop);
                    d2=CompanySetting.getCURRENT_SERVER_DATE();
                    d1=CompanySetting.getLICENSE_EXPIRY_DATE();
 
			//in milliseconds
			long diff = d1.getTime() - d2.getTime();
 
			//long diffSeconds = diff / 1000 % 60;
			//long diffMinutes = diff / (60 * 1000) % 60;
			//long diffHours = diff / (60 * 60 * 1000) % 24;
			diffDays = diff / (24 * 60 * 60 * 1000);
                        diffDays=diffDays+1;
			//System.out.print(diffDays + " days, ");
			//System.out.print(diffHours + " hours, ");
			//System.out.print(diffMinutes + " minutes, ");
			//System.out.print(diffSeconds + " seconds.");
                        
		} catch (Exception e) {
                    System.err.println(e.getMessage());
		}
            return diffDays;
    }
    
    public static long getDaysBetweenNowAndDate(Date aDate) {
        Date d1 = null;
        Date d2 = null;
        long diffDays = 0;
        try {
            d2 = CompanySetting.getCURRENT_SERVER_DATE();
            d1 = aDate;
            //in milliseconds
            long diff = d1.getTime() - d2.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
            diffDays = diffDays;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return diffDays;
    }
    
    /**
     * @return the PACKAGE_NAME
     */
    public static String getPACKAGE_NAME() {
        CompanySetting.PACKAGE_NAME=CompanySetting.getLICENSE_KEY().substring(16, 24);
        return PACKAGE_NAME;
    }

    /**
     * @param aPACKAGE_NAME the PACKAGE_NAME to set
     */
    public static void setPACKAGE_NAME(String aPACKAGE_NAME) {
        PACKAGE_NAME = aPACKAGE_NAME;
    }
    
    public static long getPackageUsers(String aPackageName){
        if(aPackageName.equals("EDGE0001")){
            return 1;
        }else if(aPackageName.equals("EDGE0005")){
            return 5;
        }else if(aPackageName.equals("EDGE0010")){
            return 10;
        }else if(aPackageName.equals("EDGE0050")){
            return 50;
        }else if(aPackageName.equals("EDGE0000")){
            return 1000000;//for un limited
        }else{
            return 0;
        }
    }
    
    public static String displayPackageUsers(long aPackageNumbers){
        if(aPackageNumbers==1000000){//for un-limited
            return "UN - LIMITED - USERS";
        }else{
            return aPackageNumbers + " USER(S)";
        }
    }

    /**
     * @return the EIsAllowAutoUnpack
     */
    public String getEIsAllowAutoUnpack() {
        return EIsAllowAutoUnpack;
    }

    /**
     * @param EIsAllowAutoUnpack the EIsAllowAutoUnpack to set
     */
    public void setEIsAllowAutoUnpack(String EIsAllowAutoUnpack) {
        this.EIsAllowAutoUnpack = EIsAllowAutoUnpack;
    }

    /**
     * @return the ETimeZone
     */
    public String getETimeZone() {
        return ETimeZone;
    }

    /**
     * @param ETimeZone the ETimeZone to set
     */
    public void setETimeZone(String ETimeZone) {
        this.ETimeZone = ETimeZone;
    }

    /**
     * @return the EDateFormat
     */
    public String getEDateFormat() {
        return EDateFormat;
    }

    /**
     * @param EDateFormat the EDateFormat to set
     */
    public void setEDateFormat(String EDateFormat) {
        this.EDateFormat = EDateFormat;
    }

    /**
     * @return the ELicenseKey
     */
    public String getELicenseKey() {
        return ELicenseKey;
    }

    /**
     * @param ELicenseKey the ELicenseKey to set
     */
    public void setELicenseKey(String ELicenseKey) {
        this.ELicenseKey = ELicenseKey;
    }

    /**
     * @return the EEnforceTransUserSelect
     */
    public String getEEnforceTransUserSelect() {
        return EEnforceTransUserSelect;
    }

    /**
     * @param EEnforceTransUserSelect the EEnforceTransUserSelect to set
     */
    public void setEEnforceTransUserSelect(String EEnforceTransUserSelect) {
        this.EEnforceTransUserSelect = EEnforceTransUserSelect;
    }

    /**
     * @return the ESalesReceiptVersion
     */
    public int getESalesReceiptVersion() {
        return ESalesReceiptVersion;
    }

    /**
     * @param ESalesReceiptVersion the ESalesReceiptVersion to set
     */
    public void setESalesReceiptVersion(int ESalesReceiptVersion) {
        this.ESalesReceiptVersion = ESalesReceiptVersion;
    }

    /**
     * @return the EShowBranchInvoice
     */
    public String getEShowBranchInvoice() {
        return EShowBranchInvoice;
    }

    /**
     * @param EShowBranchInvoice the EShowBranchInvoice to set
     */
    public void setEShowBranchInvoice(String EShowBranchInvoice) {
        this.EShowBranchInvoice = EShowBranchInvoice;
    }

    /**
     * @return the EShowStoreInvoice
     */
    public String getEShowStoreInvoice() {
        return EShowStoreInvoice;
    }

    /**
     * @param EShowStoreInvoice the EShowStoreInvoice to set
     */
    public void setEShowStoreInvoice(String EShowStoreInvoice) {
        this.EShowStoreInvoice = EShowStoreInvoice;
    }

    /**
     * @return the EShowVatAnalysisInvoice
     */
    public String getEShowVatAnalysisInvoice() {
        return EShowVatAnalysisInvoice;
    }

    /**
     * @param EShowVatAnalysisInvoice the EShowVatAnalysisInvoice to set
     */
    public void setEShowVatAnalysisInvoice(String EShowVatAnalysisInvoice) {
        this.EShowVatAnalysisInvoice = EShowVatAnalysisInvoice;
    }

    /**
     * @return the EStoreEquivName
     */
    public String getEStoreEquivName() {
        return EStoreEquivName;
    }

    /**
     * @param EStoreEquivName the EStoreEquivName to set
     */
    public void setEStoreEquivName(String EStoreEquivName) {
        this.EStoreEquivName = EStoreEquivName;
    }
    
}
