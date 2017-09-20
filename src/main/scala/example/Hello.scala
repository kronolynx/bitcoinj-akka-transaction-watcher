package example

import java.io.File
import java.util.Date

import com.google.common.util.concurrent.{FutureCallback, Futures, MoreExecutors}
import org.bitcoinj.core._
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet

import scala.collection.JavaConverters._

object Hello extends App {
  val testAddress = "n2NjG9w9qa7pCiEK2DU2kaPC2qq4YUn1Yi"
  //    val testAddress = "3A4U175prUGEn3B1gUDkz32u8fnF9Nx3Ly"
  //  val testAddress = "n4DZ8yhigWSt7wYK2wdV7Bgj7WGVrwgW4e"

  BriefLogFormatter.init()

  val params = TestNet3Params.get()
  //  val params = MainNetParams.get()

  val filePrefix = "notifications-service"
  val kit = new WalletAppKit(params, new File("./conf/btc/"), filePrefix)

  println("about to start")
  kit.startAsync()
  kit.awaitRunning()
  println("\n\n\n <<<<< done >>>>>>\n\n\n")
  setListener(kit)
  val add = Address.fromBase58(params, testAddress)
  kit.wallet.addWatchedAddress(add)
  try {
    Thread.sleep(Long.MaxValue)
  }
  catch {
    case _: InterruptedException => ()
  }


  def setListener(w: WalletAppKit) = {
    w.wallet.addCoinsReceivedEventListener((wallet: Wallet, tx: Transaction, _: Coin, _: Coin) => {

      val outputs = tx
        .getWalletOutputs(wallet)
        .asScala
        .toList
        .map { x => TxAddress(x.getAddressFromP2PKHScript(params).toBase58, x.getValue.value)}

      val txBitcoin = TxBitcoin(tx.getHashAsString, tx.getUpdateTime, outputs)

      for (i <- 0 to 6) addCallback(i, tx, txBitcoin, notifyConfirmation)
    })
  }

  private def addCallback(depth: Int, tx: Transaction,
                          txBitcoin: TxBitcoin,
                          callBack: (Int, TxBitcoin) => Unit,
                          err: (Throwable) => Unit = _ => ()): Unit = {

    Futures.addCallback(tx.getConfidence.getDepthFuture(depth),
      new FutureCallback[TransactionConfidence] {
        def onSuccess(result: TransactionConfidence) {
          callBack(result.getDepthInBlocks, txBitcoin)
        }
        def onFailure(t: Throwable) {
          err(t)
        }
      }, MoreExecutors.directExecutor())
  }

  private def notifyConfirmation(confirmations: Int, tx: TxBitcoin): Unit = {
    println("\n\n\n---- Notification of transaction =>")
    println(s"confirmations $confirmations")
    println(tx)
    println("\n\n\n\n")
  }
}

case class TxBitcoin(txHash:String, updated: Date, outputs: List[TxAddress])
case class TxAddress(address: String, value: Long)




