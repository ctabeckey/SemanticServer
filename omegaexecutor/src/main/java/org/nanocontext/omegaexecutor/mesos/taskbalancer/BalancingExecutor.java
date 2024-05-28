/**
 * 
 */
package com.paypal.credit.mesos.taskbalancer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos.ExecutorInfo;
import org.apache.mesos.Protos.FrameworkInfo;
import org.apache.mesos.Protos.SlaveInfo;
import org.apache.mesos.Protos.Status;
import org.apache.mesos.Protos.TaskID;
import org.apache.mesos.Protos.TaskInfo;
import org.apache.mesos.Protos.TaskState;
import org.apache.mesos.Protos.TaskStatus;

import com.google.protobuf.ByteString;

/**
 * An Executor executor process is launched on slave nodes to run the framework’s tasks.
 *  
 * 
 * @author cbeckey
 *
 */
public class BalancingExecutor 
implements Executor {
	private static final Logger LOG = Logger.getLogger(BalancingExecutor.class);
	private List<TaskThread> knownTasks = new ArrayList<TaskThread>();
	
	/**
	 * 
	 */
	public BalancingExecutor() {
		LOG.info("<ctor>");
	}

	/**
	 * Invoked once the executor driver has been able to successfully connect with Mesos. 
	 * In particular, a scheduler can pass some data to it's executors through the ExecutorInfo#getData() field.
	 * @param driver - The executor driver that was registered and connected to the Mesos cluster.
	 * @param executorInfo - Describes information about the executor that was registered.
	 * @param frameworkInfo - Describes the framework that was registered.
	 * @param slaveInfo - Describes the slave that will be used to launch the tasks for this executor.
	 * 
	 * @see org.apache.mesos.Executor#registered(org.apache.mesos.ExecutorDriver, org.apache.mesos.Protos.ExecutorInfo, org.apache.mesos.Protos.FrameworkInfo, org.apache.mesos.Protos.SlaveInfo)
	 */
	@Override
	public void registered(ExecutorDriver driver, ExecutorInfo executorInfo, FrameworkInfo frameworkInfo, SlaveInfo slaveInfo) {
		LOG.info(String.format( "registered(%s, %s, %s, %s)", 
				driver.toString(), executorInfo.toString(), frameworkInfo.toString(), slaveInfo.toString()) );
		
		// send a message to the Scheduler that a new executor is available
		String slaveId = slaveInfo.getId().getValue();
		driver.sendFrameworkMessage(String.format("REGISTERED|%s", slaveId).getBytes());
	}

	/**
	 * Invoked when the executor becomes "disconnected" from the slave (e.g., the slave is being restarted due to an upgrade).
	 * @param driver - The executor driver that was disconnected.
	 * 
	 * @see org.apache.mesos.Executor#disconnected(org.apache.mesos.ExecutorDriver)
	 */
	@Override
	public void disconnected(ExecutorDriver driver) {
		LOG.info(String.format( "disconnected(%s)", driver.toString()) );
		
		// send a message to the Scheduler that a new executor is available
		driver.sendFrameworkMessage("DISCONNECTED".getBytes());
	}

	/**
	 * Invoked when the executor re-registers with a restarted slave.
	 * @param driver - The executor driver that was re-registered with the Mesos master.
	 * @param slaveInfo - Describes the slave that will be used to launch the tasks for this executor.
	 * 
	 * @see org.apache.mesos.Executor#reregistered(org.apache.mesos.ExecutorDriver, org.apache.mesos.Protos.SlaveInfo)
	 */
	@Override
	public void reregistered(ExecutorDriver driver, SlaveInfo slaveInfo) {
		LOG.info(String.format( "reregistered(%s, %s)", driver.toString(), slaveInfo.toString()) );
		
		// send a message to the Scheduler that an executor was re-registered
		String slaveId = slaveInfo.getId().getValue();
		driver.sendFrameworkMessage(String.format("REREGISTERED|%s", slaveId).getBytes());
	}

	/**
	 * Invoked when a fatal error has occurred with the executor and/or executor driver. 
	 * The driver will be aborted BEFORE invoking this callback.
	 * 
	 * @param driver - The executor driver that was aborted due this error.
	 * @param msg - The error message.
	 * 
	 * @see org.apache.mesos.Executor#error(org.apache.mesos.ExecutorDriver, String)
	 */
	@Override
	public void error(ExecutorDriver driver, String msg) {
		LOG.info(String.format( "error(%s, %s)", driver.toString(), msg) );
		// send a message to the Scheduler that an executor was re-registered
		driver.sendFrameworkMessage(String.format("ERROR|%s", msg).getBytes());
	}

	/**
	 * Invoked when a framework message has arrived for this executor. 
	 * These messages are best effort; do not expect a framework message to be retransmitted in any reliable fashion.
	 * 
	 * @param driver - The executor driver that received the message.
	 * @param msg - The message payload.
	 * 
	 * @see org.apache.mesos.Executor#frameworkMessage(org.apache.mesos.ExecutorDriver, byte[])
	 */
	@Override
	public void frameworkMessage(ExecutorDriver driver, byte[] msg) {
		LOG.info(String.format( "frameworkMessage(%s)", new String(msg)) );
	}

	/**
	 * Invoked when a task has been launched on this executor 
	 * (initiated via SchedulerDriver.launchTasks(java.util.Collection<OfferID>, java.util.Collection<TaskInfo>, Filters). 
	 * Note that this task can be realized with a thread, a process, or some simple computation, however, no other callbacks 
	 * will be invoked on this executor until this callback has returned.
	 * 
	 * @param driver - The executor driver that launched the task.
	 * @param taskInfo - Describes the task that was launched.
	 * 
	 * @see org.apache.mesos.Executor#launchTask(org.apache.mesos.ExecutorDriver, org.apache.mesos.Protos.TaskInfo)
	 */
	@Override
	public void launchTask(ExecutorDriver driver, TaskInfo taskInfo) {
		LOG.info( String.format("launchTask(%s, %s, [%s])", 
				taskInfo.hasCommand() ? "COMMAND" : "NO_COMMAND", 
				taskInfo.hasContainer() ? "CONTAINER" : "NO_CONTAINER", 
				taskInfo.getData() != null ? taskInfo.getData().toStringUtf8() : "<null>") );
		TaskThread taskThread = new TaskThread(driver, taskInfo);
		knownTasks.add(taskThread);
		taskThread.start();
	}

	/**
	 * 
	 */
	private static class TaskThread extends Thread {
		private final ExecutorDriver driver;
		private final TaskInfo taskInfo;
		private boolean kill = false;

		/**
		 * 
		 * @param driver
		 * @param taskInfo
		 */
		private TaskThread(ExecutorDriver driver, TaskInfo taskInfo) {
			this.driver = driver;
			this.taskInfo = taskInfo;
		}

		TaskID getTaskID() {
			return this.taskInfo.getTaskId();
		}
		
		/**
		 * 
		 */
		public void run() {
			TaskStatus status = TaskStatus.newBuilder().setTaskId(taskInfo.getTaskId()).setState(TaskState.TASK_STARTING).build();
			driver.sendStatusUpdate(status);

			LOG.info("Running task " + taskInfo.getTaskId());
			
			try {
				
				status = TaskStatus.newBuilder()
						.setTaskId(taskInfo.getTaskId())
						.setState(TaskState.TASK_RUNNING)
						.build();
				driver.sendStatusUpdate(status);

				if( taskInfo.hasData() ) {
					ByteString taskData = taskInfo.getData();
					
					// parse the task data and run some task based on that
					// ...
					
					// our pseudo-task
					for(int i=0; i < 5 && !this.kill; ++i) {
						Thread.sleep(5000l);
					}
				}
				
				if(this.kill) {
					status = TaskStatus.newBuilder()
							.setTaskId(taskInfo.getTaskId())
							.setState(TaskState.TASK_KILLED)
							.build();
				}
				else {
					status = TaskStatus.newBuilder()
							.setTaskId(taskInfo.getTaskId())
							.setState(TaskState.TASK_FINISHED)
							.build();
				}
			} 
			catch (Exception e) {
				status = TaskStatus.newBuilder()
						.setTaskId(taskInfo.getTaskId()).setState(TaskState.TASK_FAILED)
						.setMessage(e.getMessage())
						.build();
				e.printStackTrace();
			}
			finally {
				driver.sendStatusUpdate(status);
			}
		}

		/**
		 * 
		 */
		public void killTask() {
			this.kill = true;
		}
	}
	
	/**
	 * Invoked when a task running within this executor has been killed (via SchedulerDriver.killTask(TaskID)). 
	 * Note that no status update will be sent on behalf of the executor, the executor is responsible for creating 
	 * a new TaskStatus (i.e., with TASK_KILLED) and invoking ExecutorDriver.sendStatusUpdate(TaskStatus).
	 * 
	 * @param driver - The executor driver that owned the task that was killed.
	 * @param taskID - The ID of the task that was killed.
	 * 
	 * @see org.apache.mesos.Executor#killTask(org.apache.mesos.ExecutorDriver, org.apache.mesos.Protos.TaskID)
	 */
	@Override
	public void killTask(ExecutorDriver driver, TaskID taskID) {
		LOG.info(String.format( "killTask(%s, %s)", driver.toString(), taskID.toString()) );
		TaskStatus taskStatus;
		
		TaskThread taskThread = findKnownTask(taskID);
		if(taskThread != null) {
			this.knownTasks.remove(taskThread);
			
			if( taskThread.isAlive() ) {
				taskThread.killTask();
			}
			
			taskStatus = TaskStatus.newBuilder()
					.setTaskId(taskID)
					.setMessage("KILLED")
					.setState(TaskState.TASK_KILLED)
					.build();
		}
		else {
			taskStatus = TaskStatus.newBuilder()
					.setTaskId(taskID)
					.setMessage("LOST")
					.setState(TaskState.TASK_LOST)			// need to confirm this is correct behavior
					.build();
		}
		
		driver.sendStatusUpdate(taskStatus );
	}

	/**
	 * 
	 * @param taskID
	 * @return
	 */
	private TaskThread findKnownTask(TaskID taskID) {
		if(taskID == null) {
			return null;
		}
		for(TaskThread knownTask : this.knownTasks) {
			if( taskID.equals(knownTask.getTaskID()) ) {
				return knownTask;
			}
		}
		return null;
	}

	/**
	 * void shutdown(ExecutorDriver driver)
	 * 
	 * Invoked when the executor should terminate all of it's currently running tasks. 
	 * Note that after Mesos has determined that an executor has terminated any tasks that the 
	 * executor did not send terminal status updates for (e.g. TASK_KILLED, TASK_FINISHED, TASK_FAILED, etc) 
	 * a TASK_LOST status update will be created.
	 * 
	 * @param driver - The executor driver that should terminate.
	 * 
	 * @see org.apache.mesos.Executor#shutdown(org.apache.mesos.ExecutorDriver)
	 */
	@Override
	public void shutdown(ExecutorDriver driver) {
		for(TaskThread knownTask : this.knownTasks) {
			killTask(driver, knownTask.getTaskID());
		}
		
		LOG.info(String.format( "shutdown(%s", driver.toString()) );
		driver.stop();
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) 
	throws Exception {
		System.out.println(String.format("Starting %s ...", BalancingExecutor.class.getName()));
		
		MesosExecutorDriver driver = new MesosExecutorDriver(new BalancingExecutor());
		Status status = driver.run();

		System.out.println(String.format("%s is exiting.", BalancingExecutor.class.getName()));
		System.exit(status == Status.DRIVER_STOPPED ? 0 : 1);
	}
}
