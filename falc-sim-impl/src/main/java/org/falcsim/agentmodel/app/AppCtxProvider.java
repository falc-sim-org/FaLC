package org.falcsim.agentmodel.app;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** 
* Static Application context provider 
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/
public class AppCtxProvider implements ApplicationContextAware {
    private static ApplicationContext ctx = null;
    
    public static ApplicationContext getApplicationContext() {         
        return ctx;    
    }
    @SuppressWarnings("static-access")
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // Assign the ApplicationContext into a static method
        this.ctx = ctx;
    }
}


