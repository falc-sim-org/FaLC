package org.falcsim.agentmodel.app.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Console filter class
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 * 
 */
public class ConsoleFilter extends Filter<ILoggingEvent> {

	private boolean toConsole = true;
		
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(event.getLoggerName().equalsIgnoreCase(CommunicationClass.class.getName()))
			return this.toConsole ? FilterReply.ACCEPT : FilterReply.DENY;
		else
			return this.toConsole ? FilterReply.DENY : FilterReply.ACCEPT;
	}

	public boolean isToConsole() {
		return toConsole;
	}
	
	public void setToConsole(boolean toConsole) {
		this.toConsole = toConsole;
	}

}
