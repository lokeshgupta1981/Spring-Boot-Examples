package com.howtodoinjava.gson;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class CatalogItem {
    @SerializedName("item_code")
    public String code = "P1";
    public String note = null;
    public LocalDate availableOn = LocalDate.of(2026, 11, 1);
}
