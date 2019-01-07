package com.kloudtek.anypoint.runtime.manifest;

import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.exchange.AssetVersion;
import com.kloudtek.anypoint.exchange.ExchangeAsset;
import com.kloudtek.util.BackendAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

public class ReleaseManifestExchangeDAO extends ReleaseManifestDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseManifestExchangeDAO.class);

    public ReleaseManifestExchangeDAO(Organization organization, String params) throws BackendAccessException {
        super(organization);
        String[] elems = params.split(":");
        String id;
        if (elems.length == 1) {
            orgId = organization.getId();
            groupId = organization.getId();
            id = elems[0];
        } else if (elems.length == 2) {
            orgId = organization.getId();
            groupId = elems[0];
            id = elems[1];
        } else if (elems.length == 3) {
            orgId = elems[0];
            groupId = elems[1];
            id = elems[2];
        } else {
            throw new IllegalArgumentException("");
        }
        name = "Release Manifest: " + id;
        artifactId = "relmanifest-" + id;
        try {
            logger.debug("Searching exchange assets " + groupId + ":" + artifactId);
            ExchangeAsset asset = organization.findExchangeAsset(groupId, artifactId);
            int oldestVersion = 0;
            for (AssetVersion assetVersion : asset.getVersions()) {
                String v = assetVersion.getVersion();
                Matcher m = VERSION_REGEX.matcher(v);
                logger.debug("Found version" + v);
                if (!m.find()) {
                    throw new IllegalStateException("Invalid manifest version in exchange: " + v);
                }
                try {
                    int vNb = Integer.parseInt(m.group(1));
                    if (vNb > oldestVersion) {
                        oldestVersion = vNb;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid manifest version in exchange: " + v);
                }
            }
            version = oldestVersion + ".0.0";
        } catch (HttpException e) {
            throw new BackendAccessException(e);
        } catch (NotFoundException e) {
            version = "1.0.0";
        }
    }
}
