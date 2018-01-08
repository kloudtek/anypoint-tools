package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;

import java.io.File;
import java.io.IOException;

public interface ProvisioningService {
    TransformList provision(AnypointClient client, Organization parent, File file, ProvisioningConfig provisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException;
}
