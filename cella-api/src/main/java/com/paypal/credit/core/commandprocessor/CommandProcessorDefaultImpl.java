package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprovider.RootCommandProvider;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProviderFactory;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class CommandProcessorDefaultImpl
implements CommandProcessor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandProcessorDefaultImpl.class);

	public static CommandProcessor create() {
        return new CommandProcessorDefaultImpl(100);
    }

    // ============================================================
    // Instance Members
    // ============================================================
    private Application application;
    private final BlockingQueue<Command<?>> workQueue;
    private final Executor asynchExecutor;
    private final ExecutorCompletionService asynchCompletionService;
    private final Executor completionNotifier;

	public CommandProcessorDefaultImpl(final int workQueueSize)
	{
		LOGGER.info("CommandProcessorDefaultImpl() - " + this.hashCode());
        ParameterCheckUtility.checkParameterStrictlyPositive(workQueueSize, "workQueueSize");

        this.workQueue = new ArrayBlockingQueue<Command<?>>(workQueueSize);
        this.asynchExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            private AtomicInteger threadSerialNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(final Runnable r) {
                Thread result = new Thread(r,
                        String.format("AsynchCommandExecution-%d", threadSerialNumber.addAndGet(1)));
                return result;
            }
        });

        // A CompletionService that uses a supplied Executor to execute tasks.
        // This class arranges that submitted tasks are, upon completion, placed on a queue accessible using take. The class is lightweight enough to be suitable for transient use when processing groups of tasks.
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

        this.completionNotifier.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Future<?> completed = asynchCompletionService.take();
                            try {
                                Object result = completed.get();
                            } catch (ExecutionException e) {
                                Throwable rootCause = e.getCause();

                            }
                        } catch (InterruptedException e) {
                            LOGGER.error("Interrupted while waiting for completion service.");
                            break;
                        }
                    }
                }
            }
        );
	}

    /**
     * Set the Application in which this CommandProcessor is running.
     * The implementation should retain this reference.
     *
     * @param application the "owning" application
     */
    @Override
    public void setApplication(final Application application) {
        this.application = application;
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
    public <T> Future<T> doAsynchronously(Command<T> command) {
        LOGGER.info("Asynchronous execution of command of type '{}'", command.getClass().getSimpleName());
        if(isCommandAsynchronouslyExecutable(command))
        {
            LOGGER.info("Submitting '{}' for asynchronous execution.", command.getClass().getSimpleName());
        }
        else
        {
            LOGGER.warn("'{}' is not marked as eligible for asynchronous execution.", command.getClass().getSimpleName());
            return null;
        }

        return null;
    }

    private boolean isCommandAsynchronouslyExecutable(Command<?> command)
    {
    	CommandExecution commandExecution = command.getClass().getAnnotation(CommandExecution.class);
    	return commandExecution != null && commandExecution.asynchronouslyExecutable();
    }
    
    @Override
    public <T extends Object> T doSynchronously(Command<T> command)
            throws Throwable
    {
        ParameterCheckUtility.checkParameterNotNull(command, "command");

        command.setApplicationContext(application);

        try {
            T result = command.invoke();
            return result;

        } catch (Throwable t) {
            LOGGER.error("{} caught in CommandProcessor", command.getClass().getSimpleName());
            throw t;
        }
    }
    
}
