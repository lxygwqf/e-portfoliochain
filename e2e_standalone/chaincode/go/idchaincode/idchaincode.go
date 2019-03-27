package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"strings"

	//"strconv"
)

type IdChaincode struct {
}

type DIDO struct {
	DID          string  `json:"did"`
	EnrolmentDay string  `json:"enrolmentday"`
	SchoolID     string  `json:"schoolid"`
	AcademyID    string  `json:"academyid"`
	DepartmentID string  `json:"departmentid"`
	GradeID      string  `json:"gradeid"`
	ClassID      string  `json:"classid"`
	StudentID    string  `json:"studentid"`
	SchoolPK     string  `json:"schoolpk"`
	StudentPK    string  `json:"studentpk"`
	ProfileList  string  `json:"profilelist"`
}

type Profile struct {
	UUID              string           `json:"uuid"`
	CertCategory      string           `json:"certcategory"`
	CertName          string           `json:"certname"`
	AuthorizationList string           `json:"authorizationlist"`
}

type Authorization struct {
	InstitutionID     string           `json:"institutionid"`
	Signature         string           `json:"signature"`
}

func main() {
	err := shim.Start(new(IdChaincode))
	if err != nil {
		fmt.Print("Error starting IdChaincode: %s", err)
	}
}

//实例化IdChaincode
func (t *IdChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

//invoke和query
func (t *IdChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	if function == "InitDIDO" {
		return t.InitDIDO(stub, args)
	} else if function == "ReadProfileByUUID" {
		return t.ReadProfileByUUID(stub, args)
	} else if function == "ReadAuthorizationListByUUID" {
		return t.ReadAuthorizationListByUUID(stub, args)
	} else if function == "ReadPKs" {
		return t.ReadPKs(stub, args)
	} else if function == "IsAuthorizated" {
		return t.IsAuthorizated(stub, args)
	} else if function == "AddAuthorization" {
		return t.AddAuthorization(stub, args)
	} else if function == "DeleteAuthorization" {
		return t.DeleteAuthorization(stub, args)
	} else if function == "AddProfile" {
		return t.AddProfile(stub, args)
	} else if function == "ReadDIDO" {
		return t.ReadDIDO(stub, args)
	}

	fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

func (t *IdChaincode) ReadDIDO(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	did := args[0]

	DIDOAsBytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get cert: " + err.Error())
	}

	return shim.Success(DIDOAsBytes)
}


//DIDO上链,默认字段已切分
func (t *IdChaincode) InitDIDO(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 13 {
		return shim.Error("Incorrect number of arguments. Expecting 13")
	}

	did := args[0]
	enrolmentday := args[1]
	schoolid := args[2]
	academyid := args[3]
	departmentid := args[4]
	gradeid := args[5]
	classid := args[6]
	studentid := args[7]
	schoolpk := args[8]
	studentpk := args[9]
	uuid := args[10]
	certcategory := args[11]
	certname := args[12]


	// ==== 检查DIDO是否已存在 ====
	DIDOAsBytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get cert: " + err.Error())
	} else if DIDOAsBytes != nil {
		fmt.Println("This DIDO already exists: " + did)
		return shim.Error("This DIDO already exists: " + did)
	}

	//==== 创建档案对象 ====
	profile := &Profile{uuid, certcategory, certname, ""}
	profileJSONasBytes, err := json.Marshal(profile)
	newprofileJSONasString := string(profileJSONasBytes)
	if err != nil {
	}

	//==== 创建DIDO对象并转为JSON格式 ====
	DIDO := &DIDO{did, enrolmentday, schoolid, academyid, departmentid, gradeid, classid, studentid, schoolpk, studentpk, newprofileJSONasString}
	DIDOJSONasBytes, err := json.Marshal(DIDO)
	if err != nil {
		return shim.Error(err.Error())
	}

	//==== 将DIDO保存至世界状态 ====
	err = stub.PutState(did, DIDOJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Println("- end init DIDO")
	return shim.Success([]byte("success"))
}

//读取身份链中档案列表以及授权信息,需要输入uuid
func (t *IdChaincode) ReadProfileByUUID(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	did := args[0]
	uuid := args[1]

	//==== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//==== 获得指定uuid的档案列表 ====
	profileAsstring := string(profileAsBytes)
	comma := strings.Index(profileAsstring, uuid)//comma为在profileAsstring中第一次出现输入的uuid值的位置
	var com int
	var profileresult string
	var r string
	if comma >= 0 {
		r = profileAsstring[comma:]
		str := "{\"uuid\":"
		com = strings.Index(r, str)
		if com > 0 {
			profileresult = "{\"uuid\":\"" + r[0:com-1]
		} else {
			profileresult = "{\"uuid\":\""+r
		}

	}
	//profileresult为指定uuid的档案列表字符串

	return shim.Success([]byte(profileresult))
	//return shim.Success([]byte(r))
	//return shim.Success([]byte(strconv.Itoa(com)))
}

//读取身份链中指定档案的授权列表,需要输入uuid
func (t *IdChaincode) ReadAuthorizationListByUUID(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	did := args[0]
	uuid := args[1]

	//==== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//==== 获得指定uuid的档案列表 ====
	profileAsstring := string(profileAsBytes)
	comma := strings.Index(profileAsstring, uuid)//comma为在profileAsstring中第一次出现输入的uuid值的位置
	var com int
	var profileresult string
	var r string
	var authorizationAsBytes []byte
	if comma >= 0 {
		r = profileAsstring[comma:]
		str := "{\"uuid\":"
		com = strings.Index(r, str)
		if com > 0 {
			profileresult = "{\"uuid\":\"" + r[0:com-1]
		} else {
			profileresult = "{\"uuid\":\""+r
		}
		//profileresult为指定uuid的档案列表字符串
		profileToTransfer := Profile{}
		profileresultAsBytes := []byte(profileresult)
		err = json.Unmarshal(profileresultAsBytes, &profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}
		authorizationAsBytes = []byte(profileToTransfer.AuthorizationList)

	}
	//profileresult为指定uuid的档案列表字符串

	return shim.Success(authorizationAsBytes)
	//return shim.Success([]byte(r))
	//return shim.Success([]byte(strconv.Itoa(com)))
}

//读取学校和学生公钥
func (t *IdChaincode) ReadPKs(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting did to query")
	}

	did := args[0]

	//==== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}

	schoolpkAsBytes := []byte(DIDOToTransfer.SchoolPK)
	studentpkAsBytes := []byte(DIDOToTransfer.StudentPK)

	schoolpkAndstudentpkAsString := "schoolpk:"+string(schoolpkAsBytes) + ",studentpk:" + string(studentpkAsBytes)

	return shim.Success([]byte(schoolpkAndstudentpkAsString))
}

//判断是否授权
func (t *IdChaincode) IsAuthorizated(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	did := args[0]
	uuid := args[1]
	institutionid := args[2]

	//==== 获得DIDO的JSON字符串 ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//==== 获得指定uuid的档案列表 ====
	profileAsstring := string(profileAsBytes)
	comma := strings.Index(profileAsstring, uuid)
	var com int
	var profileresult string
	var r string
	var profileresultAsBytes []byte
	var old_authorizationAsBytes []byte
	if comma >= 0 {
		r = profileAsstring[comma:]
		str := "{\"uuid\":"
		com = strings.Index(r, str)
		if com > 0 {
			profileresult = "{\"uuid\":\"" + r[0:com - 1]
		} else {
			profileresult = "{\"uuid\":\"" + r
		}
		//profileresult为指定档案的字符串
		profileToTransfer := Profile{}
		profileresultAsBytes = []byte(profileresult)
		err = json.Unmarshal(profileresultAsBytes, &profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}
		//old_authorizationAsString为当前授权列表的字符串
		old_authorizationAsBytes = []byte(profileToTransfer.AuthorizationList)
		old_authorizationAsString := string(old_authorizationAsBytes)
		if strings.Contains(old_authorizationAsString, institutionid) {
			return shim.Success([]byte("yes"))
		}
	}
	return shim.Success([]byte("no"))
}



//添加机构授权
func (t *IdChaincode) AddAuthorization(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}

	did := args[0]
	uuid := args[1]
	institutionid := args[2]
	signature := args[3]

	//==== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//==== 获得指定uuid的档案列表 ====
	profileAsstring := string(profileAsBytes)//ProfileList字段的string型
	comma := strings.Index(profileAsstring, uuid)
	var com int
	var profileresult string
	var r string
	var profileresultAsBytes []byte
	var authorizationJSONasBytes []byte
	var old_authorizationAsBytes []byte
	var newauthorizationJSONasString string
	var DIDO_nonameasBytes []byte
	if comma >= 0 {
		r = profileAsstring[comma:]
		str := "{\"uuid\":"
		com = strings.Index(r, str)
		if com > 0 {
			profileresult = "{\"uuid\":\"" + r[0:com-1]
		} else {
			profileresult = "{\"uuid\":\""+r
		}

		//profileresult为指定档案的字符串
		profileToTransfer := Profile{}
		profileresultAsBytes = []byte(profileresult)
		err = json.Unmarshal(profileresultAsBytes, &profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}
		old_authorizationAsBytes = []byte(profileToTransfer.AuthorizationList)

		//创建授权对象并转为JSON格式
		authorization := &Authorization{institutionid, signature}
		authorizationJSONasBytes, err = json.Marshal(authorization)
		if err != nil {
			return shim.Error(err.Error())
		}
		authorizationJSONasString := string(authorizationJSONasBytes)
		//追加新创建的授权对象
		if string(old_authorizationAsBytes) == "" {
			newauthorizationJSONasString = authorizationJSONasString
		}else {
			newauthorizationJSONasString = string(old_authorizationAsBytes) + "," + authorizationJSONasString
		}

		//重新创建新的档案列表对象
		profileToTransfer.AuthorizationList = newauthorizationJSONasString
		profile_nonameAsBytes, err := json.Marshal(profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}

		if com > 0 {
			if comma > 10{
				DIDOToTransfer.ProfileList = profileAsstring[0:comma-10] + "," + string(profile_nonameAsBytes) + r[com-1:]
			}else{
				DIDOToTransfer.ProfileList = string(profile_nonameAsBytes) + r[com-1:]
			}
		} else {
			if comma > 10{
				DIDOToTransfer.ProfileList = profileAsstring[0:comma-10] + "," + string(profile_nonameAsBytes)
			}else{
				DIDOToTransfer.ProfileList = string(profile_nonameAsBytes)
			}
		}
		//重新创建新的DIDO
		DIDO_nonameasBytes, err = json.Marshal(DIDOToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}

		//存储新的DIDO
		err = stub.PutState(did, DIDO_nonameasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
	}

	return shim.Success([]byte("success"))
	//return shim.Success([]byte(strconv.Itoa(comma)))
	//return shim.Success(DIDO_nonameasBytes)


}

//授权列表删除机构
func (t *IdChaincode) DeleteAuthorization(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	did := args[0]
	uuid := args[1]
	institutionid := args[2]

	//==== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//==== 获得指定uuid的档案列表 ====
	profileAsstring := string(profileAsBytes)//ProfileList字段的string型
	comma := strings.Index(profileAsstring, uuid)
	var com int
	var profileresult string
	var r string
	var profileresultAsBytes []byte
	var old_authorizationAsBytes []byte
	var DIDO_nonameasBytes []byte
	var old_authorizationAsString string
	var new_authorizationAsString string
	if comma >= 0 {
		r = profileAsstring[comma:]
		str := "{\"uuid\":"
		com = strings.Index(r, str)
		if com > 0 {
			profileresult = "{\"uuid\":\"" + r[0:com-1]
		} else {
			profileresult = "{\"uuid\":\""+r
		}

		//profileresult为指定档案的字符串
		profileToTransfer := Profile{}
		profileresultAsBytes = []byte(profileresult)
		err = json.Unmarshal(profileresultAsBytes, &profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}
		old_authorizationAsBytes = []byte(profileToTransfer.AuthorizationList)
		old_authorizationAsString = string(old_authorizationAsBytes)

		//old_authorizationAsBytes为指定档案的授权列表byte型
		co := strings.Index(old_authorizationAsString, institutionid)
		if co >= 0 {
			ra := old_authorizationAsString[co:]
			str = "{\"institutionid\":"
			c := strings.Index(ra, str)
			//删除指定的授权机构
			if c > 0 {
				if co > 19{
					new_authorizationAsString = old_authorizationAsString[0:co-19] + ra[c-1:]
				}else{
					new_authorizationAsString = ra[c-2:]
				}

			}else {
				if co > 19 {
					new_authorizationAsString = old_authorizationAsString[0:co-19]
				}else {
					new_authorizationAsString = ""
				}
			}
		}

		//构造新的档案列表
		profileToTransfer.AuthorizationList = new_authorizationAsString
		profile_nonameAsBytes, err := json.Marshal(profileToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}

		if com > 0 {
			if comma > 10{
				DIDOToTransfer.ProfileList = profileAsstring[0:comma-10] + "," + string(profile_nonameAsBytes) + r[com-1:]
			}else{
				DIDOToTransfer.ProfileList = string(profile_nonameAsBytes) + r[com-1:]
			}
		} else {
			if comma > 10{
				DIDOToTransfer.ProfileList = profileAsstring[0:comma-10] + "," + string(profile_nonameAsBytes)
			}else{
				DIDOToTransfer.ProfileList = string(profile_nonameAsBytes)
			}
		}
		//构造新的DIDO
		DIDO_nonameasBytes, err = json.Marshal(DIDOToTransfer)
		if err != nil {
			return shim.Error(err.Error())
		}

		//存储新的DIDO
		err = stub.PutState(did, DIDO_nonameasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
	}

	return shim.Success([]byte("success"))
	//return shim.Success([]byte(profileAsstring[0:comma-10]))
	//return shim.Success(DIDO_nonameasBytes)
}

//档案目录添加档案
func (t *IdChaincode) AddProfile(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}
	did := args[0]
	uuid := args[1]
	certcategory := args[2]
	certname := args[3]

	//=== 读取DIDO ====
	DIDOAsbytes, err := stub.GetState(did)
	if err != nil {
		return shim.Error("Failed to get DIDO :" + err.Error())
	} else if DIDOAsbytes == nil {
		return shim.Error("This DIDO does not exist:" + did)
	}

	//==== 获得ProfileList字段的JSON字符串 ====
	DIDOToTransfer := DIDO{}
	err = json.Unmarshal(DIDOAsbytes, &DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}
	profileAsBytes := []byte(DIDOToTransfer.ProfileList)

	//=== 创建新的档案对象 ====
	profile := &Profile{uuid, certcategory, certname, ""}
	profileJSONasBytes, err := json.Marshal(profile)
	profileJSONasString := string(profileJSONasBytes)
	if err != nil {
	}

	//=== 追加新档案 ====
	newprofileJSONasString := string(profileAsBytes) + "," + profileJSONasString

	DIDOToTransfer.ProfileList = newprofileJSONasString
	DIDOJSONasBytes, err := json.Marshal(DIDOToTransfer)
	if err != nil {
		return shim.Error(err.Error())
	}

	//=== 存储新的DIDO ====
	err = stub.PutState(did, DIDOJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	//return shim.Success(DIDOJSONasBytes)
	return shim.Success([]byte("success"))
}
