package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.deploy.DeploymentRequest;
import com.kloudtek.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDeploymentResult extends DeploymentResult {
    private static final Logger logger = LoggerFactory.getLogger(HDeploymentResult.class);
    static final String DEPLOYMENT_FAILED = "DEPLOYMENT_FAILED";
    static final String STARTED = "STARTED";
    private HApplication application;

    public HDeploymentResult(HApplication application) {
        this.application = application;
    }

    @Override
    public void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException {
        ThreadUtils.sleep(2000);
        long expires = System.currentTimeMillis() + timeout;
        for (; ; ) {
            application = application.refresh();
            if (application.getDesiredStatus().equalsIgnoreCase("UPDATED")) {
                // app hasn't started yet
            } else if (application.isStarted()) {
                return;
            } else if (DEPLOYMENT_FAILED.equals(application.getLastReportedStatus())) {
                logger.debug("Deployment failed due to status: " + application.getLastReportedStatus());
                throw ApplicationDeploymentFailedException.create(application);
            } else {
                if (expires > System.currentTimeMillis()) {
                    ThreadUtils.sleep(retryDelay);
                } else {
                    logger.error("Deployment failed due to timeout");
                    throw ApplicationDeploymentFailedException.create(application);
                }
            }
        }
    }
}
