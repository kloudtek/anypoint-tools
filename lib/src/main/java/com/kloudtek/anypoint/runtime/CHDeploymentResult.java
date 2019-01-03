package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CHDeploymentResult extends DeploymentResult {
    private static final Logger logger = LoggerFactory.getLogger(CHDeploymentResult.class);
    static final String DEPLOYMENT_FAILED = "DEPLOYMENT_FAILED";
    static final String DEPLOY_FAILED = "DEPLOY_FAILED";
    static final String STARTED = "STARTED";
    public static final String UNDEPLOYED = "UNDEPLOYED";
    private CHApplication application;

    public CHDeploymentResult(CHApplication application) {
        this.application = application;
    }

    @Override
    public void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException {
        ThreadUtils.sleep(2000);
        long expires = System.currentTimeMillis() + timeout;
        long lastUpdt = application.getLastUpdateTime();
        for (; ; ) {
            try {
                application = application.refresh();
                if (DEPLOYMENT_FAILED.equalsIgnoreCase(application.getStatus())) {
                    logger.debug("Deployment failed due to status: " + application.getStatus());
                    throw new ApplicationDeploymentFailedException();
                } else if (DEPLOY_FAILED.equalsIgnoreCase(application.getDeploymentUpdateStatus())) {
                    logger.debug("Deployment failed due to deployment update status: " + application.getDeploymentUpdateStatus());
                    throw new ApplicationDeploymentFailedException();
                } else if (application.getStatus().equalsIgnoreCase(STARTED)
                        && application.getDeploymentUpdateStatus() == null
                        && application.getLastUpdateTime() > lastUpdt) {
                    return;
                }
            } catch (NotFoundException e) {
                // application
            }
            if (expires > System.currentTimeMillis()) {
                ThreadUtils.sleep(retryDelay);
            } else {
                logger.error("Deployment failed due to timeout");
                throw new ApplicationDeploymentFailedException();
            }
        }
    }
}
