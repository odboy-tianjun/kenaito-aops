/*
 * Kruise
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v1.21.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.openkruise.client.models;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;
import java.util.Objects;

/**
 * KruiseAppsV1alpha1NodePodProbeSpecProbes
 */
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1ContainerProbe {
    @SerializedName("containerName")
    private String containerName = null;

    @SerializedName("name")
    private String name = null;

    @SerializedName("probe")
    private KruiseAppsV1alpha1ContainerProbeSpec probe = null;

    public KruiseAppsV1alpha1ContainerProbe containerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    /**
     * container name
     *
     * @return containerName
     **/
    @ApiModelProperty(required = true, value = "container name")
    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public KruiseAppsV1alpha1ContainerProbe name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name is podProbeMarker.Name#probe.Name
     *
     * @return name
     **/
    @ApiModelProperty(required = true, value = "Name is podProbeMarker.Name#probe.Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KruiseAppsV1alpha1ContainerProbe probe(KruiseAppsV1alpha1ContainerProbeSpec probe) {
        this.probe = probe;
        return this;
    }

    /**
     * Get probe
     *
     * @return probe
     **/
    @ApiModelProperty(required = true, value = "")
    public KruiseAppsV1alpha1ContainerProbeSpec getProbe() {
        return probe;
    }

    public void setProbe(KruiseAppsV1alpha1ContainerProbeSpec probe) {
        this.probe = probe;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1ContainerProbe)) {
            return false;
        }
        KruiseAppsV1alpha1ContainerProbe ioKruiseAppsV1alpha1NodePodProbeSpecProbes = (KruiseAppsV1alpha1ContainerProbe) o;
        return Objects.equals(this.containerName, ioKruiseAppsV1alpha1NodePodProbeSpecProbes.containerName) &&
                Objects.equals(this.name, ioKruiseAppsV1alpha1NodePodProbeSpecProbes.name) &&
                Objects.equals(this.probe, ioKruiseAppsV1alpha1NodePodProbeSpecProbes.probe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containerName, name, probe);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1NodePodProbeSpecProbes {\n");

        sb.append("    containerName: ").append(toIndentedString(containerName)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    probe: ").append(toIndentedString(probe)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

