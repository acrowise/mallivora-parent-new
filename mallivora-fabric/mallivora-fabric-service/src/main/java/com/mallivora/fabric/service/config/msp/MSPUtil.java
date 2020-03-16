package com.mallivora.fabric.service.config.msp;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.protos.msp.MspConfigPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSPUtil {

    private static final int FABRIC = 0;
    private static final int IDEMIX = 1;
    private static final int OTHER = 2;

    private static final String SHA2 = "SHA2";
    private static final String SHA256 = "SHA256";

    private static final String cacerts = "cacerts";
    private static final String admincerts = "admincerts";
    private static final String signcerts = "signcerts";
    private static final String keystore = "keystore";
    private static final String intermediatecerts = "intermediatecerts";
    private static final String crlsfolder = "crls";
    private static final String configfilename = "config.yaml";
    private static final String tlscacerts = "tlscacerts";
    private static final String tlsintermediatecerts = "tlsintermediatecerts";

    private static final Map<Integer, String> mspTypeStrings = new HashMap<Integer, String>() {
        {
            put(FABRIC, "bccsp");
            put(IDEMIX, "idemix");
        }
    };

    public static MspConfigPackage.MSPConfig getVerifyingMspConfig(String dir, String id, String mspType) {
        switch (mspType) {
            case "bccsp":
                return getMspConfig(dir, id, null);
            case "idemix":
                return getIdemixMspConfig(dir, id);
            default:
                throw new RuntimeException(String.format("unknown MSP type '%s'", mspType));
        }
    }

    private static MspConfigPackage.MSPConfig getMspConfig(String dir, String id,
        MspConfigPackage.SigningIdentityInfo sigid) {

        if (StringUtils.isNotBlank(dir)) {

            String cacertDir = dir + "/" + cacerts;
            String admincertDir = dir + "/" + admincerts;
            String intermediatecertsDir = dir + "/" + intermediatecerts;
            String crlsDir = dir + "/" + crlsfolder;
            String configFile = dir + "/" + configfilename;
            String tlscacertDir = dir + "/" + tlscacerts;
            String tlsintermediatecertsDir = dir + "/" + tlsintermediatecerts;

        }

        List<MspConfigPackage.FabricOUIdentifier> ouis = new ArrayList();
        MspConfigPackage.FabricOUIdentifier.Builder builder = MspConfigPackage.FabricOUIdentifier.newBuilder();
        MspConfigPackage.FabricNodeOUs.Builder fabricNodeOUs = MspConfigPackage.FabricNodeOUs.newBuilder()
            .setEnable(true).setAdminOuIdentifier(newFabricOUIdentifier("E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\cacerts\\ca.org1.example.com-cert.pem", "admin"))
            .setClientOuIdentifier(newFabricOUIdentifier("E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\cacerts\\ca.org1.example.com-cert.pem", "client"))
            .setPeerOuIdentifier(newFabricOUIdentifier("E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\cacerts\\ca.org1.example.com-cert.pem", "peer"))
            .setOrdererOuIdentifier(newFabricOUIdentifier("E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org1.example.com\\msp\\cacerts\\ca.org1.example.com-cert.pem", "orderer"));
        MspConfigPackage.FabricCryptoConfig cryptoConfig = MspConfigPackage.FabricCryptoConfig.newBuilder()
            .setSignatureHashFamily(SHA2).setIdentityIdentifierHashFunction(SHA256).build();
        MspConfigPackage.FabricMSPConfig.Builder fabricMSPConfig = MspConfigPackage.FabricMSPConfig.newBuilder()
                .addAdmins(ByteString.EMPTY)
                .addRootCerts(ByteString.EMPTY)
                .addIntermediateCerts(ByteString.EMPTY)
                //.setSigningIdentity(sigid)
                .setName(id)
                .addAllOrganizationalUnitIdentifiers(ouis)
                .addRevocationList(ByteString.EMPTY)
                .addTlsRootCerts(ByteString.EMPTY)
                .addTlsIntermediateCerts(ByteString.EMPTY)
                .setCryptoConfig(cryptoConfig)
                .setFabricNodeOus(fabricNodeOUs);


        return MspConfigPackage.MSPConfig.newBuilder().setConfig(fabricMSPConfig.build().toByteString()).setType(FABRIC).build();
    }

    private static MspConfigPackage.FabricOUIdentifier newFabricOUIdentifier(String certDir,
        String organizationalUnitIdentifier) {
        Path path = Paths.get(certDir);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFabricOUIdentifierBytes(bytes, organizationalUnitIdentifier);
    }

    private static MspConfigPackage.FabricOUIdentifier newFabricOUIdentifierBytes(byte[] certificates,
        String organizationalUnitIdentifier) {
        ByteString certificate = ByteString.copyFrom(certificates);
        return newFabricOUIdentifier(certificate, organizationalUnitIdentifier);
    }

    public static MspConfigPackage.FabricOUIdentifier newFabricOUIdentifier(ByteString certificate,
        String organizationalUnitIdentifier) {
        return MspConfigPackage.FabricOUIdentifier.newBuilder().setCertificate(certificate)
            .setOrganizationalUnitIdentifier(organizationalUnitIdentifier).build();
    }

    private static MspConfigPackage.MSPConfig getIdemixMspConfig(String dir, String id) {

        return null;
    }
}
