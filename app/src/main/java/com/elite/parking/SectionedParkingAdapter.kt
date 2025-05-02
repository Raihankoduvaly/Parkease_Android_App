import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.ListItem
import com.elite.parking.Model.parkingslots.ParkingSlot

class SectionedParkingAdapter(
    private val context: Context,
    private var items: List<ListItem>,
    private val onSlotClick: (ListItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_BLOCK_HEADER = 0
        const val TYPE_FLOOR_HEADER = 1
        const val TYPE_PARKING_SLOT = 2
    }

    fun updateData(newItems: List<ListItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.BlockHeader -> TYPE_BLOCK_HEADER
            is ListItem.FloorHeader -> TYPE_FLOOR_HEADER
            is ListItem.ParkingSlotItem -> TYPE_PARKING_SLOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            TYPE_BLOCK_HEADER -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                BlockHeaderViewHolder(view)
            }
            TYPE_FLOOR_HEADER -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                FloorHeaderViewHolder(view)
            }
            TYPE_PARKING_SLOT -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                ParkingSlotViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is BlockHeaderViewHolder -> holder.bind((item as ListItem.BlockHeader).block)
            is FloorHeaderViewHolder -> holder.bind((item as ListItem.FloorHeader).floor)
            is ParkingSlotViewHolder -> holder.bind((item as ListItem.ParkingSlotItem).slot)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class BlockHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(android.R.id.text1)
        fun bind(block: String) {
            title.text = "Block: $block"
        }
    }

    inner class FloorHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(android.R.id.text1)
        fun bind(floor: String) {
            title.text = "Floor: $floor"
        }
    }

    inner class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slotText: TextView = itemView.findViewById(android.R.id.text1)
        fun bind(slot: ParkingSlot) {
            slotText.text = slot.parkingNo + if (slot.isAvailable) " ✅" else " ❌"
            itemView.setOnClickListener {
                onSlotClick(ListItem.ParkingSlotItem(slot))
            }
        }
    }
}
