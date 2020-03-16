package com.mallivora.fabric.service;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mallivora.fabric.service.config.Profile;
import com.mallivora.fabric.service.utils.CommonUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.encoders.UTF8;
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.common.Configtx;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Genesis {

    private static final int msgVersion = 1;
    private static final Long epoch = 0L;

    private static final Long firstBlockNumber = 0L;
    private static final ByteString firstHash = ByteString.EMPTY;

    public void genesisBlockForChannel(String chainId, Profile profile) throws IOException {
        Configtx.ConfigGroup configGroup = Encoder.newChannelGroup(profile);
        Common.ChannelHeader payloadChannelHeader = CommonUtils.makeChannelHeader(Common.HeaderType.CONFIG_VALUE, msgVersion, chainId, epoch);
        Common.SignatureHeader payloadSignatureHeader = CommonUtils.makeSignatureHeader(ByteString.EMPTY, CommonUtils.createNonceOrPanic());
        String txId = CommonUtils.setTxId(payloadChannelHeader, payloadSignatureHeader);
        payloadChannelHeader = payloadChannelHeader.toBuilder().setTxId(txId).build();
        Common.Header payloadHeader = CommonUtils.makePayloadHeader(payloadChannelHeader, payloadSignatureHeader);
        Configtx.ConfigEnvelope configEnvelope = Configtx.ConfigEnvelope.newBuilder().setConfig(Configtx.Config.newBuilder().setChannelGroup(configGroup)).build();
        Common.Payload payload = Common.Payload.newBuilder().setHeader(payloadHeader).setData(configEnvelope.toByteString()).build();
        Common.Envelope envelope = Common.Envelope.newBuilder().setPayload(payload.toByteString()).setSignature(ByteString.EMPTY).build();
        Common.BlockData blockData = Common.BlockData.newBuilder().addData(envelope.toByteString()).build();
        byte[] bytes = CommonUtils.computeHash256(blockData.toByteArray());
        Common.BlockHeader blockHeader = Common.BlockHeader.newBuilder().setNumber(firstBlockNumber).setPreviousHash(firstHash).setDataHash(ByteString.copyFrom(bytes)).build();
        List<ByteString> list = new ArrayList<ByteString>();
        for (int i = 0; i < Common.BlockMetadataIndex.values().length; i++) {
            list.add(ByteString.EMPTY);
        }
        Common.BlockMetadata blockMetadata = Common.BlockMetadata.newBuilder().addAllMetadata(list).
                setMetadata(Common.BlockMetadataIndex.LAST_CONFIG_VALUE, Common.Metadata.newBuilder().setValue
                        (Common.LastConfig.newBuilder().setIndex(0L).build().toByteString()).build().toByteString()).build();
        Common.Block block = Common.Block.newBuilder().setHeader(blockHeader).setData(blockData).setMetadata(blockMetadata).build();
        File file = new File("E:\\watts\\git projects\\fabric-samples\\first-network\\genesis1.block");
        Files.write(Paths.get(file.getAbsolutePath()), block.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, URISyntaxException {

 /*       SHA256Digest sha256Digest = new SHA256Digest();

        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        DigestUtils.digest(instance,new byte[5]);*/
/*        Path path = Paths.get("E:/watts/git projects/fabric-samples/first-network/","crypto-config.yaml");
        byte[] bytes = Files.readAllBytes(path);

        System.out.println(new String(bytes, StandardCharsets.UTF_8));*/
/*        FileReader fileReader = new FileReader("E:/watts/git projects/fabric-samples/first-network/configtx.yaml");
        YamlReader yamlReader = new YamlReader(fileReader);
        Profile read = yamlReader.read(Profile.class);
        System.out.println(read);*/
        new Genesis().genesisBlockForChannel("123",Demo.newProfile());


    }
}
