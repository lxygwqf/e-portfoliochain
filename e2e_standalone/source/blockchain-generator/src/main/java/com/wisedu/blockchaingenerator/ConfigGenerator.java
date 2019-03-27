package com.wisedu.blockchaingenerator;

import java.io.IOException;

public class ConfigGenerator {
    public static void main(String[] args){
        ConfigtxGen configtxGen = new ConfigtxGen();
        CryptoConfigGen cryptoConfigGen = new CryptoConfigGen();
        DockerComposeGen dockerComposeGen = new DockerComposeGen();
        BaseComposeGen baseComposeGen = new BaseComposeGen();
        try {
            configtxGen.gen();
            cryptoConfigGen.gen();
            dockerComposeGen.gen();
            baseComposeGen.gen();
        }catch (IOException e){
            System.out.print(e);
        }
    }
} 