//package com.su.washcall.test;
//
//import com.google.gson.annotations.SerializedName;
//
///**
// * 서버의 세탁기 데이터를 앱에서 사용하기 위한 '데이터 설계도(모델)' 클래스입니다.
// * Retrofit 라이브러리가 서버의 JSON 응답을 이 클래스 객체로 자동 변환해줍니다.
// */
//public class Machine {
//
//    // @SerializedName("서버_필드명"): 서버의 JSON 필드 이름과 자바 변수 이름을 매핑합니다.
//
//    // 세탁기 고유 ID
//    @SerializedName("id")
//    private int id;
//
//    // 세탁기 이름 (예: "1번 세탁기")
//    @SerializedName("machine_name")
//    private String name;
//
//    // 세탁기 상태 (예: "AVAILABLE", "RUNNING", "FINISHED")
//    @SerializedName("status")
//    private String status;
//
//    /**
//     * 생성자: Machine 객체를 만들 때 필요한 값들을 설정합니다.
//     */
//    public Machine(int id, String name, String status) {
//        this.id = id;
//        this.name = name;
//        this.status = status;
//    }
//
//    /**
//     * Getter 메서드: 객체 외부에서 내부 변수 값을 안전하게 읽을 수 있게 해줍니다.
//     */
//    public int getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//}
