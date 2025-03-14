package models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class OrderModel {
    @SerializedName("ingredients")
    private String[] ingredients;

    public OrderModel(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static OrderModel fromJson(String json) {
        return new Gson().fromJson(json, OrderModel.class);
    }
}