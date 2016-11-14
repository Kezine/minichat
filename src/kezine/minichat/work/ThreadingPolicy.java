package kezine.minichat.work;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * An abstraction of threading policy. Since the Event thread used differs using
 * different libraries (e.g. AWT/Swing, JavaFX) different implementations should
 * be used with thoses libraries.
 * 
 * @author Kezine
 *
 */
public abstract class ThreadingPolicy {
	private static ThreadingPolicy instance;

	/**
	 * Pushes the runnable on the Event Thread stack.
	 */
	protected abstract void runLaterImpl(Runnable r);

	/**
	 * Starts a new task in another thread (can be a newly created thread or a
	 * pool of worker threads)
	 */
	protected abstract Future<?> startTaskImpl(Runnable task);

	/**
	 * Starts a new task in another thread (can be a newly created thread or a
	 * pool of worker threads)
	 */
	protected abstract <T> Future<T> startTaskImpl(Callable<T> task);

	/**
	 * Returns whether the current thread is the event thread or not.
	 */
	protected abstract boolean isEventThread();

	/**
	 * Runs a runnable in the event thread. It we're already in the event
	 * thread, the runnable is called synchronously.
	 */
	protected abstract  void runCustomEventImpl(Runnable r);
	/**
	 * Runs a runnable in the event thread. It we're already in the event
	 * thread, the runnable is called synchronously.
	 */
	
	public synchronized static void initialize(ThreadingPolicy threadingPolicy) {
		if (instance != null)
			throw new IllegalStateException("ThreadingPolicy already created");
		instance = threadingPolicy;
	}

	/**
	 * Runs a runnable in the event thread, after every already pending events.
	 */
	public static void runLater(Runnable r) {
		instance.runLaterImpl(r);
	}

	/**
	 * Runs the runnable in a custom thread , detached from JavaFX thread.
	 */
	public static void runDetachedEvent(Runnable r) {
		
	}
	
	/**
	 * Runs a runnable in the event thread. It we're already in the event
	 * thread, the runnable is called synchronously.
	 */
	public static void runEvent(Runnable r) 
	{
		if (!instance.isEventThread())
			instance.runLaterImpl(r);
		else
			r.run();	
	}
	/**
	 * Starts a new task in another thread
	 */
	public static Future<?> startTask(Runnable task) {
		return instance.startTaskImpl(task);
	}

	/**
	 * Starts a new task in another thread
	 */
	public static <T> Future<T> startTask(Callable<T> task) {
		return instance.startTaskImpl(task);
	}
}