package com.mallivora.fabric.service.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.hyperledger.fabric.protos.common.Common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CommonUtils {

    private static final int nonceSize = 24;

    public static org.hyperledger.fabric.protos.common.Common.ChannelHeader makeChannelHeader(int headerType,int version, String chainId,long epoch){
        Common.ChannelHeader.Builder channelHeaderBuilder = Common.ChannelHeader.newBuilder();
        Timestamp.Builder builder = Timestamp.newBuilder();
        builder.setSeconds(System.currentTimeMillis()/1000).setNanos(0);
        channelHeaderBuilder = channelHeaderBuilder.setType(headerType).setVersion(version).setTimestamp(builder).setChannelId(chainId).setEpoch(epoch);
        return channelHeaderBuilder.build();
    }

    public static Common.SignatureHeader makeSignatureHeader(ByteString creator, byte[] nonce){
        Common.SignatureHeader.Builder builder = Common.SignatureHeader.newBuilder();
        builder = builder.setCreator(creator).setNonce(ByteString.copyFrom(nonce));
        return builder.build();
    }

    public static byte[] createNonceOrPanic(){
        return GetRandomNonce();
    }

    private static byte[] GetRandomNonce() {
        byte[] bytes = new byte[nonceSize];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static String setTxId(Common.ChannelHeader payloadChannelHeader, Common.SignatureHeader payloadSignatureHeader) {
        String txId = computeTxID(payloadSignatureHeader.getNonce(),payloadSignatureHeader.getCreator());
        return txId;
    }

    private static String computeTxID(ByteString nonce, ByteString creator) {
        ByteString concat = nonce.concat(creator);
        return computeTxId(concat.toByteArray());
    }

    private static String computeTxId(byte[] bytes){
        try {
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            byte[] digest = DigestUtils.digest(sha256Digest, bytes);
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] computeHash256(byte[] bytes){
        MessageDigest sha256Digest = null;
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return DigestUtils.digest(sha256Digest, bytes);
    }

    public static Common.Header makePayloadHeader(Common.ChannelHeader payloadChannelHeader, Common.SignatureHeader payloadSignatureHeader) {
        Common.Header.Builder builder = Common.Header.newBuilder().setChannelHeader(payloadChannelHeader.toByteString()).setSignatureHeader(payloadSignatureHeader.toByteString());
        return builder.build();
    }


}
