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
import org.joda.time.DateTime;
import javax.annotation.Generated;
import java.util.Objects;

/**
 * UnitedDeploymentCondition describes current state of a UnitedDeployment.
 */
@ApiModel(description = "UnitedDeploymentCondition describes current state of a UnitedDeployment.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1UnitedDeploymentCondition {
    @SerializedName("lastTransitionTime")
    private DateTime lastTransitionTime = null;

    @SerializedName("message")
    private String message = null;

    @SerializedName("reason")
    private String reason = null;

    @SerializedName("status")
    private String status = null;

    @SerializedName("type")
    private String type = null;

    public KruiseAppsV1alpha1UnitedDeploymentCondition lastTransitionTime(DateTime lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
        return this;
    }

    /**
     * Last time the condition transitioned from one status to another.
     *
     * @return lastTransitionTime
     **/
    @ApiModelProperty(value = "Last time the condition transitioned from one status to another.")
    public DateTime getLastTransitionTime() {
        return lastTransitionTime;
    }

    public void setLastTransitionTime(DateTime lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
    }

    public KruiseAppsV1alpha1UnitedDeploymentCondition message(String message) {
        this.message = message;
        return this;
    }

    /**
     * A human readable message indicating details about the transition.
     *
     * @return message
     **/
    @ApiModelProperty(value = "A human readable message indicating details about the transition.")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public KruiseAppsV1alpha1UnitedDeploymentCondition reason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * The reason for the condition&#39;s last transition.
     *
     * @return reason
     **/
    @ApiModelProperty(value = "The reason for the condition's last transition.")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public KruiseAppsV1alpha1UnitedDeploymentCondition status(String status) {
        this.status = status;
        return this;
    }

    /**
     * Status of the condition, one of True, False, Unknown.
     *
     * @return status
     **/
    @ApiModelProperty(value = "Status of the condition, one of True, False, Unknown.")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public KruiseAppsV1alpha1UnitedDeploymentCondition type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Type of in place set condition.
     *
     * @return type
     **/
    @ApiModelProperty(value = "Type of in place set condition.")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1UnitedDeploymentCondition)) {
            return false;
        }
        KruiseAppsV1alpha1UnitedDeploymentCondition ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions = (KruiseAppsV1alpha1UnitedDeploymentCondition) o;
        return Objects.equals(this.lastTransitionTime, ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions.lastTransitionTime) &&
                Objects.equals(this.message, ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions.message) &&
                Objects.equals(this.reason, ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions.reason) &&
                Objects.equals(this.status, ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions.status) &&
                Objects.equals(this.type, ioKruiseAppsV1alpha1UnitedDeploymentStatusConditions.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastTransitionTime, message, reason, status, type);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1UnitedDeploymentStatusConditions {\n");

        sb.append("    lastTransitionTime: ").append(toIndentedString(lastTransitionTime)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

