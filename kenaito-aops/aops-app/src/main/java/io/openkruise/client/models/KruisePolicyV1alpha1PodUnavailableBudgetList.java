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
import io.kubernetes.client.openapi.models.V1ListMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PodUnavailableBudgetList is a list of PodUnavailableBudget
 */
@ApiModel(description = "PodUnavailableBudgetList is a list of PodUnavailableBudget")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruisePolicyV1alpha1PodUnavailableBudgetList {
    @SerializedName("apiVersion")
    private String apiVersion = null;

    @SerializedName("items")
    private List<KruisePolicyV1alpha1PodUnavailableBudget> items = new ArrayList<KruisePolicyV1alpha1PodUnavailableBudget>();

    @SerializedName("kind")
    private String kind = null;

    @SerializedName("metadata")
    private V1ListMeta metadata = null;

    public KruisePolicyV1alpha1PodUnavailableBudgetList apiVersion(String apiVersion) {
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

    public KruisePolicyV1alpha1PodUnavailableBudgetList items(List<KruisePolicyV1alpha1PodUnavailableBudget> items) {
        this.items = items;
        return this;
    }

    public KruisePolicyV1alpha1PodUnavailableBudgetList addItemsItem(KruisePolicyV1alpha1PodUnavailableBudget itemsItem) {
        this.items.add(itemsItem);
        return this;
    }

    /**
     * List of podunavailablebudgets. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md
     *
     * @return items
     **/
    @ApiModelProperty(required = true, value = "List of podunavailablebudgets. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md")
    public List<KruisePolicyV1alpha1PodUnavailableBudget> getItems() {
        return items;
    }

    public void setItems(List<KruisePolicyV1alpha1PodUnavailableBudget> items) {
        this.items = items;
    }

    public KruisePolicyV1alpha1PodUnavailableBudgetList kind(String kind) {
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

    public KruisePolicyV1alpha1PodUnavailableBudgetList metadata(V1ListMeta metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Standard list metadata. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds
     *
     * @return metadata
     **/
    @ApiModelProperty(value = "Standard list metadata. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#types-kinds")
    public V1ListMeta getMetadata() {
        return metadata;
    }

    public void setMetadata(V1ListMeta metadata) {
        this.metadata = metadata;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruisePolicyV1alpha1PodUnavailableBudgetList)) {
            return false;
        }
        KruisePolicyV1alpha1PodUnavailableBudgetList ioKruisePolicyV1alpha1PodUnavailableBudgetList = (KruisePolicyV1alpha1PodUnavailableBudgetList) o;
        return Objects.equals(this.apiVersion, ioKruisePolicyV1alpha1PodUnavailableBudgetList.apiVersion) &&
                Objects.equals(this.items, ioKruisePolicyV1alpha1PodUnavailableBudgetList.items) &&
                Objects.equals(this.kind, ioKruisePolicyV1alpha1PodUnavailableBudgetList.kind) &&
                Objects.equals(this.metadata, ioKruisePolicyV1alpha1PodUnavailableBudgetList.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, items, kind, metadata);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruisePolicyV1alpha1PodUnavailableBudgetList {\n");

        sb.append("    apiVersion: ").append(toIndentedString(apiVersion)).append("\n");
        sb.append("    items: ").append(toIndentedString(items)).append("\n");
        sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
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

