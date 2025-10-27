package com.su.washcall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.su.washcall.R;
import com.su.washcall.database.LaundryRoom;

import java.util.ArrayList;
import java.util.List;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder> {

    private List<LaundryRoom> rooms = new ArrayList<>();
    private OnItemClickListener listener;

    // 1. ViewHolder: item_laundry_room.xml의 뷰들을 관리합니다.
    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        // ▼▼▼▼▼ [수정됨] ID를 item_laundry_room.xml에 맞게 'tvRoomName'으로 변경 ▼▼▼▼▼
        public TextView textViewRoomName;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_laundry_room.xml 에 있는 TextView의 ID(tvRoomName)를 찾습니다.
            textViewRoomName = itemView.findViewById(R.id.tvRoomName);
        }
        // ▲▲▲▲▲ [수정됨] ▲▲▲▲▲

        public void bind(final LaundryRoom room, final OnItemClickListener listener) {
            textViewRoomName.setText(room.getRoomName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(room);
                }
            });
        }
    }

    // 2. onCreateViewHolder: item_laundry_room.xml 레이아웃을 로드합니다.
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ▼▼▼▼▼ [수정됨] 실제 존재하는 레이아웃 파일 이름으로 변경 ▼▼▼▼▼
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laundry_room, parent, false);
        // ▲▲▲▲▲ [수정됨] ▲▲▲▲▲
        return new RoomViewHolder(itemView);
    }

    // 3. onBindViewHolder: ViewHolder에 데이터를 바인딩합니다. (변경 없음)
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        LaundryRoom currentRoom = rooms.get(position);
        holder.bind(currentRoom, listener);
    }

    // 4. getItemCount: 아이템 개수 반환 (변경 없음)
    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
    }

    // 5. setRooms: 외부에서 데이터를 받아와서 리스트를 갱신 (null 처리 강화)
    public void setRooms(List<LaundryRoom> rooms) {
        this.rooms = (rooms != null) ? rooms : new ArrayList<>();
        notifyDataSetChanged();
    }

    // 6. 클릭 리스너 인터페이스 및 설정 메서드 (변경 없음)
    public interface OnItemClickListener {
        void onItemClick(LaundryRoom room);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
