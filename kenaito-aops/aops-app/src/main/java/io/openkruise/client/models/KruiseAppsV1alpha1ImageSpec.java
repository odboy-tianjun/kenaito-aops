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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ImageSpec defines the pulling spec of an image
 */
@ApiModel(description = "ImageSpec defines the pulling spec of an image")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1ImageSpec {
    @SerializedName("pullSecrets")
    private List<KruiseAppsV1alpha1ReferenceObject> pullSecrets = null;

    @SerializedName("sandboxConfig")
    private KruiseAppsV1alpha1SandboxConfig sandboxConfig = null;

    @SerializedName("tags")
    private List<KruiseAppsV1alpha1ImageTagSpec> tags = new ArrayList<KruiseAppsV1alpha1ImageTagSpec>();

    public KruiseAppsV1alpha1ImageSpec pullSecrets(List<KruiseAppsV1alpha1ReferenceObject> pullSecrets) {
        this.pullSecrets = pullSecrets;
        return this;
    }

    public KruiseAppsV1alpha1ImageSpec addPullSecretsItem(KruiseAppsV1alpha1ReferenceObject pullSecretsItem) {
        if (this.pullSecrets == null) {
            this.pullSecrets = new ArrayList<KruiseAppsV1alpha1ReferenceObject>();
        }
        this.pullSecrets.add(pullSecretsItem);
        return this;
    }

    /**
     * PullSecrets is an optional list of references to secrets in the same namespace to use for pulling the image. If specified, these secrets will be passed to individual puller implementations for them to use.  For example, in the case of docker, only DockerConfig type secrets are honored.
     *
     * @return pullSecrets
     **/
    @ApiModelProperty(value = "PullSecrets is an optional list of references to secrets in the same namespace to use for pulling the image. If specified, these secrets will be passed to individual puller implementations for them to use.  For example, in the case of docker, only DockerConfig type secrets are honored.")
    public List<KruiseAppsV1alpha1ReferenceObject> getPullSecrets() {
        return pullSecrets;
    }

    public void setPullSecrets(List<KruiseAppsV1alpha1ReferenceObject> pullSecrets) {
        this.pullSecrets = pullSecrets;
    }

    public KruiseAppsV1alpha1ImageSpec sandboxConfig(KruiseAppsV1alpha1SandboxConfig sandboxConfig) {
        this.sandboxConfig = sandboxConfig;
        return this;
    }

    /**
     * Get sandboxConfig
     *
     * @return sandboxConfig
     **/
    @ApiModelProperty(value = "")
    public KruiseAppsV1alpha1SandboxConfig getSandboxConfig() {
        return sandboxConfig;
    }

    public void setSandboxConfig(KruiseAppsV1alpha1SandboxConfig sandboxConfig) {
        this.sandboxConfig = sandboxConfig;
    }

    public KruiseAppsV1alpha1ImageSpec tags(List<KruiseAppsV1alpha1ImageTagSpec> tags) {
        this.tags = tags;
        return this;
    }

    public KruiseAppsV1alpha1ImageSpec addTagsItem(KruiseAppsV1alpha1ImageTagSpec tagsItem) {
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * Tags is a list of versions of this image
     *
     * @return tags
     **/
    @ApiModelProperty(required = true, value = "Tags is a list of versions of this image")
    public List<KruiseAppsV1alpha1ImageTagSpec> getTags() {
        return tags;
    }

    public void setTags(List<KruiseAppsV1alpha1ImageTagSpec> tags) {
        this.tags = tags;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1ImageSpec)) {
            return false;
        }
        KruiseAppsV1alpha1ImageSpec ioKruiseAppsV1alpha1NodeImageSpecImages = (KruiseAppsV1alpha1ImageSpec) o;
        return Objects.equals(this.pullSecrets, ioKruiseAppsV1alpha1NodeImageSpecImages.pullSecrets) &&
                Objects.equals(this.sandboxConfig, ioKruiseAppsV1alpha1NodeImageSpecImages.sandboxConfig) &&
                Objects.equals(this.tags, ioKruiseAppsV1alpha1NodeImageSpecImages.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pullSecrets, sandboxConfig, tags);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1NodeImageSpecImages {\n");

        sb.append("    pullSecrets: ").append(toIndentedString(pullSecrets)).append("\n");
        sb.append("    sandboxConfig: ").append(toIndentedString(sandboxConfig)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

