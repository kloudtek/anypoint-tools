package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.Service;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public interface DeploymentService extends Service {
    DeploymentResult deployOnPrem(Server target, @NotNull String name, @NotNull File file, @NotNull String filename,
                                  APIProvisioningConfig apiProvisioningConfig)
            throws IOException, HttpException, ProvisioningException;
}
