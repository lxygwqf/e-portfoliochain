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
package com.wisedu.blockchain.controller;

import com.wisedu.blockchain.service.ChaincodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChaincodeController {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeController.class);
    private static final long EXPIRATIONTIME = 900000;

    @Autowired
    ChaincodeService chaincodeService;

    @RequestMapping(value = "/api/invoke", method = RequestMethod.POST)
    public ResponseEntity<String> invokeChaincode(
            @RequestParam(value = "Chaincode_function") String ChaincodeFunction,
            @RequestParam(value = "Chaincode_args", required = false) String[] ChaincodeArgs)
            throws Exception {
        String response = chaincodeService.invokeChaincode(ChaincodeFunction, ChaincodeArgs);
        if (response.equals("Chaincode invoked successfully")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }

    }

    @RequestMapping(value = "/api/query", method = RequestMethod.GET)
    public ResponseEntity<String> queryChaincode(
            @RequestParam(value = "Chaincode_function") String ChaincodeFunction,
            @RequestParam(value = "Chaincode_args", required = false) String[] ChaincodeArgs)
            throws Exception {
        String response = chaincodeService.queryChaincode(ChaincodeFunction, ChaincodeArgs);
        if (!response.equals("Caught an exception while quering chaincode")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(response);
        }
    }

}
