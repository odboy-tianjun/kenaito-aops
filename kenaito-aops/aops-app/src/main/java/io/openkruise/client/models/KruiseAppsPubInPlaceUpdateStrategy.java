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
 * InPlaceUpdateStrategy contains strategies for in-place update.
 */
@ApiModel(description = "InPlaceUpdateStrategy contains strategies for in-place update.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsPubInPlaceUpdateStrategy {
    @SerializedName("gracePeriodSeconds")
    private Integer gracePeriodSeconds = null;

    public KruiseAppsPubInPlaceUpdateStrategy gracePeriodSeconds(Integer gracePeriodSeconds) {
        this.gracePeriodSeconds = gracePeriodSeconds;
        return this;
    }

    /**
     * GracePeriodSeconds is the timespan between set Pod status to not-ready and update images in Pod spec when in-place update a Pod.
     *
     * @return gracePeriodSeconds
     **/
    @ApiModelProperty(value = "GracePeriodSeconds is the timespan between set Pod status to not-ready and update images in Pod spec when in-place update a Pod.")
    public Integer getGracePeriodSeconds() {
        return gracePeriodSeconds;
    }

    public void setGracePeriodSeconds(Integer gracePeriodSeconds) {
        this.gracePeriodSeconds = gracePeriodSeconds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsPubInPlaceUpdateStrategy)) {
            return false;
        }
        KruiseAppsPubInPlaceUpdateStrategy ioKruiseAppsV1alpha1CloneSetSpecUpdateStrategyInPlaceUpdateStrategy = (KruiseAppsPubInPlaceUpdateStrategy) o;
        return Objects.equals(this.gracePeriodSeconds, ioKruiseAppsV1alpha1CloneSetSpecUpdateStrategyInPlaceUpdateStrategy.gracePeriodSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gracePeriodSeconds);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1CloneSetSpecUpdateStrategyInPlaceUpdateStrategy {\n");

        sb.append("    gracePeriodSeconds: ").append(toIndentedString(gracePeriodSeconds)).append("\n");
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

