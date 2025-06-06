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
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1NodeSelectorTerm;
import io.kubernetes.client.openapi.models.V1Toleration;
import io.kubernetes.client.proto.Runtime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Subset defines the detail of a subset.
 */
@ApiModel(description = "Subset defines the detail of a subset.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1Subset {
    @SerializedName("name")
    private String name = null;

    @SerializedName("nodeSelectorTerm")
    private V1NodeSelectorTerm nodeSelectorTerm = null;

    @SerializedName("patch")
    private Runtime.RawExtension patch = null;

    @SerializedName("replicas")
    private IntOrString replicas = null;

    @SerializedName("tolerations")
    private List<V1Toleration> tolerations = null;

    public KruiseAppsV1alpha1Subset name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Indicates subset name as a DNS_LABEL, which will be used to generate subset workload name prefix in the format &#39;&lt;deployment-name&gt;-&lt;subset-name&gt;-&#39;. Name should be unique between all of the subsets under one UnitedDeployment.
     *
     * @return name
     **/
    @ApiModelProperty(required = true, value = "Indicates subset name as a DNS_LABEL, which will be used to generate subset workload name prefix in the format '<deployment-name>-<subset-name>-'. Name should be unique between all of the subsets under one UnitedDeployment.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KruiseAppsV1alpha1Subset nodeSelectorTerm(V1NodeSelectorTerm nodeSelectorTerm) {
        this.nodeSelectorTerm = nodeSelectorTerm;
        return this;
    }

    /**
     * Get nodeSelectorTerm
     *
     * @return nodeSelectorTerm
     **/
    @ApiModelProperty(value = "")
    public V1NodeSelectorTerm getNodeSelectorTerm() {
        return nodeSelectorTerm;
    }

    public void setNodeSelectorTerm(V1NodeSelectorTerm nodeSelectorTerm) {
        this.nodeSelectorTerm = nodeSelectorTerm;
    }

    public KruiseAppsV1alpha1Subset patch(Runtime.RawExtension patch) {
        this.patch = patch;
        return this;
    }

    /**
     * Patch indicates patching to the templateSpec. Patch takes precedence over other fields If the Patch also modifies the Replicas, NodeSelectorTerm or Tolerations, use value in the Patch
     *
     * @return patch
     **/
    @ApiModelProperty(value = "Patch indicates patching to the templateSpec. Patch takes precedence over other fields If the Patch also modifies the Replicas, NodeSelectorTerm or Tolerations, use value in the Patch")
    public Runtime.RawExtension getPatch() {
        return patch;
    }

    public void setPatch(Runtime.RawExtension patch) {
        this.patch = patch;
    }

    public KruiseAppsV1alpha1Subset replicas(IntOrString replicas) {
        this.replicas = replicas;
        return this;
    }

    /**
     * Indicates the number of the pod to be created under this subset. Replicas could also be percentage like &#39;10%&#39;, which means 10% of UnitedDeployment replicas of pods will be distributed under this subset. If nil, the number of replicas in this subset is determined by controller. Controller will try to keep all the subsets with nil replicas have average pods.
     *
     * @return replicas
     **/
    @ApiModelProperty(value = "Indicates the number of the pod to be created under this subset. Replicas could also be percentage like '10%', which means 10% of UnitedDeployment replicas of pods will be distributed under this subset. If nil, the number of replicas in this subset is determined by controller. Controller will try to keep all the subsets with nil replicas have average pods.")
    public IntOrString getReplicas() {
        return replicas;
    }

    public void setReplicas(IntOrString replicas) {
        this.replicas = replicas;
    }

    public KruiseAppsV1alpha1Subset tolerations(List<V1Toleration> tolerations) {
        this.tolerations = tolerations;
        return this;
    }

    public KruiseAppsV1alpha1Subset addTolerationsItem(V1Toleration tolerationsItem) {
        if (this.tolerations == null) {
            this.tolerations = new ArrayList<V1Toleration>();
        }
        this.tolerations.add(tolerationsItem);
        return this;
    }

    /**
     * Indicates the tolerations the pods under this subset have. A subset&#39;s tolerations is not allowed to be updated.
     *
     * @return tolerations
     **/
    @ApiModelProperty(value = "Indicates the tolerations the pods under this subset have. A subset's tolerations is not allowed to be updated.")
    public List<V1Toleration> getTolerations() {
        return tolerations;
    }

    public void setTolerations(List<V1Toleration> tolerations) {
        this.tolerations = tolerations;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1Subset)) {
            return false;
        }
        KruiseAppsV1alpha1Subset ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets = (KruiseAppsV1alpha1Subset) o;
        return Objects.equals(this.name, ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets.name) &&
                Objects.equals(this.nodeSelectorTerm, ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets.nodeSelectorTerm) &&
                Objects.equals(this.patch, ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets.patch) &&
                Objects.equals(this.replicas, ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets.replicas) &&
                Objects.equals(this.tolerations, ioKruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets.tolerations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nodeSelectorTerm, patch, replicas, tolerations);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1UnitedDeploymentSpecTopologySubsets {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    nodeSelectorTerm: ").append(toIndentedString(nodeSelectorTerm)).append("\n");
        sb.append("    patch: ").append(toIndentedString(patch)).append("\n");
        sb.append("    replicas: ").append(toIndentedString(replicas)).append("\n");
        sb.append("    tolerations: ").append(toIndentedString(tolerations)).append("\n");
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

