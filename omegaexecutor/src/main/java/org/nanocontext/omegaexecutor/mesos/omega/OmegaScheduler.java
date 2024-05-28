package com.paypal.credit.mesos.omega;

import com.google.protobuf.ByteString;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cbeckey on 2/25/16.
 */
public class OmegaScheduler implements Scheduler {
    /** */
    private final static String INITIATE_OMEGA_SERVICER_INVALID_ARGS = "slaveId must be non-null and cpus and memInMega must be strictly positive.";
    /** */
    private final static Logger LOGGER = LoggerFactory.getLogger(OmegaScheduler.class);
    /** */
    private final Protos.ExecutorInfo executor;
    /** A map of the queues to monitor and the omega functions to trigger */
    private final Map<String, String> omegaTriggers = new ConcurrentHashMap<>();

    /**
     * @param executor
     */
    public OmegaScheduler(final Protos.ExecutorInfo executor) {
        this.executor = executor;
    }

    // ================================================================================================================
    // Administrative interface, add and remove the transactions to process
    // A transaction being defined as a queue and the definition of the processing of messages on
    // that queue.
    // ================================================================================================================
    public void addOmegaTrigger(final String queueLocation, final String omegaDefinition) {
        omegaTriggers.put(queueLocation, omegaDefinition);
    }

    public void removeOmegaTrigger(final String queueLocation) {
        omegaTriggers.remove(queueLocation);
    }

    // ================================================================================================================
    // Queue monitoring methods
    // ================================================================================================================

    // ================================================================================================================
    // Mesos Scheduler Implementation
    //
    // ================================================================================================================

    /** Registered with a Mesos Master for the first time */
    @Override
    public void registered(SchedulerDriver schedulerDriver, Protos.FrameworkID frameworkID, Protos.MasterInfo masterInfo) {

    }

    /** (re)Registered with a new Mesos Master */
    @Override
    public void reregistered(SchedulerDriver schedulerDriver, Protos.MasterInfo masterInfo) {

    }

    /** A resource offer, claims resources if we decide we need them */
    @Override
    public void resourceOffers(SchedulerDriver schedulerDriver, List<Protos.Offer> list) {

    }

    /** A resource offer was rescinded by the master, don't claim the referenced resources */
    @Override
    public void offerRescinded(SchedulerDriver schedulerDriver, Protos.OfferID offerID) {

    }

    /** A status update from a Scheduler */
    @Override
    public void statusUpdate(SchedulerDriver schedulerDriver, Protos.TaskStatus taskStatus) {

    }

    /** A message from within this framework */
    @Override
    public void frameworkMessage(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, byte[] bytes) {

    }

    /** Disconnected from a Scheduler */
    @Override
    public void disconnected(SchedulerDriver schedulerDriver) {

    }

    /** A hosting slave was lost */
    @Override
    public void slaveLost(SchedulerDriver schedulerDriver, Protos.SlaveID slaveID) {

    }

    /** An executor was lost */
    @Override
    public void executorLost(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, int i) {

    }

    /** An error has occurred */
    @Override
    public void error(SchedulerDriver schedulerDriver, String s) {

    }

    // ================================================================================================================
    // Private (helper) Methods
    // ================================================================================================================

    /**
     * Initiate a new Omega Servicer instance. The servicer gets the queue name that it is to
     * get from and the definition of the Omega function it is to run.
     *
     * @param driver
     * @param offerId
     * @param slaveId
     * @param cpus
     * @param memInMega
     * @param queueConnnection
     * @param omegaDefinitionLocation
     */
    private void initiateOmegaServicer(
            final SchedulerDriver driver,
            final Protos.OfferID offerId,
            final Protos.SlaveID slaveId,
            final int cpus,
            final int memInMega,
            final String queueConnnection,
            final String omegaDefinitionLocation)
            throws UnsupportedEncodingException {
        if (slaveId == null || cpus <= 0 || memInMega <= 0 ) {
            LOGGER.error(INITIATE_OMEGA_SERVICER_INVALID_ARGS);
            throw new IllegalArgumentException(INITIATE_OMEGA_SERVICER_INVALID_ARGS);
        }
        final Protos.TaskID taskId = Protos.TaskID.newBuilder().setValue(UUID.randomUUID().toString()).build();

        LOGGER.info("Launching task " + taskId.getValue() + " with omega definition: " + omegaDefinitionLocation);

        final Protos.TaskInfo task = Protos.TaskInfo
                .newBuilder()
                .setName("Omega " + taskId.getValue())
                .setTaskId(taskId)
                .setSlaveId(slaveId)
                .addResources(
                        Protos.Resource.newBuilder().setName("cpus").setType(Protos.Value.Type.SCALAR)
                                .setScalar(Protos.Value.Scalar.newBuilder().setValue(cpus)))
                .addResources(
                        Protos.Resource.newBuilder().setName("mem").setType(Protos.Value.Type.SCALAR)
                                .setScalar(Protos.Value.Scalar.newBuilder().setValue(memInMega)))
                .setData(ByteString.copyFromUtf8(
                        String.format("%s,%s",
                                URLEncoder.encode(queueConnnection, "UTF-8"),
                                URLEncoder.encode(omegaDefinitionLocation, "UTF-8"))
                ))
                .setExecutor(Protos.ExecutorInfo.newBuilder(this.executor)).build();

        driver.launchTasks(Collections.singleton(offerId), Collections.singleton(task));
    }
}
