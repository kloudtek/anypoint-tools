package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.*;
import com.kloudtek.unpack.transformer.Transformer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ProvisioningService {
    List<Transformer> provision(AnypointClient client, Organization parent, File file, ProvisioningConfig provisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException;
}
