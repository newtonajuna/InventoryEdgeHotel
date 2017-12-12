
import java.io.Serializable;
import java.util.List;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author btwesigye
 */
@ManagedBean
@SessionScoped
public class Login implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String ActionMessageSuccess=null;
    private String ActionMessageFailure=null;
    private UserDetail LoggedInUserDetail=null;
    private String LoggedInUserName;
    private String LoggedInPassword;
    private int LoggedInStoreId;
    
    public void confirmUser(){
        if(new DBConnection().isMySQLConnectionAvailable().equals("ON")){
            UserDetailBean udb=new UserDetailBean();
            this.setLoggedInUserDetail(udb.getUserDetailByUserName(this.LoggedInUserName));
            if(this.LoggedInUserDetail!=null && LoggedInPassword.equals(this.LoggedInUserDetail.getUserPassword())){
                //it means username and password are valid
                this.ActionMessageSuccess="Select Store and Click Login to proceed...";
                this.ActionMessageFailure="";
            }else{
                this.LoggedInUserDetail=null;
                this.ActionMessageSuccess="";
                this.ActionMessageFailure="Invalid Username and/or Password...";
            }
        }else{
            this.ActionMessageSuccess="";
            this.ActionMessageFailure="Branch database connection is off, contact systems administrator please...";
        }
    }
    public List<Store> getUserStores() {
        StoreBean sb=new StoreBean();
        if(this.LoggedInUserDetail!=null){
            if("Yes".equals(this.LoggedInUserDetail.getIsUserGenAdmin())){
                return sb.getStoresAll();
            }else{
                return sb.getStoresByUser(this.LoggedInUserDetail.getUserDetailId());
            }
        }else{
            return null;
        }
    }
    public void userLogin(int aLoginType){
        if(new DBConnection().isMySQLConnectionAvailable().equals("ON")){
            UserDetailBean udb=new UserDetailBean();
            this.setLoggedInUserDetail(udb.getUserDetailByUserName(this.LoggedInUserName));
            if(this.LoggedInUserDetail!=null && this.LoggedInPassword.equals(this.LoggedInUserDetail.getUserPassword()) && "No".equals(this.LoggedInUserDetail.getIsUserLocked())){
                //it means username and password are valid and un-locked
                //check store
                if(this.LoggedInStoreId!=0){
                    //create seesion
                    FacesContext context = FacesContext.getCurrentInstance();  
                    HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();  
                    HttpSession httpSession = request.getSession(true);  
                    //set LoggedIn/Current User in session
                    httpSession.setAttribute("CURRENT_USER", this.LoggedInUserDetail);
                    //set LoggedIn/Current store in session
                    httpSession.setAttribute("CURRENT_STORE", new StoreBean().getStore(this.LoggedInStoreId)); 
                    //Set user rights for all the groups the user belongs to in session
                    httpSession.setAttribute("CURRENT_GROUP_RIGHTS", new GroupRightBean().getCurrentGroupRights(this.LoggedInStoreId, this.LoggedInUserDetail.getUserDetailId())); 
                    //Set is approve discount needed in session
                    if(new GroupRightBean().IsUserGroupsFunctionAccessAllowed(this.LoggedInUserDetail, new GroupRightBean().getCurrentGroupRights(this.LoggedInStoreId, this.LoggedInUserDetail.getUserDetailId()),"DISCOUNT", "Add")==1){
                         httpSession.setAttribute("IS_APPROVE_DISCOUNT_NEEDED", 0);
                    }else{
                        httpSession.setAttribute("IS_APPROVE_DISCOUNT_NEEDED", 1); 
                    }
                    //Set is approve points needed in session
                    if(new GroupRightBean().IsUserGroupsFunctionAccessAllowed(this.LoggedInUserDetail, new GroupRightBean().getCurrentGroupRights(this.LoggedInStoreId, this.LoggedInUserDetail.getUserDetailId()),"SPEND POINT", "Add")==1){
                        httpSession.setAttribute("IS_APPROVE_POINTS_NEEDED", 0); 
                    }else{
                        httpSession.setAttribute("IS_APPROVE_POINTS_NEEDED", 1); 
                    }
                    //Set LOGIN_TYPE in session
                    httpSession.setAttribute("LOGIN_TYPE", aLoginType); //1=BRANCH, 2=INTER-BRANCH
                    
                    //first delete all un-logged out sessions of this user that are older than 12 hours
                    new LoginSessionBean().deleteOldUnloggedOutSessions();
                    
                    //---------------add login session to the session database---
                    LoginSession ls=new LoginSession();
                    ls.setUserDetailId(this.LoggedInUserDetail.getUserDetailId());
                    ls.setStoreId(this.LoggedInStoreId);
                    ls.setSessionId(FacesContext.getCurrentInstance().getExternalContext().getSessionId(false));
                    
                    String aRemoteIp="";
                    String aRemoteHost="";
                    String aRemoteUser="";
                    
                    aRemoteIp = request.getHeader("X-FORWARDED-FOR");
                    if (aRemoteIp == null) {
                        aRemoteIp = request.getRemoteAddr();
                    }
                    ls.setRemoteIp(aRemoteIp);
                    
                    try{
                        aRemoteHost=request.getRemoteHost();
                        if(aRemoteHost==null){
                            aRemoteHost="";
                        }
                    }catch(NullPointerException npe){
                        aRemoteHost="";
                    }
                    ls.setRemoteHost(aRemoteHost);
                    
                    try{
                        aRemoteUser=request.getRemoteUser();
                        if(aRemoteUser==null){
                            aRemoteUser="";
                        }
                    }catch(NullPointerException npe){
                        aRemoteUser="";
                    }
                    ls.setRemoteUser(aRemoteUser);
                    
                    //System.out.println("SID:" + ls.getSessionId() + " UID:" + ls.getUserDetailId() + "Saved:" + new LoginSessionBean().saveLoginSession(ls));
                    if(new LoginSessionBean().saveLoginSession(ls)==1){
                        //added successfully
                    }

                    //Navigate to the Menu or Home page
                    FacesContext fc = FacesContext.getCurrentInstance();
                    ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
                    nav.performNavigation("ReportRoomingList?faces-redirect=true");
                }else{
                    this.ActionMessageSuccess="";
                    this.ActionMessageFailure="Invalid Store...";
                }
            }else{
                this.LoggedInUserDetail=null;
                this.ActionMessageSuccess="";
                this.ActionMessageFailure="User account is either Invalid or Locked, contact system admin...";
            }
        }else{
            this.ActionMessageSuccess="";
            this.ActionMessageFailure="Branch database connection is off, contact systems administrator please...";
        }
    }
    
    public void userApprove(String aUserName,int aStoreId, String aUserPassword,String aFunctionName,String aRole){
        int ApproveUserId=0;
        String ApproveDiscountStatus="";
        String ApprovePointsStatus="";
        
        UserDetailBean udb=new UserDetailBean();
        UserDetail ud=new UserDetail();
        
        ud=udb.getUserDetailByUserName(aUserName);
        if(ud!=null && aUserPassword.equals(ud.getUserPassword()) && "No".equals(ud.getIsUserLocked())){
            //it means username and password are valid and un-locked
            //check right to approve
            GroupRightBean grb=new GroupRightBean();
            if (grb.IsUserGroupsFunctionAccessAllowed2(ud,new GroupRightBean().getCurrentGroupRights(aStoreId,ud.getUserDetailId()), aFunctionName, aRole)==1) {
                ApproveUserId=ud.getUserDetailId();
                if(aFunctionName.equals("DISCOUNT")){
                    ApproveDiscountStatus="APPROVED";
                }else if(aFunctionName.equals("SPEND POINT")){
                    ApprovePointsStatus="APPROVED";
                }
            }else{
                ApproveUserId=0;
                if(aFunctionName.equals("DISCOUNT")){
                    ApproveDiscountStatus="REJECTED";
                }else if(aFunctionName.equals("SPEND POINT")){
                    ApprovePointsStatus="REJECTED";
                }
            }
        }else{
                ApproveUserId=0;
                if(aFunctionName.equals("DISCOUNT")){
                    ApproveDiscountStatus="REJECTED";
                }else if(aFunctionName.equals("SPEND POINT")){
                    ApprovePointsStatus="REJECTED";
                }
        }
        //update session for discount and points approvals
        FacesContext context = FacesContext.getCurrentInstance();  
        HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();  
        HttpSession httpSession = request.getSession(false);  
        httpSession.setAttribute("APPROVE_USER_ID", ApproveUserId);
        if (aFunctionName.equals("DISCOUNT")) {
            httpSession.setAttribute("APPROVE_DISCOUNT_STATUS", ApproveDiscountStatus);
        } else if (aFunctionName.equals("SPEND POINT")) {
            httpSession.setAttribute("APPROVE_POINTS_STATUS", ApprovePointsStatus);
        }
    }
    
    public void userLogout(){
      ////this.LoggedInUserDetail=null;
      ////this.confirmUser();
      //first delete session from database
      ////String aSessionId=FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
      ////if(!aSessionId.isEmpty()){
          ////if(new LoginSessionBean().deleteLoginSessionBySession(aSessionId)==1){
              //deleted success
          ////}
      ////}
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
      ////return "Login?faces-redirect=true";
    }

    /**
     * @return the LoggedInUserName
     */
    public String getLoggedInUserName() {
        return LoggedInUserName;
    }
    
    
    /**
     * @return the LoggedInUserDetail
     */
    public UserDetail getLoggedInUserDetail() {
        return this.LoggedInUserDetail;
    }

    /**
     * @param aLoggedInUserDetail the LoggedInUserDetail to set
     */
    public void setLoggedInUserDetail(UserDetail aLoggedInUserDetail) {
        this.LoggedInUserDetail = aLoggedInUserDetail;
    }

    /**
     * @param LoggedInUserName the LoggedInUserName to set
     */
    public void setLoggedInUserName(String LoggedInUserName) {
        this.LoggedInUserName = LoggedInUserName;
    }

    /**
     * @return the LoggedInStoreId
     */
    public int getLoggedInStoreId() {
        return LoggedInStoreId;
    }

    /**
     * @param LoggedInStoreId the LoggedInStoreId to set
     */
    public void setLoggedInStoreId(int LoggedInStoreId) {
        this.LoggedInStoreId = LoggedInStoreId;
    }

    /**
     * @return the LoggedInPassword
     */
    public String getLoggedInPassword() {
        return LoggedInPassword;
    }

    /**
     * @param LoggedInPassword the LoggedInPassword to set
     */
    public void setLoggedInPassword(String LoggedInPassword) {
        this.LoggedInPassword = LoggedInPassword;
    }

    /**
     * @return the ActionMessageSuccess
     */
    public String getActionMessageSuccess() {
        return ActionMessageSuccess;
    }

    /**
     * @param ActionMessageSuccess the ActionMessageSuccess to set
     */
    public void setActionMessageSuccess(String ActionMessageSuccess) {
        this.ActionMessageSuccess = ActionMessageSuccess;
    }

    /**
     * @return the ActionMessageFailure
     */
    public String getActionMessageFailure() {
        return ActionMessageFailure;
    }

    /**
     * @param ActionMessageFailure the ActionMessageFailure to set
     */
    public void setActionMessageFailure(String ActionMessageFailure) {
        this.ActionMessageFailure = ActionMessageFailure;
    }
    
}
