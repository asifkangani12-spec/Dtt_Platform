package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.OrgBucketConfigDTO;
import com.dtt.organization.dto.OrgClientAppConfigDto;

public interface OrgBucketsIface {



    ApiResponse getBucketDetailsById(int id);

    ApiResponse getAllBucketConfigListByOuid(String ouid);


    ApiResponse getBucketConfigByAppid(String appId);

    ApiResponse getBucketsListByOuid(String ouid);

    ApiResponse getBucketHistoryByBucketId(String bucketId);

    ApiResponse addOrgBucketConfig(OrgBucketConfigDTO orgBucketConfigDTO);

    ApiResponse updateBucketConfigById(OrgBucketConfigDTO orgBucketConfigDTO);

    ApiResponse getBucketHistoryByBucketConfigId(int bucketConfigId);

    ApiResponse getBucketConfigByid(int id);


    ApiResponse addOrgClientAppConfig(OrgClientAppConfigDto orgClientAppConfigDto);

    ApiResponse enableDisableSponsorship(int id);
}
