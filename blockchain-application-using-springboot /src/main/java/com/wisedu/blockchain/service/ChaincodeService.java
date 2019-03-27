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

/**
 * @author SWATI RAJ
 */

import java.io.IOException;

/**
 * Interface for all the chaincode services that is implemented by
 * Chaincode_Service_Impl class
 */
public interface ChaincodeService {


    public String invokeChaincode(String chaincodeFunction, String[] chaincodeArgs);

    public String queryChaincode(String chaincodeFunction, String[] chaincodeArgs);

    public String readJsonDIDO(String path) throws IOException;

    //public void blockchainInfo(Org sampleOrg, Channel channel);

}
