package com.wl4g.devops.umc.store;

import com.wl4g.devops.common.bean.umc.model.third.ZookeeperStatInfo;

/**
 * Zookeeper(Memory/network/core/disk) monitor data store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public interface ZookeeperMetricStore extends MetricStore {

	boolean save(ZookeeperStatInfo zookeeper);

}