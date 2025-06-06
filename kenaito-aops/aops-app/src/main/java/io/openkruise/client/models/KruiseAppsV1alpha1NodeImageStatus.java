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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * NodeImageStatus defines the observed state of NodeImage
 */
@ApiModel(description = "NodeImageStatus defines the observed state of NodeImage")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1NodeImageStatus {
    @SerializedName("desired")
    private Integer desired = null;

    @SerializedName("failed")
    private Integer failed = null;

    @SerializedName("firstSyncStatus")
    private KruiseAppsV1alpha1SyncStatus firstSyncStatus = null;

    @SerializedName("imageStatuses")
    private Map<String, KruiseAppsV1alpha1ImageStatus> imageStatuses = null;

    @SerializedName("pulling")
    private Integer pulling = null;

    @SerializedName("succeeded")
    private Integer succeeded = null;

    public KruiseAppsV1alpha1NodeImageStatus desired(Integer desired) {
        this.desired = desired;
        return this;
    }

    /**
     * The desired number of pulling tasks, this is typically equal to the number of images in spec.
     *
     * @return desired
     **/
    @ApiModelProperty(required = true, value = "The desired number of pulling tasks, this is typically equal to the number of images in spec.")
    public Integer getDesired() {
        return desired;
    }

    public void setDesired(Integer desired) {
        this.desired = desired;
    }

    public KruiseAppsV1alpha1NodeImageStatus failed(Integer failed) {
        this.failed = failed;
        return this;
    }

    /**
     * The number of pulling tasks  which reached phase Failed.
     *
     * @return failed
     **/
    @ApiModelProperty(value = "The number of pulling tasks  which reached phase Failed.")
    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public KruiseAppsV1alpha1NodeImageStatus firstSyncStatus(KruiseAppsV1alpha1SyncStatus firstSyncStatus) {
        this.firstSyncStatus = firstSyncStatus;
        return this;
    }

    /**
     * Get firstSyncStatus
     *
     * @return firstSyncStatus
     **/
    @ApiModelProperty(value = "")
    public KruiseAppsV1alpha1SyncStatus getFirstSyncStatus() {
        return firstSyncStatus;
    }

    public void setFirstSyncStatus(KruiseAppsV1alpha1SyncStatus firstSyncStatus) {
        this.firstSyncStatus = firstSyncStatus;
    }

    public KruiseAppsV1alpha1NodeImageStatus imageStatuses(Map<String, KruiseAppsV1alpha1ImageStatus> imageStatuses) {
        this.imageStatuses = imageStatuses;
        return this;
    }

    public KruiseAppsV1alpha1NodeImageStatus putImageStatusesItem(String key, KruiseAppsV1alpha1ImageStatus imageStatusesItem) {
        if (this.imageStatuses == null) {
            this.imageStatuses = new HashMap<String, KruiseAppsV1alpha1ImageStatus>();
        }
        this.imageStatuses.put(key, imageStatusesItem);
        return this;
    }

    /**
     * all statuses of active image pulling tasks
     *
     * @return imageStatuses
     **/
    @ApiModelProperty(value = "all statuses of active image pulling tasks")
    public Map<String, KruiseAppsV1alpha1ImageStatus> getImageStatuses() {
        return imageStatuses;
    }

    public void setImageStatuses(Map<String, KruiseAppsV1alpha1ImageStatus> imageStatuses) {
        this.imageStatuses = imageStatuses;
    }

    public KruiseAppsV1alpha1NodeImageStatus pulling(Integer pulling) {
        this.pulling = pulling;
        return this;
    }

    /**
     * The number of pulling tasks which are not finished.
     *
     * @return pulling
     **/
    @ApiModelProperty(value = "The number of pulling tasks which are not finished.")
    public Integer getPulling() {
        return pulling;
    }

    public void setPulling(Integer pulling) {
        this.pulling = pulling;
    }

    public KruiseAppsV1alpha1NodeImageStatus succeeded(Integer succeeded) {
        this.succeeded = succeeded;
        return this;
    }

    /**
     * The number of pulling tasks which reached phase Succeeded.
     *
     * @return succeeded
     **/
    @ApiModelProperty(value = "The number of pulling tasks which reached phase Succeeded.")
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
        if (!(o instanceof KruiseAppsV1alpha1NodeImageStatus)) {
            return false;
        }
        KruiseAppsV1alpha1NodeImageStatus ioKruiseAppsV1alpha1NodeImageStatus = (KruiseAppsV1alpha1NodeImageStatus) o;
        return Objects.equals(this.desired, ioKruiseAppsV1alpha1NodeImageStatus.desired) &&
                Objects.equals(this.failed, ioKruiseAppsV1alpha1NodeImageStatus.failed) &&
                Objects.equals(this.firstSyncStatus, ioKruiseAppsV1alpha1NodeImageStatus.firstSyncStatus) &&
                Objects.equals(this.imageStatuses, ioKruiseAppsV1alpha1NodeImageStatus.imageStatuses) &&
                Objects.equals(this.pulling, ioKruiseAppsV1alpha1NodeImageStatus.pulling) &&
                Objects.equals(this.succeeded, ioKruiseAppsV1alpha1NodeImageStatus.succeeded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desired, failed, firstSyncStatus, imageStatuses, pulling, succeeded);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1NodeImageStatus {\n");

        sb.append("    desired: ").append(toIndentedString(desired)).append("\n");
        sb.append("    failed: ").append(toIndentedString(failed)).append("\n");
        sb.append("    firstSyncStatus: ").append(toIndentedString(firstSyncStatus)).append("\n");
        sb.append("    imageStatuses: ").append(toIndentedString(imageStatuses)).append("\n");
        sb.append("    pulling: ").append(toIndentedString(pulling)).append("\n");
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

