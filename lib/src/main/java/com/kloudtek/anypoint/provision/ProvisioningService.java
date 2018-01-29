package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.*;

import java.io.File;
import java.io.IOException;

public interface ProvisioningService {
    TransformList provision(AnypointClient client, Organization parent, File file, ProvisioningConfig provisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException;
}
