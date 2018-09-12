package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.runtime.HDeploymentResult;

import java.io.File;
import java.io.IOException;

public interface DeploymentOperation {
    HDeploymentResult deploy(Environment environment, String appName, String filename, File file) throws IOException, HttpException;
}
