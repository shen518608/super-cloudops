package com.wl4g.devops.umc.opentsdb;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_CPU;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_FREE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_INODES_FREE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_INODES_TOTAL;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_INODES_USED;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_INODES_USED_PERCENT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_TOTAL;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_USED;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_DISK_USED_PERCENT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_BUFFERS;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_CACHE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_FREE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_TOTAL;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_USED;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_MEM_USED_PERCENT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_CLOSE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_CLOSE_WAIT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_CLOSING;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_COUNT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_DOWN;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_ESTAB;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_LISTEN;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_TIME_WAIT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.METRIC_NET_UP;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_DISK_DEVICE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_DISK_NET_PORT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_ID;

import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.model.physical.Cpu;
import com.wl4g.devops.common.bean.umc.model.physical.Disk;
import com.wl4g.devops.common.bean.umc.model.physical.Disk.DiskInfo;
import com.wl4g.devops.common.bean.umc.model.physical.Disk.PartitionStat;
import com.wl4g.devops.common.bean.umc.model.physical.Disk.Usage;
import com.wl4g.devops.common.bean.umc.model.physical.Mem;
import com.wl4g.devops.common.bean.umc.model.physical.Net;
import com.wl4g.devops.common.bean.umc.model.physical.Net.NetInfo;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.PhysicalMetricStore;

/**
 * OpenTSDB foundation store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class TsdbPhysicalMetricStore implements PhysicalMetricStore {

	final protected OpenTSDBClient client;

	public TsdbPhysicalMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(Cpu cpu) {
		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳
		Assert.notNull(cpu, "cpu is null");
		Assert.notEmpty(cpu.getCpu(), "cpu is null");
		Assert.hasText(cpu.getPhysicalId(), "id can not null");
		Point point = Point.metric(METRIC_CPU).tag(TAG_ID, cpu.getPhysicalId()).value(timestamp, cpu.getCpu()[0]).build();
		client.put(point);
		return true;
	}

	@Override
	public boolean save(Mem mem) {
		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳
		Assert.notNull(mem, "mem is null");
		Assert.notNull(mem.getMemInfo(), "mem info is null");
		Mem.MemInfo memInfo = mem.getMemInfo();
		Point total = Point.metric(METRIC_MEM_TOTAL).tag(TAG_ID, mem.getPhysicalId()).value(timestamp, memInfo.getTotal())
				.build();
		Point used = Point.metric(METRIC_MEM_USED).tag(TAG_ID, mem.getPhysicalId()).value(timestamp, memInfo.getUsed()).build();
		Point free = Point.metric(METRIC_MEM_FREE).tag(TAG_ID, mem.getPhysicalId()).value(timestamp, memInfo.getFree()).build();
		Point usedPercent = Point.metric(METRIC_MEM_USED_PERCENT).tag(TAG_ID, mem.getPhysicalId())
				.value(timestamp, memInfo.getUsedPercent()).build();
		Point buffers = Point.metric(METRIC_MEM_BUFFERS).tag(TAG_ID, mem.getPhysicalId()).value(timestamp, memInfo.getBuffers())
				.build();
		Point cache = Point.metric(METRIC_MEM_CACHE).tag(TAG_ID, mem.getPhysicalId()).value(timestamp, memInfo.getCached())
				.build();
		client.put(total);
		client.put(used);
		client.put(free);
		client.put(usedPercent);
		client.put(buffers);
		client.put(cache);
		return true;
	}

	@Override
	public boolean save(Disk disk) {
		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳
		Assert.notNull(disk, "disk is null");
		Assert.notNull(disk.getDiskInfos(), "disks info is null");
		for (DiskInfo diskInfo : disk.getDiskInfos()) {
			PartitionStat partitionStat = diskInfo.getPartitionStat();
			Usage usage = diskInfo.getUsage();

			Point total = Point.metric(METRIC_DISK_TOTAL).tag(TAG_ID, disk.getPhysicalId())
					// .tag(TAG_DISK_MOUNT_POINT,partitionStat.getMountpoint())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getTotal()).build();
			Point free = Point.metric(METRIC_DISK_FREE).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getFree()).build();
			Point used = Point.metric(METRIC_DISK_USED).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getUsed()).build();
			Point usedPercent = Point.metric(METRIC_DISK_USED_PERCENT).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getUsedPercent()).build();
			Point inodesTotal = Point.metric(METRIC_DISK_INODES_TOTAL).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getInodesTotal()).build();
			Point inodesFree = Point.metric(METRIC_DISK_INODES_FREE).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getInodesFree()).build();
			Point inodesUsed = Point.metric(METRIC_DISK_INODES_USED).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getInodesUsed()).build();
			Point inodesUsedPercent = Point.metric(METRIC_DISK_INODES_USED_PERCENT).tag(TAG_ID, disk.getPhysicalId())
					.tag(TAG_DISK_DEVICE, partitionStat.getDevice()).value(timestamp, usage.getInodesUsedPercent()).build();

			client.put(total);
			client.put(used);
			client.put(free);
			client.put(usedPercent);
			client.put(inodesTotal);
			client.put(inodesFree);
			client.put(inodesUsed);
			client.put(inodesUsedPercent);
		}
		return true;
	}

	@Override
	public boolean save(Net net) {
		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳
		Assert.notNull(net, "net is null");
		Assert.notEmpty(net.getNetInfos(), "net info is null");

		for (NetInfo netInfo : net.getNetInfos()) {
			Point up = Point.metric(METRIC_NET_UP).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getUp()).build();
			Point down = Point.metric(METRIC_NET_DOWN).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getDown()).build();
			Point count = Point.metric(METRIC_NET_COUNT).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getCount()).build();
			Point estab = Point.metric(METRIC_NET_ESTAB).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getEstab()).build();
			Point closeWait = Point.metric(METRIC_NET_CLOSE_WAIT).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getCloseWait()).build();
			Point timeWait = Point.metric(METRIC_NET_TIME_WAIT).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getTimeWait()).build();
			Point close = Point.metric(METRIC_NET_CLOSE).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getClose()).build();
			Point listen = Point.metric(METRIC_NET_LISTEN).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getListen()).build();
			Point closing = Point.metric(METRIC_NET_CLOSING).tag(TAG_ID, net.getPhysicalId())
					.tag(TAG_DISK_NET_PORT, String.valueOf(netInfo.getPort())).value(timestamp, netInfo.getClosing()).build();
			client.put(up);
			client.put(down);
			client.put(count);
			client.put(estab);
			client.put(closeWait);
			client.put(timeWait);
			client.put(close);
			client.put(listen);
			client.put(closing);
		}
		return true;
	}

}