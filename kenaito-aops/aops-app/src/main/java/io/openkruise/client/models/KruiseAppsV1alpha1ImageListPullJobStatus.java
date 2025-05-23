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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ImageListPullJobStatus defines the observed state of ImageListPullJob
 */
@ApiModel(description = "ImageListPullJobStatus defines the observed state of ImageListPullJob")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1ImageListPullJobStatus {
    @SerializedName("active")
    private Integer active = null;

    @SerializedName("completed")
    private Integer completed = null;

    @SerializedName("completionTime")
    private DateTime completionTime = null;

    @SerializedName("desired")
    private Integer desired = null;

    @SerializedName("failedImageStatuses")
    private List<KruiseAppsV1alpha1FailedImageStatus> failedImageStatuses = null;

    @SerializedName("startTime")
    private DateTime startTime = null;

    @SerializedName("succeeded")
    private Integer succeeded = null;

    public KruiseAppsV1alpha1ImageListPullJobStatus active(Integer active) {
        this.active = active;
        return this;
    }

    /**
     * The number of running ImagePullJobs which are acknowledged by the imagepulljob controller.
     *
     * @return active
     **/
    @ApiModelProperty(value = "The number of running ImagePullJobs which are acknowledged by the imagepulljob controller.")
    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus completed(Integer completed) {
        this.completed = completed;
        return this;
    }

    /**
     * The number of ImagePullJobs which are finished
     *
     * @return completed
     **/
    @ApiModelProperty(value = "The number of ImagePullJobs which are finished")
    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus completionTime(DateTime completionTime) {
        this.completionTime = completionTime;
        return this;
    }

    /**
     * Represents time when the all the image pull job was completed. It is not guaranteed to be set in happens-before order across separate operations. It is represented in RFC3339 form and is in UTC.
     *
     * @return completionTime
     **/
    @ApiModelProperty(value = "Represents time when the all the image pull job was completed. It is not guaranteed to be set in happens-before order across separate operations. It is represented in RFC3339 form and is in UTC.")
    public DateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(DateTime completionTime) {
        this.completionTime = completionTime;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus desired(Integer desired) {
        this.desired = desired;
        return this;
    }

    /**
     * The desired number of ImagePullJobs, this is typically equal to the number of len(spec.Images).
     *
     * @return desired
     **/
    @ApiModelProperty(required = true, value = "The desired number of ImagePullJobs, this is typically equal to the number of len(spec.Images).")
    public Integer getDesired() {
        return desired;
    }

    public void setDesired(Integer desired) {
        this.desired = desired;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus failedImageStatuses(List<KruiseAppsV1alpha1FailedImageStatus> failedImageStatuses) {
        this.failedImageStatuses = failedImageStatuses;
        return this;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus addFailedImageStatusesItem(KruiseAppsV1alpha1FailedImageStatus failedImageStatusesItem) {
        if (this.failedImageStatuses == null) {
            this.failedImageStatuses = new ArrayList<KruiseAppsV1alpha1FailedImageStatus>();
        }
        this.failedImageStatuses.add(failedImageStatusesItem);
        return this;
    }

    /**
     * The status of ImagePullJob which has the failed nodes(status.Failed&gt;0) .
     *
     * @return failedImageStatuses
     **/
    @ApiModelProperty(value = "The status of ImagePullJob which has the failed nodes(status.Failed>0) .")
    public List<KruiseAppsV1alpha1FailedImageStatus> getFailedImageStatuses() {
        return failedImageStatuses;
    }

    public void setFailedImageStatuses(List<KruiseAppsV1alpha1FailedImageStatus> failedImageStatuses) {
        this.failedImageStatuses = failedImageStatuses;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus startTime(DateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Represents time when the job was acknowledged by the job controller. It is not guaranteed to be set in happens-before order across separate operations. It is represented in RFC3339 form and is in UTC.
     *
     * @return startTime
     **/
    @ApiModelProperty(value = "Represents time when the job was acknowledged by the job controller. It is not guaranteed to be set in happens-before order across separate operations. It is represented in RFC3339 form and is in UTC.")
    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public KruiseAppsV1alpha1ImageListPullJobStatus succeeded(Integer succeeded) {
        this.succeeded = succeeded;
        return this;
    }

    /**
     * The number of image pull job which are finished and status.Succeeded&#x3D;&#x3D;status.Desired.
     *
     * @return succeeded
     **/
    @ApiModelProperty(value = "The number of image pull job which are finished and status.Succeeded==status.Desired.")
    public Integer getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Integer succeeded) {
        this.succeeded = succeeded;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1ImageListPullJobStatus)) {
            return false;
        }
        KruiseAppsV1alpha1ImageListPullJobStatus ioKruiseAppsV1alpha1ImageListPullJobStatus = (KruiseAppsV1alpha1ImageListPullJobStatus) o;
        return Objects.equals(this.active, ioKruiseAppsV1alpha1ImageListPullJobStatus.active) &&
                Objects.equals(this.completed, ioKruiseAppsV1alpha1ImageListPullJobStatus.completed) &&
                Objects.equals(this.completionTime, ioKruiseAppsV1alpha1ImageListPullJobStatus.completionTime) &&
                Objects.equals(this.desired, ioKruiseAppsV1alpha1ImageListPullJobStatus.desired) &&
                Objects.equals(this.failedImageStatuses, ioKruiseAppsV1alpha1ImageListPullJobStatus.failedImageStatuses) &&
                Objects.equals(this.startTime, ioKruiseAppsV1alpha1ImageListPullJobStatus.startTime) &&
                Objects.equals(this.succeeded, ioKruiseAppsV1alpha1ImageListPullJobStatus.succeeded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, completed, completionTime, desired, failedImageStatuses, startTime, succeeded);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1ImageListPullJobStatus {\n");

        sb.append("    active: ").append(toIndentedString(active)).append("\n");
        sb.append("    completed: ").append(toIndentedString(completed)).append("\n");
        sb.append("    completionTime: ").append(toIndentedString(completionTime)).append("\n");
        sb.append("    desired: ").append(toIndentedString(desired)).append("\n");
        sb.append("    failedImageStatuses: ").append(toIndentedString(failedImageStatuses)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    succeeded: ").append(toIndentedString(succeeded)).append("\n");
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

