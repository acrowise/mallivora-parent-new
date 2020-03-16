package com.mallivora.fabric.service.config.orderer;

import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.orderer.etcdraft.Configuration.*;

import java.util.List;

public class Configuration {

    public void marshal(ConfigMetadata md) {
        List<Consenter> consenters = md.getConsentersList();
        for (Consenter c : consenters) {
            ByteString serverTlsCert = c.getServerTlsCert();
        }


    }
}
