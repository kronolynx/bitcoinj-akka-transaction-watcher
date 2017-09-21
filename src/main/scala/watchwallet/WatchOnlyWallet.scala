package watchwallet

import com.google.common.util.concurrent.{FutureCallback, Futures, MoreExecutors}
import org.bitcoinj.core._
import org.bitcoinj.wallet.Wallet

import scala.collection.JavaConverters._

/**
  * @param params    network parameters
  * @param kitWallet wallet
  */
case class WatchOnlyWallet(params: NetworkParameters, kitWallet: Wallet) {

  /**
    * Sets a listener for a wallet
    *
    * @param confirmations number of confirmations
    * @param callBack      function to call each time a confirmation is received
    * @param err           optional function to handle error
    */
  def setListener(confirmations: Int,
                  callBack: (Int, TxBitcoin) => Unit,
                  err: (Throwable) => Unit = _ => ()): Unit = {

    kitWallet.addCoinsReceivedEventListener((wallet: Wallet, tx: Transaction, _: Coin, _: Coin) => {

      // map transaction outputs to List[TxAddress]
      val outputs =
        tx
          .getWalletOutputs(wallet)
          .asScala
          .toList
          .map { x => TxAddress(x.getAddressFromP2PKHScript(params).toBase58, x.getValue.value) }

      val txBitcoin = TxBitcoin(tx.getHashAsString, tx.getUpdateTime, outputs)

      // add a callback for each confirmation
      for (i <- 0 to confirmations) addCallback(i, tx, txBitcoin, callBack, err)
    })
  }


  /**
    * Watch address for received transactions
    *
    * @param address
    */
  def addWatchAddress(address: Address): Unit = {
    kitWallet.addWatchedAddress(address)
  }

  /**
    * Stop watching address for received transactions
    *
    * @param address
    * @return
    */
  def removeWatchAddress(address: Address): Boolean = {
    kitWallet.removeWatchedAddress(address)
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
}
