package example

import java.io.File

import WatchWallet.{TxBitcoin, WatchOnlyWallet}
import org.bitcoinj.core._
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.utils.BriefLogFormatter


object Main extends App {
  val testAddress = "n2NjG9w9qa7pCiEK2DU2kaPC2qq4YUn1Yi"
  //    val testAddress = "3A4U175prUGEn3B1gUDkz32u8fnF9Nx3Ly"
  //  val testAddress = "n4DZ8yhigWSt7wYK2wdV7Bgj7WGVrwgW4e"

  BriefLogFormatter.init()

  val notifyConfirmation = (confirmations: Int, tx: TxBitcoin) => {
    println("\n\n\n---- Notification of transaction =>")
    println(s"confirmations $confirmations")
    println(tx)
    println("\n\n\n")
  }

  val params: TestNet3Params = TestNet3Params.get()
  //  val params = MainNetParams.get()

  val filePrefix = "notifications-service"
  val kit = new WalletAppKit(params, new File("./conf/btc/"), filePrefix)


  println("about to start")
  // start bitcoinj
  kit.startAsync()
  kit.awaitRunning()
  println("\n\n\n <<<<< done >>>>>>\n\n\n")

  // watchOnlyWallet
  val watchOnlyWallet = WatchOnlyWallet(params, kit.wallet)
  // set number of confirmations to watch
  watchOnlyWallet.setListener(6, notifyConfirmation)
  //create address
  val address = Address.fromBase58(params, testAddress)
  //watch address
  watchOnlyWallet.watchAddress(address)

  try {
    Thread.sleep(Long.MaxValue)
  }
  catch {
    case _: InterruptedException => ()
  }
}










