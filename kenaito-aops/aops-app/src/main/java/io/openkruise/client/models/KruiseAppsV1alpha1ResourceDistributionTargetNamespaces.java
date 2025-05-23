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
 * If ExcludedNamespaces is not empty, Resource will never be distributed to the listed namespaces. ExcludedNamespaces has the highest priority.
 */
@ApiModel(description = "If ExcludedNamespaces is not empty, Resource will never be distributed to the listed namespaces. ExcludedNamespaces has the highest priority.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1ResourceDistributionTargetNamespaces {
    @SerializedName("list")
    private List<KruiseAppsV1alpha1ResourceDistributionNamespace> _list = null;

    public KruiseAppsV1alpha1ResourceDistributionTargetNamespaces _list(List<KruiseAppsV1alpha1ResourceDistributionNamespace> _list) {
        this._list = _list;
        return this;
    }

    public KruiseAppsV1alpha1ResourceDistributionTargetNamespaces addListItem(KruiseAppsV1alpha1ResourceDistributionNamespace _listItem) {
        if (this._list == null) {
            this._list = new ArrayList<KruiseAppsV1alpha1ResourceDistributionNamespace>();
        }
        this._list.add(_listItem);
        return this;
    }

    /**
     * Get _list
     *
     * @return _list
     **/
    @ApiModelProperty(value = "")
    public List<KruiseAppsV1alpha1ResourceDistributionNamespace> getList() {
        return _list;
    }

    public void setList(List<KruiseAppsV1alpha1ResourceDistributionNamespace> _list) {
        this._list = _list;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1ResourceDistributionTargetNamespaces)) {
            return false;
        }
        KruiseAppsV1alpha1ResourceDistributionTargetNamespaces ioKruiseAppsV1alpha1ResourceDistributionSpecTargetsExcludedNamespaces = (KruiseAppsV1alpha1ResourceDistributionTargetNamespaces) o;
        return Objects.equals(this._list, ioKruiseAppsV1alpha1ResourceDistributionSpecTargetsExcludedNamespaces._list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_list);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1ResourceDistributionSpecTargetsExcludedNamespaces {\n");

        sb.append("    _list: ").append(toIndentedString(_list)).append("\n");
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

