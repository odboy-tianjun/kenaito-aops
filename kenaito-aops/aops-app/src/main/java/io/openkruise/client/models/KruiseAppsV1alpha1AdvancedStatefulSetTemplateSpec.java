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
 * AdvancedStatefulSet template
 */
@ApiModel(description = "AdvancedStatefulSet template")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec {
    @SerializedName("metadata")
    private V1ObjectMeta metadata = null;

    @SerializedName("spec")
    private KruiseAppsV1beta1StatefulSetSpec spec = null;

    public KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec metadata(V1ObjectMeta metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Get metadata
     *
     * @return metadata
     **/
    @ApiModelProperty(value = "")
    public V1ObjectMeta getMetadata() {
        return metadata;
    }

    public void setMetadata(V1ObjectMeta metadata) {
        this.metadata = metadata;
    }

    public KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec spec(KruiseAppsV1beta1StatefulSetSpec spec) {
        this.spec = spec;
        return this;
    }

    /**
     * Get spec
     *
     * @return spec
     **/
    @ApiModelProperty(required = true, value = "")
    public KruiseAppsV1beta1StatefulSetSpec getSpec() {
        return spec;
    }

    public void setSpec(KruiseAppsV1beta1StatefulSetSpec spec) {
        this.spec = spec;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec)) {
            return false;
        }
        KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec ioKruiseAppsV1alpha1UnitedDeploymentSpecTemplateAdvancedStatefulSetTemplate = (KruiseAppsV1alpha1AdvancedStatefulSetTemplateSpec) o;
        return Objects.equals(this.metadata, ioKruiseAppsV1alpha1UnitedDeploymentSpecTemplateAdvancedStatefulSetTemplate.metadata) &&
                Objects.equals(this.spec, ioKruiseAppsV1alpha1UnitedDeploymentSpecTemplateAdvancedStatefulSetTemplate.spec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, spec);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1UnitedDeploymentSpecTemplateAdvancedStatefulSetTemplate {\n");

        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    spec: ").append(toIndentedString(spec)).append("\n");
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

