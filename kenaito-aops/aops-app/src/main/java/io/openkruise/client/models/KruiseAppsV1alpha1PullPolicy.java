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
import java.util.Objects;

/**
 * PullPolicy is an optional field to set parameters of the pulling task. If not specified, the system will use the default values.
 */
@ApiModel(description = "PullPolicy is an optional field to set parameters of the pulling task. If not specified, the system will use the default values.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1PullPolicy {
    @SerializedName("backoffLimit")
    private Integer backoffLimit = null;

    @SerializedName("timeoutSeconds")
    private Integer timeoutSeconds = null;

    public KruiseAppsV1alpha1PullPolicy backoffLimit(Integer backoffLimit) {
        this.backoffLimit = backoffLimit;
        return this;
    }

    /**
     * Specifies the number of retries before marking the pulling task failed. Defaults to 3
     *
     * @return backoffLimit
     **/
    @ApiModelProperty(value = "Specifies the number of retries before marking the pulling task failed. Defaults to 3")
    public Integer getBackoffLimit() {
        return backoffLimit;
    }

    public void setBackoffLimit(Integer backoffLimit) {
        this.backoffLimit = backoffLimit;
    }

    public KruiseAppsV1alpha1PullPolicy timeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    /**
     * Specifies the timeout of the pulling task. Defaults to 600
     *
     * @return timeoutSeconds
     **/
    @ApiModelProperty(value = "Specifies the timeout of the pulling task. Defaults to 600")
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1PullPolicy)) {
            return false;
        }
        KruiseAppsV1alpha1PullPolicy ioKruiseAppsV1alpha1ImageListPullJobSpecPullPolicy = (KruiseAppsV1alpha1PullPolicy) o;
        return Objects.equals(this.backoffLimit, ioKruiseAppsV1alpha1ImageListPullJobSpecPullPolicy.backoffLimit) &&
                Objects.equals(this.timeoutSeconds, ioKruiseAppsV1alpha1ImageListPullJobSpecPullPolicy.timeoutSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backoffLimit, timeoutSeconds);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1ImageListPullJobSpecPullPolicy {\n");

        sb.append("    backoffLimit: ").append(toIndentedString(backoffLimit)).append("\n");
        sb.append("    timeoutSeconds: ").append(toIndentedString(timeoutSeconds)).append("\n");
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

