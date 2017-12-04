package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationImpl;
import com.paypal.utility.ParameterCheckUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class CommandProcessorDefaultImpl
implements CommandProcessor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandProcessorDefaultImpl.class);
    public static final int DEFAULT_WORK_QUEUE_SIZE = 100;
    private static final int DEFAULT_CORE_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 100;
    private static final long DEFAULT_KEEP_ALIVE = 10L;
    private static final TimeUnit DEFAULT_KEEP_ALIVE_UNITS = TimeUnit.SECONDS;

    public static CommandProcessorDefaultImpl create() {
        return create(DEFAULT_WORK_QUEUE_SIZE);
    }

    public static CommandProcessorDefaultImpl create(int workQueueSize) {
        return new CommandProcessorDefaultImpl(workQueueSize);
    }

    // ============================================================
    // Instance Members
    // ============================================================
    private ApplicationImpl application;
    private final BlockingQueue<Callable<?>> workQueue;
    private final Map<Future<?>, AsynchronousExecutionCallback<?>> callbackMap;
    private final ExecutorService asynchExecutor;
    private final ExecutorCompletionService asynchCompletionService;
    private final Executor completionNotifier;

    /**
     * Create the following:
     * 1.) A (blocking) work queue
     * 2.) An Executor that takes its tasks from the work queue
     *     - tasks are Command instances (Command extends Callable)
     * 3.) An ExecutorCompletionService that wraps the Executor
     *     - makes the Future results available on a queue
     * 4.) An Executor that takes its tasks from the ExecutorCompletionService queue
     *     - and notifies the callback if one was provided
     * @param workQueueSize
     */
	private CommandProcessorDefaultImpl(final int workQueueSize)
	{
		LOGGER.info("CommandProcessorDefaultImpl() - " + this.hashCode());
        ParameterCheckUtility.checkParameterStrictlyPositive(workQueueSize, "workQueueSize");

        this.workQueue = new ArrayBlockingQueue<Callable<?>>(workQueueSize);
        this.asynchExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            private AtomicInteger threadSerialNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(final Runnable r) {
                Thread result = new Thread(r,
                        String.format("AsynchCommandExecution-%d", threadSerialNumber.addAndGet(1)));
                return result;
            }
        });
        this.callbackMap = new ConcurrentHashMap<>(workQueueSize);

        // A CompletionService that uses a supplied Executor to execute tasks.
        // This class arranges that submitted tasks are, upon completion, placed on a queue
        // accessible using take.
        this.asynchCompletionService = new ExecutorCompletionService<>(this.asynchExecutor);

        this.completionNotifier = Executors.newCachedThreadPool(new ThreadFactory() {
            private AtomicInteger threadSerialNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(final Runnable r) {
                Thread result = new Thread(r,
                        String.format("AsynchCompletionNotifier-%d", threadSerialNumber.addAndGet(1)));
                return result;
            }
        });

        this.completionNotifier.execute(new CompletionNotifier());
	}

    /**
     * Set the Application in which this CommandProcessor is running.
     * The implementation should retain this reference.
     *
     * @param application the "owning" application
     */
    public void setApplication(final Application application) {
        this.application = this.application;
    }

    @Override
    public void shutdown() {
        ((ThreadPoolExecutor)this.asynchExecutor).shutdown();
    }

    @Override
    public boolean isShutdown() {
        return ((ThreadPoolExecutor)this.asynchExecutor).isShutdown();
    }

    // =====================================================================================================
	// Asynchronous Processing Related Methods
	// =====================================================================================================
	/**
	 * Submit a command for asynchronous execution, optionally providing a listener where the results
	 * may be communicated back to the client.
	 * 
	 * @param command - an AsynchronousCommand instance, created by this CommandProcessor's AsynchronousCommandFactory
	 * as returned by the getAsynchronousCommandFactory().
     */
    @Override
    public <R> void doAsynchronously(Callable<R> command, AsynchronousExecutionCallback<R> callback) {
        LOGGER.info("Asynchronous execution of command of type '{}'", command.getClass().getSimpleName());
        if(isCommandAsynchronouslyExecutable(command))
        {
            LOGGER.info("Submitting '{}' for asynchronous execution.", command.getClass().getSimpleName());

            if (ApplicationAwareCommand.class.isInstance(command)) {
                ((ApplicationAwareCommand)command).setApplicationContext(this.application);
            }

            Future<R> future = this.asynchCompletionService.submit(command);
            if (callback != null) {
                this.callbackMap.put(future, callback);
            }
        }
        else
        {
            LOGGER.warn("'{}' is not marked as eligible for asynchronous execution.", command.getClass().getSimpleName());
            return;
        }

        return;
    }

    /**
     *
     * @param command the commandprovider to execute
     * @param <T>
     * @return
     * @throws Throwable
     */
    @Override
    public <T extends Object> T doSynchronously(Callable<T> command)
            throws Throwable
    {
        ParameterCheckUtility.checkParameterNotNull(command, "command");

        if (ApplicationAwareCommand.class.isInstance(command)) {
            ((ApplicationAwareCommand)command).setApplicationContext(this.application);
        }

        try {
            T result = command.call();
            return result;

        } catch (Throwable t) {
            LOGGER.error("{} caught in CommandProcessor", command.getClass().getSimpleName());
            throw t;
        }
    }

    /**
     * Return true if the Command is marked as eligible for asynchronous execution.
     * @param command
     * @return
     */
    private boolean isCommandAsynchronouslyExecutable(Callable<?> command)
    {
        return command.getClass().getAnnotation(AsynchronouslyExecutableCommand.class) != null;
    }

    /**
     *
     */
    private class CompletionNotifier implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Future<?> completed = asynchCompletionService.take();
                    AsynchronousExecutionCallback callback = callbackMap.get(completed);
                    callbackMap.remove(completed);

                    try {
                        Object result = completed.get();
                        if (callback != null) {
                            callback.success(result);
                        }
                    } catch (Throwable t) {
                        Throwable rootCause = t instanceof ExecutionException ? t.getCause() : t;
                        if (callback != null) {
                            callback.failure(rootCause);
                        }
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting for completion service.");
                    break;
                }
            }
        }
    }
}
