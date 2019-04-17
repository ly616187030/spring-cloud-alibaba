/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.alibaba.dubbo.service;

import org.apache.dubbo.common.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.dubbo.metadata.ServiceRestMetadata;
import org.springframework.cloud.alibaba.dubbo.metadata.repository.DubboServiceMetadataRepository;
import org.springframework.cloud.alibaba.dubbo.util.JSONUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Introspective {@link DubboMetadataService} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public class IntrospectiveDubboMetadataService implements DubboMetadataService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DubboServiceMetadataRepository dubboServiceMetadataRepository;

    @Autowired
    private JSONUtils jsonUtils;

    @Override
    public String getServiceRestMetadata() {
        Set<ServiceRestMetadata> serviceRestMetadata = dubboServiceMetadataRepository.getServiceRestMetadata();
        String serviceRestMetadataJsonConfig = null;
        if (!isEmpty(serviceRestMetadata)) {
            serviceRestMetadataJsonConfig = jsonUtils.toJSON(serviceRestMetadata);
        }
        return serviceRestMetadataJsonConfig;
    }

    @Override
    public Set<String> getAllServiceKeys() {
        return dubboServiceMetadataRepository.getAllServiceKeys();
    }

    @Override
    public Map<String, String> getAllExportedURLs() {
        Map<String, List<URL>> allExportedUrls = dubboServiceMetadataRepository.getAllExportedUrls();
        if (isEmpty(allExportedUrls)) {
            if (logger.isDebugEnabled()) {
                logger.debug("There is no registered URL.");
            }
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();

        allExportedUrls.forEach((serviceKey, urls) -> {
            result.put(serviceKey, jsonUtils.toJSON(urls));
        });

        return unmodifiableMap(result);
    }

    @Override
    public String getExportedURLs(String serviceInstance, String group, String version) {
        List<URL> urls = dubboServiceMetadataRepository.getExportedURLs(serviceInstance, group, version);
        return jsonUtils.toJSON(urls);
    }
}
