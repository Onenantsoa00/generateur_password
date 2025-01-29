package com.password_generator.data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.password_generator.R
import com.password_generator.data.Password

class PasswordAdapter(
    private val onDeleteClick: (Password) -> Unit,
    private val onRefreshClick: (Password) -> Unit,
    private val onCopyClick: (String) -> Unit
) : ListAdapter<Password, PasswordAdapter.PasswordViewHolder>(PasswordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPassword: TextView = itemView.findViewById(R.id.tvPassword)
        private val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val btnRefresh: ImageButton = itemView.findViewById(R.id.btnRefresh)
        private val btnCopy: ImageButton = itemView.findViewById(R.id.btnCopy)

        fun bind(password: Password) {
            tvName.text = password.name
            tvPassword.text = password.password
            tvDifficulty.text = password.difficulty.name

            btnDelete.setOnClickListener {
                onDeleteClick(password)
            }

            btnRefresh.setOnClickListener {
                onRefreshClick(password)
            }

            btnCopy.setOnClickListener {
                onCopyClick(password.password)
            }
        }
    }
}

    class PasswordDiffCallback : DiffUtil.ItemCallback<Password>() {
        override fun areItemsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem == newItem
        }
    }
