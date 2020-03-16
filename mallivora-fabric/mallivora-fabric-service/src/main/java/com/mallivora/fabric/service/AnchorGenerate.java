package com.mallivora.fabric.service;

import org.hyperledger.fabric.protos.peer.Configuration;

import com.mallivora.fabric.service.config.AnchorPeer;
import com.mallivora.fabric.service.config.ChannelConfig;
import com.mallivora.fabric.service.config.Organization;
import com.mallivora.fabric.service.config.Profile;

import org.hyperledger.fabric.protos.common.Configtx;
import org.hyperledger.fabric.protos.common.Common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AnchorGenerate {

	public static void main(String[] args) throws IOException {
		String asOrgs = "Org2MSP";
		String channelId = "mychannel";
		Profile conf = Demo.newOutputChannelProfile();
		Organization org = new Organization();
		for (Organization iorg : conf.getApplication().getOrganizations()) {
			if (asOrgs.equals(iorg.getName())) {
				org = iorg;
			}
		}
		List<Configuration.AnchorPeer> anchorPeers = new ArrayList<>();
		for (AnchorPeer anchorPeer : org.getAnchorPeers()) {
			anchorPeers.add(Configuration.AnchorPeer.newBuilder().setHost(anchorPeer.getHost())
					.setPort(anchorPeer.getPort()).build());
		}
		Configtx.ConfigGroup orgGroup = Configtx.ConfigGroup.newBuilder()
				.putValues(ChannelConfig.MSP_KEY, Configtx.ConfigValue.newBuilder().build())
				.putPolicies(ChannelConfig.ReadersPolicyKey, Configtx.ConfigPolicy.newBuilder().build())
				.putPolicies(ChannelConfig.WritersPolicyKey, Configtx.ConfigPolicy.newBuilder().build())
				.putPolicies(ChannelConfig.AdminsPolicyKey, Configtx.ConfigPolicy.newBuilder().build()).build();

		Configtx.ConfigGroup readSetGroup = Configtx.ConfigGroup.newBuilder()
				.putGroups(ChannelConfig.ApplicationGroupKey, Configtx.ConfigGroup.newBuilder().setVersion(1)
						.setModPolicy(ChannelConfig.AdminsPolicyKey).putGroups(org.getName(), orgGroup).build())
				.build();

		Configtx.ConfigGroup writeOrgGroup = Configtx.ConfigGroup.newBuilder().setVersion(1)
				.setModPolicy(ChannelConfig.AdminsPolicyKey)
				.putValues(ChannelConfig.MSP_KEY, Configtx.ConfigValue.newBuilder().build())
				.putValues(ChannelConfig.AnchorPeersKey,
						Configtx.ConfigValue.newBuilder()
								.setValue(ChannelConfig.getAnchorPeersValue(anchorPeers).getValue().toByteString())
								.setModPolicy(ChannelConfig.AdminsPolicyKey).build())
				.putPolicies(ChannelConfig.ReadersPolicyKey, Configtx.ConfigPolicy.newBuilder().build())
				.putPolicies(ChannelConfig.WritersPolicyKey, Configtx.ConfigPolicy.newBuilder().build())
				.putPolicies(ChannelConfig.AdminsPolicyKey, Configtx.ConfigPolicy.newBuilder().build()).build();

		Configtx.ConfigGroup writeSetGroup = Configtx.ConfigGroup.newBuilder()
				.putGroups(ChannelConfig.ApplicationGroupKey, Configtx.ConfigGroup.newBuilder().setVersion(1)
						.setModPolicy(ChannelConfig.AdminsPolicyKey).putGroups(org.getName(), writeOrgGroup).build())
				.build();
		Configtx.ConfigUpdate configUpdate = Configtx.ConfigUpdate.newBuilder().setChannelId(channelId)
				.setWriteSet(writeSetGroup).setReadSet(readSetGroup).build();

		Configtx.ConfigUpdateEnvelope configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.newBuilder()
				.setConfigUpdate(configUpdate.toByteString()).build();

		Common.Header header = Common.Header.newBuilder()
				.setChannelHeader(
						Common.ChannelHeader.newBuilder().setChannelId(channelId).setType(2).build().toByteString())
				.build();
		Common.Envelope.Builder envelope = Common.Envelope.newBuilder().setPayload(Common.Payload.newBuilder()
				.setHeader(header).setData(configUpdateEnvelope.toByteString()).build().toByteString());
		Files.write(Paths.get("E:\\Org1MSPanchors.tx"), envelope.build().toByteArray(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

}
