package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.HttpException;

public abstract class DeploymentResult {
    public void waitDeployed() throws HttpException, ApplicationDeploymentFailedException {
        waitDeployed(60000L, 1500L);
    }

    public abstract void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException;
}
