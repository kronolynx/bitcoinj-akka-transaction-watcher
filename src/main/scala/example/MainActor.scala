package example

import actors.{AddListener, WatchAddress, WatchWalletActor}
import akka.actor.ActorSystem
import org.bitcoinj.params.TestNet3Params
import watchwallet.TxBitcoin


object MainActor extends App {
  val system = ActorSystem("Bitcoin")

  // testnet
  val testAddress = "n2NjG9w9qa7pCiEK2DU2kaPC2qq4YUn1Yi"
  val params: TestNet3Params = TestNet3Params.get()

  // mainnet
  //  val testAddress = "3A4U175prUGEn3B1gUDkz32u8fnF9Nx3Ly"
  //  val params = MainNetParams.get()

  val btcActor = system.actorOf(WatchWalletActor.props(params))

  /**
    * Callback function for testing
    */
  val notifyConfirmation = (confirmations: Int, tx: TxBitcoin) => {
    println("\n\n\n---- Notification of transaction =>")
    println(s"confirmations $confirmations")
    println(tx)
    println("\n\n\n")
  }


  println("About to add listener")
  btcActor ! AddListener(6, notifyConfirmation)
  println("about to watch address")
  btcActor ! WatchAddress.fromString(testAddress, params)


}
