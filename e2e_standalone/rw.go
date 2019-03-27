package main

//
// There is web resource describing the detailed block structure:
//   https://blockchain-fabric.blogspot.com/2017/04/hyperledger-fabric-v10-block-structure.html
//

import (
     "os"
     "fmt"
     "strings"
     "encoding/base64"

     "github.com/hyperledger/fabric/peer/common"
     "github.com/hyperledger/fabric/core/ledger/kvledger"
     "github.com/hyperledger/fabric/protos/common"
     "github.com/spf13/viper"
)

const cmdRoot = "core"
const channel = "mychannel"

// TODO: print more block data fields
func printBlock(prefix string, block * cb.Block) {
    fmt.Printf("%s Block: Number=[%d], CurrentBlockHash=[%s], PreviousBlockHash=[%s]\n",
        prefix,
        block.GetHeader().Number,
        base64.StdEncoding.EncodeToString(block.GetHeader().DataHash),
        base64.StdEncoding.EncodeToString(block.GetHeader().PreviousHash))
}


func main() {
    viper.SetEnvPrefix(cmdRoot)
    viper.AutomaticEnv()
    replacer := strings.NewReplacer(".", "_")
    viper.SetEnvKeyReplacer(replacer)
    err := common.InitConfig("core")
    if err != nil { // Handle errors reading the config file
        fmt.Printf("Cannot init configure, error=[%v]", err)
        os.Exit(1)
    }

    provider, err := kvledger.NewProvider()   // core/ledger/kvledger/kv_ledger_provider.go
    if err != nil {
        fmt.Printf("Cannot new provider, error=[%s]", err)
        os.Exit(1)
    }
    defer provider.Close()

    // Print channel list
    channels, err := provider.List()        // core/ledger/kvledger/kv_ledger_provider.go
    if err != nil {
        fmt.Printf("Cannot get channel list, error=[%v]\n", err)
        os.Exit(1)
    }
    fmt.Printf("channels=[%v]\n", channels)
    
    // Open a channel
    ledger, err := provider.Open(channel)   // core/ledger/kvledger/kv_ledger_provider.go
    if err != nil {
        fmt.Printf("Cannot open channel ledger, error=[%v]\n", err)
        os.Exit(1)
    }
    defer ledger.Close()
    // Return ledger as kvLedger is defined in core/ledger/kvledger/kv_ledger.go, following API:
    //  func (l *kvLedger) GetBlockchainInfo() (*common.BlockchainInfo, error)
    //  func (l *kvLedger) GetTransactionByID(txID string) (*peer.ProcessedTransaction, error)
    //  func (l *kvLedger) GetBlockByNumber(blockNumber uint64) (*common.Block, error)
    //  func (l *kvLedger) GetBlockByHash(blockHash []byte) (*common.Block, error)
    //  func (l *kvLedger) GetBlockByTxID(txID string) (*common.Block, error)
    //  func (l *kvLedger) GetBlocksIterator(startBlockNumber uint64) (commonledger.ResultsIterator, error)
    //  func (l *kvLedger) GetTxValidationCodeByTxID(txID string) (peer.TxValidationCode, error)
    //  func (l *kvLedger) Close()

    // Get basic channel information
    chainInfo, err := ledger.GetBlockchainInfo() // (*common.BlockchainInfo, error)
    if err != nil {
        fmt.Printf("Cannot get block chain info, error=[%v]\n", err)
        os.Exit(1)
    }
    fmt.Printf("chainInfo: Height=[%d], CurrentBlockHash=[%s], PreviousBlockHash=[%s]\n",
                    chainInfo.GetHeight(),
                    base64.StdEncoding.EncodeToString(chainInfo.CurrentBlockHash),
                    base64.StdEncoding.EncodeToString(chainInfo.PreviousBlockHash))

    // Retrieve blocks based on block number
    for i := uint64(0); i < chainInfo.GetHeight(); i++ {
        block, err := ledger.GetBlockByNumber(i) // (blockNumber uint64) (*common.Block, error)
        if err != nil {
            fmt.Printf("Cannot get block for %d, error=[%v]\n", i, err)
            os.Exit(1)
        }
        printBlock("Get", block)
    }


    // Retrieve blocks based on iterator
    itr, err := ledger.GetBlocksIterator(0) // (ResultsIterator, error)
    if err != nil {
        fmt.Printf("Cannot get iterator, error=[%v]\n", err)
        os.Exit(1)
    }
    defer itr.Close()

    queryResult, err := itr.Next()    // commonledger.QueryResult
    for i := uint64(0); err == nil; i++ {
        block := queryResult.(*cb.Block)
        printBlock("Iterator", block)
        if i >= chainInfo.GetHeight() - 1 {
            break
        }
        queryResult, err = itr.Next()    // commonledger.QueryResult
    }   
}

作者：CodingCode
链接：https://www.jianshu.com/p/5e6cbdfe2657
來源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
