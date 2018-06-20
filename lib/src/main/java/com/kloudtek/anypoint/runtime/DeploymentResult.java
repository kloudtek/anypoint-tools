package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.util.ThreadUtils;

public class DeploymentResult extends AnypointObject<Application> {
    static final String DEPLOYMENT_FAILED = "DEPLOYMENT_FAILED";
    static final String STARTED = "STARTED";

    public DeploymentResult(Application parent) {
        super(parent);
    }

    public Application getApplication() {
        return parent;
    }

    public void waitDeployed() throws HttpException, ApplicationDeploymentFailedException {
        waitDeployed(60000L, 1500L);
    }

    public void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException {
        long expires = System.currentTimeMillis() + timeout;
        for (; ; ) {
            String json = httpHelper.httpGet(parent.getUriPath(), parent.getParent().getParent());
            Application application = jsonHelper.readJson(new Application(parent.getParent()), json, "/data");
            if( application.getDesiredStatus().equalsIgnoreCase("UPDATED") ) {
                // app hasn't started yet
            } else if (application.isStarted()) {
                return;
            } else if (DEPLOYMENT_FAILED.equals(application.getLastReportedStatus())) {
                throw ApplicationDeploymentFailedException.create(application);
            } else {
                if (expires > System.currentTimeMillis()) {
                    ThreadUtils.sleep(retryDelay);
                } else {
                    throw ApplicationDeploymentFailedException.create(application);
                }
            }
        }
    }

}
