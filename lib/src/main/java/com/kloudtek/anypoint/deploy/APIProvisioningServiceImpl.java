package com.kloudtek.anypoint.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIProvisioningServiceImpl implements APIProvisioningService {
    private static final Logger logger = LoggerFactory.getLogger(APIProvisioningServiceImpl.class);

//    @Override
//    public List<Transformer> provision(AnypointClient client, Organization org, File file, APIProvisioningConfig APIProvisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException {
//        ZipFile zipFile = new ZipFile(file);
//        ZipEntry entry = zipFile.getEntry(APIProvisioningConfig.getDescriptorLocation());
//        if (entry != null) {
//            try (InputStream inputStream = zipFile.getInputStream(entry)) {
//                APIProvisioningDescriptor APIProvisioningDescriptor = new APIProvisioningDescriptor(this, zipFile, APIProvisioningConfig, envSuffix);
//                String json = IOUtils.toString(inputStream);
//                try {
//                    APIProvisioningDescriptor = client.getJsonHelper().readJson(APIProvisioningDescriptor, json);
//                    APIProvisioningDescriptor.validate();
//                    APIProvisioningDescriptor.provision(org, APIProvisioningConfig);
//                    return APIProvisioningDescriptor.getTransformList();
//                } catch (InvalidJsonException e) {
//                    throw new InvalidAnypointDescriptorException(e.getMessage(), e);
//                }
//            }
//        } else {
//            logger.debug("No descriptor found");
//        }
//        return Collections.emptyList();
//    }
}
