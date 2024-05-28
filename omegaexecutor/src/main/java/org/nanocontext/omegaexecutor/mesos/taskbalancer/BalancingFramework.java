package com.paypal.credit.mesos.taskbalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos.CommandInfo;
import org.apache.mesos.Protos.Credential;
import org.apache.mesos.Protos.ExecutorID;
import org.apache.mesos.Protos.ExecutorInfo;
import org.apache.mesos.Protos.FrameworkID;
import org.apache.mesos.Protos.FrameworkInfo;
import org.apache.mesos.Protos.Status;
import org.apache.mesos.Scheduler;

import com.google.protobuf.ByteString;

/**
 * A framework running on top of Mesos consists of two components: 
 * a scheduler that registers with the master to be offered resources, 
 * and an executor process that is launched on slave nodes to run the framework’s tasks
 * 
 * You can write a framework scheduler in C, C++, Java/Scala, or Python.
 * Your framework scheduler should inherit from the Scheduler class (see
 * API below). Your scheduler should create a SchedulerDriver (which
 * will mediate communication between your scheduler and the Mesos
 * master) and then call SchedulerDriver.run()

 * 
 * @author cbeckey
 *
 */
public class BalancingFramework {
	private static final Logger LOG = Logger.getLogger(BalancingFramework.class);

	/**
	 * Private constructor to prevent instantiation
	 */
	private BalancingFramework() {}

	/**
	 * 
	 */
	private static void usage() {
		String name = BalancingFramework.class.getName();
		System.err.println("Usage: " + name + " <master-ip:master-port> <task info> [<task info>]*");
		System.err.println("where:");
		System.err.println("\t<master-ip:master-port> is the socket address of the Mesos master");
		System.err.println("\t<task info> is the docker image and command, delimited with :.  e.g. Ubuntu:dir");
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			usage();
			System.exit(1);
		}
		String masterSocketAddress = args[0];
		List<AbstractTaskDescription> tasks = createTaskList(Arrays.copyOfRange(args, 1, args.length));		
		
		FrameworkID frameworkId = FrameworkID.newBuilder()
				.setValue("TryStuffFrameworkID")
				.build();
		LOG.info(String.format("Creating FrameworkInfo [%s]", frameworkId.toString()));
		
		FrameworkInfo.Builder frameworkBuilder = FrameworkInfo.newBuilder()
				.setId(frameworkId )
				.setUser("") // Have Mesos fill in the current user.
				.setName("TryStuffFramework");
		
		// From http://mesos.apache.org/documentation/latest/app-framework-development-guide/
		// You need to put your executor somewhere that all slaves on the cluster can get it from. 
		// If you are running HDFS, you can put your executor into HDFS. 
		// Then, you tell Mesos where it is via the ExecutorInfo parameter of MesosSchedulerDriver’s constructor
		//String uri = new File("./startTryStuffExecutor").getCanonicalPath();
		//CommandInfo.newBuilder().setValue(uri).build();
		
		ExecutorID executorId = ExecutorID.newBuilder().setValue("TryStuffExecutorID").build();
		CommandInfo startExecutorCommandInfo = CommandInfo.newBuilder()
				.setShell(true)
				.setValue("/var/mesos/frameworks/startTryStuffExecutor.sh")
				.build();
		ExecutorInfo tryStuffExecutorInfo = ExecutorInfo.newBuilder()
				.setExecutorId(executorId)
				.setCommand(startExecutorCommandInfo)
				.setName("TryStuffExecutor")
				.setSource("TryStuff")
				.build();

		// create the Scheduler with the specified number of tasks
		Scheduler scheduler = new BalancingScheduler(tryStuffExecutorInfo, tasks);

		MesosSchedulerDriver driver = null;
		Credential credential = createCredentials(); 
		
		// 
		if(credential != null) {
			frameworkBuilder.setPrincipal(credential.getPrincipal());
			driver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), masterSocketAddress, credential);
		}
		else {
			frameworkBuilder.setPrincipal("test-framework-java");
			driver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), masterSocketAddress);
		}

		int status = driver.run() == Status.DRIVER_STOPPED ? 0 : 1;

		// Ensure that the driver process terminates.
		driver.stop();

		System.exit(status);
	}

	/**
	 * Take an array of args, each in the form of [<container>]:[<command>] and create a 
	 * List of AbstractTaskDescription.
	 * If the <container> exists then create a Docker Container task, which may include an initial command
	 * If the <command> exists and not the <container> then create a Command task
	 * 
	 * @param args
	 * @return
	 */
	private static List<AbstractTaskDescription> createTaskList(String[] args) {
		List<AbstractTaskDescription> taskList = new ArrayList<AbstractTaskDescription>(args.length);
		
		for(String arg : args) {
			String[] argElements = arg.split(":");
			if(argElements.length == 2) {
				if(argElements[0] != null && argElements[0].length() > 0) {
					taskList.add(new DockerTaskDescription(argElements[0], argElements[1]));	// start a Docker container and run a command
				}
				else {
					taskList.add(new CommandTaskDescription(argElements[1]));	// just run a command
				}
			}
			else if(argElements.length == 1) {
				taskList.add(new DockerTaskDescription(argElements[0], null));		// start a Docker Container with its default init task
			}
		}
		
		return taskList;
	}

	/**
	 * Create credentials from environment variables, or return null
	 * if the environment variables are not set.
	 * If MESOS_AUTHENTICATE exists then
	 *   DEFAULT_PRINCIPAL and DEFAULT_SECRET must exist or exit
	 * @return
	 */
	private static Credential createCredentials() {
		Credential credential = null;
		if (System.getenv("MESOS_AUTHENTICATE") != null) {
			System.out.println("Enabling authentication for the framework");

			if (System.getenv("DEFAULT_PRINCIPAL") == null) {
				System.err.println("Expecting authentication principal in the environment");
				System.exit(1);
			}

			if (System.getenv("DEFAULT_SECRET") == null) {
				System.err.println("Expecting authentication secret in the environment");
				System.exit(1);
			}

			String principalId = System.getenv("DEFAULT_PRINCIPAL");
			
			credential = Credential.newBuilder()
					.setPrincipal(principalId)
					.setSecret(System.getenv("DEFAULT_SECRET"))
					.build();
		}
		return credential;
	}
}
