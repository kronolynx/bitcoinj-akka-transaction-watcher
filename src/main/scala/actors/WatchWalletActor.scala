package actors

import java.io.File

import akka.actor.{Actor, Props, Stash}
import akka.event.Logging
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.utils.BriefLogFormatter
import watchwallet.WatchOnlyWallet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class WatchWalletActor(params: NetworkParameters) extends Actor with Stash {

  val log = Logging(context.system, this)

  override def receive = uninitialized

  def uninitialized: Receive = {
    case w: WatchOnlyWallet =>
      // initialize after bitcoinj has finished loading
      context.become(initialized(w))
      unstashAll()
    case _ => stash()

  }

  def initialized(watchWallet: WatchOnlyWallet): Receive = {
    case AddListener(confirmations, callback, err) =>
      log.debug("adding listener")
      watchWallet.setListener(confirmations, callback, err)
    case WatchAddress(address) =>
      log.debug("watching address")
      watchWallet.addWatchAddress(address)
    case UnWatchAddress(address) =>
      watchWallet.removeWatchAddress(address)
    case x: String => log.debug(s"got string $x")
    case x => log.debug(s"unknown $x")
  }

  BriefLogFormatter.init()

  val filePrefix = "notifications-service"
  val kit = new WalletAppKit(params, new File("./conf/btc/"), filePrefix)

  Future {
    kit.startAsync()
    kit.awaitRunning()
    log.debug("\n---------- done loading ---------------\n")
  }.onComplete {
    case Success(_) =>
      // create watch only wallet
      val watchOnlyWallet = WatchOnlyWallet(params, kit.wallet)
      self ! watchOnlyWallet
    case Failure(e) => log.error(s"error -> ${e.getMessage}")
  }
}

object WatchWalletActor {
  def props(params: NetworkParameters) =
    Props(classOf[WatchWalletActor], params)
}
