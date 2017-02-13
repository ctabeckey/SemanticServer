package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationTransactionContext;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException;
import com.paypal.credit.core.commandprovider.exceptions.CommandProviderException;
import com.paypal.credit.core.processorbridge.exceptions.InvalidTransactionContextException;
import com.paypal.credit.core.processorbridge.exceptions.UnmappableCommandException;
import com.paypal.credit.core.semantics.AbstractBaseSemantics;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An implementation of the ProcessorBridgeFactory interface that
 * creates ProcessorBridge realizations as dynamic proxies.
 */
public class ProcessorBridgeByteCodeImplFactory
        extends AbstractCachingProcessorBridgeFactory
        implements ProcessorBridgeFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProcessorBridgeByteCodeImplFactory.class);

    public ProcessorBridgeByteCodeImplFactory(Application application) {
        super(application);
    }

    /**
     * Creates a realization of the processorBridge (interface) directly in byte code.
     *
     * @param processorBridge
     * @param <T>
     * @return
     */
    protected <T> T createNewInstance(final Class<T> processorBridge)
            throws ProcessorBridgeInstantiationException {
        T result = null;

        DynamicType.Builder<T> builder = (DynamicType.Builder<T>) new ByteBuddy()
                .subclass(AbstractProcessorBridgeImpl.class)
                .implement(processorBridge)
                .name(new BridgeRealizationNamingStrategy())
                .modifiers(Modifier.PUBLIC + Modifier.FINAL)
                .annotateType(processorBridge.getAnnotations());

        // create methods in the form:
        // public final <method signature> {
        //      ApplicationTransactionContext ctx = TransactionContextFactory.get(ApplicationTransactionContext.class);
        //      RoutingToken routingToken = ctx.getRoutingToken();
        //      if (routingToken == null) {
        //          throw new InvalidTransactionContextException("getRoutingToken");
        //      }
        //   Callable<method result type> command =
        //      createCommand(<interface method>, args, routingToken, commandClassSemantics, resultType);
        //   if (asynchronous) {
        //      submitCommand(command);
        //   } else {
        //      return executeCommand(command);
        //   }
        // }

        for (Method method : processorBridge.getDeclaredMethods()) {
            DynamicType.Builder.ExceptionDeclarableMethodInterception methodRealization = builder.defineMethod(method);


            CommandClassSemantics commandClassSemantics = null;
            try {
                commandClassSemantics = Utility.getCommandClassSemantics(getApplication(), method);
            } catch (UnmappableCommandException e) {
                e.printStackTrace();
            }
            //Callable<R> command =
            //        createCommand(method, args, routingToken, commandClassSemantics, resultType);

        }

        Class<T> dynamicType = (Class<T>) builder
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();


        try {
            return dynamicType.newInstance();
        } catch (InstantiationException | IllegalAccessException x) {
            throw new ProcessorBridgeInstantiationException(processorBridge, x);
        }
    }

    public static class BridgeRealizationNamingStrategy implements AuxiliaryType.NamingStrategy {
        @Override
        public String name(final TypeDescription typeDescription) {
            return typeDescription.getPackage().getName() + "." + typeDescription.getSimpleName() + "Impl";
        }
    }

    private static class PrototypeProcessorBridge extends AbstractProcessorBridgeImpl {
        public PrototypeProcessorBridge(final Application application) {
            super(application);
        }

        private Callable<?> prototypeCommand = null;
        private ReentrantLock prototypeCommandLock = new ReentrantLock();
        public final void prototypeMethod()
                throws InvalidTransactionContextException, CoreRouterSemanticsException, UnknownCommandException,
                NoSuchMethodException {
            Method method = this.getClass().getMethod("prototypeMethod", new Class<?>[0]);
            Object[] args = new Object[0];
            Class<?>[] parameterTypes = Utility.getParameterTypes(args);

            ApplicationTransactionContext ctx = TransactionContextFactory.get(ApplicationTransactionContext.class);
            RoutingToken routingToken = ctx.getRoutingToken();
            if (routingToken == null) {
                throw new InvalidTransactionContextException("getRoutingToken");
            }

            prototypeCommandLock.lock();
            try {
                if (prototypeCommand == null) {
                    ProcessorBridgeMethodSemantics pbmSemantics =
                            getApplication().getApplicationSemantics().createProcessorBridgeMethodSemantics(method);
                    CommandClassSemantics commandClassSemantics =
                            getApplication().getApplicationSemantics().createCommandClassSemantic(pbmSemantics);
                    Callable command =
                            getApplication().getRootCommandProvider().createCommand(
                                    routingToken, commandClassSemantics, args, Void.class
                                    );
                }
            } catch (CommandProviderException e) {
                e.printStackTrace();
            } finally {
                prototypeCommandLock.unlock();
            }

            // if (asynchronous) {
            submitCommand(prototypeCommand, null);
            // } else {
            executeCommand(prototypeCommand);
            //}
        }

    }
}
