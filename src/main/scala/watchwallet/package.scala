import java.util.Date

package object watchwallet {

  case class TxBitcoin(txHash: String, updated: Date, outputs: List[TxAddress])

  case class TxAddress(address: String, value: Long)

}
