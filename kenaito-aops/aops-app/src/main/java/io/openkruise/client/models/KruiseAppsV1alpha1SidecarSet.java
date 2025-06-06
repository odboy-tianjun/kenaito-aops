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
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;
import java.util.Objects;

/**
 * SidecarSet is the Schema for the sidecarsets API
 */
@ApiModel(description = "SidecarSet is the Schema for the sidecarsets API")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1SidecarSet {
    @SerializedName("apiVersion")
    private String apiVersion = null;

    @SerializedName("kind")
    private String kind = null;

    @SerializedName("metadata")
    private V1ObjectMeta metadata = null;

    @SerializedName("spec")
    private KruiseAppsV1alpha1SidecarSetSpec spec = null;

    @SerializedName("status")
    private KruiseAppsV1alpha1SidecarSetStatus status = null;

    public KruiseAppsV1alpha1SidecarSet apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * APIVersion defines the versioned schema of this representation of an object. Servers should convert recognized schemas to the latest internal value, and may reject unrecognized values. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#resources
     *
     * @return apiVersion
     **/
    @ApiModelProperty(value = "APIVersion defines the versioned schema of this representation of an object. Servers should convert recognized schemas to the latest internal value, and may reject unrecognized values. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#resources")
    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public KruiseAppsV1alpha1SidecarSet kind(String kind) {
        this.kind = kind;
        return this;
    }

    /**
     * Kind is a string value representing the REST resource this object represents. Servers may infer this from the endpoint the client submits requests to. Cannot be updated. In CamelCase. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds
     *
     * @return kind
     **/
    @ApiModelProperty(value = "Kind is a string value representing the REST resource this object represents. Servers may infer this from the endpoint the client submits requests to. Cannot be updated. In CamelCase. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds")
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public KruiseAppsV1alpha1SidecarSet metadata(V1ObjectMeta metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Standard object&#39;s metadata. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata
     *
     * @return metadata
     **/
    @ApiModelProperty(value = "Standard object's metadata. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata")
    public V1ObjectMeta getMetadata() {
        return metadata;
    }

    public void setMetadata(V1ObjectMeta metadata) {
        this.metadata = metadata;
    }

    public KruiseAppsV1alpha1SidecarSet spec(KruiseAppsV1alpha1SidecarSetSpec spec) {
        this.spec = spec;
        return this;
    }

    /**
     * Get spec
     *
     * @return spec
     **/
    @ApiModelProperty(value = "")
    public KruiseAppsV1alpha1SidecarSetSpec getSpec() {
        return spec;
    }

    public void setSpec(KruiseAppsV1alpha1SidecarSetSpec spec) {
        this.spec = spec;
    }

    public KruiseAppsV1alpha1SidecarSet status(KruiseAppsV1alpha1SidecarSetStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     **/
    @ApiModelProperty(value = "")
    public KruiseAppsV1alpha1SidecarSetStatus getStatus() {
        return status;
    }

    public void setStatus(KruiseAppsV1alpha1SidecarSetStatus status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1SidecarSet)) {
            return false;
        }
        KruiseAppsV1alpha1SidecarSet ioKruiseAppsV1alpha1SidecarSet = (KruiseAppsV1alpha1SidecarSet) o;
        return Objects.equals(this.apiVersion, ioKruiseAppsV1alpha1SidecarSet.apiVersion) &&
                Objects.equals(this.kind, ioKruiseAppsV1alpha1SidecarSet.kind) &&
                Objects.equals(this.metadata, ioKruiseAppsV1alpha1SidecarSet.metadata) &&
                Objects.equals(this.spec, ioKruiseAppsV1alpha1SidecarSet.spec) &&
                Objects.equals(this.status, ioKruiseAppsV1alpha1SidecarSet.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, kind, metadata, spec, status);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1SidecarSet {\n");

        sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
        sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    spec: ").append(toIndentedString(spec)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

