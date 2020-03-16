package com.mallivora.fabric.service.utils;

import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;

public class TxUtils {

	public static org.hyperledger.fabric.protos.common.Common.Envelope createSignedEnvelope(int txType,
			String channelId, Configtx.ConfigUpdateEnvelope newConfigUpdateEnv, int msgVersion, int epoch) {
		return CreateSignedEnvelopeWithTLSBinding(txType, channelId, newConfigUpdateEnv, msgVersion, epoch,
				ByteString.EMPTY);
	}

	private static org.hyperledger.fabric.protos.common.Common.Envelope CreateSignedEnvelopeWithTLSBinding(int txType,
			String channelId, Configtx.ConfigUpdateEnvelope data, int msgVersion, int epoch, ByteString tlsCertHash) {
		Common.ChannelHeader channelHeader = CommonUtils.makeChannelHeader(txType, msgVersion, channelId, epoch);
		Common.ChannelHeader.Builder channelHeaderBuilder = channelHeader.toBuilder().setTlsCertHash(tlsCertHash);
		Common.SignatureHeader.Builder payloadSignatureHeaderBuilder = Common.SignatureHeader.newBuilder();
		Common.Payload.Builder payloadBuilder = Common.Payload.newBuilder().setData(data.toByteString()).setHeader(
				CommonUtils.makePayloadHeader(channelHeaderBuilder.build(), payloadSignatureHeaderBuilder.build()));
		ByteString payloadBytes = payloadBuilder.build().toByteString();
		return Common.Envelope.newBuilder().setPayload(payloadBytes).setSignature(ByteString.EMPTY).build();
	}

}
