package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.runtime.DeploymentResult;

import java.io.File;
import java.io.IOException;

public interface DeploymentOperation {
    DeploymentResult deploy(String appName, String filename, File file) throws IOException, HttpException;
}
