//package com.yoyo.hobbyist.ViewModel;
//
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.ViewModel;
//
//import com.yoyo.hobbyist.DataModels.Chat;
//import com.yoyo.hobbyist.DataModels.UserPost;
//import com.yoyo.hobbyist.DataModels.UserProfile;
//import com.yoyo.hobbyist.repository.ChatRepository;
//import com.yoyo.hobbyist.repository.FirebaseDatabaseRepository;
//import com.yoyo.hobbyist.repository.UserPostRepository;
//import com.yoyo.hobbyist.repository.UserProfileRepository;
//
//import java.util.List;
//
//public class DataViewModel extends ViewModel {
//
//    private MutableLiveData<List<UserPost>> postsList;
//    private MutableLiveData<List<UserProfile>> usersList;
//    private MutableLiveData<List<Chat>> chatList;
//
//    private ChatRepository chatRepository = new ChatRepository();
//    private UserPostRepository userPostRepository = new UserPostRepository();
//    private UserProfileRepository userProfileRepository = new UserProfileRepository();
//
//    public LiveData<List<UserPost>> getPostsList() {
//        if (postsList == null) {
//            postsList = new MutableLiveData<>();
//
//            loadPostsList();
//        }
//        return postsList;
//    }
//
//    public MutableLiveData<List<UserProfile>> getUsersList() {
//        if (usersList == null) {
//            usersList = new MutableLiveData<>();
//
//            loadUsersList();
//        }
//        return usersList;
//
//    }
//
//    public LiveData<List<Chat>> getChatList() {
//        if (chatList == null) {
//            chatList = new MutableLiveData<>();
//
//            loadChatsList();
//        }
//        return chatList;
//    }
//
//    @Override
//    protected void onCleared() {
//        chatRepository.removeListener();
//        userPostRepository.removeListener();
//        userProfileRepository.removeListener();
//    }
//
//
//    private void loadPostsList() {
//        userPostRepository.addListener( new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<UserPost>() {
//            @Override
//            public void onSuccess(List<UserPost> result) {
//                postsList.setValue( result );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                usersList.setValue( null );
//            }
//        } );
//    }
//
//
//    private void loadChatsList() {
//        chatRepository.addListener( new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Chat>() {
//            @Override
//            public void onSuccess(List<Chat> result) {
//                chatList.setValue( result );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                chatList.setValue( null );
//            }
//        } );
//
//    }
//
//
//    private void loadUsersList() {
//        userProfileRepository.addListener( new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<UserProfile>() {
//            @Override
//            public void onSuccess(List<UserProfile> result) {
//                usersList.setValue( result );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                usersList.setValue( null );
//            }
//        } );
//    }
//}
