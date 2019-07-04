package com.yoyo.hobbyist.Utilis;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class UtilFuncs {
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        {

            // Create a new LinkedHashSet
            Set<T> set = new LinkedHashSet<>();

            // Add the elements to set
            set.addAll(list);

            // Clear the list
            list.clear();

            // add the elements of set
            // with no duplicates to the list
            list.addAll(set);

            // return the list
            return list;
        }
    }
    public static void saveUserToFireBase(UserProfile userProfile){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appUsers").child(userProfile.getmUserToken());
        reference.setValue(userProfile);
    }
}
