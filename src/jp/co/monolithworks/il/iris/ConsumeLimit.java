package jp.co.monolithworks.il.iris;

import java.util.Map;
import java.util.HashMap;

public class ConsumeLimit{

    public static String[][] limit;
    
    public static final String MEET = "1";
    public static final String FISH = "2";
    public static final String VEGETABLE = "3";
    public static final String DRINK = "4";
    public static final String DAIRY_PRODUCTS = "5";
    public static final String FRUIT = "6";
    public static final String PROCESSED_FOOD = "7";
    public static final String CONDIMENT = "8";
    public static final String FROZEN_FOOD = "9";

    public ConsumeLimit(){
        setLimitday();
    }

    private void setLimitday(){

        limit = new String[][]{
            {"トマト","１４",VEGETABLE}
            ,{"キュウリ","２",VEGETABLE}
            ,{"タマネギ","１４",VEGETABLE}
            ,{"ナス","２",VEGETABLE}
            ,{"ニンジン","５",VEGETABLE}//冬なら1週間としか書いてありませんでした。
            ,{"ジャガイモ","３０",VEGETABLE}
            ,{"ダイコン","１０",VEGETABLE}
            ,{"キャベツ","１４",VEGETABLE}
            ,{"レタス","４",VEGETABLE}
            ,{"ホウレンソウ","７",VEGETABLE}
            ,{"ピーマン","７",VEGETABLE}
            ,{"バナナ","７",FRUIT}
            ,{"リンゴ","３０",FRUIT}
            ,{"イチゴ","４",FRUIT}
            ,{"ブドウ","１４",FRUIT}
            ,{"キウイフルーツ","７",FRUIT}//1~2週間
            ,{"シイタケ","６",VEGETABLE}
            ,{"生クリーム","９０",PROCESSED_FOOD}
            ,{"マーガリン","１８０",PROCESSED_FOOD}
            ,{"ヨーグルト","１４",PROCESSED_FOOD}//プルーン
            ,{"バター","１８０",PROCESSED_FOOD}
            ,{"チーズ","４",PROCESSED_FOOD}//開封後
            ,{"牛乳","７",DRINK}
            ,{"パックジュース","１５",DRINK}
            ,{"ビール","２７０",DRINK}
            ,{"冷凍食品","３６５",FROZEN_FOOD}
            ,{"マヨネーズ","９０",CONDIMENT}
            ,{"ケチャップ","５５０",CONDIMENT}
            ,{"肉","２",MEET}
            ,{"魚","４",FISH}};
    }
}
