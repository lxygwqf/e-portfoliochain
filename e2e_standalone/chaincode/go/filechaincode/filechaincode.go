//证书上链
//peer chaincode invoke -o orderer.wisedu.com:7050 -C mychannel -n mycc3 -c '{"Args":["InitCert","uuid1","identityid1","SchoolID1","StudentID1","CertName1","CertCategory1","HashValue1","CertContent1"]}'
//证书hash是否被篡改
//peer chaincode query -o orderer.wisedu.com:7050 -C mychannel -n mycc3 -c '{"Args":["ValidateHash","uuid2","HashValue1"]}'
//根据uuid查询证书所有元素
//peer chaincode query -o orderer.wisedu.com:7050 -C mychannel -n mycc3 -c '{"Args":["QueryCertByUUID","uuid2"]}'

//证书绑定
//peer chaincode invoke -o orderer.wisedu.com:7050 -C mychannel -n mycc3 -c '{"Args":["BindCert","uuid2"]}'
//证书撤销
//peer chaincode invoke -o orderer.wisedu.com:7050 -C mychannel -n mycc3 -c '{"Args":["RevokeCert","uuid2","reason1"]}'

package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"strings"
	"time"
)

type SimpleChaincode struct {
}

type cert struct{
	UUID                     string  `json:"uuid"`
	IdentityID               string  `json:”identityid”`
	SchoolID                 string  `json:"schoolid"`
	StudentID                string  `json:"studentid"`
	CertName                 string  `json:"certname"`
	CertCategory             string  `json:"certcategory"`
	HashValue                string  `json:"hashvalue"`
	CertContent              string  `json:"certcontent"`
	BindDate                 string  `json:"binddate"`
	CertState                string  `json:"certstate"`
	Validation               string  `json:"validation"`
	RevokeReason             string  `json:"revokereason"`
}


func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Print("Error starting Simple chaincode: %s",err)
	}
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	if function == "InitCert" {
		return t.InitCert(stub, args)
	} else if function == "QueryCertByUUID" {
		return t.QueryCertByUUID(stub, args)
	} else if function == "QueryCertByStudent" {
		return t.QueryCertByStudent(stub, args)
	} else if function == "QueryCertByIdentityID" {
		return t.QueryCertByIdentityID(stub, args)
	} else if function == "BindCert" {
		return t.BindCert(stub, args)
	} else if function == "ValidateHash" {
		return t.ValidateHash(stub, args)
	}else if function == "RevokeCert" {
		return t.RevokeCert(stub, args)
	}


	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

//args[] : UUID,IdentityID,SchoolID,StudentID,CertName,CertCategory,HashValue,CertContent
func (t *SimpleChaincode) InitCert(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 8 {
		return shim.Error("Incorrect number of arguments. Expecting 10")
	}

	uuid := args[0]
	identityid := args[1]
	schoolid := args[2]
	studentid := args[3]
	certname := args[4]
	certcategory := args[5]
	hashvalue := args[6]
	certcontent := args[7]
	binddate := ""
	certstate := "0"      // 0 means not bind, 1 means is
	validation := "1"    //1 means validated , 0 means hasn't validation
	revokereason := ""

	// ==== Check if cert already exists ====
	certAsBytes, err := stub.GetState(uuid)
	if err != nil {
		return shim.Error("Failed to get cert: " + err.Error())
	} else if certAsBytes != nil {
		fmt.Println("This cert already exists: " + uuid)
		return shim.Error("This cert already exists: " + uuid)
	}

	// ==== Create cert object and marshal to JSON ====
	cert := &cert{uuid, identityid, schoolid, studentid, certname, certcategory, hashvalue, certcontent, binddate, certstate, validation ,revokereason}
	certJSONasBytes, err := json.Marshal(cert)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === Save cert to state ===
	err = stub.PutState(uuid, certJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	// === uuid with SchoolID and studentid as a composite Key ===
	indexName := "composition"
	indexKey, err := stub.CreateCompositeKey(indexName, []string{cert.SchoolID,cert.StudentID, cert.UUID})
	if err != nil {
		return shim.Error(err.Error())
	}
	value := []byte{0x00}
	stub.PutState(indexKey, value)

	// === uuid with identityid as a composite Key ===
	indexName = "identityidIndex"
	indexKey, err = stub.CreateCompositeKey(indexName, []string{cert.IdentityID, cert.UUID})
	if err != nil {
		return shim.Error(err.Error())
	}
	value = []byte{0x00}
	stub.PutState(indexKey, value)

	fmt.Println("- end init cert")
	return shim.Success(nil)

}

// === args[]:uuid
func (t *SimpleChaincode) QueryCertByUUID(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var jsonResp string
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting uuid to query")
	}

	uuid := args[0]
	valAsbytes, err := stub.GetState(uuid)
	if err != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + uuid + "\"}"
		return shim.Error(jsonResp)
	} else if valAsbytes == nil {
		jsonResp = "{\"Error\":\"cert does not exist: " + uuid + "\"}"
		return shim.Error(jsonResp)
	}

	return shim.Success(valAsbytes)
}

// ==== args[]:SchoolID,StudentID ===
func (t *SimpleChaincode) QueryCertByStudent(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	// === GET VALUE ====
	schoolid := args[0]
	studentid := args[1]
	resultsIterator, err := stub.GetStateByPartialCompositeKey("composition", []string{schoolid,studentid})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// Iterate through result set and for each cert found, transfer to newOwner
	var responsePayload string
	var i int
	for i = 0; resultsIterator.HasNext(); i++ {

		responseRange, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)
		if err != nil {
			return shim.Error(err.Error())
		}
		returnedUUID := compositeKeyParts[2]
		valAsbytes, err := stub.GetState(returnedUUID)
		responsePayload = responsePayload+" "+ string(valAsbytes[:])
	}
	//responsePayload := fmt.Sprintf("we find %d items about %s : %s ", i, studentid, UUIDs)
	return shim.Success([]byte(responsePayload))
}

// ==== args[]:IdentityID ===
func (t *SimpleChaincode) QueryCertByIdentityID(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	// === GET VALUE ====
	identityid := args[0]
	resultsIterator, err := stub.GetStateByPartialCompositeKey("identityidIndex", []string{identityid})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// Iterate through result set and for each cert found, transfer to newOwner
	var responsePayload string
	var i int
	for i = 0; resultsIterator.HasNext(); i++ {

		responseRange, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)
		if err != nil {
			return shim.Error(err.Error())
		}
		returnedUUID := compositeKeyParts[1]
		valAsbytes, err := stub.GetState(returnedUUID)
		responsePayload = responsePayload+" "+ string(valAsbytes[:])
	}
	//responsePayload := fmt.Sprintf("we find %d items about %s : %s ", i, studentid, UUIDs)
	return shim.Success([]byte(responsePayload))
}

// args[] : uuid
func (t *SimpleChaincode) BindCert(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting uuid")
	}

	uuid := args[0]
	binddate := time.Now().Format("2006-01-02 15:04:05")
	certstate := "1"
	fmt.Println("- start binding cert ")

	certAsBytes, err := stub.GetState(uuid)
	if err != nil {
		return shim.Error("Failed to get cert :" + err.Error())
	} else if certAsBytes == nil {
		return shim.Error("This cert does not exist:" +uuid)
	}

	certToTransfer := cert{}
	err = json.Unmarshal(certAsBytes, &certToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	if strings.Compare(certToTransfer.CertState,"1") == 0 {
		return shim.Error("this cert has already binded !")
	}
	certToTransfer.BindDate = binddate
	certToTransfer.CertState = certstate

	certJSONasBytes, _ := json.Marshal(certToTransfer)
	err = stub.PutState(uuid, certJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("- end binding cert (success)")
	return shim.Success(nil)
}

//args[] : uuid,hashvalue
func (t *SimpleChaincode) ValidateHash(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	uuid := args[0]
	hashvalue := args[1]

	certAsBytes, err := stub.GetState(uuid)
	if err != nil {
		return shim.Error("Failed to get cert :" + err.Error())
	} else if certAsBytes == nil {
		return shim.Error("This cert does not exist:" +uuid)
	}

	certToTransfer := cert{}
	err = json.Unmarshal(certAsBytes, &certToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}

	if strings.Compare(hashvalue, certToTransfer.HashValue) != 0 {
		//return shim.Error("Validation failed ")
		return shim.Success([]byte("Validation failed"))
	}

	return shim.Success([]byte("equals"))
}

//args[]:uuid,revokereason
func (t *SimpleChaincode) RevokeCert(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	uuid := args[0]
	revokereason := args[1]
	validation := "0"

	certAsBytes, err := stub.GetState(uuid)
	if err != nil {
		return shim.Error("Failed to get cert :" + err.Error())
	} else if certAsBytes == nil {
		return shim.Error("This cert does not exist:" +uuid)
	}

	certToTransfer := cert{}
	err = json.Unmarshal(certAsBytes, &certToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}

	if strings.Compare(certToTransfer.Validation,"0") == 0 {
		return shim.Error("this cert has already invalid !")
	}
	certToTransfer.Validation = validation
	certToTransfer.RevokeReason = revokereason

	certJSONasBytes, _ := json.Marshal(certToTransfer)
	err = stub.PutState(uuid, certJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}
