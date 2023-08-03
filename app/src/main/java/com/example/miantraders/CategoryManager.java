package com.example.miantraders;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CategoryManager {
    private static final String PREF_NAME = "CategoryPrefs";
    private static final String CATEGORY_KEY = "categoryList";

    public static void addCategory(Context context, String newCategory) {
        ArrayList<String> categoryList = getCategoryList(context);
        // Add new category
        categoryList.add(newCategory);
        // Save updated category list
        saveCategoryList(context, categoryList);
    }

    public static ArrayList<String> getCategoryList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String categoryListJson = sharedPreferences.getString(CATEGORY_KEY, null);

        if (categoryListJson != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(categoryListJson, type);
        } else {
            return new ArrayList<>();
        }
    }

    private static void saveCategoryList(Context context, ArrayList<String> categoryList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String categoryListJson = new Gson().toJson(categoryList);
        editor.putString(CATEGORY_KEY, categoryListJson);
        editor.apply();
    }
}
