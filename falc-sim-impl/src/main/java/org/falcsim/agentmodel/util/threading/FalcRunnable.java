package org.falcsim.agentmodel.util.threading;

import java.util.List;

import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.util.threading.ThreadManager.ProcessCallback;

public class FalcRunnable implements Runnable {

	private RCRandomGenerator rg;
	private List<Object> list;
	private ProcessCallback callback;
	private int threadId;
	private Object[] beans;
	private Class<?> clazz;
	
	public FalcRunnable(RCRandomGenerator rg, int threadId, List<Object> list, Class<?> clazz, ProcessCallback callback){
		this.rg = rg;
		this.threadId = threadId;
		this.list = list;
		this.callback = callback;
		this.clazz = clazz;
	}

	public void SetBean(Object...beans){
		this.beans = beans;
	}	
	
	@Override
	public void run() {
		callback.execute(threadId, list, clazz, beans);
	}

}
