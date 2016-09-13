package com.paypal.credit.mesos.taskbalancer;

import org.apache.mesos.Protos;
import org.apache.mesos.Protos.ContainerInfo;
import org.apache.mesos.Protos.ContainerInfo.DockerInfo;

/**
 * An immutable object containing a non-null and non-empty docker image name 
 * and a possibly empty command.
 * 
 */
public class DockerTaskDescription 
extends CommandTaskDescription {
	private final String dockerImage;
	
	public DockerTaskDescription(String dockerImage, String command) {
		super(command);
		if(dockerImage == null || dockerImage.length() == 0)
			throw new IllegalArgumentException("'dockerImage' must be a non-empty String");
		this.dockerImage = dockerImage;
	}

	public String getDockerImage() {
		return dockerImage;
	}
	
	/**
	 * 
	 * @param imageName
	 * @return
	 */
	@Override
	public ContainerInfo buildContainerInfo() {
		Protos.ContainerInfo.Builder containerInfoBuilder = Protos.ContainerInfo.newBuilder()
				.setType(Protos.ContainerInfo.Type.DOCKER)
        		.setDocker(buildDockerInfo(getDockerImage()))
        		;
        
        return containerInfoBuilder.build();
	}

	/**
	 * Create a DockerInfo with the given image name and default network configuration.
	 * @param imageName
	 * @return
	 */
	private DockerInfo buildDockerInfo(String imageName) {
		Protos.ContainerInfo.DockerInfo.Builder dockerInfoBuilder = Protos.ContainerInfo.DockerInfo.newBuilder();
        dockerInfoBuilder.setImage(imageName);
        dockerInfoBuilder.setNetwork(Protos.ContainerInfo.DockerInfo.Network.BRIDGE);	
        
        return dockerInfoBuilder.build();
	}
}
