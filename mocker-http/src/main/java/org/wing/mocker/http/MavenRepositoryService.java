package org.wing.mocker.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wing.mocker.http.model.PomLocation;

import java.net.URL;
import java.net.URLConnection;

@Component
public class MavenRepositoryService {
    @Value("${nexus.server}")
    private String nexusServerHost;
    @Value("${nexus.server}/nexus/content/groups/public")
    private String downloadBaseUrl;
    @Value("${nexus.server}/nexus/service/local/artifact/maven/resolve")
    private String queryJarSiteUrl;

    public String getJarClassURL(PomLocation pomLocation) {
        String siteUrl;
        if (isSnapshotVersion(pomLocation.getVersion())) {
            siteUrl = getSnapshotSite(pomLocation);
        } else {
            siteUrl = getReleaseSite(pomLocation);
        }
        return downloadBaseUrl + siteUrl;
    }


    private String getReleaseSite(PomLocation pomLocation) {
        return pomLocation.getGroupId().replace(".", "/") + "/" + pomLocation.getArtifactId() + "/" + pomLocation.getVersion() + ".jar";
    }

    private String getSnapshotSite(PomLocation pomLocation) {
        String queryString = String.format("r=snapshots&g=%s&a=%s&v=%s&e=jar", pomLocation.getGroupId(), pomLocation.getArtifactId(), pomLocation.getVersion());
        URLConnection urlConnection = null;
        try {
            URL httpUrl = new URL(queryJarSiteUrl + "?" + queryString);
            urlConnection = httpUrl.openConnection();
            urlConnection.setRequestProperty("accept", "application/json");
            JsonNode jsonNode = new ObjectMapper().readValue(urlConnection.getInputStream(), JsonNode.class);
            return jsonNode.get("data").get("repositoryPath").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.getInputStream().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private boolean isSnapshotVersion(String version) {
        return version.toUpperCase().indexOf("SNAPSHOT")>0;
    }

}

