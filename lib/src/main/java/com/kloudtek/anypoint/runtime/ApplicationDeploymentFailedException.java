package com.kloudtek.anypoint.runtime;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDeploymentFailedException extends Exception {
    private int failed;
    private int successful;
    private int other;
    private List<String> messages;

    public ApplicationDeploymentFailedException() {
        super("Failed to deploy application");
    }

    public ApplicationDeploymentFailedException(int failed, int successful, int other, List<String> messages) {
        super("Failed to deploy application (failed: " + failed + ", successful: " + successful + ", other: " + other + ") : " + messages);
        this.failed = failed;
        this.successful = successful;
        this.other = other;
        this.messages = messages;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSuccessful() {
        return successful;
    }

    public void setSuccessful(int successful) {
        this.successful = successful;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public static ApplicationDeploymentFailedException create(HApplication application) {
        if (application.getApplicationDeployments() != null) {
            int successful = 0;
            int failed = 0;
            int other = 0;
            ArrayList<String> messages = new ArrayList<>();
            for (ApplicationDeployment deployment : application.getApplicationDeployments()) {
                if (HDeploymentResult.DEPLOYMENT_FAILED.equals(deployment.getLastReportedStatus())) {
                    failed++;
                } else if (HDeploymentResult.STARTED.equals(deployment.getLastReportedStatus())) {
                    successful++;
                } else {
                    other++;
                }
                if (deployment.getMessage() != null) {
                    messages.add(deployment.getMessage());
                }
            }
            return new ApplicationDeploymentFailedException(failed, successful, other, messages);
        } else {
            return new ApplicationDeploymentFailedException();
        }
    }
}
