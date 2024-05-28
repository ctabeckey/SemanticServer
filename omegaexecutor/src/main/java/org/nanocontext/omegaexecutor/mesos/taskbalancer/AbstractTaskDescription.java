package com.paypal.credit.mesos.taskbalancer;

import org.apache.mesos.Protos.CommandInfo;
import org.apache.mesos.Protos.ContainerInfo;

/**
 * 
 * @author cbeckey
 *
 */
public abstract class AbstractTaskDescription {

	protected AbstractTaskDescription() {
	}
	
	public abstract ContainerInfo buildContainerInfo();

	public abstract CommandInfo buildCommandInfo();
}
