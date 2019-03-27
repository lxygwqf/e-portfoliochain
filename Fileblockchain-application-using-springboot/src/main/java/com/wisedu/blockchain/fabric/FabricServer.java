package com.wisedu.blockchain.fabric;

import com.wisedu.blockchain.config.Config;
import com.wisedu.blockchain.config.Orderer;
import com.wisedu.blockchain.config.Peer;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class FabricServer {

    private static Logger log = Logger.getLogger(FabricServer.class);
    private ChaincodeManager manager;

    private static FabricServer instance = null;

    private Config config;

    public static FabricServer obtain(Config config)
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (null == instance) {
            synchronized (FabricServer.class) {
                if (null == instance) {
                    instance = new FabricServer(config);
                }
            }
        }
        return instance;
    }

    private FabricServer(Config config)
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.config = config;
        manager = new ChaincodeManager(getConfig());
    }

    /**
     * 获取节点服务器管理器
     *
     * @return 节点服务器管理器
     */
    public ChaincodeManager getManager() {
        return manager;
    }

    /**
     * 根据节点作用类型获取节点服务器配置
     *
     * @param //type 服务器作用类型（1、执行；2、查询）
     * @return 节点服务器配置
     */
    private FabricConfig getConfig() {
        FabricConfig fabricConfig = new FabricConfig();
        fabricConfig.setOrderers(getOrderers());
        fabricConfig.setPeers(getPeers());
        fabricConfig.setChaincode(getChaincode(config.getChannel_name(), config.getChaincode_name(),
                config.getChaincode_path(), config.getChaincode_version()));
        fabricConfig.setChannelArtifactsPath(getChannelArtifactsPath());
        fabricConfig.setCryptoConfigPath(getCryptoConfigPath());
        return fabricConfig;
    }

    private Orderers getOrderers() {
        Orderers orderers = new Orderers();
        orderers.setOrdererDomainName(config.getOrderer_domain_name());
        for (Orderer item : config.getOrderers()) {
            orderers.addOrderer(item.getOrderer_name(), item.getOrderer_location());
        }
        return orderers;
    }

    /**
     * 获取节点服务器集
     *
     * @return 节点服务器集
     */
    private Peers getPeers() {
        Peers peers = new Peers();
        peers.setOrgName(config.getOrg_name());
        peers.setOrgMSPID(config.getOrg_msp());
        peers.setOrgDomainName(config.getOrg_domain_name());
        for (Peer peer : config.getPeers()) {
            peers.addPeer(peer.getPeer_name(), peer.getPeer_eventhub_name(), peer.getPeer_location(),
                    peer.getPeer_eventhub_location(), peer.getCa_location());
        }
        return peers;
    }

    /**
     * 获取智能合约
     *
     * @param channelName      频道名称
     * @param chaincodeName    智能合约名称
     * @param chaincodePath    智能合约路径
     * @param chaincodeVersion 智能合约版本
     * @return 智能合约
     */
    private Chaincode getChaincode(String channelName, String chaincodeName, String chaincodePath, String chaincodeVersion) {
        Chaincode chaincode = new Chaincode();
        chaincode.setChannelName(channelName);
        chaincode.setChaincodeName(chaincodeName);
        chaincode.setChaincodePath(chaincodePath);
        chaincode.setChaincodeVersion(chaincodeVersion);
        chaincode.setInvokeWatiTime(100000);
        chaincode.setDeployWatiTime(120000);
        return chaincode;
    }

    /**
     * 获取channel-artifacts配置路径
     *
     * @return /WEB-INF/classes/fabric/channel-artifacts/
     */
    private String getChannelArtifactsPath() {
        String directorys = FabricServer.class.getClassLoader().getResource("fabric").getFile();
        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());

        return directory.getPath() + "/channel-artifacts/";
    }

    /**
     * 获取crypto-config配置路径
     *
     * @return /WEB-INF/classes/fabric/crypto-config/
     */
    private String getCryptoConfigPath() {
        String directorys = FabricServer.class.getClassLoader().getResource("fabric").getFile();
        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());

        return directory.getPath() + "/crypto-config/";
    }

//    public static void main(String[] args) {
//        try {
//            ChaincodeManager manager = FabricServer.obtain().getManager();
//
//            String[] str = {"a", "b", "20"};
//            Map<String, String> result = manager.invoke("invoke", str);
//            System.out.println(result);
////            String[] str = {"a"};
////            Map<String, String> query = manager.query("query", str);
////            System.out.println(query);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}