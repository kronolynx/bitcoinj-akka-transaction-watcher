package example

import java.io.File

import org.bitcoinj.core._
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.utils.BriefLogFormatter
import watchwallet.{TxBitcoin, WatchOnlyWallet}


object Main extends App {
  // testnet
  val testAddress = "n2NjG9w9qa7pCiEK2DU2kaPC2qq4YUn1Yi"
  val params: TestNet3Params = TestNet3Params.get()

  // mainnet
  //  val testAddress = "3A4U175prUGEn3B1gUDkz32u8fnF9Nx3Ly"
  //  val params = MainNetParams.get()

  BriefLogFormatter.init()

  /**
    * Callback function for testing
    */
  val notifyConfirmation = (confirmations: Int, tx: TxBitcoin) => {
    println("\n\n\n---- Notification of transaction =>")
    println(s"confirmations $confirmations")
    println(tx)
    println("\n\n\n")
  }


  val filePrefix = "notifications-service"
  val kit = new WalletAppKit(params, new File("./conf/btc/"), filePrefix)

  // start bitcoinj
  kit.startAsync()
  kit.awaitRunning()
  println("\n\n\n <<<<< done loading >>>>>>\n\n\n")

  // watchOnlyWallet
  val watchOnlyWallet = WatchOnlyWallet(params, kit.wallet)
  // set number of confirmations to watch
  watchOnlyWallet.setListener(6, notifyConfirmation)
  //create address
  val address = Address.fromBase58(params, testAddress)
  //watch address
  watchOnlyWallet.addWatchAddress(address)


  try {
    Thread.sleep(Long.MaxValue)
  }
  catch {
    case _: InterruptedException => ()
  }
}










