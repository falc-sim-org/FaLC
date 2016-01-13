package org.falcsim.agentmodel.util.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadManagerImpl1 implements ThreadManager {	
	private static Logger logger = Logger.getLogger(ThreadManagerImpl1.class);
	@Autowired
	private RCRandomGenerator rg;
	
	private List<FalcRunnable> threadCache;
	private CountDownLatch latch;
	
	@Override
	public <T> void run(int thCount, List<T> list, Class<?> clazz, ProcessCallback callback, Object...beans){
		logger.info("Thread manager - cleaning");	
		clean();

		init(thCount, list, clazz, callback, beans);
		
		logger.info("Thread manager - Threads started");		
		try {
			latch.await();
		} catch(InterruptedException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.THREAD_ERROR.getValue());
		}
		logger.info("Thread manager - Threads finished");	
		callback.finallize();
		logger.info("Thread manager - Done");	
	}
	
	
	private void clean(){
		if(threadCache != null && threadCache.size() > 0 ){
			
		}
		threadCache = new ArrayList<FalcRunnable>();
	}
	
	private <T> void init(int maxThreadCount, List<T> list, Class<?> clazz, ProcessCallback callback, Object...beans){				
		latch = new CountDownLatch(maxThreadCount);
		
		List<Object> tmpList = new ArrayList<Object>(list);
		
		int lhhSize = list.size(); 
		int perThread = lhhSize / maxThreadCount; 
		int modThread = lhhSize % maxThreadCount; 
		int current = 0;
		int step = perThread + 1;
		for (int i = 0; i < maxThreadCount; i++) {
			if (step != perThread) {
				if (modThread != 0) {
					modThread--;
				} else {
					step = perThread;
				}
			}
			FalcRunnable falcthread = new FalcRunnable(rg, i, tmpList.subList(current, current + step), clazz, callback );
			falcthread.SetBean(beans);
			threadCache.add(falcthread);
			Thread thread = new Thread(falcthread);
			thread.start();
			current += step;
		}		
	}
}
