package org.falcsim.agentmodel;

import java.io.IOException;

import org.falcsim.agentmodel.app.RunModules;
import org.falcsim.agentmodel.util.LogUtil;
import org.falcsim.exit.ExitCodes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RunFaLC contains FaLC main(...) method.
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5
 * 
 */

public class RunFaLC {
	/**
	 * application context for custom bean instances
	 */
	private static ApplicationContext ctx;
	private static final String PROJECT_NAME_PARAMETER = "project.name";
	
	/**
	 * @param args
	 *            not used
	 * 
	 */
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(RCConstants.APP_CONFIG_PATH);
		LogUtil.log();
		RunFaLC m = new RunFaLC();
		m.run(args);
		System.exit(ExitCodes.EXIT_SUCCESS.getValue());
	}

	/**
	 * The context is initialized so that the container becomes accessible
	 * 
	 * @throws IOException
	 */
	public void run(String ... args) {
		RunModules rm = (RunModules) ctx.getBean(RunModules.class);
		for(int i = 0; i < args.length; i++){
			if(args[i].startsWith("-D") && args[i].substring(2).startsWith(PROJECT_NAME_PARAMETER)){
				String projectName = args[i].substring(2 + PROJECT_NAME_PARAMETER.length() + 1);
				rm.setProjectName(projectName);
			}
		}
		rm.process();
	}

}