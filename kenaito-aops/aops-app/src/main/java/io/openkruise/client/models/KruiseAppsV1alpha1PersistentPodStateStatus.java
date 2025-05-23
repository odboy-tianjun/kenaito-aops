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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * KruiseAppsV1alpha1PersistentPodStateStatus
 */
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1PersistentPodStateStatus {
    @SerializedName("observedGeneration")
    private Long observedGeneration = null;

    @SerializedName("podStates")
    private Map<String, KruiseAppsV1alpha1PodState> podStates = null;

    public KruiseAppsV1alpha1PersistentPodStateStatus observedGeneration(Long observedGeneration) {
        this.observedGeneration = observedGeneration;
        return this;
    }

    /**
     * observedGeneration is the most recent generation observed for this PersistentPodState. It corresponds to the PersistentPodState&#39;s generation, which is updated on mutation by the API Server.
     *
     * @return observedGeneration
     **/
    @ApiModelProperty(required = true, value = "observedGeneration is the most recent generation observed for this PersistentPodState. It corresponds to the PersistentPodState's generation, which is updated on mutation by the API Server.")
    public Long getObservedGeneration() {
        return observedGeneration;
    }

    public void setObservedGeneration(Long observedGeneration) {
        this.observedGeneration = observedGeneration;
    }

    public KruiseAppsV1alpha1PersistentPodStateStatus podStates(Map<String, KruiseAppsV1alpha1PodState> podStates) {
        this.podStates = podStates;
        return this;
    }

    public KruiseAppsV1alpha1PersistentPodStateStatus putPodStatesItem(String key, KruiseAppsV1alpha1PodState podStatesItem) {
        if (this.podStates == null) {
            this.podStates = new HashMap<String, KruiseAppsV1alpha1PodState>();
        }
        this.podStates.put(key, podStatesItem);
        return this;
    }

    /**
     * When the pod is ready, record some status information of the pod, such as: labels, annotations, topologies, etc. map[string]PodState -&gt; map[Pod.Name]PodState
     *
     * @return podStates
     **/
    @ApiModelProperty(value = "When the pod is ready, record some status information of the pod, such as: labels, annotations, topologies, etc. map[string]PodState -> map[Pod.Name]PodState")
    public Map<String, KruiseAppsV1alpha1PodState> getPodStates() {
        return podStates;
    }

    public void setPodStates(Map<String, KruiseAppsV1alpha1PodState> podStates) {
        this.podStates = podStates;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1PersistentPodStateStatus)) {
            return false;
        }
        KruiseAppsV1alpha1PersistentPodStateStatus ioKruiseAppsV1alpha1PersistentPodStateStatus = (KruiseAppsV1alpha1PersistentPodStateStatus) o;
        return Objects.equals(this.observedGeneration, ioKruiseAppsV1alpha1PersistentPodStateStatus.observedGeneration) &&
                Objects.equals(this.podStates, ioKruiseAppsV1alpha1PersistentPodStateStatus.podStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(observedGeneration, podStates);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1PersistentPodStateStatus {\n");

        sb.append("    observedGeneration: ").append(toIndentedString(observedGeneration)).append("\n");
        sb.append("    podStates: ").append(toIndentedString(podStates)).append("\n");
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

