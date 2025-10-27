package com.su.washcall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.su.washcall.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.su.washcall.network.washmachinResponse.MachineInfo;

import java.util.ArrayList;
import java.util.List;

public class MachineListAdapter extends RecyclerView.Adapter<MachineListAdapter.MachineViewHolder> {

    private List<MachineInfo> machines = new ArrayList<>();

    // 1. ViewHolder: item_machine.xml의 뷰들을 보관하는 객체
    static class MachineViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMachineIcon;
        private final TextView tvMachineName;
        private final TextView tvMachineId;
        private final TextView tvMachineStatus;
        private final Context context;

        public MachineViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ivMachineIcon = itemView.findViewById(R.id.ivMachineIcon);
            tvMachineName = itemView.findViewById(R.id.tvMachineName);
            tvMachineId = itemView.findViewById(R.id.tvMachineId);
            tvMachineStatus = itemView.findViewById(R.id.tvMachineStatus);
        }

        // 데이터를 뷰에 바인딩하는 메서드
        public void bind(MachineInfo machine) {
            tvMachineName.setText(machine.getMachineName());
            tvMachineId.setText("기기 ID: " + machine.getMachineId());

            // 상태(status)에 따라 텍스트와 배경색 변경
            if ("available".equalsIgnoreCase(machine.getStatus())) {
                tvMachineStatus.setText("사용 가능");
                tvMachineStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_status_available));
            } else {
                tvMachineStatus.setText("사용 중");
                // TODO: '사용 중' 상태에 맞는 배경(예: bg_status_in_use)을 만들고 적용하면 더 좋습니다.
                // 지금은 임시로 회색으로 처리합니다.
                tvMachineStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
        }
    }

    // 2. onCreateViewHolder: ViewHolder를 새로 만들어야 할 때 호출됨
    @NonNull
    @Override
    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_machine, parent, false);
        return new MachineViewHolder(view);
    }

    // 3. onBindViewHolder: ViewHolder를 데이터와 연결할 때 호출됨
    @Override
    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
        MachineInfo currentMachine = machines.get(position);
        holder.bind(currentMachine);
    }

    // 4. getItemCount: 데이터 세트의 크기를 알려줌
    @Override
    public int getItemCount() {
        return machines.size();
    }

    // 5. Activity에서 새로운 데이터 리스트를 받았을 때, RecyclerView를 갱신하기 위한 메서드
    public void setMachines(List<MachineInfo> newMachines) {
        this.machines.clear();
        if (newMachines != null) {
            this.machines.addAll(newMachines);
        }
        notifyDataSetChanged();
    }
}
