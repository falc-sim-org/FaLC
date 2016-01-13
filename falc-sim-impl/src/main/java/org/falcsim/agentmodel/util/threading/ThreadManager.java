package org.falcsim.agentmodel.util.threading;

import java.util.List;

public interface ThreadManager {
	
	public interface ProcessCallback{
	    <T> void execute(int thCount, List<T> list, Class<?> clazz, Object...beans);
	    void finallize();
	}
	
	public <T> void run(int thCount, List<T> list, Class<?> clazz, ProcessCallback callback, Object...beans);
	
}
