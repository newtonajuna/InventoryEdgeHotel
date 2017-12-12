
import java.io.Serializable;
import java.util.List;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class NavigationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String NavMsg;

    public String redirectToBranch() {
        return "Branch?faces-redirect=true";
    }

    public String redirectToHome() {
        return "Home?faces-redirect=true";
    }

    public String redirectToItem() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 5);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "ITEM");
        return "Item?faces-redirect=true";
    }

    public String redirectToCategory() {
        return "Category?faces-redirect=true";
    }
    public String redirectToCurrencyType() {
        return "CurrencyType?faces-redirect=true";
    }

    public String redirectToRoomCategory() {
        return "RoomCategory?faces-redirect=true";
    }

    public String redirectToRoom() {
        return "Room?faces-redirect=true";
    }

    public String redirectToRoomPackage() {
        return "RoomPackage?faces-redirect=true";
    }

    public String redirectToCompanySetting() {
        return "CompanySetting?faces-redirect=true";
    }

    public String redirectToDiscountPackage() {
        return "DiscountPackage?faces-redirect=true";
    }

    public String redirectToDiscountPackageItem() {
        return "DiscountPackageItem?faces-redirect=true";
    }

    public String redirectToDispose() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        // httpSession.setAttribute("TRANSACTION_TYPE", new TransactionTypeBean().getTransactionType(3));
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 3);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "DISPOSE");
        return "DisposeTrans?faces-redirect=true";
    }

    public String redirectToGroupDetail() {
        return "GroupDetail?faces-redirect=true";
    }

    public String redirectToUserCategory() {
        return "UserCategory?faces-redirect=true";
    }

    public String redirectToUserItemEarn() {
        return "UserItemEarn?faces-redirect=true";
    }

    public String redirectToGroupRight() {
        return "GroupRight?faces-redirect=true";
    }

    public String redirectToGroupUser() {
        return "GroupUser?faces-redirect=true";
    }

    public String redirectToItemMap() {
        return "ItemMap?faces-redirect=true";
    }

    public String redirectToMenu() {
        return "Menu?faces-redirect=true";
    }

    public String redirectToPayPurchase() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 6);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PAYMENT");
        return "Pay?faces-redirect=true";
    }

    public String redirectToPaySale() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 6);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PAYMENT");
        return "Pay?faces-redirect=true";
    }

    public String redirectToPayIN() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("PAY_CATEGORY", "IN");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 6);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PAYMENT");
        return "Pay?faces-redirect=true";
    }

    public String redirectToPayOUT() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("PAY_CATEGORY", "OUT");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 6);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PAYMENT");
        return "Pay?faces-redirect=true";
    }

    public String redirectToPayMethod() {
        return "PayMethod?faces-redirect=true";
    }

    public String redirectToPointsCard() {
        return "PointsCard?faces-redirect=true";
    }

    public String redirectToPointsTransaction() {
        return "PointsTransaction?faces-redirect=true";
    }

    public String redirectToPurchase() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 1);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PURCHASE");
        return "PurchaseTrans?faces-redirect=true";
    }

    public String redirectToPurchaseOrder() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 8);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PURCHASE ORDER");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(8);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "PurchaseOrderTrans?faces-redirect=true";
    }

    public String redirectToPurchaseReceiveItem() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 9);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "GOODS RECEIVED");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(9);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "PurchaseGoodsReceivedTrans?faces-redirect=true";
    }

    public String redirectToGoodsDelivery() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 12);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "GOODS DELIVERY");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(12);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleGoodsDeliveryTrans?faces-redirect=true";
    }

    public String redirectToPurchaseInvoice() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 1);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PURCHASE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(1);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "PurchaseInvoiceTrans?faces-redirect=true";
    }

    public String redirectToWholeSaleQuotation() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "WHOLE SALE QUOTATION");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 15);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 10);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE QUOTATION");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(10);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleQuotationTrans?faces-redirect=true";
    }

    public String redirectToRetailSaleQuotation() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RETAIL SALE QUOTATION");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 14);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 10);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE QUOTATION");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(10);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleQuotationTrans?faces-redirect=true";
    }

    public String redirectToHome2() {
        return "ReportRoomingList?faces-redirect=true";
    }

    public String redirectToReservation() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RESERVATION");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 14);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "RESERVATION");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(14);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "ReservationTrans?faces-redirect=true";
    }

    public String redirectToSaleOrder() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "SALE ORDER");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 16);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 11);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE ORDER");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(11);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleOrderTrans?faces-redirect=true";
    }

    public String redirectToWholeSale() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "WHOLE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 10);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE");
        return "SaleTrans?faces-redirect=true";
    }

    public String redirectToRetailSale() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RETAIL");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 2);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE");
        return "SaleTrans?faces-redirect=true";
    }

    public String redirectToCostPriceSale() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "COST-PRICE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 11);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE");
        return "SaleTrans?faces-redirect=true";
    }

    public String redirectToExemptSale() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "EXEMPT");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 12);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE");
        return "SaleTrans?faces-redirect=true";
    }

    public String redirectToWholeSaleInvoice() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "WHOLE SALE INVOICE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 10);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTrans?faces-redirect=true";
    }

    public String redirectToRetailSaleInvoice() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RETAIL SALE INVOICE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 2);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTrans?faces-redirect=true";
    }

    public String redirectToRetailSaleInvoiceRoom() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RETAIL SALE INVOICE (ROOM)");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 21);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTransRoom?faces-redirect=true";
    }
    public String redirectToRetailSaleInvoiceRoomGroupCheckIn() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "RETAIL SALE INVOICE (ROOM)");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 21);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTransRoomGroupCheckIn?faces-redirect=true";
    }

    public String redirectToCostPriceSaleInvoice() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "COST-PRICE SALE INVOICE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 11);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTrans?faces-redirect=true";
    }

    public String redirectToExemptSaleInvoice() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "EXEMPT SALE INVOICE");
        httpSession.setAttribute("TRANSACTION_REASON_ID", 17);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE INVOICE");
        TransactionType TempTransType = new TransactionTypeBean().getTransactionType(2);
        if (TempTransType != null) {
            httpSession.setAttribute("TRANSACTOR_LABEL", TempTransType.getTransactorLabel());
            httpSession.setAttribute("TRANSACTION_NUMBER_LABEL", TempTransType.getTransactionNumberLabel());
            httpSession.setAttribute("TRANSACTION_OUTPUT_LABEL", TempTransType.getTransactionOutputLabel());
        }
        return "SaleInvoiceTrans?faces-redirect=true";
    }

    public String redirectToSaleView() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "");
        httpSession.setAttribute("TRANSACTOR_TYPE", "");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 2);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "SALE");
        return "SaleView?faces-redirect=true";
    }

    public String redirectToTransView() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "");
        httpSession.setAttribute("TRANSACTOR_TYPE", "");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);//this was 2 bfr edit
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "");//this was SALE bfr edit
        return "TransView?faces-redirect=true";
    }

    public String redirectToPurchaseView() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "");
        httpSession.setAttribute("TRANSACTOR_TYPE", "");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 1);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "PURCHASE");
        return "PurchaseView?faces-redirect=true";
    }

    public String redirectToDisposeView() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "");
        httpSession.setAttribute("TRANSACTOR_TYPE", "");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 3);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "DISPOSE");
        return "DisposeView?faces-redirect=true";
    }

    public String redirectToTransferView() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("SALE_TYPE", "");
        httpSession.setAttribute("TRANSACTOR_TYPE", "");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 4);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSFER");
        return "TransferView?faces-redirect=true";
    }

    public void defineTransactionTypes(int aTransactionTypeId, String aTransactionTypeName, String aTransactorType, String aSaleType) {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTION_TYPE_ID", aTransactionTypeId);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", aTransactionTypeName);
        httpSession.setAttribute("TRANSACTOR_TYPE", aTransactorType);
        httpSession.setAttribute("SALE_TYPE", aSaleType);
    }

    public String redirectToStore() {
        return "Store?faces-redirect=true";
    }

    public String redirectToSubCategory() {
        return "SubCategory?faces-redirect=true";
    }

    public String redirectToLicenseDetail() {
        return "LicenseDetail?faces-redirect=true";
    }

    public String redirectToTransactionReason() {
        return "TransactionReason?faces-redirect=true";
    }

    public String redirectToTransactionType() {
        return "TransactionType?faces-redirect=true";
    }

    public String redirectToGuestFolio() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "GUESTFOLIO");
        httpSession.setAttribute("INVOKE_MODE", "PARENT");
        return "GuestFolio?faces-redirect=true";
    }
    
    public String redirectToTransactorCustomer() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "CUSTOMER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSACTOR");
        httpSession.setAttribute("INVOKE_MODE", "PARENT");
        return "Transactor?faces-redirect=true";
    }

    public String redirectToTransactorSupplier() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SUPPLIER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSACTOR");
        httpSession.setAttribute("INVOKE_MODE", "PARENT");
        return "Transactor?faces-redirect=true";
    }

    public String redirectToTransactorScheme() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "SCHEME");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSACTOR");
        httpSession.setAttribute("INVOKE_MODE", "PARENT");
        return "Transactor?faces-redirect=true";
    }

    public String redirectToTransactorProvider() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTOR_TYPE", "PROVIDER");
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 0);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSACTOR");
        httpSession.setAttribute("INVOKE_MODE", "PARENT");
        return "Transactor?faces-redirect=true";
    }

    public String redirectToTransfer() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 4);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSFER");
        return "TransferTrans?faces-redirect=true";
    }

    public String redirectToTransferRequest() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 13);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "TRANSFER REQUEST");
        return "TransferRequestTrans?faces-redirect=true";
    }

    public String redirectToUnit() {
        return "Unit?faces-redirect=true";
    }

    public String redirectToUnpack() {
        //update seesion
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute("TRANSACTION_TYPE_ID", 7);
        httpSession.setAttribute("TRANSACTION_TYPE_NAME", "UNPACK");
        return "UnpackTrans?faces-redirect=true";
    }

    public String redirectToUserDetail() {
        return "UserDetail?faces-redirect=true";
    }

    public String redirectToUserRight() {
        return "UserRight?faces-redirect=true";
    }

    public String redirectToIndex() {
        return "Index?faces-redirect=true";
    }

    public String redirectToMyAccount() {
        return "MyAccount?faces-redirect=true";
    }

    public String redirectToReportTransaction() {
        return "ReportTransaction?faces-redirect=true";
    }

    public String redirectToReportPay() {
        return "ReportPay?faces-redirect=true";
    }

    public String redirectToReportTransactionItem() {
        return "ReportTransactionItem?faces-redirect=true";
    }

    public String redirectToReportTransactionUserEarn() {
        return "ReportTransactionUserEarn?faces-redirect=true";
    }

    public String redirectToReportStockIn() {
        return "ReportStockIn?faces-redirect=true";
    }

    public String redirectToReportStockTotal() {
        return "ReportStockTotal?faces-redirect=true";
    }

    public String redirectToReportStockAll() {
        return "ReportStockAll?faces-redirect=true";
    }

    public String redirectToReportItem() {
        return "ReportItem?faces-redirect=true";
    }

    public String redirectToReportItemLocation() {
        return "ReportItemLocation?faces-redirect=true";
    }

    public String redirectToReportTransactor() {
        return "ReportTransactor?faces-redirect=true";
    }

    public String redirectToReportTransactorLedger() {
        return "ReportTransactorLedger?faces-redirect=true";
    }
    public String redirectToReportTransactorLedger_Grand() {
        return "ReportTransactorLedger_Grand?faces-redirect=true";
    }

    public String redirectToReportTransactorLedgerSummary() {
        return "ReportTransactorLedgerSummary?faces-redirect=true";
    }

    public String redirectToReportBill() {
        return "ReportBill?faces-redirect=true";
    }

    public String redirectToLocation() {
        return "Location?faces-redirect=true";
    }

    public String redirectToItemLocation() {
        return "ItemLocation?faces-redirect=true";
    }

    public void checkAccessDenied(String aFunctionName, String aRole) {
        String RealFunctionName = "";

        if ("SALE INVOICE".equals(aFunctionName)) {

            if ("WHOLE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                RealFunctionName = "WHOLE SALE INVOICE";
            } else if ("RETAIL SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                RealFunctionName = "RETAIL SALE INVOICE";
            } else if ("COST-PRICE SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                RealFunctionName = "COST-PRICE SALE INVOICE";
            } else if ("EXEMPT SALE INVOICE".equals(new GeneralUserSetting().getCurrentSaleType())) {
                RealFunctionName = "EXEMPT SALE INVOICE";
            } else {
                RealFunctionName = "RETAIL SALE INVOICE";
            }
        } else if ("TRANSACTOR".equals(aFunctionName)) {
            RealFunctionName = "TRANSACTOR";
        } else if ("GUESTFOLIO".equals(aFunctionName)) {
            RealFunctionName = "GUESTFOLIO";
        } else {
            RealFunctionName = aFunctionName;
        }

        UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
        List<GroupRight> aCurrentGroupRights = new GeneralUserSetting().getCurrentGroupRights();
        GroupRightBean grb = new GroupRightBean();

        if (grb.IsUserGroupsFunctionAccessAllowed(aCurrentUserDetail, aCurrentGroupRights, RealFunctionName, aRole) == 0) {
            this.setNavMsg(RealFunctionName + ": Unauthorized access, contact system admin...");
            FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(getNavMsg()));
            FacesContext fc = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("Home?faces-redirect=true");
        } else {
            this.setNavMsg("");
        }
    }

    public void checkAccessDeniedHome() {

        try {
            UserDetail aCurrentUserDetail = new GeneralUserSetting().getCurrentUser();
            if (aCurrentUserDetail.getUserDetailId() == 0) {
                this.setNavMsg("Unauthorized access, contact system admin...");
                FacesContext.getCurrentInstance().addMessage("Save", new FacesMessage(getNavMsg()));
                FacesContext fc = FacesContext.getCurrentInstance();
                ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
                nav.performNavigation("Login?faces-redirect=true");
            }
        } catch (NullPointerException npe) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("Login?faces-redirect=true");
        }
    }

    public void checkLicenseExpired() {
        if (CompanySetting.getLicenseDaysLeft() <= 0) {
            this.setNavMsg("--- LICENSE IS EXPIRED, CONTACT SYSTEM VENDOR ---");
            FacesContext.getCurrentInstance().addMessage("License", new FacesMessage(getNavMsg()));
            FacesContext fc = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("Home?faces-redirect=true");
        } else {
            this.setNavMsg("");
        }
    }

    public void checkCurrentPage(String aTransactionType, String aTransactorType, String aSaleType) {

        int LogOut = 0;
        if (!aTransactionType.equals(new GeneralUserSetting().getCurrentTransactionTypeName())) {
            //in the wrong place
            LogOut = 1;
        } else {
            LogOut = 0;
        }

        if (LogOut == 1) {
            //log-out
            this.setNavMsg("You will be logged out! Stop opening multiple Transactional Pages...");
            FacesContext.getCurrentInstance().addMessage("Security", new FacesMessage(getNavMsg()));
            Login aLogin = new Login();
            aLogin.userLogout();
        }
    }

    /**
     * @return the NavMsg
     */
    public String getNavMsg() {
        return NavMsg;
    }

    /**
     * @param NavMsg the NavMsg to set
     */
    public void setNavMsg(String NavMsg) {
        this.NavMsg = NavMsg;
    }

}
