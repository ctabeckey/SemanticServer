package com.paypal.credit.mesos.taskbalancer;

import org.apache.mesos.Protos;
import org.apache.mesos.Protos.CommandInfo;
import org.apache.mesos.Protos.ContainerInfo;

public class CommandTaskDescription 
extends AbstractTaskDescription {
	private final String command;

	/**
	 * 
	 * @param command
	 */
	public CommandTaskDescription(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
	
	@Override
	public ContainerInfo buildContainerInfo() {
		return null;
	}

	/**
	 * 
	 * @param command
	 * @return
	 */
	@Override
	public CommandInfo buildCommandInfo() {
		Protos.CommandInfo.Builder commandInfoBuilder = Protos.CommandInfo.newBuilder();
		if(getCommand() != null && getCommand().length() > 0) {
			commandInfoBuilder.setShell(true);
			commandInfoBuilder.setValue(getCommand());
		}
		else {
			commandInfoBuilder.setShell(false);
		}
		return commandInfoBuilder.build();
	}
}
