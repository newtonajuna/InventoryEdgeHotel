import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        //System.out.println("session created...");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            //System.out.println("session destroyed...");
            HttpSession session = event.getSession();
            if (new LoginSessionBean().deleteLoginSessionBySession(session.getId()) == 1) {
                //deleted session success, now do some clean up of usersettings
                FacesContext fc = FacesContext.getCurrentInstance();
                ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
                nav.performNavigation("Login?faces-redirect=true");
            }
        } catch (NullPointerException npe) {
            System.out.println("Session Problems...");
//            FacesContext fc = FacesContext.getCurrentInstance();
//            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
//            nav.performNavigation("Login?faces-redirect=true");
        }
    }

}
