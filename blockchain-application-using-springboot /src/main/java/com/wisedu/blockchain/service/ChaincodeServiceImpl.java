/*
 *  Copyright 2018, Mindtree Ltd. - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.wisedu.blockchain.service;

import com.wisedu.blockchain.config.Config;
import com.wisedu.blockchain.fabric.ChaincodeManager;
import com.wisedu.blockchain.fabric.FabricServer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * @author cheng en
 */

@Service("Chaincode_Service")
public class ChaincodeServiceImpl implements ChaincodeService {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeServiceImpl.class);
    @Autowired
    public Config config;
    public ChaincodeServiceImpl() throws IOException, NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, InstantiationException, InvalidKeySpecException, CryptoException, InvalidArgumentException, IllegalAccessException, NoSuchProviderException, TransactionException, ClassNotFoundException {
    }

    public String invokeChaincode(String chaincodeFunction, String[] chaincodeArgs) {

        try {
            ChaincodeManager manager = FabricServer.obtain(config).getManager();
            manager.invoke(chaincodeFunction,chaincodeArgs);
            return "Chaincode invoked successfully";
        } catch (Exception e) {
            logger.info("Caught an exception while invoking chaincode");
            logger.error("ChaincodeServiceImpl | invokeChaincode | " + e.getMessage());
            return "Caught an exception while invoking chaincode. |" + e.getMessage();

        }
    }


    public String queryChaincode(String chaincodeFunction, String[] chaincodeArgs) {
        try {
            ChaincodeManager manager = FabricServer.obtain(config).getManager();
            Map<String, String> response = manager.query(chaincodeFunction, chaincodeArgs);
            return response.get("data");
        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | queryChaincode | " + e.getMessage());
            return "Caught an exception while quering chaincode";
        }

    }

    //从本地路径下的json文件从读取证书
    public String readJsonDIDO(String path) throws IOException {
        System.out.println("开始读文件");
        File file = new File(path);
        StringBuilder stringb = new StringBuilder();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                //用逗号会出问题,这里把json文件的，替换掉
                if (((char) tempchar) == ',')
                    tempchar=';';
                stringb.append((char) tempchar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reader != null){
            reader.close();
        }
        String plainText=stringb.toString();
        System.out.println(plainText);
        return plainText;
    }

}
