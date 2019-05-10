这是在fabric-java-sdk基础上写的项目。  
xuchengEncrypt这个项目是加密服务，为下面两个项目调用：  
blockchain-application-using-springboot这个里面安装的是身份链链码idchaincode,  
Fileblockchain-application-using-springboot里面安装的是存证链链码filechaincode。  
e-portfoliochain是总的有网站的项目，通过这个项目调用上面两个项目接口


运行步骤：
1.idchaincode filechaincode链码写到e2e_standalone/chaincode/go中。
2.先运行只安装一个链码的idchaincode，运行步骤如下:  
	1. bash manual.sh  
	2.bash ./scripts/createChannel.sh  
        3.bash ./scripts/joinChannel.sh  
        4.bash ./scripts/installChaincode.sh  
        5.bash ./scripts/instantiateChaincode.sh  
idchaincode链码安装、实例化完毕

3.再安装filechaincode链码  
 	1.使用configtxgen工具与configtx.yaml配置文件中的TwoOrgsChannel模板, 来生成新建通道的配置交易文件  
	xuli@xuli-OptiPlex-7050:~/e2e_standalone$ ./bin/configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./channel-artifacts/filechannel.tx -channelID filechannel  

	2.使用configtxgen工具与configtx.yaml配置文件生成锚节点配置更新文件：  
		xuli@xuli-OptiPlex-7050:~/e2e_standalone$ ./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/fileOrg1MSPanchors.tx -channelID filechannel -asOrg Org1MSP  
		xuli@xuli-OptiPlex-7050:~/e2e_standalone$ ./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/fileOrg2MSPanchors.tx -channelID filechannel -asOrg Org2MSP  
		xuli@xuli-OptiPlex-7050:~/e2e_standalone$ ./bin/configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/fileOrg3MSPanchors.tx -channelID filechannel -asOrg Org3MSP  

	5.进入cli：  
	xuli@xuli-OptiPlex-7050:~/e2e_standalone$ sudo docker exec -it cli bash  

	6.根据filechannel.tx文件创建通道  
	root@57f01d0a1215:/opt/gopath/src/github.com/hyperledger/fabric/peer# peer channel create -o orderer.wisedu.com:7050 -c filechannel -f ./channel-artifacts/filechannel.tx  
	
	7.所有节点加入通道  
	root@57f01d0a1215:/opt/gopath/src/github.com/hyperledger/fabric/peer#  bash ./scripts/joinfileChannel.sh  

	8.默认是org1,更新org1锚节点  
	root@57f01d0a1215:/opt/gopath/src/github.com/hyperledger/fabric/peer# peer channel update -o orderer.wisedu.com:7050 -c filechannel -f ./channel-artifacts/fileOrg1MSPanchors.tx  
	
	9.转到org2的cli  
		root@57f01d0a1215:/opt/gopath/src/github.com/hyperledger/fabric/peer# export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.wisedu.com/users/Admin@org2.wisedu.com/msp  
		export CORE_PEER_ADDRESS=peer0.org2.wisedu.com:7051  
		export CORE_PEER_LOCALMSPID="Org2MSP"  
	12.更新org2锚节点  
	peer channel update -o orderer.wisedu.com:7050 -c filechannel -f ./channel-artifacts/fileOrg2MSPanchors.tx  

        13.转到org3的cli  
		export CORE_PEER_LOCALMSPID="Org3MSP"  
		export CORE_PEER_ADDRESS=peer0.org3.wisedu.com:7051  
 		export CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org3.wisedu.com/users/Admin@org3.wisedu.com/msp  
	16.更新org3锚节点  
	peer channel update -o orderer.wisedu.com:7050 -c filechannel -f ./channel-artifacts/fileOrg3MSPanchors.tx  

	17.安装filechaincode  
	bash ./scripts/installfileChaincode.sh  

	18. 实例化链码  
	bash ./scripts/instantiatefileChaincode.sh  


///////////////////////////////////////////////////////////至此e2e_standalone操作完成

接下来处理IDEA
4.将e2e_standalone里面生成的channel-artifacts和crypto-config替换掉blockchain-application-using-springboot和Fileblockchain-application-using-springboot工程里面的  
5.application.yaml中：下面这些内容要匹配  
	channel_name: mychannel  
    	chaincode_name: idchaincode  
  	chaincode_path: github.com/hyperledger/fabric/examples/chaincode/go/idchaincode  

///////////////////////////////////////  
启动所有项目。  



