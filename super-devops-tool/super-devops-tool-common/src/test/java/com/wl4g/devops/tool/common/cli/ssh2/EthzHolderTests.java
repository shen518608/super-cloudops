package com.wl4g.devops.tool.common.cli.ssh2;

import com.wl4g.devops.tool.common.cli.ssh2.EthzHolder;
import com.wl4g.devops.tool.common.cli.ssh2.Ssh2Holders;
import com.wl4g.devops.tool.common.io.ByteStreams2;

public class EthzHolderTests {

	public static void main(String[] args) throws Exception {
		String command = "sleep 10";
		Ssh2Holders.getInstance(EthzHolder.class).execWaitForCompleteWithSsh2("10.0.0.160", "root", null, command, s -> {
			System.err.println(ByteStreams2.readFullyToString(s.getStderr()));
			System.out.println(ByteStreams2.readFullyToString(s.getStdout()));
			s.close();
			System.err.println("signal:" + s.getExitSignal() + ", state:" + s.getState() + ", status:" + s.getExitStatus());
			return null;
		}, 30_000);

	}

}
