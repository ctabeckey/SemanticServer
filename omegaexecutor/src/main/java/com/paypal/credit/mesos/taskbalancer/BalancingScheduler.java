/**
 * 
 */
package com.paypal.credit.mesos.taskbalancer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.mesos.Protos.ContainerInfo;
import org.apache.mesos.Protos.ExecutorID;
import org.apache.mesos.Protos.ExecutorInfo;
import org.apache.mesos.Protos.Filters;
import org.apache.mesos.Protos.FrameworkID;
import org.apache.mesos.Protos.MasterInfo;
import org.apache.mesos.Protos.Offer;
import org.apache.mesos.Protos.OfferID;
import org.apache.mesos.Protos.Request;
import org.apache.mesos.Protos.Resource;
import org.apache.mesos.Protos.SlaveID;
import org.apache.mesos.Protos.Status;
import org.apache.mesos.Protos.TaskID;
import org.apache.mesos.Protos.TaskInfo;
import org.apache.mesos.Protos.TaskStatus;
import org.apache.mesos.Protos.Value;
import org.apache.mesos.Protos.Value.Range;
import org.apache.mesos.Protos.Value.Ranges;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;

import com.google.protobuf.ByteString;

/**
 * @author cbeckey
 * 
 * A Scheduler registers with the Master and is offered resources.
 * 
 *         You can write a framework scheduler in C, C++, Java/Scala, or Python.
 *         Your framework scheduler should inherit from the Scheduler class (see
 *         API below). Your scheduler should create a SchedulerDriver (which
 *         will mediate communication between your scheduler and the Mesos
 *         master) and then call SchedulerDriver.run()
 * 
 */
public class BalancingScheduler 
implements Scheduler {
	private static final int DEFAULT_MEMORY = 128;
	private static final int DEFAULT_CPUS = 1;
	private final Logger LOG = Logger.getLogger(BalancingScheduler.class);
	private final ExecutorInfo executorInfo;
	private final Queue<AbstractTaskDescription> taskQueue;
	private final Map<TaskID, AbstractTaskDescription> submittedTasks = new HashMap<TaskID, AbstractTaskDescription>(100);
	
	
	// Save our framework ID, for no real reason because it is available
	// on most every callback from Mesos.
	private FrameworkID frameworkId = null;

	/**
	 * Instantiate with a reference to the executor to direct tasks to
	 * and a list of tasks to do.
	 */
	public BalancingScheduler(ExecutorInfo executor, List<AbstractTaskDescription> initialTasks) {
		this.executorInfo = executor;
		this.taskQueue = new ArrayDeque<AbstractTaskDescription>(initialTasks.size());
		for(AbstractTaskDescription initialTask : initialTasks) {
			this.taskQueue.add(initialTask);
		}
	}

	/**
	 * Our SchedulerDriver, may be null if this Scheduler is not registered.
	 * This is included on every callback from Mesos but when we initiate a call
	 * asynchronously then we may need it.
	 */
	private volatile SchedulerDriver schedulerDriver = null;

	/**
	 * Add the referenced task to the queue of work.
	 * Determine if we need to ask for more resources and 
	 * then do so if we do.
	 */
	public void addTask(AbstractTaskDescription taskDescription) {
		if(taskDescription == null) {
			return;
		}
		
		this.taskQueue.add(taskDescription);
		
		if(this.schedulerDriver != null) {
			Collection<Request> requests = new ArrayList<Request>();
			this.schedulerDriver.requestResources(requests );
		}
	}
	
	/**
	 * Kill this Scheduler
	 */
	public void kill() {
		if(this.schedulerDriver != null) {
			this.schedulerDriver.stop();
		}
	}
	
	/**
	 * Invoked when the scheduler successfully registers with a Mesos master. A
	 * unique ID (generated by the master) used for distinguishing this
	 * framework from others and MasterInfo with the ip and port of the current
	 * master are provided as arguments.
	 */
	@Override
	public void registered(SchedulerDriver schedulerDriver, FrameworkID frameworkId, MasterInfo masterInfo) {
		this.frameworkId = frameworkId;
		this.schedulerDriver = schedulerDriver;
		
		LOG.info(String.format( "registered(%s, %s, %s)", schedulerDriver.toString(), frameworkId.toString(), masterInfo.toString()) );
	}

	/**
	 * Invoked when the scheduler becomes "disconnected" from the master (e.g.,
	 * the master fails and another is taking over).
	 */
	@Override
	public void disconnected(SchedulerDriver schedulerDriver) {
		this.schedulerDriver = null;
		LOG.info(String.format( "disconnected(%s)", schedulerDriver.toString()) );
	}

	/**
	 * Invoked when the scheduler re-registers with a newly elected Mesos
	 * master. This is only called when the scheduler has previously been
	 * registered. MasterInfo containing the updated information about the
	 * elected master is provided as an argument.
	 */
	@Override
	public void reregistered(SchedulerDriver schedulerDriver, MasterInfo masterInfo) {
		this.schedulerDriver = schedulerDriver;
		LOG.info(String.format( "reregistered(%s, %s)", schedulerDriver.toString(), masterInfo.toString()) );
	}

	// ====================================================================================================
	// Framework Status Messages
	// ====================================================================================================

	/**
	 * Invoked when an executor sends a message. These messages are best effort;
	 * do not expect a framework message to be retransmitted in any reliable
	 * fashion.
	 */
	@Override
	public void frameworkMessage(SchedulerDriver schedulerDriver, ExecutorID executorId, SlaveID slaveId, byte[] message) {
		LOG.info(String.format( "frameworkMessage(%s, %s, %s, ... )", schedulerDriver.toString(), executorId.toString(), slaveId.toString()) );
	}

	/**
	 * Invoked when an executor has exited/terminated. Note that any tasks
	 * running will have TASK_LOST status updates automagically generated.
	 */
	@Override
	public void executorLost(SchedulerDriver schedulerDriver, ExecutorID executorId, SlaveID slaveId, int status) {
		LOG.info(String.format( "executorLost(%s, %s, %s, %d )", schedulerDriver.toString(), executorId.toString(), slaveId.toString(), status) );
	}

	// ====================================================================================================
	// Slave Lost
	// ====================================================================================================

	/**
	 * Invoked when a slave has been determined unreachable (e.g., machine
	 * failure, network partition). Most frameworks will need to reschedule any
	 * tasks launched on this slave on a new slave.
	 * NOTE: we should get TASK_LOST messages for each of the lost tasks, so no
	 * restarting of tasks is needed here.
	 */
	@Override
	public void slaveLost(SchedulerDriver schedulerDriver, SlaveID slaveId) {
		LOG.info(String.format( "offerRescinded(%s, %s)", schedulerDriver.toString(), slaveId.toString()) );
	}

	// ====================================================================================================
	// Task Status Update
	// ====================================================================================================

	private void addTaskToSubmittedMap(TaskID taskID, AbstractTaskDescription taskDescription) {
		if(taskID == null || taskDescription == null) {
			throw new IllegalArgumentException("Both taskID and taskDescription must be non-null.");
		}
		synchronized(this.submittedTasks) {
			this.submittedTasks.put(taskID, taskDescription);
		}
	}
	
	private AbstractTaskDescription removeTaskFromSubmittedMap(TaskID taskID) {
		if(taskID == null) {
			throw new IllegalArgumentException("taskID must be non-null.");
		}
		AbstractTaskDescription atd = null;
		synchronized(this.submittedTasks) {
			atd = this.submittedTasks.remove(taskID);
		}
		
		return atd;
	}
	
	private AbstractTaskDescription getTaskFromSubmittedMap(TaskID taskID) {
		if(taskID == null) {
			throw new IllegalArgumentException("taskID must be non-null.");
		}
		AbstractTaskDescription atd = null;
		synchronized(this.submittedTasks) {
			atd = this.submittedTasks.get(taskID);
		}
		
		return atd;
	}
	
	/**
	 * Invoked when the status of a task has changed (e.g., a slave is lost and
	 * so the task is lost, a task finishes and an executor sends a status
	 * update saying so, etc). Note that returning from this callback
	 * _acknowledges_ receipt of this status update! If for whatever reason the
	 * scheduler aborts during this callback (or the process exits) another
	 * status update will be delivered (note, however, that this is currently
	 * not true if the slave sending the status update is lost/fails during that
	 * time).
	 */
	@Override
	public void statusUpdate(SchedulerDriver schedulerDriver, TaskStatus taskStatus) {
		switch(taskStatus.getState()) {
		case TASK_STAGING:
		case TASK_STARTING:
			break;				// in general we don't care 'cause things are just doing what they are supposed to do
		case TASK_RUNNING:
			break;				// again, we don't care 'cause things are just doing what they are supposed to do
			
		case TASK_KILLED:
			LOG.warn(String.format("Task [%s] has been killed (%s).", 
					taskStatus.getTaskId().toString(),
					taskStatus.hasMessage() ? taskStatus.getMessage() : "no further information available.")
			);
			removeTaskFromSubmittedMap(taskStatus.getTaskId());
			break;
			
		case TASK_FAILED:
			LOG.warn(String.format("Task [%s] has failed. %s", 
					taskStatus.getTaskId().toString(),
					taskStatus.hasMessage() ? taskStatus.getMessage() : "no further information available.")
			);
			removeTaskFromSubmittedMap(taskStatus.getTaskId());
			break;
			
		case TASK_FINISHED:
			// happy path, just remove it from the list of running tasks
			removeTaskFromSubmittedMap(taskStatus.getTaskId());
			break;
			
		case TASK_LOST:
			LOG.warn(String.format("Task [%s] has been lost, attempting to restart. %s", 
					taskStatus.getTaskId().toString(),
					taskStatus.hasMessage() ? taskStatus.getMessage() : "no further information available.")
			);
			AbstractTaskDescription taskDescription = removeTaskFromSubmittedMap(taskStatus.getTaskId());
			this.taskQueue.add(taskDescription);
			
			// restart the task here
			break;
			
		}
		
		LOG.info(String.format( "statusUpdate(%s, %s)", schedulerDriver.toString(), taskStatus.toString()) );
	}

	// ====================================================================================================
	// Resource Offers
	// ====================================================================================================

	/**
	 * Invoked when resources have been offered to this framework. A single
	 * offer will only contain resources from a single slave. Resources
	 * associated with an offer will not be re-offered to _this_ framework until
	 * either (a) this framework has rejected those resources (see
	 * SchedulerDriver::launchTasks) or (b) those resources have been rescinded
	 * (see Scheduler::offerRescinded). Note that resources may be concurrently
	 * offered to more than one framework at a time (depending on the allocator
	 * being used). In that case, the first framework to launch tasks using
	 * those resources will be able to use them while the other frameworks will
	 * have those resources rescinded (or if a framework has already launched
	 * tasks with those resources then those tasks will fail with a TASK_LOST
	 * status and a message saying as much).
	 */
	@Override
	public void resourceOffers(SchedulerDriver schedulerDriver, List<Offer> offers) {
		LOG.info(String.format( "resourceOffers(%d)", offers == null ? 0 : offers.size()) );
		
		// nothing to do, return immediately
		if(this.taskQueue.isEmpty()) {
			LOG.info("resourceOffers but no tasks ... guess I'll go eat worms." );
			for(Offer offer : offers) {
				schedulerDriver.declineOffer(offer.getId());
			}
			return;
		}
		
		// maintain a list of the offer IDs from which we take resources
		// this is needed when we send task execution requests to Mesos
		Set<OfferID> offerIds = new HashSet<OfferID>(offers.size());
		List<TaskInfo> tasksInfo = new ArrayList<TaskInfo>();

		// Keep track of whether all tasks have been started, to decide if there
		// are offers that we should decline when all tasks are running
		boolean allTasksStarted = false;
		
		// Iterate over the Offers received until we have started all of the tasks 
		for (Offer offer : offers) {
			
			// if all tasks have been started then decline the remainder of the offers
			if(allTasksStarted) {
				schedulerDriver.declineOffer(offer.getId());
				continue;
			}
			
			int availableCpus = Integer.MAX_VALUE;
			int availableMemory = Integer.MAX_VALUE;
			int availableDisk = Integer.MAX_VALUE;
			List<Range> portRanges = Collections.singletonList(Range.newBuilder().setBegin(1025l).setEnd(655535l).build());

			LOG.info(String.format("Received Offer [%s]", offer.getId()));
			
			for(Resource resource : offer.getResourcesList()) {
				if( "cpus".equals(resource.getName()) ) {
					availableCpus = (int)resource.getScalar().getValue();
					LOG.info( String.format("\tOffer cpus(%d)", availableCpus) );
				}
				if( "mem".equals(resource.getName()) ) {
					availableMemory = (int)resource.getScalar().getValue();
					LOG.info( String.format("\tOffer memory(%d)", availableMemory) );
				}
				if( "disk".equals(resource.getName()) ) {
					availableDisk = (int)resource.getScalar().getValue();
					LOG.info( String.format("\tOffer disk(%d)", availableDisk) );
				}
				if( "ports".equals(resource.getName()) ) {
					Ranges ranges = resource.getRanges();
					portRanges = ranges.getRangeList();
					LOG.info( "\tOffer port ranges:");
					for(Range portRange : portRanges) {
						LOG.info( String.format("\t\t(%d - %d)", 
								portRange.hasBegin() ? portRange.getBegin() : -1l, 
								portRange.hasEnd() ? portRange.getEnd() : -1l) );
					}
				}
			}
			
			offerIds.add(offer.getId());		// we are going to use at least some of the resources offered
			
			// no claims are being made as to the efficiency of synchronizing the entire block
			synchronized(this.taskQueue) {
				// NOTE that it is possible that the task at the end of the queue could ask for more than the available resources
				// while later tasks could run with the available resources ... for now, that is just the way it is.
				// Say that it "enforces ordering of tasks" and it sounds better.
				for( AbstractTaskDescription currentTaskDescription = this.taskQueue.peek(); 
						currentTaskDescription != null; 
						currentTaskDescription = this.taskQueue.peek() ) {
					
					// NOTE: the way that this is coded now, the tasks MUST use the default CPU and memory
					// otherwise the Offer may not have enough remaining resources for the task to run.
					// Someday we should change this to look at what the task actually needs.
					if(availableCpus < DEFAULT_CPUS && availableMemory < DEFAULT_MEMORY) {
						// kick us out to the next Offer if one exists, else wait for resources to be freed
						break;
					}
					currentTaskDescription = this.taskQueue.poll();		// claim the task from the queue
					
					// Use UUID for TaskId so we have something unique.
					// We should prepend some kind of group identifier to help in debugging.
					TaskID taskId = TaskID.newBuilder().setValue(UUID.randomUUID().toString()).build();
	
					System.out.println("Launching task " + taskId.getValue());

//					CommandInfo simpleCommandInfo = CommandInfo.newBuilder()
//							.setShell(true)
//							.setValue("touch mesos.test")
//							.build();
					
					// the TaskInfo is sent through Mesos to the Executor.launchTask() method
					ExecutorInfo executor = ExecutorInfo.newBuilder(executorInfo).build();
					LOG.info(String.format("Executor will run \"%s\"", executor.getCommand().getValue()));
					
					ByteString startupData = ByteString.copyFromUtf8("Hello World");
					
					TaskInfo.Builder taskInfoBuilder = TaskInfo.newBuilder()
							.setName("task " + taskId.getValue())
							.setTaskId(taskId)
							.setSlaveId(offer.getSlaveId())
							.addResources(buildCpuResourceConstraint(DEFAULT_CPUS))
							.addResources(buildMemoryResourceConstraint(DEFAULT_MEMORY))
//							.setCommand(simpleCommandInfo)
							.setExecutor(executor)
							.setData(startupData);
					ContainerInfo containerInfo = currentTaskDescription.buildContainerInfo();
					if(containerInfo != null) {
						taskInfoBuilder.setContainer(containerInfo);
					}
					
					TaskInfo taskInfo = taskInfoBuilder.build();
					tasksInfo.add(taskInfo);
					availableCpus -= DEFAULT_CPUS;
					availableMemory -= DEFAULT_MEMORY;
					
					this.addTaskToSubmittedMap(taskId, currentTaskDescription);
				}
				
				allTasksStarted = this.taskQueue.isEmpty();
			}		// end of synch block
		}
		
		Filters filters = Filters.newBuilder().setRefuseSeconds(1).build();
		Status status = schedulerDriver.launchTasks(offerIds, tasksInfo, filters);
		
		switch(status) {
		case DRIVER_ABORTED:
			LOG.info(String.format( "DRIVER_ABORTED(%s, %d)", schedulerDriver.toString(), offers == null ? 0 : offers.size()) );
			break;
		case DRIVER_NOT_STARTED:
			LOG.info(String.format( "DRIVER_NOT_STARTED(%s, %d)", schedulerDriver.toString(), offers == null ? 0 : offers.size()) );
			break;
		case DRIVER_STOPPED:
			LOG.info(String.format( "DRIVER_STOPPED(%s, %d)", schedulerDriver.toString(), offers == null ? 0 : offers.size()) );
			break;
		case DRIVER_RUNNING:
			LOG.info(String.format( "DRIVER_RUNNING(%s, %d)", schedulerDriver.toString(), offers == null ? 0 : offers.size()) );
			break;
		}
	}

	/**
	 * Create a CPU resource constraint
	 * 
	 * @param cpus
	 * @return
	 */
	private Resource buildCpuResourceConstraint(int cpus) {
		cpus = Math.max(1, cpus);		// must be at least 1 core
		return Resource.newBuilder()
			.setName("cpus")
			.setType(Value.Type.SCALAR)
			.setScalar(Value.Scalar.newBuilder().setValue(cpus))
			.build();
	}

	/**
	 * Create a memory resource constraint
	 * 
	 * @param sizeInMegabytes
	 * @return
	 */
	private Resource buildMemoryResourceConstraint(int sizeInMegabytes) {
		sizeInMegabytes = Math.max(1, sizeInMegabytes);		// must be at least 1M
		return Resource.newBuilder()
			.setName("mem")
			.setType(Value.Type.SCALAR)
			.setScalar(Value.Scalar.newBuilder().setValue(sizeInMegabytes))
			.build();
	}
	
	/**
	 * If this Scheduler retains resource offers, without starting tasks on those resources,
	 * then the offers referenced in this method should be removed from the list of
	 * available resources.
	 * 
	 * Invoked when an offer is no longer valid (e.g., the slave was lost or
	 * another framework used resources in the offer). If for whatever reason an
	 * offer is never rescinded (e.g., dropped message, failing over framework,
	 * etc.), a framework that attempts to launch tasks using an invalid offer
	 * will receive TASK_LOST status updates for those tasks (see
	 * Scheduler::resourceOffers).
	 */
	public void offerRescinded(SchedulerDriver schedulerDriver, OfferID offer) {
		LOG.info(String.format( "offerRescinded(%s, %d)", schedulerDriver.toString(), offer.toString()) );
	}

	// ====================================================================================================
	// Report an Error
	// ====================================================================================================

	/**
	 * Invoked when there is an unrecoverable error in the scheduler or
	 * scheduler driver. The driver will be aborted BEFORE invoking this
	 * callback.
	 */
	public void error(SchedulerDriver schedulerDriver, String msg) {
		LOG.info(String.format( "error(%s, %s)", schedulerDriver.toString(), msg) );
	}

}