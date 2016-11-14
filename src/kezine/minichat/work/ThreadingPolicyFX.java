package kezine.minichat.work;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import javafx.application.Platform;

public class ThreadingPolicyFX extends ThreadingPolicy {
	protected Logger logger = Logger.getLogger(this.getClass());
	private ExecutorService computingExecutors;
	private ExecutorService tasksExecutors;
	private ExecutorService eventsExecutors;
	protected UncaughtExceptionHandler handler;
	public ThreadingPolicyFX()
	{
		this(15,5,5);
	}
	public ThreadingPolicyFX(int computingThread,int taskThread, int customEventThread)
	{
		handler = new UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				logger.warn("Throwable catched in threading policy, thread("+t+")",e);
			}
		};
		computingExecutors = Executors.newFixedThreadPool(computingThread, new DaemonThreadFactory("computing",handler));
		tasksExecutors = Executors.newFixedThreadPool(taskThread, new DaemonThreadFactory("task",handler));
		eventsExecutors = Executors.newFixedThreadPool(customEventThread, new DaemonThreadFactory("event",handler));
	}
	@Override
	protected void runLaterImpl(Runnable r) {
		Platform.runLater(new RunableWraper(r));
		//eventsExecutors.submit(r);
	}

	@Override
	protected Future<?> startTaskImpl(Runnable r) {
		return tasksExecutors.submit(new RunableWraper(r));
	}

	@Override
	protected <T> Future<T> startTaskImpl(Callable<T> r) {
		return tasksExecutors.submit(r);
	}

	@Override
	protected boolean isEventThread() {
		return Platform.isFxApplicationThread();
	}

	

	@Override
	protected void runCustomEventImpl(Runnable r)
	{
		eventsExecutors.submit(new RunableWraper(r));
	}

		
	private static class DaemonThreadFactory implements ThreadFactory {
		static int i = 0;
		private String type;
		private UncaughtExceptionHandler handler;
		public DaemonThreadFactory(String type, UncaughtExceptionHandler handler)
		{
			this.type = type;
			this.handler = handler;
		}
		public Thread newThread(Runnable r) 
		{
			Thread thread = new Thread(r, "Deamon "+type+" thread #" + i++);
			thread.setUncaughtExceptionHandler(handler);
			thread.setDaemon(true);
			return thread;
		}
	}
	private static class RunableWraper implements Runnable
	{
		Runnable runnable;
		protected Logger logger = Logger.getLogger(ThreadingPolicyFX.class);
		public RunableWraper(Runnable runnable)
		{
			this.runnable = runnable;
		}
		@Override
		public void run()
		{
			try
			{
				runnable.run();
			}
			catch(Throwable ex)
			{
				logger.warn("Thowable catched in RunnableWraper", ex);
			}
		}
		
	}
}
