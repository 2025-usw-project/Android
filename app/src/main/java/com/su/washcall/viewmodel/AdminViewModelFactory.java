package com.su.washcall.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * AdminViewModel 인스턴스를 생성하는 팩토리 클래스입니다.
 * <p>
 * AdminViewModel은 생성자에서 Application 객체를 필요로 하기 때문에,
 * ViewModel을 생성할 때 이 팩토리를 반드시 사용해야 합니다.
 * 이 팩토리는 ViewModelProvider에게 AdminViewModel을 어떻게 생성해야 하는지 알려주는 역할을 합니다.
 */
public class AdminViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    /**
     * 생성자
     * @param application ViewModel에 전달할 Application 인스턴스입니다.
     */
    public AdminViewModelFactory(Application application) {
        this.application = application;
    }

    /**
     * ViewModelProvider가 ViewModel 인스턴스 생성을 요청할 때 호출되는 메서드입니다.
     * @param modelClass 생성 요청된 ViewModel의 클래스 타입입니다.
     * @return 생성된 ViewModel 인스턴스를 반환합니다.
     * @throws IllegalArgumentException 요청된 modelClass가 AdminViewModel이 아닐 경우 예외를 발생시킵니다.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // 요청된 클래스가 AdminViewModel 클래스이거나 그 자식 클래스인지 확인합니다.
        if (modelClass.isAssignableFrom(AdminViewModel.class)) {
            try {
                // AdminViewModel 생성자를 호출하여 인스턴스를 생성하고 반환합니다.
                // (T) 캐스팅을 통해 제네릭 타입으로 안전하게 변환합니다.
                return (T) new AdminViewModel(application);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        // 이 팩토리가 만들 수 없는 다른 타입의 ViewModel이 요청되면 예외를 발생시킵니다.
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
