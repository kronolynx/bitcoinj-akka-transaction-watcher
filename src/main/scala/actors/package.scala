import org.bitcoinj.core.{Address, NetworkParameters}
import watchwallet.TxBitcoin

package object actors {

  case class WatchAddress(address: Address)

  object WatchAddress {
    def fromString(address: String,
                   params: NetworkParameters): WatchAddress = {
      val add = Address.fromBase58(params, address)
      new WatchAddress(add)
    }
  }

  case class RemoveWatchedAddress(address: Address)

  case class AddListener(confirmations: Int,
                         callBack: (Int, TxBitcoin) => Unit,
                         err: (Throwable) => Unit = _ => ())

}
