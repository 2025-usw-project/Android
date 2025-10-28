//package com.su.washcall.test;
//
//import android.view.LayoutInflater;
//import android.view.View;import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.su.washcall.R;
//
//import java.util.List;
//
//public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MachineViewHolder> {
//
//    private List<Machine> machineList;
//
//    public MachineAdapter(List<Machine> machineList) {
//        this.machineList = machineList;
//    }
//
//    @NonNull
//    @Override
//    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // 🚨 중요: R.layout.item_machine 파일이 프로젝트에 있어야 합니다!
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machine, parent, false);
//        return new MachineViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
//        Machine machine = machineList.get(position);
//        holder.bind(machine);
//    }
//
//    @Override
//    public int getItemCount() {
//        return machineList.size();
//    }
//
//    // [수정] 외부에서 데이터를 한 번에 교체할 수 있는 메서드 추가
//    public void updateData(List<Machine> newMachineList) {
//        this.machineList.clear();
//        this.machineList.addAll(newMachineList);
//        notifyDataSetChanged(); // 데이터가 바뀌었음을 알려 화면을 새로고침
//    }
//
//    static class MachineViewHolder extends RecyclerView.ViewHolder {
//        // 🚨 중요: item_machine.xml에 아래 ID를 가진 UI 요소들이 있어야 합니다.
//        TextView tvMachineName;
//        ImageView ivMachineStatus;
//
//        public MachineViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvMachineName = itemView.findViewById(R.id.tvMachineName);
//            ivMachineStatus = itemView.findViewById(R.id.ivMachineStatus);
//        }
//
//        void bind(Machine machine) {
//            tvMachineName.setText(machine.getName());
//
//            // [핵심] 상태(status)에 따라 다른 이미지를 보여줍니다.
//            switch (machine.getStatus()) {
//                case "RUNNING":
//                    // 🚨 중요: R.drawable.machine_running 이미지가 있어야 합니다.
//                    ivMachineStatus.setImageResource(R.drawable.machine_running);
//                    break;
//                case "FINISHED":
//                    // 🚨 중요: R.drawable.machine_finished 이미지가 있어야 합니다.
//                    ivMachineStatus.setImageResource(R.drawable.machine_finished);
//                    break;
//                case "AVAILABLE":
//                default:
//                    // 🚨 중요: R.drawable.machine_available 이미지가 있어야 합니다.
//                    ivMachineStatus.setImageResource(R.drawable.machine_available);
//                    break;
//            }
//        }
//    }
//}
