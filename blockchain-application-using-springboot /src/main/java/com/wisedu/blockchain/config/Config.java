/**
 * Copyright 2018 bejson.com
 */
package com.wisedu.blockchain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Auto-generated: 2018-09-03 13:50:55
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Component
@ConfigurationProperties(prefix = "fabric")
public class Config {

    private String channel_name;
    private String chaincode_name;
    private String chaincode_path;
    private String chaincode_version;
    private String org_name;
    private String org_msp;
    private String org_domain_name;
    private List<Peer> peers;
    private String orderer_domain_name;
    private List<Orderer> orderers;

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChaincode_name(String chaincode_name) {
        this.chaincode_name = chaincode_name;
    }

    public String getChaincode_name() {
        return chaincode_name;
    }

    public void setChaincode_path(String chaincode_path) {
        this.chaincode_path = chaincode_path;
    }

    public String getChaincode_path() {
        return chaincode_path;
    }

    public void setChaincode_version(String chaincode_version) {
        this.chaincode_version = chaincode_version;
    }

    public String getChaincode_version() {
        return chaincode_version;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_msp(String org_msp) {
        this.org_msp = org_msp;
    }

    public String getOrg_msp() {
        return org_msp;
    }

    public void setOrg_domain_name(String org_domain_name) {
        this.org_domain_name = org_domain_name;
    }

    public String getOrg_domain_name() {
        return org_domain_name;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public void setOrderer_domain_name(String orderer_domain_name) {
        this.orderer_domain_name = orderer_domain_name;
    }

    public String getOrderer_domain_name() {
        return orderer_domain_name;
    }

    public void setOrderers(List<Orderer> orderer) {
        this.orderers = orderer;
    }

    public List<Orderer> getOrderers() {
        return orderers;
    }

}