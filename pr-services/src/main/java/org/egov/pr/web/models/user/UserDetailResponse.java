package org.egov.pr.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pr.web.models.OwnerInfo;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserDetailResponse {
    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("user")
    List<OwnerInfo> user;
}
